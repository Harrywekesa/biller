package com.mpesa.tracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mpesa.tracker.data.local.entities.CategoryDaySpend
import com.mpesa.tracker.data.local.entities.CategoryExpense
import com.mpesa.tracker.data.local.entities.DailySpend
import com.mpesa.tracker.data.local.entities.MerchantSummary
import com.mpesa.tracker.data.local.entities.MonthlyTotal
import com.mpesa.tracker.data.local.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    /**
     * Scoped bulk category update: updates only matching transactionNature (so categorizing
     * money sent to a person never overwrites money received from that person).
     */
    @Query("UPDATE transactions SET categoryId = :categoryId WHERE recipientName = :merchantName AND transactionNature = :nature")
    suspend fun updateCategoryForMerchant(merchantName: String, categoryId: Int, nature: String = "EXPENSE")

    @Query("UPDATE transactions SET categoryId = :categoryId WHERE recipientNumber = :code AND transactionNature = :nature")
    suspend fun updateCategoryForCode(code: String, categoryId: Int, nature: String = "EXPENSE")

    @Query("SELECT * FROM transactions WHERE transactionNature = 'REVERSAL' AND linkedReceiptNumber = :originalReceipt LIMIT 1")
    suspend fun findReversalPointingTo(originalReceipt: String): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE transactionNature = 'REVERSAL' AND linkedReceiptNumber IS NOT NULL")
    suspend fun getAllReversals(): List<TransactionEntity>

    @Query("UPDATE transactions SET categoryId = :categoryId WHERE receiptNumber = :receiptNumber")
    suspend fun updateSingleTransactionCategory(receiptNumber: String, categoryId: Int)

    @Query("UPDATE transactions SET transactionNature = :nature, transferGroupRef = :transferGroupRef WHERE receiptNumber = :receiptNumber")
    suspend fun updateTransactionNature(receiptNumber: String, nature: String, transferGroupRef: String? = null)

    @Query("UPDATE transactions SET transactionNature = :nature, categoryId = NULL WHERE recipientName = :merchantName AND isManualEntry = 0")
    suspend fun updateNatureForMerchant(merchantName: String, nature: String)

    @Query("UPDATE transactions SET userNote = :note WHERE receiptNumber = :receiptNumber")
    suspend fun updateUserNote(receiptNumber: String, note: String?)

    @Query("DELETE FROM transactions WHERE receiptNumber = :receiptNumber")
    suspend fun deleteTransaction(receiptNumber: String)

    @Query("DELETE FROM transactions WHERE receiptNumber IN (:receiptNumbers)")
    suspend fun deleteTransactions(receiptNumbers: List<String>)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    @Query("SELECT DISTINCT simSubscriptionId FROM transactions WHERE simSubscriptionId IS NOT NULL ORDER BY simSubscriptionId ASC")
    fun getActiveSimIds(): Flow<List<Int>>

    @Query("SELECT DISTINCT accountSource FROM transactions ORDER BY accountSource ASC")
    fun getActiveAccountSources(): Flow<List<String>>

    @Query("SELECT * FROM transactions WHERE (:simId IS NULL OR simSubscriptionId = :simId) ORDER BY dateTimestamp DESC")
    fun getAllTransactions(simId: Int? = null): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY dateTimestamp DESC")
    suspend fun getAllTransactionsOnce(): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE receiptNumber = :receiptNumber LIMIT 1")
    suspend fun getTransactionByReceipt(receiptNumber: String): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE transferGroupRef = :groupRef")
    suspend fun getTransactionsByTransferRef(groupRef: String): List<TransactionEntity>

    @Query(
        """
        SELECT * FROM transactions
        WHERE accountSource = :source
        AND ABS(amount - :amount) < 0.01
        AND transactionNature = :nature
        AND ABS(dateTimestamp - :timestampMs) <= :windowMs
        ORDER BY ABS(dateTimestamp - :timestampMs) ASC
        LIMIT 1
        """
    )
    suspend fun findNearbyMatchingTransaction(
        source: String,
        amount: Double,
        nature: String,
        timestampMs: Long,
        windowMs: Long = 300_000L
    ): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE dateTimestamp BETWEEN :startDate AND :endDate AND (:simId IS NULL OR simSubscriptionId = :simId) ORDER BY dateTimestamp DESC")
    fun getTransactionsBetween(startDate: Long, endDate: Long, simId: Int? = null): Flow<List<TransactionEntity>>

    /** True spending only: strictly transactionNature = 'EXPENSE' (excludes transfers, Fuliza, reversals). */
    @Query("SELECT SUM(amount) FROM transactions WHERE transactionNature = 'EXPENSE' AND dateTimestamp BETWEEN :startDate AND :endDate AND (:simId IS NULL OR simSubscriptionId = :simId)")
    fun getTotalSpentBetween(startDate: Long, endDate: Long, simId: Int? = null): Flow<Double?>

    /** True income only: strictly transactionNature = 'INCOME' (excludes internal bank->M-Pesa transfers). */
    @Query("SELECT SUM(amount) FROM transactions WHERE transactionNature = 'INCOME' AND dateTimestamp BETWEEN :startDate AND :endDate AND (:simId IS NULL OR simSubscriptionId = :simId)")
    fun getTotalIncomeBetween(startDate: Long, endDate: Long, simId: Int? = null): Flow<Double?>

    /** Internal transfers volume between accounts (shown transparently, excluded from Income & Spend). */
    @Query("SELECT SUM(amount) FROM transactions WHERE transactionNature = 'TRANSFER' AND dateTimestamp BETWEEN :startDate AND :endDate AND (:simId IS NULL OR simSubscriptionId = :simId)")
    fun getTotalTransfersBetween(startDate: Long, endDate: Long, simId: Int? = null): Flow<Double?>

    /** Transaction fees ALWAYS count across ALL transaction natures including internal transfers. */
    @Query("SELECT SUM(transactionCost) FROM transactions WHERE transactionNature != 'REVERSAL' AND dateTimestamp BETWEEN :startDate AND :endDate AND (:simId IS NULL OR simSubscriptionId = :simId)")
    fun getTotalFeesBetween(startDate: Long, endDate: Long, simId: Int? = null): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE transactionNature = 'EXPENSE' AND (recipientName LIKE '%' || :personName || '%' OR recipientNumber LIKE '%' || :personName || '%') AND dateTimestamp BETWEEN :startDate AND :endDate AND (:simId IS NULL OR simSubscriptionId = :simId)")
    fun getSpentForPersonBetween(personName: String, startDate: Long, endDate: Long, simId: Int? = null): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE transactionNature = 'INCOME' AND (recipientName LIKE '%' || :personName || '%' OR recipientNumber LIKE '%' || :personName || '%') AND dateTimestamp BETWEEN :startDate AND :endDate AND (:simId IS NULL OR simSubscriptionId = :simId)")
    fun getIncomeFromPersonBetween(personName: String, startDate: Long, endDate: Long, simId: Int? = null): Flow<Double?>

    @Query("SELECT SUM(transactionCost) FROM transactions WHERE transactionNature != 'REVERSAL' AND (recipientName LIKE '%' || :personName || '%' OR recipientNumber LIKE '%' || :personName || '%') AND dateTimestamp BETWEEN :startDate AND :endDate AND (:simId IS NULL OR simSubscriptionId = :simId)")
    fun getFeesForPersonBetween(personName: String, startDate: Long, endDate: Long, simId: Int? = null): Flow<Double?>

    @Query(
        """
        SELECT 
            c.id as categoryId,
            c.name as categoryName, 
            c.colorCode as colorCode, 
            SUM(t.amount) as totalAmount,
            b.monthlyLimit as budgetLimit
        FROM transactions t
        LEFT JOIN categories c ON t.categoryId = c.id
        LEFT JOIN budgets b ON c.id = b.categoryId
        WHERE t.dateTimestamp BETWEEN :startDate AND :endDate
        AND t.transactionNature = 'EXPENSE'
        AND (:simId IS NULL OR t.simSubscriptionId = :simId)
        GROUP BY c.id
        ORDER BY totalAmount DESC
        """
    )
    fun getExpensesByCategory(startDate: Long, endDate: Long, simId: Int? = null): Flow<List<CategoryExpense>>

    @Query(
        """
        SELECT 
            strftime('%Y-%m-%d', dateTimestamp / 1000, 'unixepoch', 'localtime') as dateString,
            SUM(amount) as totalAmount
        FROM transactions
        WHERE dateTimestamp BETWEEN :startDate AND :endDate
        AND transactionNature = 'EXPENSE'
        AND (:simId IS NULL OR simSubscriptionId = :simId)
        GROUP BY dateString
        ORDER BY dateString ASC
        """
    )
    fun getDailySpendingTrend(startDate: Long, endDate: Long, simId: Int? = null): Flow<List<DailySpend>>

    @Query(
        """
        SELECT 
            c.name as categoryName, 
            strftime('%w', t.dateTimestamp / 1000, 'unixepoch', 'localtime') as dayOfWeek,
            SUM(t.amount) as totalAmount
        FROM transactions t
        LEFT JOIN categories c ON t.categoryId = c.id
        WHERE t.dateTimestamp BETWEEN :startDate AND :endDate
        AND t.transactionNature = 'EXPENSE'
        AND (:simId IS NULL OR t.simSubscriptionId = :simId)
        GROUP BY c.id, dayOfWeek
        ORDER BY totalAmount DESC
        """
    )
    fun getSpendingByCategoryAndDayOfWeek(startDate: Long, endDate: Long, simId: Int? = null): Flow<List<CategoryDaySpend>>

    @Query(
        """
        SELECT recipientName, SUM(amount) as totalSpent, COUNT(*) as txCount
        FROM transactions
        WHERE transactionNature = 'EXPENSE'
        AND (:simId IS NULL OR simSubscriptionId = :simId)
        GROUP BY recipientName
        ORDER BY totalSpent DESC
        LIMIT :limit
        """
    )
    fun getTopMerchants(limit: Int = 10, simId: Int? = null): Flow<List<MerchantSummary>>

    @Query(
        """
        SELECT 
            strftime('%Y-%m', dateTimestamp / 1000, 'unixepoch', 'localtime') as monthString,
            SUM(amount) as totalAmount
        FROM transactions
        WHERE transactionNature = 'LOAN_DRAW'
        AND (:simId IS NULL OR simSubscriptionId = :simId)
        GROUP BY monthString
        ORDER BY monthString ASC
        """
    )
    fun getFulizaUsageByMonth(simId: Int? = null): Flow<List<MonthlyTotal>>

    @Query(
        """
        SELECT 
            strftime('%Y-%m', dateTimestamp / 1000, 'unixepoch', 'localtime') as monthString,
            SUM(amount) as totalAmount
        FROM transactions
        WHERE transactionNature = 'EXPENSE'
        AND dateTimestamp >= :startDate
        AND (:simId IS NULL OR simSubscriptionId = :simId)
        GROUP BY monthString
        ORDER BY monthString ASC
        """
    )
    fun getMonthlyExpenseTotals(startDate: Long, simId: Int? = null): Flow<List<MonthlyTotal>>

    @Query(
        """
        SELECT 
            strftime('%Y-%m', dateTimestamp / 1000, 'unixepoch', 'localtime') as monthString,
            SUM(amount) as totalAmount
        FROM transactions
        WHERE transactionNature = 'INCOME'
        AND dateTimestamp >= :startDate
        AND (:simId IS NULL OR simSubscriptionId = :simId)
        GROUP BY monthString
        ORDER BY monthString ASC
        """
    )
    fun getMonthlyIncomeTotals(startDate: Long, simId: Int? = null): Flow<List<MonthlyTotal>>

    @Query(
        """
        SELECT accountSource as monthString, SUM(amount) as totalAmount
        FROM transactions
        WHERE transactionNature = 'EXPENSE'
        AND dateTimestamp BETWEEN :startDate AND :endDate
        GROUP BY accountSource
        ORDER BY totalAmount DESC
        """
    )
    fun getExpensesByAccountSource(startDate: Long, endDate: Long): Flow<List<MonthlyTotal>>
}
