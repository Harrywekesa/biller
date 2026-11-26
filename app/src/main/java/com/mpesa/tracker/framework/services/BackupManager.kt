package com.mpesa.tracker.framework.services

import android.content.Context
import android.os.Environment
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import com.mpesa.tracker.data.local.AppDatabase
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
    @ApplicationContext private val context: Context,
    private val database: AppDatabase
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
            if (entry != null) return entry.secretKey
        }

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
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
     * Checkpoints SQLite WAL to main DB file before creating an AES-GCM encrypted backup
     * so zero uncommitted transactions are missed. Returns created File path or null.
     */
    fun createLocalBackup(): File? {
        return try {
            val dbName = "mpesa_tracker_db"
            val dbFile = context.getDatabasePath(dbName)
            if (!dbFile.exists()) {
                Log.e("BackupManager", "Database does not exist.")
                return null
            }

            // Flush WAL changes safely into mpesa_tracker_db before reading bytes
            database.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(TRUNCATE)").use { it.moveToFirst() }

            val backupDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "BillerBackups")
            if (!backupDir.exists()) backupDir.mkdirs()

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val backupFile = File(backupDir, "biller_backup_$timestamp.enc")

            val key = getOrCreateKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, key)

            FileOutputStream(backupFile).use { fos ->
                val iv = cipher.iv
                fos.write(iv.size)
                fos.write(iv)
                CipherOutputStream(fos, cipher).use { cos ->
                    FileInputStream(dbFile).use { fis -> fis.copyTo(cos) }
                }
            }

            Log.d("BackupManager", "Encrypted backup created: ${backupFile.absolutePath}")
            backupFile
        } catch (e: Exception) {
            Log.e("BackupManager", "Backup failed", e)
            null
        }
    }

    fun listBackups(): List<File> {
        val backupDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "BillerBackups")
        if (!backupDir.exists()) return emptyList()
        return backupDir.listFiles { file -> file.extension == "enc" }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
    }

    fun restoreBackup(backupFile: File): Boolean {
        return try {
            val dbName = "mpesa_tracker_db"
            val dbFile = context.getDatabasePath(dbName)
            if (!backupFile.exists()) return false

            val key = getOrCreateKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)

            FileInputStream(backupFile).use { fis ->
                val ivSize = fis.read()
                if (ivSize <= 0 || ivSize > 100) return false
                val iv = ByteArray(ivSize)
                java.io.DataInputStream(fis).readFully(iv)

                val spec = GCMParameterSpec(128, iv)
                cipher.init(Cipher.DECRYPT_MODE, key, spec)

                val tempDbFile = File(context.cacheDir, "temp_restore.db")
                CipherInputStream(fis, cipher).use { cis ->
                    FileOutputStream(tempDbFile).use { fos -> cis.copyTo(fos) }
                }
                if (!tempDbFile.exists() || tempDbFile.length() == 0L) {
                    tempDbFile.delete()
                    return false
                }

                database.close()
                tempDbFile.copyTo(dbFile, overwrite = true)
                tempDbFile.delete()
                File(dbFile.absolutePath + "-journal").delete()
                File(dbFile.absolutePath + "-shm").delete()
                File(dbFile.absolutePath + "-wal").delete()
            }
            true
        } catch (e: Exception) {
            Log.e("BackupManager", "Restore failed", e)
            false
        }
    }
}
