package com.mpesa.tracker.data.local.entities

/**
 * Economic nature of a ledger entry, decoupled from [TransactionType] channel.
 *
 * - [EXPENSE]: Real outflow counted in spending, category breakdowns, and budgets.
 * - [INCOME]: Real inflow counted in income.
 * - [TRANSFER]: Internal movement between your own accounts (Bank <-> M-Pesa, M-Shwari,
 *   ATM/Agent Cash Withdrawals). Excluded from True Income and True Spending, while
 *   associated [TransactionEntity.transactionCost] fees ARE always counted.
 * - [LOAN_DRAW]: Overdraft/loan principal drawn (e.g. Fuliza). Tracked under debt, not spend.
 * - [LOAN_REPAY]: Repayment of overdraft/loan principal. Tracked under debt, not spend.
 * - [REVERSAL]: Reversal of an earlier transaction, neutralizing spend/income totals.
 */
enum class TransactionNature {
    EXPENSE,
    INCOME,
    TRANSFER,
    LOAN_DRAW,
    LOAN_REPAY,
    REVERSAL;

    companion object {
        fun fromName(name: String?): TransactionNature =
            entries.firstOrNull { it.name == name } ?: EXPENSE
    }
}
