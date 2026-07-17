package com.mpesa.tracker.framework.services

import android.content.Context
import android.os.Environment
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private companion object {
        const val KEY_ALIAS = "mpesa_tracker_backup_key"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val ANDROID_KEY_STORE = "AndroidKeyStore"
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }
        if (keyStore.containsAlias(KEY_ALIAS)) {
            val entry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
            if (entry != null) {
                return entry.secretKey
            }
        }

        // Key doesn't exist, create it securely in Android KeyStore
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEY_STORE
        )
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
            
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    /**
     * Creates a local, encrypted backup of the Room Database.
     * Uses AES-GCM encryption with keys stored in the Android KeyStore.
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
            val backupFile = File(backupDir, "backup_$timestamp.enc") // Using .enc to mark it as encrypted

            val key = getOrCreateKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, key)

            FileOutputStream(backupFile).use { fos ->
                val iv = cipher.iv
                // Write IV length and IV value first
                fos.write(iv.size)
                fos.write(iv)
                
                CipherOutputStream(fos, cipher).use { cos ->
                    FileInputStream(dbFile).use { fis ->
                        fis.copyTo(cos)
                    }
                }
            }

            Log.d("BackupManager", "Encrypted backup successful: ${backupFile.absolutePath}")
            return true
            
        } catch (e: Exception) {
            Log.e("BackupManager", "Backup failed", e)
            return false
        }
    }

    /**
     * Decrypts and restores a database backup, replacing the current active database.
     */
    fun restoreBackup(backupFile: File): Boolean {
        try {
            val dbName = "mpesa_tracker_db"
            val dbFile = context.getDatabasePath(dbName)
            
            if (!backupFile.exists()) {
                Log.e("BackupManager", "Backup file does not exist.")
                return false
            }

            val key = getOrCreateKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            
            FileInputStream(backupFile).use { fis ->
                val ivSize = fis.read()
                if (ivSize <= 0 || ivSize > 100) {
                    Log.e("BackupManager", "Invalid IV size in backup file.")
                    return false
                }
                
                val iv = ByteArray(ivSize)
                fis.read(iv)
                
                val spec = GCMParameterSpec(128, iv)
                cipher.init(Cipher.DECRYPT_MODE, key, spec)
                
                val tempDbFile = File(context.cacheDir, "temp_restore.db")
                
                CipherInputStream(fis, cipher).use { cis ->
                    FileOutputStream(tempDbFile).use { fos ->
                        cis.copyTo(fos)
                    }
                }
                
                // Replace original database file safely
                if (dbFile.exists()) {
                    dbFile.delete()
                }
                tempDbFile.renameTo(dbFile)
                
                // Clean up journal/shm/wal files to prevent corruption with the new db file
                File(dbFile.absolutePath + "-journal").delete()
                File(dbFile.absolutePath + "-shm").delete()
                File(dbFile.absolutePath + "-wal").delete()
            }
            
            Log.d("BackupManager", "Database successfully restored from encrypted backup.")
            return true
        } catch (e: Exception) {
            Log.e("BackupManager", "Restore failed", e)
            return false
        }
    }
}
