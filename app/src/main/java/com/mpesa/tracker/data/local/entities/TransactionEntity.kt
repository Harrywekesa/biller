package com.mpesa.tracker.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mpesa.tracker.domain.parser.AccountSource

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val receiptNumber: String, // Receipt ID or deterministic fingerprint for refless bank SMS
    val type: TransactionType,
    val amount: Double,
    val transactionCost: Double = 0.0,
    val dateTimestamp: Long, // Unix timestamp in milliseconds
    val recipientName: String,
    val recipientNumber: String? = null,
    val balance: Double? = null,
    val categoryId: Int? = null,
    val isRecurring: Boolean = false,
    val isIncome: Boolean,
    val fulizaAmount: Double? = null,
    val fulizaFee: Double? = null,
    val rawSmsBody: String,
    val simSubscriptionId: Int? = null,
    val linkedReceiptNumber: String? = null,
    val userNote: String? = null,
    val accountSource: String = AccountSource.MPESA.name,
    val transactionNature: String = TransactionNature.EXPENSE.name,
    val transferGroupRef: String? = null,
    val isManualEntry: Boolean = false
) {
    val natureEnum: TransactionNature
        get() = TransactionNature.fromName(transactionNature)

    val sourceEnum: AccountSource
        get() = AccountSource.fromName(accountSource)
}
