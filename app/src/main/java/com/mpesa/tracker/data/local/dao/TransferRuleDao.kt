package com.mpesa.tracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mpesa.tracker.data.local.entities.TransferRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransferRuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRule(rule: TransferRuleEntity)

    @Query("SELECT nature FROM transfer_rules WHERE merchantKey = :merchantKey LIMIT 1")
    suspend fun getNatureForMerchant(merchantKey: String): String?

    @Query("SELECT * FROM transfer_rules ORDER BY createdAt DESC")
    fun getAllRules(): Flow<List<TransferRuleEntity>>

    @Query("DELETE FROM transfer_rules WHERE merchantKey = :merchantKey")
    suspend fun deleteRule(merchantKey: String)
}
