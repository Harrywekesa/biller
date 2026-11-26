package com.mpesa.tracker.domain.intelligence

import com.google.ai.client.generativeai.Chat
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
            android.util.Log.e("FinancialEngine", "Failed to generate financial report", e)
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
            android.util.Log.e("FinancialEngine", "Failed to generate daily advice", e)
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

        val recentExpenses = transactions.filter {
            it.natureEnum == com.mpesa.tracker.data.local.entities.TransactionNature.EXPENSE &&
                it.dateTimestamp >= cutoffTimestamp
        }
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
        val outgoing = transactions.filter {
            it.natureEnum == com.mpesa.tracker.data.local.entities.TransactionNature.EXPENSE
        }
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

    /**
     * Builds a system-context-aware prompt for the chat feature.
     * Injects a summary of the user's recent finances so Gemini can answer
     * questions like "what did I spend on food last month?".
     */
    fun buildChatSystemPrompt(
        totalSpentThisMonth: Double,
        totalIncomeThisMonth: Double,
        topCategories: List<com.mpesa.tracker.data.local.entities.CategoryExpense>,
        currentBalance: String
    ): String {
        val sb = StringBuilder()
        sb.appendLine("You are a personal financial assistant embedded inside an M-Pesa expense tracking app for a Kenyan user.")
        sb.appendLine("You have access to the user's M-Pesa transaction history. Answer questions conversationally, in 1-3 sentences unless a detailed breakdown is requested.")
        sb.appendLine("Always use Kenyan Shillings (KES or Ksh) for amounts. Never make up data — only use the context provided.")
        sb.appendLine()
        sb.appendLine("=== User's Current Financial Context ===")
        sb.appendLine("Current M-Pesa Balance: $currentBalance")
        sb.appendLine("Total Spent This Month: KES ${String.format("%.2f", totalSpentThisMonth)}")
        sb.appendLine("Total Received This Month: KES ${String.format("%.2f", totalIncomeThisMonth)}")
        sb.appendLine()
        sb.appendLine("Top Spending Categories This Month:")
        topCategories.take(5).forEach {
            sb.appendLine("  - ${it.categoryName ?: "Uncategorized"}: KES ${String.format("%.2f", it.totalAmount)}")
        }
        sb.appendLine("=== End of Context ===")
        sb.appendLine()
        sb.appendLine("Now answer the user's question based on the above context.")
        return sb.toString()
    }

    /**
     * Starts a new Gemini multi-turn chat session with financial context injected.
     */
    fun startChatSession(systemPrompt: String): Chat {
        return generativeModel.startChat(
            history = listOf(
                content(role = "user") { text(systemPrompt) },
                content(role = "model") { text("Understood! I'm your M-Pesa financial assistant. I can see your spending data for this month. What would you like to know?") }
            )
        )
    }

    /**
     * Sends a message in an existing chat session and returns the model's reply.
     */
    suspend fun sendChatMessage(chat: Chat, userMessage: String): String? =
        withContext(Dispatchers.IO) {
            try {
                val response = chat.sendMessage(userMessage)
                response.text?.trim()
            } catch (e: Exception) {
                android.util.Log.e("FinancialEngine", "Chat message failed", e)
                null
            }
        }

    /**
     * Detects spending categories where this month's spending is significantly
     * higher than the 3-month average. Returns a list of anomalies sorted by
     * severity (highest multiplier first).
     */
    suspend fun detectSpendingAnomalies(
        thisMonthByCategory: List<com.mpesa.tracker.data.local.entities.CategoryExpense>,
        lastMonthByCategory: List<com.mpesa.tracker.data.local.entities.CategoryExpense>,
        twoMonthsAgoByCategory: List<com.mpesa.tracker.data.local.entities.CategoryExpense>
    ): List<SpendingAnomaly> = withContext(Dispatchers.Default) {
        val anomalies = mutableListOf<SpendingAnomaly>()

        for (thisMonth in thisMonthByCategory) {
            val name = thisMonth.categoryName ?: continue
            val thisAmount = thisMonth.totalAmount
            if (thisAmount < 100.0) continue // Ignore trivial amounts

            val lastAmount = lastMonthByCategory.firstOrNull { it.categoryName == name }?.totalAmount ?: 0.0
            val twoAgoAmount = twoMonthsAgoByCategory.firstOrNull { it.categoryName == name }?.totalAmount ?: 0.0

            // Need at least one prior month of data to compare
            val priorTotal = lastAmount + twoAgoAmount
            if (priorTotal < 100.0) continue

            val historicalMonths = if (lastAmount > 0 && twoAgoAmount > 0) 2.0 else 1.0
            val average = priorTotal / historicalMonths

            if (average > 0) {
                val multiplier = thisAmount / average
                if (multiplier >= 1.5) { // 50% above average threshold
                    anomalies.add(SpendingAnomaly(name, thisAmount, average, multiplier))
                }
            }
        }

        anomalies.sortedByDescending { it.multiplier }
    }

    /**
     * Calculates a 0-100 monthly financial health score based on:
     *   - Budget adherence (40 pts): are you staying within category budgets?
     *   - Fuliza reliance (30 pts): how much Fuliza did you use relative to income?
     *   - Savings rate (30 pts): income vs spending ratio
     */
    fun calculateFinancialScore(
        totalIncome: Double,
        totalSpent: Double,
        totalFulizaDrawn: Double,
        categoryExpenses: List<com.mpesa.tracker.data.local.entities.CategoryExpense>
    ): FinancialScore {
        var score = 100
        val tips = mutableListOf<String>()

        // ── Savings rate (30 pts) ─────────────────────────────────────────
        val savingsRate = if (totalIncome > 0) (totalIncome - totalSpent) / totalIncome else 0.0
        val savingsScore = when {
            savingsRate >= 0.20 -> 30 // Saving 20%+ is excellent
            savingsRate >= 0.10 -> 22
            savingsRate >= 0.05 -> 15
            savingsRate >= 0.0  -> 8
            else -> 0 // Spending more than earning
        }
        score = score - 30 + savingsScore
        if (savingsRate < 0.10) {
            tips.add("Try saving at least 10% of your income (Ksh ${String.format("%.0f", totalIncome * 0.10)})")
        }

        // ── Fuliza reliance (30 pts) ──────────────────────────────────────
        val fulizaRate = if (totalIncome > 0) totalFulizaDrawn / totalIncome else 1.0
        val fulizaScore = when {
            fulizaRate == 0.0  -> 30 // No Fuliza at all
            fulizaRate < 0.05  -> 25
            fulizaRate < 0.15  -> 15
            fulizaRate < 0.30  -> 5
            else -> 0
        }
        score = score - 30 + fulizaScore
        if (totalFulizaDrawn > 0) {
            tips.add("You used Ksh ${String.format("%.0f", totalFulizaDrawn)} Fuliza this month. Reducing reliance improves your score.")
        }

        // ── Budget adherence (40 pts) ─────────────────────────────────────
        val overBudgetCategories = categoryExpenses.filter {
            it.budgetLimit != null && it.totalAmount > it.budgetLimit
        }
        val budgetScore = when {
            overBudgetCategories.isEmpty() -> 40
            overBudgetCategories.size == 1 -> 28
            overBudgetCategories.size == 2 -> 16
            else -> 0
        }
        score = score - 40 + budgetScore
        if (overBudgetCategories.isNotEmpty()) {
            val worst = overBudgetCategories.maxByOrNull { it.totalAmount - (it.budgetLimit ?: 0.0) }
            if (worst != null) {
                tips.add("${worst.categoryName} is over budget by Ksh ${String.format("%.0f", worst.totalAmount - (worst.budgetLimit ?: 0.0))}")
            }
        }

        val finalScore = score.coerceIn(0, 100)
        val grade = when {
            finalScore >= 85 -> "A"
            finalScore >= 70 -> "B"
            finalScore >= 55 -> "C"
            finalScore >= 40 -> "D"
            else -> "F"
        }
        val summary = when {
            finalScore >= 85 -> "Excellent financial health this month! 🎉"
            finalScore >= 70 -> "Good financial management. A few areas to watch."
            finalScore >= 55 -> "Fair. Focus on your budget and reduce Fuliza usage."
            finalScore >= 40 -> "Needs improvement. Review your spending habits."
            else -> "Critical: You're spending more than you earn. Act now."
        }

        return FinancialScore(finalScore, grade, summary, tips.take(3))
    }
}

data class PotentialSubscription(
    val merchantName: String,
    val suggestedAmount: Double,
    val occurrences: Int
)

data class SpendingAnomaly(
    val categoryName: String,
    val currentAmount: Double,
    val averageAmount: Double,
    val multiplier: Double // e.g. 2.3 means "2.3x above average"
)

data class FinancialScore(
    val score: Int,          // 0-100
    val grade: String,       // "A", "B", "C", "D", "F"
    val summary: String,     // One-sentence summary
    val tips: List<String>   // Up to 3 actionable tips
)
