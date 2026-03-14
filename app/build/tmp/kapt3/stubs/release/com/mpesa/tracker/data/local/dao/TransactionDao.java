package com.mpesa.tracker.data.local.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J$\u0010\u0006\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u00040\u00032\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tH\'J \u0010\u000b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\f0\u00032\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tH\'J.\u0010\r\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\f0\u00032\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\t2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0004H\'J\u0018\u0010\u0010\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0011\u001a\u00020\u000fH\u00a7@\u00a2\u0006\u0002\u0010\u0012J$\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tH\'J\u0016\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0017\u00a8\u0006\u0018"}, d2 = {"Lcom/mpesa/tracker/data/local/dao/TransactionDao;", "", "getAllTransactions", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/mpesa/tracker/data/local/entities/TransactionEntity;", "getExpensesByCategory", "Lcom/mpesa/tracker/data/local/entities/CategoryExpense;", "startDate", "", "endDate", "getTotalFeesBetween", "", "getTotalSpentBetween", "types", "", "getTransactionByReceipt", "receiptNumber", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getTransactionsBetween", "insertTransaction", "", "transaction", "(Lcom/mpesa/tracker/data/local/entities/TransactionEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_release"})
@androidx.room.Dao()
public abstract interface TransactionDao {
    
    @androidx.room.Insert(onConflict = 5)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertTransaction(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.entities.TransactionEntity transaction, @org.jetbrains.annotations.NotNull()
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
    
    @androidx.room.Query(value = "SELECT * FROM transactions WHERE receiptNumber = :receiptNumber LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTransactionByReceipt(@org.jetbrains.annotations.NotNull()
    java.lang.String receiptNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.mpesa.tracker.data.local.entities.TransactionEntity> $completion);
    
    @androidx.room.Query(value = "\n        SELECT \n            c.name as categoryName, \n            c.colorCode as colorCode, \n            SUM(t.amount) as totalAmount\n        FROM transactions t\n        LEFT JOIN categories c ON t.categoryId = c.id\n        WHERE t.dateTimestamp BETWEEN :startDate AND :endDate\n        AND t.isIncome = 0\n        GROUP BY c.id\n        ORDER BY totalAmount DESC\n    ")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.mpesa.tracker.data.local.entities.CategoryExpense>> getExpensesByCategory(long startDate, long endDate);
}