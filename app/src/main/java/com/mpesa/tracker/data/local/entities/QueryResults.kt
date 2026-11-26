package com.mpesa.tracker.data.local.entities

import androidx.room.ColumnInfo

/** Top merchant by total spend */
data class MerchantSummary(
    @ColumnInfo(name = "recipientName") val recipientName: String,
    @ColumnInfo(name = "totalSpent") val totalSpent: Double,
    @ColumnInfo(name = "txCount") val txCount: Int
)

/** Monthly aggregation result */
data class MonthlyTotal(
    @ColumnInfo(name = "monthString") val monthString: String, // "2026-10"
    @ColumnInfo(name = "totalAmount") val totalAmount: Double
)
