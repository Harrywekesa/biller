package com.mpesa.tracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.data.local.entities.CategoryExpense
import com.mpesa.tracker.data.local.entities.DailySpend
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @androidx.room.Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("UPDATE transactions SET categoryId = :categoryId WHERE recipientName = :merchantName")
    suspend fun updateCategoryForMerchant(merchantName: String, categoryId: Int)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    @Query("SELECT DISTINCT simSubscriptionId FROM transactions WHERE simSubscriptionId IS NOT NULL ORDER BY simSubscriptionId ASC")
    fun getActiveSimIds(): Flow<List<Int>>

    @Query("SELECT * FROM transactions WHERE (:simId IS NULL OR simSubscriptionId = :simId) ORDER BY dateTimestamp DESC")
    fun getAllTransactions(simId: Int? = null): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE dateTimestamp BETWEEN :startDate AND :endDate AND (:simId IS NULL OR simSubscriptionId = :simId) ORDER BY dateTimestamp DESC")
    fun getTransactionsBetween(startDate: Long, endDate: Long, simId: Int? = null): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type IN (:types) AND dateTimestamp BETWEEN :startDate AND :endDate AND (:simId IS NULL OR simSubscriptionId = :simId)")
    fun getTotalSpentBetween(startDate: Long, endDate: Long, types: List<String>, simId: Int? = null): Flow<Double?>
    
    @Query("SELECT SUM(transactionCost) FROM transactions WHERE dateTimestamp BETWEEN :startDate AND :endDate AND (:simId IS NULL OR simSubscriptionId = :simId)")
    fun getTotalFeesBetween(startDate: Long, endDate: Long, simId: Int? = null): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE isIncome = 1 AND dateTimestamp BETWEEN :startDate AND :endDate AND (:simId IS NULL OR simSubscriptionId = :simId)")
    fun getTotalIncomeBetween(startDate: Long, endDate: Long, simId: Int? = null): Flow<Double?>

    @Query("SELECT * FROM transactions WHERE receiptNumber = :receiptNumber LIMIT 1")
    suspend fun getTransactionByReceipt(receiptNumber: String): TransactionEntity?

    @Query("""
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
        AND t.isIncome = 0
        AND (:simId IS NULL OR t.simSubscriptionId = :simId)
        GROUP BY c.id
        ORDER BY totalAmount DESC
    """)
    fun getExpensesByCategory(startDate: Long, endDate: Long, simId: Int? = null): Flow<List<CategoryExpense>>

    @Query("""
        SELECT 
            strftime('%Y-%m-%d', dateTimestamp / 1000, 'unixepoch', 'localtime') as dateString,
            SUM(amount) as totalAmount
        FROM transactions
        WHERE dateTimestamp BETWEEN :startDate AND :endDate
        AND isIncome = 0
        AND (:simId IS NULL OR simSubscriptionId = :simId)
        GROUP BY dateString
        ORDER BY dateString ASC
    """)
    fun getDailySpendingTrend(startDate: Long, endDate: Long, simId: Int? = null): Flow<List<DailySpend>>
}
