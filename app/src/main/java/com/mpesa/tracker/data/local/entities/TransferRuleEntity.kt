package com.mpesa.tracker.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persists user overrides / learned rules for whether a merchant/account
 * represents an internal [TransactionNature.TRANSFER] vs regular [EXPENSE]/[INCOME].
 */
@Entity(tableName = "transfer_rules")
data class TransferRuleEntity(
    @PrimaryKey
    val merchantKey: String,
    val nature: String, // TransactionNature.name
    val createdAt: Long = System.currentTimeMillis()
)
