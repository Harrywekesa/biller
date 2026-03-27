package com.mpesa.tracker.framework.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mpesa.tracker.data.local.dao.TransactionDao
import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.data.local.entities.TransactionType
import com.mpesa.tracker.domain.parser.MpesaParser
import com.mpesa.tracker.domain.classifier.CategorizationEngine
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class MpesaSmsWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val transactionDao: TransactionDao,
    private val categorizationEngine: CategorizationEngine
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val smsBody = inputData.getString("sms_body")

        if (smsBody.isNullOrEmpty()) {
            Log.e("MpesaSmsWorker", "No SMS body provided to worker")
            return@withContext Result.failure()
        }

        try {
            val parsedResult = MpesaParser.parseMessage(smsBody)
            if (parsedResult != null) {
                val simId = inputData.getInt("sim_id", -1)
                val finalSimId = if (simId != -1) simId else null
                
                var transaction = TransactionEntity(
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
                    simSubscriptionId = finalSimId
                )
                
                val categoryId = categorizationEngine.categorize(transaction)
                if (categoryId != null) {
                    transaction = transaction.copy(categoryId = categoryId)
                }

                transactionDao.insertTransaction(transaction)
                Log.d("MpesaSmsWorker", "Transaction saved to database via WorkManager: ${transaction.receiptNumber}")
                return@withContext Result.success()
            } else {
                Log.w("MpesaSmsWorker", "Worker failed to Regex match the M-Pesa message.")
                return@withContext Result.failure()
            }
        } catch (e: Exception) {
            Log.e("MpesaSmsWorker", "Error processing SMS", e)
            return@withContext Result.failure()
        }
    }
}
