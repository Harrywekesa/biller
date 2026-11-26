package com.mpesa.tracker.domain.intelligence

import com.mpesa.tracker.data.local.entities.CategoryExpense
import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.data.local.entities.TransactionNature
import com.mpesa.tracker.data.local.entities.TransactionType
import com.mpesa.tracker.domain.parser.AccountSource
import org.junit.Assert.*
import org.junit.Test

class FinancialIntelligenceEngineTest {

    private val engine = FinancialIntelligenceEngine()

    @Test
    fun `detectRecurrentPatterns excludes internal transfers and Fuliza loans`() {
        val now = System.currentTimeMillis()
        val txs = listOf(
            // Legitimate recurring bill (EXPENSE)
            tx("EXP1", "KPLC PREPAID", 1500.0, TransactionType.PAYBILL, TransactionNature.EXPENSE, false, now - 86_400_000L),
            tx("EXP2", "KPLC PREPAID", 1500.0, TransactionType.PAYBILL, TransactionNature.EXPENSE, false, now - 172_800_000L),
            // Repeated internal transfers from I&M -> M-Pesa (MUST be excluded!)
            tx("TRF1", "HARRISON WEKESA", 1000.0, TransactionType.SEND_MONEY, TransactionNature.TRANSFER, false, now - 86_400_000L, AccountSource.IM_BANK),
            tx("TRF2", "HARRISON WEKESA", 1000.0, TransactionType.SEND_MONEY, TransactionNature.TRANSFER, false, now - 172_800_000L, AccountSource.IM_BANK),
            // Repeated Fuliza repayments (MUST be excluded!)
            tx("FLZ1", "Fuliza Repayment", 500.0, TransactionType.FULIZA_REPAYMENT, TransactionNature.LOAN_REPAY, false, now - 86_400_000L),
            tx("FLZ2", "Fuliza Repayment", 500.0, TransactionType.FULIZA_REPAYMENT, TransactionNature.LOAN_REPAY, false, now - 172_800_000L)
        )

        val patterns = engine.detectRecurrentPatterns(txs)
        assertEquals(1, patterns.size)
        assertEquals("KPLC PREPAID", patterns.first().merchantName)
    }

    @Test
    fun `calculateDailyBurnRate counts only EXPENSE nature and excludes TRANSFER`() {
        val now = System.currentTimeMillis()
        val txs = listOf(
            tx("EXP1", "NAIVAS", 300.0, TransactionType.BUY_GOODS, TransactionNature.EXPENSE, false, now - 1_000L, fee = 0.0),
            // Bank-to-M-Pesa transfer: principal 10,000 must NOT inflate daily burn rate
            tx("TRF1", "SELF TRANSFER", 10_000.0, TransactionType.SEND_MONEY, TransactionNature.TRANSFER, false, now - 2_000L, AccountSource.ABSA)
        )

        val burnRate = engine.calculateDailyBurnRate(txs, daysToConsider = 30)
        assertEquals(10.0, burnRate, 0.01) // 300 / 30 = 10.0/day
    }

    @Test
    fun `calculateFinancialScore penalizes overspending and Fuliza dependency`() {
        val healthy = engine.calculateFinancialScore(
            totalIncome = 100_000.0,
            totalSpent = 60_000.0,
            totalFulizaDrawn = 0.0,
            categoryExpenses = listOf(CategoryExpense(1, "Food", "#4CAF50", 15_000.0, 20_000.0))
        )
        assertTrue(healthy.score >= 85)
        assertEquals("A", healthy.grade)

        val stressed = engine.calculateFinancialScore(
            totalIncome = 50_000.0,
            totalSpent = 55_000.0,
            totalFulizaDrawn = 20_000.0,
            categoryExpenses = listOf(
                CategoryExpense(1, "Food", "#4CAF50", 25_000.0, 15_000.0),
                CategoryExpense(2, "Transport", "#2196F3", 12_000.0, 10_000.0)
            )
        )
        assertTrue(stressed.score < 50)
    }

    private fun tx(
        receipt: String,
        recipient: String,
        amount: Double,
        type: TransactionType,
        nature: TransactionNature,
        isIncome: Boolean,
        ts: Long,
        source: AccountSource = AccountSource.MPESA,
        fee: Double = 0.0
    ) = TransactionEntity(
        receiptNumber = receipt,
        type = type,
        amount = amount,
        transactionCost = fee,
        dateTimestamp = ts,
        recipientName = recipient,
        isIncome = isIncome,
        rawSmsBody = "test",
        accountSource = source.name,
        transactionNature = nature.name
    )
}
