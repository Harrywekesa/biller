package com.mpesa.tracker.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val receiptNumber: String, // e.g., "OJM12ABCD34"
    val type: TransactionType,
    val amount: Double,
    val transactionCost: Double = 0.0,
    val dateTimestamp: Long, // Unix timestamp in milliseconds
    val recipientName: String, // e.g., "NAIVAS SUPERMARKET"
    val recipientNumber: String? = null, // Paybill/Till number or Phone number
    val balance: Double? = null, // The remaining M-Pesa balance after the transaction
    val categoryId: Int? = null, // Foreign Key relation to CategoryEntity
    val isRecurring: Boolean = false,
    val isIncome: Boolean, // True if RECEIVED_MONEY, False otherwise
    val fulizaAmount: Double? = null, // Extracted Fuliza loan amount if utilized
    val fulizaFee: Double? = null, // Extracted Fuliza dynamic fee
    val rawSmsBody: String // Useful for debugging parsing failures 
)
