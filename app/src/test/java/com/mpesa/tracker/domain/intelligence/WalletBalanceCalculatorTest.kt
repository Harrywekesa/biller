package com.mpesa.tracker.domain.intelligence

import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.data.local.entities.TransactionNature
import com.mpesa.tracker.data.local.entities.TransactionType
import com.mpesa.tracker.domain.parser.AccountSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WalletBalanceCalculatorTest {

    private fun mpesaTx(
        receipt: String,
        ts: Long,
        bal: Double?,
        simId: Int? = null,
        name: String = "MERCHANT",
        type: TransactionType = TransactionType.PAYBILL,
        nature: TransactionNature = TransactionNature.EXPENSE,
        amount: Double = 100.0
    ) = TransactionEntity(
        receiptNumber = receipt,
        type = type,
        amount = amount,
        transactionCost = 0.0,
        dateTimestamp = ts,
        recipientName = name,
        balance = bal,
        isIncome = nature == TransactionNature.INCOME,
        rawSmsBody = "sms",
        simSubscriptionId = simId,
        accountSource = AccountSource.MPESA.name,
        transactionNature = nature.name
    )

    @Test
    fun `newer real-time null-SIM SMS updates single-SIM balance instead of freezing`() {
        val olderInbox = mpesaTx("REC1", ts = 1000L, bal = 5000.0, simId = 1)
        val newerRealtimeNullSim = mpesaTx("REC2", ts = 2000L, bal = 3450.0, simId = null)

        val computed = WalletBalanceCalculator.computeMpesaBalance(listOf(olderInbox, newerRealtimeNullSim))
        assertEquals(3450.0, computed, 0.01)
    }

    @Test
    fun `dual-SIM sums latest balances and reflects newer null-SIM SMS on active slot`() {
        val sim1Old = mpesaTx("R1", ts = 1000L, bal = 1000.0, simId = 1)
        val sim2Latest = mpesaTx("R2", ts = 2000L, bal = 2500.0, simId = 2)
        assertEquals(3500.0, WalletBalanceCalculator.computeMpesaBalance(listOf(sim1Old, sim2Latest)), 0.01)

        // Newer real-time SMS arrives without simSubscriptionId on dual-SIM device -> updates latest active slot (sim 2)
        val realtimeUpdate = mpesaTx("R3", ts = 3000L, bal = 1800.0, simId = null)
        assertEquals(2800.0, WalletBalanceCalculator.computeMpesaBalance(listOf(sim1Old, sim2Latest, realtimeUpdate)), 0.01)
    }

    @Test
    fun `Fuliza debt reflects as negative M-Pesa balance`() {
        val fulizaDraw = mpesaTx(
            receipt = "F1",
            ts = 2000L,
            bal = 1200.0,
            simId = 1,
            name = "Fuliza M-PESA",
            type = TransactionType.FULIZA_DRAW,
            nature = TransactionNature.LOAN_DRAW
        )
        assertEquals(-1200.0, WalletBalanceCalculator.computeMpesaBalance(listOf(fulizaDraw)), 0.01)
    }

    @Test
    fun `computeEstimatedCashBalance tracks withdrawals plus manual cash inflows minus cash expenses`() {
        assertNull(WalletBalanceCalculator.computeEstimatedCashBalance(emptyList()))

        val withdrawal = TransactionEntity(
            receiptNumber = "W1",
            type = TransactionType.WITHDRAW_CASH,
            amount = 3000.0,
            transactionCost = 29.0,
            dateTimestamp = 1000L,
            recipientName = "123456 - AGENT",
            balance = 2000.0,
            isIncome = false,
            rawSmsBody = "Withdraw",
            accountSource = AccountSource.MPESA.name,
            transactionNature = TransactionNature.TRANSFER.name
        )
        val cashExpense = TransactionEntity(
            receiptNumber = "MAN-1",
            type = TransactionType.BUY_GOODS,
            amount = 450.0,
            transactionCost = 0.0,
            dateTimestamp = 2000L,
            recipientName = "Mama Mboga",
            balance = null,
            isIncome = false,
            rawSmsBody = "Manual Entry",
            accountSource = AccountSource.CASH.name,
            transactionNature = TransactionNature.EXPENSE.name,
            isManualEntry = true
        )
        val reversedCash = cashExpense.copy(
            receiptNumber = "MAN-2",
            amount = 999.0,
            transactionNature = TransactionNature.REVERSAL.name
        )

        val estimated = WalletBalanceCalculator.computeEstimatedCashBalance(listOf(withdrawal, cashExpense, reversedCash))
        assertEquals(2550.0, estimated!!, 0.01)
    }
}
