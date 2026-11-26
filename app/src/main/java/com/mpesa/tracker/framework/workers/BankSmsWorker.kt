package com.mpesa.tracker.framework.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.data.local.entities.TransactionNature
import com.mpesa.tracker.data.local.entities.TransactionType
import com.mpesa.tracker.data.repository.TransactionRepository
import com.mpesa.tracker.domain.parser.BankSmsParser
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class BankSmsWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val transactionRepository: TransactionRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val smsBody = inputData.getString("sms_body") ?: return@withContext Result.failure()
        val smsSender = inputData.getString("sms_sender") ?: return@withContext Result.failure()
        val smsDate = inputData.getLong("sms_date", System.currentTimeMillis())

        return@withContext try {
            val parsed = BankSmsParser.parseMessage(smsSender, smsBody, smsDate)
            if (parsed == null) {
                if (smsBody.contains("KES", ignoreCase = true) || smsBody.contains("Ksh", ignoreCase = true)) {
                    transactionRepository.logUnparsedSms(
                        sender = smsSender,
                        body = smsBody,
                        bodyHash = BankSmsParser.sha256("$smsSender|$smsBody"),
                        receivedAt = smsDate
                    )
                }
                return@withContext Result.success()
            }

            if (parsed.isTransientNotice) {
                parsed.linkedMpesaRef?.let { transactionRepository.recordMpesaRefTransferHint(it) }
                return@withContext Result.success()
            }

            val nature = parsed.natureHint
                ?: if (parsed.isIncome) TransactionNature.INCOME else TransactionNature.EXPENSE

            val transaction = TransactionEntity(
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
                rawTx = transaction,
                linkedMpesaRef = parsed.linkedMpesaRef,
                isTransferCandidate = parsed.isTransferCandidate
            )
            Log.d("BankSmsWorker", "Ingested bank tx: ${parsed.referenceNumber} (${parsed.source.displayName})")
            Result.success()
        } catch (e: Exception) {
            Log.e("BankSmsWorker", "Error processing bank SMS", e)
            Result.failure()
        }
    }
}
