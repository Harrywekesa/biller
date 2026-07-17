package com.mpesa.tracker.domain.intelligence

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.mpesa.tracker.BuildConfig
import com.mpesa.tracker.data.local.entities.CategoryDaySpend
import com.mpesa.tracker.data.local.entities.CategoryExpense
import com.mpesa.tracker.data.local.entities.DailySpend
import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.data.local.entities.SubscriptionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinancialIntelligenceEngine @Inject constructor() {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun generateFinancialReport(
        expensesByCategory: List<CategoryExpense>,
        dailySpendingTrend: List<DailySpend>,
        spendingByDayAndCategory: List<CategoryDaySpend>,
        periodName: String
    ): String? = withContext(Dispatchers.IO) {
        if (BuildConfig.GEMINI_API_KEY.isBlank() || BuildConfig.GEMINI_API_KEY == "null") {
            return@withContext "API Key not configured. Please add GEMINI_API_KEY to local.properties."
        }

        try {
            val prompt = buildReportPrompt(expensesByCategory, dailySpendingTrend, spendingByDayAndCategory, periodName)
            val response = generativeModel.generateContent(prompt)
            return@withContext response.text
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext "Failed to generate AI report: ${e.localizedMessage}"
        }
    }

    suspend fun generateDailyAdvice(
        dayOfWeek: String, // 0 for Sunday to 6 for Saturday
        todayHistoricalSpending: List<CategoryDaySpend>,
        recentDailyAverage: Double
    ): String? = withContext(Dispatchers.IO) {
        if (BuildConfig.GEMINI_API_KEY.isBlank() || BuildConfig.GEMINI_API_KEY == "null") {
            return@withContext null
        }

        try {
            val prompt = buildDailyAdvicePrompt(dayOfWeek, todayHistoricalSpending, recentDailyAverage)
            val response = generativeModel.generateContent(
                content {
                    text(prompt)
                }
            )
            return@withContext response.text?.trim()
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    private fun buildReportPrompt(
        expensesByCategory: List<CategoryExpense>,
        dailySpendingTrend: List<DailySpend>,
        spendingByDayAndCategory: List<CategoryDaySpend>,
        periodName: String
    ): String {
        val sb = StringBuilder()
        sb.appendLine("You are an expert financial advisor AI embedded in an M-Pesa expense tracking app.")
        sb.appendLine("Please analyze the following expense data for the period: '$periodName' and provide a personalized financial report.")
        sb.appendLine("Use beautiful and readable Markdown (headings, bold, lists). Do not wrap the whole response in a markdown code block.")
        sb.appendLine()
        
        sb.appendLine("### Category Breakdown")
        if (expensesByCategory.isEmpty()) {
            sb.appendLine("No expenses recorded.")
        } else {
            expensesByCategory.forEach {
                sb.appendLine("- ${it.categoryName ?: "Uncategorized"}: KES ${it.totalAmount} (Budget: ${it.budgetLimit ?: "None"})")
            }
        }
        sb.appendLine()

        sb.appendLine("### Day of the Week Insights")
        val dayMap = mapOf("0" to "Sunday", "1" to "Monday", "2" to "Tuesday", "3" to "Wednesday", "4" to "Thursday", "5" to "Friday", "6" to "Saturday")
        spendingByDayAndCategory.take(15).forEach {
            sb.appendLine("- ${dayMap[it.dayOfWeek] ?: "Unknown Day"} -> ${it.categoryName ?: "Uncategorized"}: KES ${it.totalAmount}")
        }
        sb.appendLine()
        
        sb.appendLine("Please output a short summary of 2-3 paragraphs pointing out any dangerous spending behaviors, categories where the budget limit is being breached, and recommend actions.")
        return sb.toString()
    }

    private fun buildDailyAdvicePrompt(
        dayOfWeek: String,
        todayHistoricalSpending: List<CategoryDaySpend>,
        recentDailyAverage: Double
    ): String {
        val dayMap = mapOf("0" to "Sunday", "1" to "Monday", "2" to "Tuesday", "3" to "Wednesday", "4" to "Thursday", "5" to "Friday", "6" to "Saturday")
        val dayName = dayMap[dayOfWeek] ?: "Today"

        val sb = StringBuilder()
        sb.appendLine("You are a proactive financial advisor sending a morning push notification to the user.")
        sb.appendLine("Keep the response to ONE punchy, engaging sentence (max 100 characters). Do not use hashtags.")
        sb.appendLine()
        sb.appendLine("Context:")
        sb.appendLine("- Today is $dayName.")
        sb.appendLine("- The user's overall daily average spend is KES $recentDailyAverage.")
        sb.appendLine("- Historically on $dayName, they spend heavily on the following categories:")
        todayHistoricalSpending.take(3).forEach {
            sb.appendLine("  - ${it.categoryName}: KES ${it.totalAmount}")
        }
        sb.appendLine()
        sb.appendLine("Give me the short text for the morning push notification.")
        return sb.toString()
    }

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
