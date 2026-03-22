package com.mpesa.tracker.domain.export

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.mpesa.tracker.data.local.entities.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CsvExporter(private val context: Context) {

    suspend fun exportTransactionsToCsv(transactions: List<TransactionEntity>): Result<String> = withContext(Dispatchers.IO) {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val filename = "mpesa_export_$timestamp.csv"
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw Exception("Failed to create MediaStore entry in Downloads")
                    
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    val writer = outputStream.writer()
                    writeCsvPayload(writer, transactions)
                } ?: throw Exception("Failed to open output stream")
            } else {
                // Fallback for API 28 and below
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }
                
                val file = File(downloadsDir, filename)
                val writer = FileWriter(file)
                writeCsvPayload(writer, transactions)
            }
            
            Result.success("Successfully exported to Downloads folder.")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun writeCsvPayload(writer: java.io.Writer, transactions: List<TransactionEntity>) {
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
    }
}
