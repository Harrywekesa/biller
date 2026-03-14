package com.mpesa.tracker.domain.export

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.mpesa.tracker.data.local.entities.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CsvExporter(private val context: Context) {

    suspend fun exportTransactionsToCsv(transactions: List<TransactionEntity>): Result<File> = withContext(Dispatchers.IO) {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val filename = "mpesa_export_$timestamp.csv"
            
            // Use external downloads directory
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            
            val file = File(downloadsDir, filename)
            val writer = FileWriter(file)
            
            // CSV Header
            writer.append("Date,Receipt Number,Type,Merchant Name,Amount,Fee,Balance,Income\n")
            
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            
            transactions.forEach { tx ->
                val dateStr = sdf.format(Date(tx.dateTimestamp))
                
                // Escape commas and quotes to maintain valid CSV structure
                val safeMerchant = "\"${tx.recipientName.replace("\"", "\"\"")}\""
                
                writer.append("$dateStr,")
                writer.append("${tx.receiptNumber},")
                writer.append("${tx.type},")
                writer.append("$safeMerchant,")
                writer.append("${tx.amount},")
                writer.append("${tx.transactionCost ?: 0.0},")
                writer.append("${tx.balance},")
                writer.append("${tx.isIncome}\n")
            }
            
            writer.flush()
            writer.close()
            
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
