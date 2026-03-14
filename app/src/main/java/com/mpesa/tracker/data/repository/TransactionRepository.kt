package com.mpesa.tracker.data.repository

import com.mpesa.tracker.data.local.dao.CategoryDao
import com.mpesa.tracker.data.local.dao.CustomRuleDao
import com.mpesa.tracker.data.local.dao.TransactionDao
import com.mpesa.tracker.data.local.dao.BudgetDao
import com.mpesa.tracker.data.local.entities.CustomRuleEntity
import com.mpesa.tracker.data.local.entities.DailySpend
import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.domain.dictionary.BusinessDictionary
import com.mpesa.tracker.domain.models.ReportPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val customRuleDao: CustomRuleDao,
    private val budgetDao: BudgetDao
) {

    fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }

    fun getAllCategories(): Flow<List<com.mpesa.tracker.data.local.entities.CategoryEntity>> {
        return categoryDao.getAllCategories()
    }

    suspend fun deleteAllTransactions() {
        transactionDao.deleteAllTransactions()
    }
    
    suspend fun addCategory(name: String, colorCode: String, iconName: String) {
        val newCategory = com.mpesa.tracker.data.local.entities.CategoryEntity(
            name = name,
            colorCode = colorCode,
            iconName = iconName
        )
        categoryDao.insertCategory(newCategory)
    }

    suspend fun setCategoryBudget(categoryId: Int, limit: Double) {
        budgetDao.insertOrUpdateBudget(
            com.mpesa.tracker.data.local.entities.BudgetEntity(
                categoryId = categoryId,
                monthlyLimit = limit
            )
        )
    }

    suspend fun clearCategoryBudget(categoryId: Int) {
        budgetDao.deleteBudgetForCategory(categoryId)
    }

    fun getTotalSpentForPeriod(period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null): Flow<Double?> {
        val (start, end) = getTimestampRangeForPeriod(period, customStart, customEnd)
        
        // Types that usually represent spending money
        val spendingTypes = listOf(
            "PAYBILL",
            "BUY_GOODS",
            "SEND_MONEY",
            "WITHDRAW_CASH",
            "BUY_AIRTIME"
        )
        return transactionDao.getTotalSpentBetween(start, end, spendingTypes)
    }

    suspend fun insertTransaction(transaction: TransactionEntity) {
        var finalTx = transaction
        
        // Ensure recipientName is not null for lookup
        val merchantName = finalTx.recipientName

        // Auto-categorize if no category is assigned
        if (finalTx.categoryId == null) {
            
            // 1. Check User's Custom Rules FIRST
            val customCategoryId = customRuleDao.getCategoryIdForMerchant(merchantName)
            
            if (customCategoryId != null) {
                // User has manually mapped this before! Apply logic.
                finalTx = finalTx.copy(categoryId = customCategoryId)
            } else {
                // 2. Fallback to hardcoded heuristics Dictionary
                val categoryName = BusinessDictionary.getCategoryForRecipient(
                    recipientName = merchantName, 
                    recipientNumber = finalTx.recipientNumber
                )
                
                if (categoryName != null) {
                    val category = categoryDao.getCategoryByName(categoryName)
                    if (category != null) {
                        finalTx = finalTx.copy(categoryId = category.id)
                    }
                }
            }
        }
        
        transactionDao.insertTransaction(finalTx)
    }
    
    suspend fun updateTransactionCategoryAndSaveRule(receiptNumber: String, newCategoryId: Int, merchantName: String) {
        // 1. Find existing tx
        val tx = transactionDao.getTransactionByReceipt(receiptNumber) ?: return
        
        // 2. Safely Bulk Update Room DB for all past transactions from this merchant
        transactionDao.updateCategoryForMerchant(merchantName, newCategoryId)
        
        // 3. Save as a Custom Rule for Future sms
        customRuleDao.insertOrUpdateRule(
            CustomRuleEntity(
                merchantName = merchantName,
                categoryId = newCategoryId
            )
        )
    }

    private fun getTimestampRangeForPeriod(period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        
        val endTimestamp = System.currentTimeMillis()
        var startTimestamp = 0L

        when (period) {
            ReportPeriod.THIS_WEEK -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.clear(Calendar.MINUTE)
                calendar.clear(Calendar.SECOND)
                calendar.clear(Calendar.MILLISECOND)
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                startTimestamp = calendar.timeInMillis
            }
            ReportPeriod.THIS_MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.clear(Calendar.MINUTE)
                calendar.clear(Calendar.SECOND)
                calendar.clear(Calendar.MILLISECOND)
                startTimestamp = calendar.timeInMillis
            }
            ReportPeriod.LAST_MONTH -> {
                calendar.add(Calendar.MONTH, -1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.clear(Calendar.MINUTE)
                calendar.clear(Calendar.SECOND)
                calendar.clear(Calendar.MILLISECOND)
                startTimestamp = calendar.timeInMillis
                
                val endCalendar = Calendar.getInstance()
                endCalendar.add(Calendar.MONTH, -1)
                endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                endCalendar.set(Calendar.HOUR_OF_DAY, 23)
                endCalendar.set(Calendar.MINUTE, 59)
                endCalendar.set(Calendar.SECOND, 59)
                endCalendar.set(Calendar.MILLISECOND, 999)
                return Pair(startTimestamp, endCalendar.timeInMillis)
            }
            ReportPeriod.ALL_TIME -> {
                startTimestamp = 0L // Epoch start
            }
            ReportPeriod.CUSTOM -> {
                return Pair(customStart ?: 0L, customEnd ?: endTimestamp)
            }
        }
        
        return Pair(startTimestamp, endTimestamp)
    }

    fun getExpensesByCategory(period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null): Flow<List<com.mpesa.tracker.data.local.entities.CategoryExpense>> {
        val (start, end) = getTimestampRangeForPeriod(period, customStart, customEnd)
        return transactionDao.getExpensesByCategory(start, end)
    }

    fun getDailySpendingTrend(period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null): Flow<List<DailySpend>> {
        val (start, end) = getTimestampRangeForPeriod(period, customStart, customEnd)
        return transactionDao.getDailySpendingTrend(start, end)
    }

    /**
     * Scans the user's historical transactions to automatically detect recurring payments.
     * Criteria: Same merchant, similar amounts, and a roughly 30-day (or 7-day) cadence.
     */
    fun getAutoDetectedSubscriptions(): Flow<List<com.mpesa.tracker.data.local.entities.SubscriptionEntity>> {
        return transactionDao.getAllTransactions().map { allTxs ->
            val detectedSubs = mutableListOf<com.mpesa.tracker.data.local.entities.SubscriptionEntity>()
            
            // Group spending by exact merchant name (ignoring tiny variations if possible, testing exact first)
            val merchantGroups = allTxs.filter { !it.isIncome }.groupBy { it.recipientName }
            
            for ((merchant, txs) in merchantGroups) {
                // Need at least 2 occurrences to establish a pattern
                if (txs.size < 2) continue
                
                // Sort chronologically
                val sortedTxs = txs.sortedBy { it.dateTimestamp }
                
                // Check average time gap between payments
                var totalGapTime = 0L
                for (i in 1 until sortedTxs.size) {
                    totalGapTime += (sortedTxs[i].dateTimestamp - sortedTxs[i-1].dateTimestamp)
                }
                
                val avgGapMs = totalGapTime / (sortedTxs.size - 1)
                val avgGapDays = (avgGapMs / (1000 * 60 * 60 * 24)).toInt()
                
                // Check if the gap is reasonably close to a standard billing cycle
                val isMonthly = avgGapDays in 25..35
                val isWeekly = avgGapDays in 6..8
                
                if (isMonthly || isWeekly) {
                    // Average the amount
                    val avgAmount = txs.map { it.amount }.average()
                    
                    // Estimate next payment date based on the LAST payment + the average gap
                    val lastPaymentDate = sortedTxs.last().dateTimestamp
                    val nextExpectedDate = lastPaymentDate + avgGapMs
                    
                    detectedSubs.add(
                        com.mpesa.tracker.data.local.entities.SubscriptionEntity(
                            id = merchant.hashCode(), // transient ID for UI
                            name = merchant.take(20), // Truncate super long strings
                            merchantNameMatcher = merchant,
                            amount = avgAmount,
                            isActive = true,
                            billingCycleDays = if (isMonthly) 30 else 7,
                            categoryId = sortedTxs.last().categoryId,
                            expectedNextPaymentDate = nextExpectedDate
                        )
                    )
                }
            }
            // Sort by next expected payment date (closest first)
            detectedSubs.sortedBy { it.expectedNextPaymentDate }
        }
    }
}
