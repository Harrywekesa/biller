package com.mpesa.tracker.domain.parser

import com.mpesa.tracker.data.local.entities.TransactionNature

/**
 * Intermediate data class holding extracted bank SMS data before persistence and
 * Bank <-> M-Pesa transfer reconciliation.
 */
data class ParsedBankResult(
    val referenceNumber: String,
    val isIncome: Boolean,
    val amount: Double,
    val recipientName: String,
    val recipientNumber: String? = null,
    val balance: Double? = null,
    val transactionCost: Double = 0.0,
    val rawSms: String,
    val timestamp: Long,
    val source: AccountSource = AccountSource.UNKNOWN_BANK,
    val natureHint: TransactionNature? = null,
    val linkedMpesaRef: String? = null,
    val isTransientNotice: Boolean = false,
    val selfHintName: String? = null,
    val selfHintPhone: String? = null,
    val isTransferCandidate: Boolean = false
)
