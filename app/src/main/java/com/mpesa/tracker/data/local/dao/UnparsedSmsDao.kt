package com.mpesa.tracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mpesa.tracker.data.local.entities.UnparsedSmsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UnparsedSmsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entry: UnparsedSmsEntity)

    @Query("SELECT * FROM unparsed_sms ORDER BY receivedAt DESC LIMIT 100")
    fun getAll(): Flow<List<UnparsedSmsEntity>>

    @Query("DELETE FROM unparsed_sms WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM unparsed_sms")
    suspend fun clearAll()
}
