package com.mpesa.tracker.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String, // e.g. "Netflix", "Zuku Internet"
    val merchantNameMatcher: String, // The exact or partial string to match in merchant name (e.g., "ZUKU")
    val amount: Double,
    val isActive: Boolean = true,
    val billingCycleDays: Int = 30, // Default to monthly
    val categoryId: Int? = null,
    val expectedNextPaymentDate: Long? = null
)
