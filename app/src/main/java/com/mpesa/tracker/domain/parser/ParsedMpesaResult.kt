package com.mpesa.tracker.domain.parser

import com.mpesa.tracker.data.local.entities.TransactionType

/**
 * Intermediate data class to hold extracted data before mapping it to a Database Entity
 */
data class ParsedMpesaResult(
    val receiptNumber: String,
    val type: TransactionType,
    val amount: Double,
    val transactionCost: Double,
    val timestamp: Long, // Processed Unix timestamp
    val recipientName: String,
    val recipientNumber: String? = null,
    val balance: Double? = null,
    val fulizaAmount: Double? = null,
    val fulizaFee: Double? = null,
    val rawSms: String
)
