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

    fun getAllTransactions(simId: Int? = null): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions(simId)
    }

    fun getActiveSimIds(): Flow<List<Int>> {
        return transactionDao.getActiveSimIds()
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

    fun getTotalSpentForPeriod(period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null, simId: Int? = null): Flow<Double?> {
        val (start, end) = getTimestampRangeForPeriod(period, customStart, customEnd)
        
        // Types that usually represent spending money
        val spendingTypes = listOf(
            "PAYBILL",
            "BUY_GOODS",
            "SEND_MONEY",
            "WITHDRAW_CASH",
            "BUY_AIRTIME"
        )
        return transactionDao.getTotalSpentBetween(start, end, spendingTypes, simId)
    }

    fun getTotalIncomeForPeriod(period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null, simId: Int? = null): Flow<Double?> {
        val (start, end) = getTimestampRangeForPeriod(period, customStart, customEnd)
        return transactionDao.getTotalIncomeBetween(start, end, simId)
    }

    fun getTotalFeesForPeriod(period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null, simId: Int? = null): Flow<Double?> {
        val (start, end) = getTimestampRangeForPeriod(period, customStart, customEnd)
        return transactionDao.getTotalFeesBetween(start, end, simId)
    }

    fun getSpentForPerson(personName: String, period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null, simId: Int? = null): Flow<Double?> {
        val (start, end) = getTimestampRangeForPeriod(period, customStart, customEnd)
        return transactionDao.getSpentForPersonBetween(personName, start, end, simId)
    }

    fun getIncomeFromPerson(personName: String, period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null, simId: Int? = null): Flow<Double?> {
        val (start, end) = getTimestampRangeForPeriod(period, customStart, customEnd)
        return transactionDao.getIncomeFromPersonBetween(personName, start, end, simId)
    }

    fun getFeesForPerson(personName: String, period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null, simId: Int? = null): Flow<Double?> {
        val (start, end) = getTimestampRangeForPeriod(period, customStart, customEnd)
        return transactionDao.getFeesForPersonBetween(personName, start, end, simId)
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
        
        var endTimestamp = System.currentTimeMillis()
        var startTimestamp = 0L

        when (period) {
            ReportPeriod.THIS_WEEK -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                startTimestamp = calendar.timeInMillis
                
                val endCal = Calendar.getInstance()
                endCal.timeInMillis = startTimestamp
                endCal.add(Calendar.DAY_OF_YEAR, 7)
                endCal.add(Calendar.MILLISECOND, -1)
                endTimestamp = endCal.timeInMillis
            }
            ReportPeriod.THIS_MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                startTimestamp = calendar.timeInMillis
                
                val endCal = Calendar.getInstance()
                endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH))
                endCal.set(Calendar.HOUR_OF_DAY, 23)
                endCal.set(Calendar.MINUTE, 59)
                endCal.set(Calendar.SECOND, 59)
                endCal.set(Calendar.MILLISECOND, 999)
                endTimestamp = endCal.timeInMillis
            }
            ReportPeriod.LAST_MONTH -> {
                calendar.add(Calendar.MONTH, -1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
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
                endTimestamp = Long.MAX_VALUE
            }
            ReportPeriod.CUSTOM -> {
                return Pair(customStart ?: 0L, customEnd ?: endTimestamp)
            }
        }
        
        return Pair(startTimestamp, endTimestamp)
    }

    fun getExpensesByCategory(period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null, simId: Int? = null): Flow<List<com.mpesa.tracker.data.local.entities.CategoryExpense>> {
        val (start, end) = getTimestampRangeForPeriod(period, customStart, customEnd)
        return transactionDao.getExpensesByCategory(start, end, simId)
    }

    fun getDailySpendingTrend(period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null, simId: Int? = null): Flow<List<DailySpend>> {
        val (start, end) = getTimestampRangeForPeriod(period, customStart, customEnd)
        return transactionDao.getDailySpendingTrend(start, end, simId)
    }

    /**
     * Replaced auto-detection with explicit categorization. 
     * Users assign bills to the "Subscriptions" category, and this function reads those merchants.
     */
    fun getAutoDetectedSubscriptions(simId: Int? = null): Flow<List<com.mpesa.tracker.data.local.entities.SubscriptionEntity>> {
        return kotlinx.coroutines.flow.combine(
            categoryDao.getAllCategories(),
            transactionDao.getAllTransactions(simId)
        ) { categories, allTxs ->
            
            val subCategory = categories.firstOrNull { it.name.equals("Subscriptions", ignoreCase = true) }
            val subCategoryId = subCategory?.id ?: -1
            
            if (subCategoryId == -1) {
                return@combine emptyList()
            }
            
            val detectedSubs = mutableListOf<com.mpesa.tracker.data.local.entities.SubscriptionEntity>()
            val subscriptionTxs = allTxs.filter { it.categoryId == subCategoryId && !it.isIncome }
            val merchantGroups = subscriptionTxs.groupBy { it.recipientName }
            
            for ((merchant, txs) in merchantGroups) {
                val sortedTxs = txs.sortedBy { it.dateTimestamp }
                
                // Average the amount
                val avgAmount = txs.map { it.amount }.average()
                
                // Base the billing cycle on the most common gap, or default to 30.
                var avgGapDays = 30
                if (sortedTxs.size >= 2) {
                    var totalGapTime = 0L
                    for (i in 1 until sortedTxs.size) {
                        totalGapTime += (sortedTxs[i].dateTimestamp - sortedTxs[i-1].dateTimestamp)
                    }
                    val avgGapMs = totalGapTime / (sortedTxs.size - 1)
                    avgGapDays = (avgGapMs / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(1)
                }
                
                val lastPaymentDate = sortedTxs.last().dateTimestamp
                val nextExpectedDate = lastPaymentDate + (avgGapDays * 24L * 60 * 60 * 1000)
                
                detectedSubs.add(
                    com.mpesa.tracker.data.local.entities.SubscriptionEntity(
                        id = merchant.hashCode(),
                        name = merchant.take(20),
                        merchantNameMatcher = merchant,
                        amount = avgAmount,
                        isActive = true,
                        billingCycleDays = avgGapDays,
                        categoryId = subCategoryId,
                        expectedNextPaymentDate = nextExpectedDate
                    )
                )
            }
            // Sort by next expected payment date (closest first)
            detectedSubs.sortedBy { it.expectedNextPaymentDate ?: Long.MAX_VALUE }
        }
    }
}
