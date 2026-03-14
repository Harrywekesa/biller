package com.mpesa.tracker.data.local.entities

data class CategoryExpense(
    val categoryId: Int?,
    val categoryName: String?,
    val colorCode: String?,
    val totalAmount: Double,
    val budgetLimit: Double? = null
)
