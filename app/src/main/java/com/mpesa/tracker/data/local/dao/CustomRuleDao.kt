package com.mpesa.tracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mpesa.tracker.data.local.entities.CustomRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomRuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRule(rule: CustomRuleEntity)

    @Query("SELECT categoryId FROM custom_rules WHERE merchantName = :merchantName LIMIT 1")
    suspend fun getCategoryIdForMerchant(merchantName: String): Int?

    @Query("SELECT * FROM custom_rules ORDER BY createdAt DESC")
    fun getAllRules(): Flow<List<CustomRuleEntity>>
    
    @Query("DELETE FROM custom_rules WHERE merchantName = :merchantName")
    suspend fun deleteRule(merchantName: String)
}
