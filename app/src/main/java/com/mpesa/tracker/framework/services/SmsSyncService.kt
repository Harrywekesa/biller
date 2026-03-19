package com.mpesa.tracker.framework.services

import android.content.Context
import android.net.Uri
import android.util.Log
import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.data.local.entities.TransactionType
import com.mpesa.tracker.data.repository.TransactionRepository
import com.mpesa.tracker.domain.parser.MpesaParser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import com.mpesa.tracker.domain.classifier.CategorizationEngine
@Singleton
class SmsSyncService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val transactionRepository: TransactionRepository,
    private val categorizationEngine: CategorizationEngine
) {
    suspend fun syncHistoricMessages(): Int = withContext(Dispatchers.IO) {
        var syncedCount = 0
        try {
            val inboxUri = Uri.parse("content://sms/inbox")
            val cursor = context.contentResolver.query(
                inboxUri,
                arrayOf("_id", "address", "body", "date"),
                "address = ? OR address = ?",
                arrayOf("MPESA", "M-PESA"),
                "date ASC" // Older to newer to maintain correct balance history conceptually
            )

            cursor?.use {
                val bodyIndex = it.getColumnIndex("body")
                val dateIndex = it.getColumnIndex("date")
                
                while (it.moveToNext()) {
                    val body = it.getString(bodyIndex) ?: ""
                    val smsDate = it.getLong(dateIndex)
                    
                    val parsedResult = MpesaParser.parseMessage(body, smsDate)
                    if (parsedResult != null) {
                        try {
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
                                rawSmsBody = parsedResult.rawSms
                            )
                            
                            val categoryId = categorizationEngine.categorize(transaction)
                            if (categoryId != null) {
                                transaction = transaction.copy(categoryId = categoryId)
                            }
                            
                            transactionRepository.insertTransaction(transaction)
                            syncedCount++
                        } catch (e: Exception) {
                            Log.e("SmsSyncService", "Error saving transaction: ${parsedResult.receiptNumber}", e)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SmsSyncService", "Error reading SMS inbox", e)
        }
        return@withContext syncedCount
    }
}
