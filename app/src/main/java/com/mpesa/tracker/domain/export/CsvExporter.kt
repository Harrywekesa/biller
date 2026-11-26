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
            val filename = "biller_export_$timestamp.csv"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw Exception("Failed to create MediaStore entry in Downloads")

                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    writeCsvPayload(outputStream.writer(), transactions)
                } ?: throw Exception("Failed to open output stream")
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val file = File(downloadsDir, filename)
                FileWriter(file).use { writer -> writeCsvPayload(writer, transactions) }
            }

            Result.success("Exported $filename to Downloads folder.")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun writeCsvPayload(writer: java.io.Writer, transactions: List<TransactionEntity>) {
        writer.append("Date,Receipt Number,Account,Nature,Channel,Merchant Name,Amount,Fee,Balance,Note\n")
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        transactions.forEach { tx ->
            val dateStr = sdf.format(Date(tx.dateTimestamp))
            val safeMerchant = "\"${tx.recipientName.replace("\"", "\"\"")}\""
            val safeNote = "\"${(tx.userNote ?: "").replace("\"", "\"\"")}\""
            val balStr = tx.balance?.toString() ?: ""

            writer.append("$dateStr,")
            writer.append("${tx.receiptNumber},")
            writer.append("${tx.accountSource},")
            writer.append("${tx.transactionNature},")
            writer.append("${tx.type},")
            writer.append("$safeMerchant,")
            writer.append("${tx.amount},")
            writer.append("${tx.transactionCost},")
            writer.append("$balStr,")
            writer.append("$safeNote\n")
        }
        writer.flush()
    }
}
