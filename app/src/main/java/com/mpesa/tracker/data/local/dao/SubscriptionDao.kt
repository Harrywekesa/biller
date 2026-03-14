package com.mpesa.tracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mpesa.tracker.data.local.entities.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: SubscriptionEntity)

    @Update
    suspend fun updateSubscription(subscription: SubscriptionEntity)

    @Query("SELECT * FROM subscriptions WHERE isActive = 1 ORDER BY expectedNextPaymentDate ASC")
    fun getActiveSubscriptions(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions WHERE merchantNameMatcher = :matcher LIMIT 1")
    suspend fun getSubscriptionByMatcher(matcher: String): SubscriptionEntity?
}
