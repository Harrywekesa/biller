package com.mpesa.tracker.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_rules")
data class CustomRuleEntity(
    @PrimaryKey
    val merchantName: String,
    val categoryId: Int, // The target Category ID to assign
    val createdAt: Long = System.currentTimeMillis()
)
