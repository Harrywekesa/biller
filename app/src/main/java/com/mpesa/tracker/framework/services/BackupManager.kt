package com.mpesa.tracker.framework.services

import android.content.Context
import android.os.Environment
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Creates a local, file-based backup of the Room Database.
     * In a production app, this stream would be wrapped in a CipherOutputStream.
     */
    fun createLocalBackup(): Boolean {
        try {
            val dbName = "mpesa_tracker_db"
            val dbFile = context.getDatabasePath(dbName)
            
            if (!dbFile.exists()) {
                Log.e("BackupManager", "Database does not exist.")
                return false
            }

            // Create a Backup Directory in external storage (or Documents)
            val backupDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "MpesaTrackerBackups")
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val backupFile = File(backupDir, "backup_$timestamp.db")

            FileInputStream(dbFile).use { input ->
                FileOutputStream(backupFile).use { output ->
                    input.copyTo(output)
                }
            }

            Log.d("BackupManager", "Backup successful: ${backupFile.absolutePath}")
            return true
            
        } catch (e: Exception) {
            Log.e("BackupManager", "Backup failed", e)
            return false
        }
    }
}
