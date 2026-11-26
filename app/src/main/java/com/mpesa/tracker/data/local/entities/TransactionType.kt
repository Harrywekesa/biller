package com.mpesa.tracker.data.local.entities

enum class TransactionType {
    PAYBILL,
    BUY_GOODS,
    SEND_MONEY,
    RECEIVED_MONEY,
    WITHDRAW_CASH,
    BUY_AIRTIME,
    FULIZA_DRAW,       // Fuliza M-PESA overdraft draw — this is DEBT, not a spending transaction
    FULIZA_REPAYMENT,  // Auto-deduction from M-Pesa to repay outstanding Fuliza
    UNKNOWN
}
