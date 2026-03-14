package com.mpesa.tracker.data.repository

import com.mpesa.tracker.data.local.dao.SubscriptionDao
import com.mpesa.tracker.data.local.entities.SubscriptionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepository @Inject constructor(
    private val subscriptionDao: SubscriptionDao
) {

    fun getActiveSubscriptions(): Flow<List<SubscriptionEntity>> = 
        subscriptionDao.getActiveSubscriptions()

    suspend fun insertOrUpdateSubscription(subscription: SubscriptionEntity) {
        // If ID is 0, Room treats it as a new insert because of autoGenerate=true
        if (subscription.id == 0) {
            subscriptionDao.insertSubscription(subscription)
        } else {
            subscriptionDao.updateSubscription(subscription)
        }
    }
    
    suspend fun getSubscriptionByMatcher(matcher: String): SubscriptionEntity? =
        subscriptionDao.getSubscriptionByMatcher(matcher)
}
