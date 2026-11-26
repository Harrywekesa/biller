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
import com.mpesa.tracker.domain.parser.AccountSource
import com.mpesa.tracker.domain.parser.BankSmsParser
import com.mpesa.tracker.domain.parser.MpesaParser
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class MpesaSmsWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val transactionRepository: TransactionRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val smsBody = inputData.getString("sms_body") ?: return@withContext Result.failure()
        val smsSender = inputData.getString("sms_sender") ?: "MPESA"
        val smsDate = inputData.getLong("sms_date", System.currentTimeMillis())
        if (smsBody.isBlank()) return@withContext Result.failure()

        return@withContext try {
            val parsedResult = MpesaParser.parseMessage(smsBody, smsDate)
            if (parsedResult != null) {
                val rawSimId = inputData.getInt("sim_id", -1)
                val finalSimId = if (rawSimId >= 0) rawSimId else null

                val initialNature = when {
                    parsedResult.recipientName.startsWith("Reversal (") -> TransactionNature.REVERSAL
                    parsedResult.type == TransactionType.FULIZA_DRAW -> TransactionNature.LOAN_DRAW
                    parsedResult.type == TransactionType.FULIZA_REPAYMENT -> TransactionNature.LOAN_REPAY
                    parsedResult.type == TransactionType.WITHDRAW_CASH -> TransactionNature.TRANSFER
                    parsedResult.type == TransactionType.RECEIVED_MONEY -> TransactionNature.INCOME
                    else -> TransactionNature.EXPENSE
                }

                val transaction = TransactionEntity(
                    receiptNumber = parsedResult.receiptNumber,
                    type = parsedResult.type,
                    amount = parsedResult.amount,
                    transactionCost = parsedResult.transactionCost,
                    dateTimestamp = parsedResult.timestamp,
                    recipientName = parsedResult.recipientName,
                    recipientNumber = parsedResult.recipientNumber,
                    balance = parsedResult.balance,
                    categoryId = null,
                    isIncome = parsedResult.type == TransactionType.RECEIVED_MONEY,
                    fulizaAmount = parsedResult.fulizaAmount,
                    fulizaFee = parsedResult.fulizaFee,
                    rawSmsBody = parsedResult.rawSms,
                    simSubscriptionId = finalSimId,
                    linkedReceiptNumber = parsedResult.linkedReceiptNumber,
                    accountSource = AccountSource.MPESA.name,
                    transactionNature = initialNature.name
                )

                transactionRepository.ingestTransaction(transaction)
                Log.d("MpesaSmsWorker", "Ingested M-Pesa tx: ${transaction.receiptNumber}")
                Result.success()
            } else {
                if (smsBody.contains("Confirmed", ignoreCase = true) && !smsBody.contains("Please await MPESA", ignoreCase = true)) {
                    transactionRepository.logUnparsedSms(
                        sender = smsSender,
                        body = smsBody,
                        bodyHash = BankSmsParser.sha256("$smsSender|$smsBody"),
                        receivedAt = smsDate
                    )
                }
                Result.success()
            }
        } catch (e: Exception) {
            Log.e("MpesaSmsWorker", "Error processing SMS", e)
            Result.failure()
        }
    }
}
