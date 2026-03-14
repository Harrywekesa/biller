package com.mpesa.tracker.domain.intelligence

import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.data.local.entities.SubscriptionEntity
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinancialIntelligenceEngine @Inject constructor() {

    /**
     * Calculates the average daily spend over a given list of transactions.
     * Useful for seeing the "Daily Burn Rate".
     */
    fun calculateDailyBurnRate(transactions: List<TransactionEntity>, daysToConsider: Int = 30): Double {
        if (transactions.isEmpty() || daysToConsider <= 0) return 0.0

        // Filter to only expenses (outgoing money) within the last `daysToConsider` days
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysToConsider)
        val cutoffTimestamp = calendar.timeInMillis

        val recentExpenses = transactions.filter { !it.isIncome && it.dateTimestamp >= cutoffTimestamp }
        val totalSpent = recentExpenses.sumOf { it.amount + it.transactionCost }

        return totalSpent / daysToConsider.toDouble()
    }

    /**
     * Projects the cash flow end date based on the current balance and daily burn rate.
     * Returns the estimated number of days until the balance hits 0.
     */
    fun forecastDaysUntilEmpty(currentBalance: Double, dailyBurnRate: Double): Int? {
        if (dailyBurnRate <= 0) return null // Never runs out at 0 burn rate
        if (currentBalance <= 0) return 0
        
        return (currentBalance / dailyBurnRate).toInt()
    }

    /**
     * Analyzes transactions to detect potential recurring expenses (subscriptions/bills).
     * e.g., if a merchant is paid approximately the same amount more than twice.
     */
    fun detectRecurrentPatterns(transactions: List<TransactionEntity>): List<PotentialSubscription> {
        val outgoing = transactions.filter { !it.isIncome }
        val groupedByMerchant = outgoing.groupBy { it.recipientName }
        
        val patterns = mutableListOf<PotentialSubscription>()

        for ((merchant, txs) in groupedByMerchant) {
            if (txs.size >= 2) {
                // Check if amounts are similar (within 10% tolerance)
                val averageAmount = txs.map { it.amount }.average()
                val isAmountConsistent = txs.all { 
                    Math.abs(it.amount - averageAmount) < (averageAmount * 0.1) 
                }

                if (isAmountConsistent && averageAmount > 50.0) {
                    patterns.add(
                        PotentialSubscription(
                            merchantName = merchant,
                            suggestedAmount = averageAmount,
                            occurrences = txs.size
                        )
                    )
                }
            }
        }
        
        return patterns.sortedByDescending { it.occurrences }
    }
}

data class PotentialSubscription(
    val merchantName: String,
    val suggestedAmount: Double,
    val occurrences: Int
)
