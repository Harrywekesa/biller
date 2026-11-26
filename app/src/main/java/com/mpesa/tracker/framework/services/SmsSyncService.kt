package com.mpesa.tracker.framework.services

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.data.local.entities.TransactionNature
import com.mpesa.tracker.data.local.entities.TransactionType
import com.mpesa.tracker.data.repository.TransactionRepository
import com.mpesa.tracker.domain.parser.AccountSource
import com.mpesa.tracker.domain.parser.BankSmsParser
import com.mpesa.tracker.domain.parser.MpesaParser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsSyncService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val transactionRepository: TransactionRepository
) {
    companion object {
        private const val PREFS_NAME = "mpesa_sync_prefs"
        private const val KEY_LAST_SYNCED_TS = "last_synced_timestamp"
        private const val KEY_LAST_BANK_SYNCED = "last_bank_synced_timestamp_v2"
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Incrementally syncs M-Pesa & Kenyan bank SMS (Equity, I&M, Absa, KCB, Co-op, NCBA).
     * Never deletes existing transactions, notes, custom rules, or budgets.
     */
    suspend fun syncHistoricMessages(): Int = withContext(Dispatchers.IO) {
        transactionRepository.reconcileExistingDataOnceIfNeeded()
        val mpesaCount = syncMpesa()
        val bankCount = syncBanks()
        Log.d("SmsSyncService", "Sync finished. New M-Pesa=$mpesaCount, Bank=$bankCount")
        mpesaCount + bankCount
    }

    internal suspend fun syncMpesa(): Int {
        var syncedCount = 0
        val lastSynced = prefs.getLong(KEY_LAST_SYNCED_TS, 0L)
        var newest = lastSynced

        try {
            val inboxUri = Uri.parse("content://sms/inbox")
            val selection = if (lastSynced > 0L) "(address = ? COLLATE NOCASE OR address = ? COLLATE NOCASE) AND date > ?" else "address = ? COLLATE NOCASE OR address = ? COLLATE NOCASE"
            val selectionArgs = if (lastSynced > 0L) arrayOf("MPESA", "M-PESA", lastSynced.toString()) else arrayOf("MPESA", "M-PESA")

            context.contentResolver.query(inboxUri, null, selection, selectionArgs, "date ASC")?.use { cursor ->
                val bodyIdx = cursor.getColumnIndex("body")
                val dateIdx = cursor.getColumnIndex("date")
                val addrIdx = cursor.getColumnIndex("address")
                val subIdx = cursor.getColumnIndex(android.provider.Telephony.Sms.SUBSCRIPTION_ID)

                while (cursor.moveToNext()) {
                    val body = cursor.getString(bodyIdx) ?: ""
                    val smsDate = cursor.getLong(dateIdx)
                    val sender = if (addrIdx != -1) cursor.getString(addrIdx) ?: "MPESA" else "MPESA"

                    var simId: Int? = if (subIdx != -1 && !cursor.isNull(subIdx)) cursor.getInt(subIdx) else null
                    if (simId == null) {
                        val alt = cursor.getColumnIndex("sim_id")
                        if (alt != -1 && !cursor.isNull(alt)) simId = cursor.getInt(alt)
                    }
                    if (simId != null && simId < 0) simId = null

                    val parsed = MpesaParser.parseMessage(body, smsDate)
                    if (parsed != null) {
                        try {
                            val nature = when {
                                parsed.recipientName.startsWith("Reversal (") -> TransactionNature.REVERSAL
                                parsed.type == TransactionType.FULIZA_DRAW -> TransactionNature.LOAN_DRAW
                                parsed.type == TransactionType.FULIZA_REPAYMENT -> TransactionNature.LOAN_REPAY
                                parsed.type == TransactionType.WITHDRAW_CASH -> TransactionNature.TRANSFER
                                parsed.type == TransactionType.RECEIVED_MONEY -> TransactionNature.INCOME
                                else -> TransactionNature.EXPENSE
                            }
                            val tx = TransactionEntity(
                                receiptNumber = parsed.receiptNumber,
                                type = parsed.type,
                                amount = parsed.amount,
                                transactionCost = parsed.transactionCost,
                                dateTimestamp = parsed.timestamp,
                                recipientName = parsed.recipientName,
                                recipientNumber = parsed.recipientNumber,
                                balance = parsed.balance,
                                categoryId = null,
                                isIncome = parsed.type == TransactionType.RECEIVED_MONEY,
                                fulizaAmount = parsed.fulizaAmount,
                                fulizaFee = parsed.fulizaFee,
                                rawSmsBody = parsed.rawSms,
                                simSubscriptionId = simId,
                                linkedReceiptNumber = parsed.linkedReceiptNumber,
                                accountSource = AccountSource.MPESA.name,
                                transactionNature = nature.name
                            )
                            transactionRepository.ingestTransaction(tx)
                            syncedCount++
                            if (smsDate > newest) newest = smsDate
                        } catch (e: Exception) {
                            Log.e("SmsSyncService", "Error saving M-Pesa tx: ${parsed.receiptNumber}", e)
                        }
                    } else if (body.contains("Confirmed", ignoreCase = true) && !body.contains("Please await MPESA", ignoreCase = true)) {
                        transactionRepository.logUnparsedSms(sender, body, BankSmsParser.sha256("$sender|$body"), smsDate)
                    }
                }
            }
            if (newest > lastSynced) prefs.edit().putLong(KEY_LAST_SYNCED_TS, newest).apply()
            transactionRepository.reconcileExistingReversals()
        } catch (e: Exception) {
            Log.e("SmsSyncService", "Error reading M-Pesa SMS inbox", e)
        }
        return syncedCount
    }

    internal suspend fun syncBanks(): Int {
        var syncedCount = 0
        val lastSynced = prefs.getLong(KEY_LAST_BANK_SYNCED, 0L)
        var newest = lastSynced

        try {
            val inboxUri = Uri.parse("content://sms/inbox")
            val senderClauses = BankSmsParser.INBOX_SENDER_TOKENS.map { "address LIKE ? COLLATE NOCASE" }
            val bodyFallbackClauses = listOf(
                "body LIKE '%Bank to M-PESA transfer%'",
                "body LIKE '%IM Bank.%'",
                "body LIKE '%VIA Digital/Branch Banking%'",
                "body LIKE '%MPESA payment accepted%'",
                "body LIKE '%Please await MPESA notification%'",
                "body LIKE '%Till No.%'"
            )
            val allOrClauses = (senderClauses + bodyFallbackClauses).joinToString(" OR ")
            val selection = if (lastSynced > 0L) "($allOrClauses) AND date > ?" else "($allOrClauses)"
            val senderArgs = BankSmsParser.INBOX_SENDER_TOKENS.map { "%$it%" }
            val selectionArgs = if (lastSynced > 0L) (senderArgs + lastSynced.toString()).toTypedArray() else senderArgs.toTypedArray()

            context.contentResolver.query(inboxUri, null, selection, selectionArgs, "date ASC")?.use { cursor ->
                val bodyIdx = cursor.getColumnIndex("body")
                val dateIdx = cursor.getColumnIndex("date")
                val addressIdx = cursor.getColumnIndex("address")

                while (cursor.moveToNext()) {
                    val body = cursor.getString(bodyIdx) ?: ""
                    val smsDate = cursor.getLong(dateIdx)
                    val sender = cursor.getString(addressIdx) ?: ""

                    // Skip MPESA sender unless body is a special till confirmation not parsed by MpesaParser
                    if (sender.equals("MPESA", ignoreCase = true) || sender.equals("M-PESA", ignoreCase = true)) {
                        continue
                    }

                    val parsed = BankSmsParser.parseMessage(sender, body, smsDate)
                    if (parsed == null) {
                        if (body.contains("KES", ignoreCase = true) || body.contains("Ksh", ignoreCase = true)) {
                            transactionRepository.logUnparsedSms(sender, body, BankSmsParser.sha256("$sender|$body"), smsDate)
                        }
                        continue
                    }

                    if (parsed.isTransientNotice) {
                        parsed.linkedMpesaRef?.let { transactionRepository.recordMpesaRefTransferHint(it) }
                        if (smsDate > newest) newest = smsDate
                        continue
                    }

                    try {
                        val nature = parsed.natureHint
                            ?: if (parsed.isIncome) TransactionNature.INCOME else TransactionNature.EXPENSE

                        val tx = TransactionEntity(
                            receiptNumber = parsed.referenceNumber,
                            type = if (parsed.isIncome) TransactionType.RECEIVED_MONEY else TransactionType.PAYBILL,
                            amount = parsed.amount,
                            transactionCost = parsed.transactionCost,
                            dateTimestamp = parsed.timestamp,
                            recipientName = parsed.recipientName,
                            recipientNumber = parsed.recipientNumber,
                            balance = parsed.balance,
                            categoryId = null,
                            isIncome = parsed.isIncome,
                            rawSmsBody = parsed.rawSms,
                            simSubscriptionId = null,
                            linkedReceiptNumber = parsed.linkedMpesaRef,
                            accountSource = parsed.source.name,
                            transactionNature = nature.name,
                            transferGroupRef = parsed.linkedMpesaRef
                        )
                        transactionRepository.ingestTransaction(
                            rawTx = tx,
                            linkedMpesaRef = parsed.linkedMpesaRef,
                            isTransferCandidate = parsed.isTransferCandidate
                        )
                        syncedCount++
                        if (smsDate > newest) newest = smsDate
                    } catch (e: Exception) {
                        Log.e("SmsSyncService", "Error saving bank tx: ${parsed.referenceNumber}", e)
                    }
                }
            }
            if (newest > lastSynced) prefs.edit().putLong(KEY_LAST_BANK_SYNCED, newest).apply()
        } catch (e: Exception) {
            Log.e("SmsSyncService", "Error reading bank SMS inbox", e)
        }
        return syncedCount
    }

    /**
     * Re-scans all SMS from Inbox using idempotent upsert/ignore WITHOUT deleting existing user notes,
     * manual cash entries, categories, budgets, or custom rules.
     */
    suspend fun nonDestructiveFullInboxRescan(): Int {
        resetSyncTimestamp()
        return syncHistoricMessages()
    }

    fun resetSyncTimestamp() {
        prefs.edit()
            .remove(KEY_LAST_SYNCED_TS)
            .remove(KEY_LAST_BANK_SYNCED)
            .apply()
    }
}
