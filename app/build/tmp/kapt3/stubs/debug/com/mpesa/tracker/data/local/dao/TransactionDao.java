package com.mpesa.tracker.data.local.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u000e\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0014\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006H\'J%\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u00070\u00062\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\bH\'\u00a2\u0006\u0002\u0010\fJ5\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u00070\u00062\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\bH\'\u00a2\u0006\u0002\u0010\u0012J5\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00140\u00070\u00062\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\bH\'\u00a2\u0006\u0002\u0010\u0012J1\u0010\u0015\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00160\u00062\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\bH\'\u00a2\u0006\u0002\u0010\u0012J1\u0010\u0017\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00160\u00062\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\bH\'\u00a2\u0006\u0002\u0010\u0012J?\u0010\u0018\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00160\u00062\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001a0\u00072\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\bH\'\u00a2\u0006\u0002\u0010\u001bJ\u0018\u0010\u001c\u001a\u0004\u0018\u00010\n2\u0006\u0010\u001d\u001a\u00020\u001aH\u00a7@\u00a2\u0006\u0002\u0010\u001eJ5\u0010\u001f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u00070\u00062\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\bH\'\u00a2\u0006\u0002\u0010\u0012J\u0016\u0010 \u001a\u00020\u00032\u0006\u0010!\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\"J\u001e\u0010#\u001a\u00020\u00032\u0006\u0010$\u001a\u00020\u001a2\u0006\u0010%\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010&J\u0016\u0010\'\u001a\u00020\u00032\u0006\u0010!\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\"\u00a8\u0006("}, d2 = {"Lcom/mpesa/tracker/data/local/dao/TransactionDao;", "", "deleteAllTransactions", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getActiveSimIds", "Lkotlinx/coroutines/flow/Flow;", "", "", "getAllTransactions", "Lcom/mpesa/tracker/data/local/entities/TransactionEntity;", "simId", "(Ljava/lang/Integer;)Lkotlinx/coroutines/flow/Flow;", "getDailySpendingTrend", "Lcom/mpesa/tracker/data/local/entities/DailySpend;", "startDate", "", "endDate", "(JJLjava/lang/Integer;)Lkotlinx/coroutines/flow/Flow;", "getExpensesByCategory", "Lcom/mpesa/tracker/data/local/entities/CategoryExpense;", "getTotalFeesBetween", "", "getTotalIncomeBetween", "getTotalSpentBetween", "types", "", "(JJLjava/util/List;Ljava/lang/Integer;)Lkotlinx/coroutines/flow/Flow;", "getTransactionByReceipt", "receiptNumber", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getTransactionsBetween", "insertTransaction", "transaction", "(Lcom/mpesa/tracker/data/local/entities/TransactionEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateCategoryForMerchant", "merchantName", "categoryId", "(Ljava/lang/String;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateTransaction", "app_debug"})
@androidx.room.Dao()
public abstract interface TransactionDao {
    
    @androidx.room.Insert(onConflict = 5)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertTransaction(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.entities.TransactionEntity transaction, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateTransaction(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.entities.TransactionEntity transaction, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE transactions SET categoryId = :categoryId WHERE recipientName = :merchantName")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateCategoryForMerchant(@org.jetbrains.annotations.NotNull()
    java.lang.String merchantName, int categoryId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM transactions")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAllTransactions(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT DISTINCT simSubscriptionId FROM transactions WHERE simSubscriptionId IS NOT NULL ORDER BY simSubscriptionId ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<java.lang.Integer>> getActiveSimIds();
    
    @androidx.room.Query(value = "SELECT * FROM transactions WHERE (:simId IS NULL OR simSubscriptionId = :simId) ORDER BY dateTimestamp DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.mpesa.tracker.data.local.entities.TransactionEntity>> getAllTransactions(@org.jetbrains.annotations.Nullable()
    java.lang.Integer simId);
    
    @androidx.room.Query(value = "SELECT * FROM transactions WHERE dateTimestamp BETWEEN :startDate AND :endDate AND (:simId IS NULL OR simSubscriptionId = :simId) ORDER BY dateTimestamp DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.mpesa.tracker.data.local.entities.TransactionEntity>> getTransactionsBetween(long startDate, long endDate, @org.jetbrains.annotations.Nullable()
    java.lang.Integer simId);
    
    @androidx.room.Query(value = "SELECT SUM(amount) FROM transactions WHERE type IN (:types) AND dateTimestamp BETWEEN :startDate AND :endDate AND (:simId IS NULL OR simSubscriptionId = :simId)")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Double> getTotalSpentBetween(long startDate, long endDate, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> types, @org.jetbrains.annotations.Nullable()
    java.lang.Integer simId);
    
    @androidx.room.Query(value = "SELECT SUM(transactionCost) FROM transactions WHERE dateTimestamp BETWEEN :startDate AND :endDate AND (:simId IS NULL OR simSubscriptionId = :simId)")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Double> getTotalFeesBetween(long startDate, long endDate, @org.jetbrains.annotations.Nullable()
    java.lang.Integer simId);
    
    @androidx.room.Query(value = "SELECT SUM(amount) FROM transactions WHERE isIncome = 1 AND dateTimestamp BETWEEN :startDate AND :endDate AND (:simId IS NULL OR simSubscriptionId = :simId)")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Double> getTotalIncomeBetween(long startDate, long endDate, @org.jetbrains.annotations.Nullable()
    java.lang.Integer simId);
    
    @androidx.room.Query(value = "SELECT * FROM transactions WHERE receiptNumber = :receiptNumber LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTransactionByReceipt(@org.jetbrains.annotations.NotNull()
    java.lang.String receiptNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.mpesa.tracker.data.local.entities.TransactionEntity> $completion);
    
    @androidx.room.Query(value = "\n        SELECT \n            c.id as categoryId,\n            c.name as categoryName, \n            c.colorCode as colorCode, \n            SUM(t.amount) as totalAmount,\n            b.monthlyLimit as budgetLimit\n        FROM transactions t\n        LEFT JOIN categories c ON t.categoryId = c.id\n        LEFT JOIN budgets b ON c.id = b.categoryId\n        WHERE t.dateTimestamp BETWEEN :startDate AND :endDate\n        AND t.isIncome = 0\n        AND (:simId IS NULL OR t.simSubscriptionId = :simId)\n        GROUP BY c.id\n        ORDER BY totalAmount DESC\n    ")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.mpesa.tracker.data.local.entities.CategoryExpense>> getExpensesByCategory(long startDate, long endDate, @org.jetbrains.annotations.Nullable()
    java.lang.Integer simId);
    
    @androidx.room.Query(value = "\n        SELECT \n            strftime(\'%Y-%m-%d\', dateTimestamp / 1000, \'unixepoch\', \'localtime\') as dateString,\n            SUM(amount) as totalAmount\n        FROM transactions\n        WHERE dateTimestamp BETWEEN :startDate AND :endDate\n        AND isIncome = 0\n        AND (:simId IS NULL OR simSubscriptionId = :simId)\n        GROUP BY dateString\n        ORDER BY dateString ASC\n    ")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.mpesa.tracker.data.local.entities.DailySpend>> getDailySpendingTrend(long startDate, long endDate, @org.jetbrains.annotations.Nullable()
    java.lang.Integer simId);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}