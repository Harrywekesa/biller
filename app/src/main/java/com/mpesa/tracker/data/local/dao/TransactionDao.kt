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

    @Query("SELECT * FROM transactions ORDER BY dateTimestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE dateTimestamp BETWEEN :startDate AND :endDate ORDER BY dateTimestamp DESC")
    fun getTransactionsBetween(startDate: Long, endDate: Long): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type IN (:types) AND dateTimestamp BETWEEN :startDate AND :endDate")
    fun getTotalSpentBetween(startDate: Long, endDate: Long, types: List<String>): Flow<Double?>
    
    @Query("SELECT SUM(transactionCost) FROM transactions WHERE dateTimestamp BETWEEN :startDate AND :endDate")
    fun getTotalFeesBetween(startDate: Long, endDate: Long): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE isIncome = 1 AND dateTimestamp BETWEEN :startDate AND :endDate")
    fun getTotalIncomeBetween(startDate: Long, endDate: Long): Flow<Double?>

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
        GROUP BY c.id
        ORDER BY totalAmount DESC
    """)
    fun getExpensesByCategory(startDate: Long, endDate: Long): Flow<List<CategoryExpense>>

    @Query("""
        SELECT 
            strftime('%Y-%m-%d', dateTimestamp / 1000, 'unixepoch', 'localtime') as dateString,
            SUM(amount) as totalAmount
        FROM transactions
        WHERE dateTimestamp BETWEEN :startDate AND :endDate
        AND isIncome = 0
        GROUP BY dateString
        ORDER BY dateString ASC
    """)
    fun getDailySpendingTrend(startDate: Long, endDate: Long): Flow<List<DailySpend>>
}
