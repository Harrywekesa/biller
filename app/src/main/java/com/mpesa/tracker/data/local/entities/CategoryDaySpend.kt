package com.mpesa.tracker.data.local.entities

data class CategoryDaySpend(
    val categoryName: String?,
    val dayOfWeek: String, // "0" to "6" (Sunday to Saturday)
    val totalAmount: Double
)
