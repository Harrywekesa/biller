package com.mpesa.tracker.data.local.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\b\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0014\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006H\'J$\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u00070\u00062\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\fH\'J$\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u00070\u00062\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\fH\'J \u0010\u0010\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00110\u00062\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\fH\'J \u0010\u0012\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00110\u00062\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\fH\'J.\u0010\u0013\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00110\u00062\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\f2\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\u0007H\'J\u0018\u0010\u0016\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0017\u001a\u00020\u0015H\u00a7@\u00a2\u0006\u0002\u0010\u0018J$\u0010\u0019\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u00062\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\fH\'J\u0016\u0010\u001a\u001a\u00020\u00032\u0006\u0010\u001b\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\u001cJ\u001e\u0010\u001d\u001a\u00020\u00032\u0006\u0010\u001e\u001a\u00020\u00152\u0006\u0010\u001f\u001a\u00020 H\u00a7@\u00a2\u0006\u0002\u0010!J\u0016\u0010\"\u001a\u00020\u00032\u0006\u0010\u001b\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\u001c\u00a8\u0006#"}, d2 = {"Lcom/mpesa/tracker/data/local/dao/TransactionDao;", "", "deleteAllTransactions", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllTransactions", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/mpesa/tracker/data/local/entities/TransactionEntity;", "getDailySpendingTrend", "Lcom/mpesa/tracker/data/local/entities/DailySpend;", "startDate", "", "endDate", "getExpensesByCategory", "Lcom/mpesa/tracker/data/local/entities/CategoryExpense;", "getTotalFeesBetween", "", "getTotalIncomeBetween", "getTotalSpentBetween", "types", "", "getTransactionByReceipt", "receiptNumber", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getTransactionsBetween", "insertTransaction", "transaction", "(Lcom/mpesa/tracker/data/local/entities/TransactionEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateCategoryForMerchant", "merchantName", "categoryId", "", "(Ljava/lang/String;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateTransaction", "app_debug"})
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
    
    @androidx.room.Query(value = "SELECT * FROM transactions ORDER BY dateTimestamp DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.mpesa.tracker.data.local.entities.TransactionEntity>> getAllTransactions();
    
    @androidx.room.Query(value = "SELECT * FROM transactions WHERE dateTimestamp BETWEEN :startDate AND :endDate ORDER BY dateTimestamp DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.mpesa.tracker.data.local.entities.TransactionEntity>> getTransactionsBetween(long startDate, long endDate);
    
    @androidx.room.Query(value = "SELECT SUM(amount) FROM transactions WHERE type IN (:types) AND dateTimestamp BETWEEN :startDate AND :endDate")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Double> getTotalSpentBetween(long startDate, long endDate, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> types);
    
    @androidx.room.Query(value = "SELECT SUM(transactionCost) FROM transactions WHERE dateTimestamp BETWEEN :startDate AND :endDate")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Double> getTotalFeesBetween(long startDate, long endDate);
    
    @androidx.room.Query(value = "SELECT SUM(amount) FROM transactions WHERE isIncome = 1 AND dateTimestamp BETWEEN :startDate AND :endDate")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Double> getTotalIncomeBetween(long startDate, long endDate);
    
    @androidx.room.Query(value = "SELECT * FROM transactions WHERE receiptNumber = :receiptNumber LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTransactionByReceipt(@org.jetbrains.annotations.NotNull()
    java.lang.String receiptNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.mpesa.tracker.data.local.entities.TransactionEntity> $completion);
    
    @androidx.room.Query(value = "\n        SELECT \n            c.id as categoryId,\n            c.name as categoryName, \n            c.colorCode as colorCode, \n            SUM(t.amount) as totalAmount,\n            b.monthlyLimit as budgetLimit\n        FROM transactions t\n        LEFT JOIN categories c ON t.categoryId = c.id\n        LEFT JOIN budgets b ON c.id = b.categoryId\n        WHERE t.dateTimestamp BETWEEN :startDate AND :endDate\n        AND t.isIncome = 0\n        GROUP BY c.id\n        ORDER BY totalAmount DESC\n    ")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.mpesa.tracker.data.local.entities.CategoryExpense>> getExpensesByCategory(long startDate, long endDate);
    
    @androidx.room.Query(value = "\n        SELECT \n            strftime(\'%Y-%m-%d\', dateTimestamp / 1000, \'unixepoch\', \'localtime\') as dateString,\n            SUM(amount) as totalAmount\n        FROM transactions\n        WHERE dateTimestamp BETWEEN :startDate AND :endDate\n        AND isIncome = 0\n        GROUP BY dateString\n        ORDER BY dateString ASC\n    ")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.mpesa.tracker.data.local.entities.DailySpend>> getDailySpendingTrend(long startDate, long endDate);
}