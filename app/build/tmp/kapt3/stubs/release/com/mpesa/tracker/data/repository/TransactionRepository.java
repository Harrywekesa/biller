package com.mpesa.tracker.data.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0012\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\bJ\u0014\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\r0\fH\u0002J\u0012\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\t0\bJ\u000e\u0010\u0010\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00110\bJ\u0016\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u0015R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/mpesa/tracker/data/repository/TransactionRepository;", "", "transactionDao", "Lcom/mpesa/tracker/data/local/dao/TransactionDao;", "categoryDao", "Lcom/mpesa/tracker/data/local/dao/CategoryDao;", "(Lcom/mpesa/tracker/data/local/dao/TransactionDao;Lcom/mpesa/tracker/data/local/dao/CategoryDao;)V", "getAllTransactions", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/mpesa/tracker/data/local/entities/TransactionEntity;", "getCurrentMonthTimestamps", "Lkotlin/Pair;", "", "getExpensesByCategoryThisMonth", "Lcom/mpesa/tracker/data/local/entities/CategoryExpense;", "getTotalSpentThisMonth", "", "insertTransaction", "", "transaction", "(Lcom/mpesa/tracker/data/local/entities/TransactionEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_release"})
public final class TransactionRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.mpesa.tracker.data.local.dao.TransactionDao transactionDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.mpesa.tracker.data.local.dao.CategoryDao categoryDao = null;
    
    @javax.inject.Inject()
    public TransactionRepository(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.dao.TransactionDao transactionDao, @org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.dao.CategoryDao categoryDao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.mpesa.tracker.data.local.entities.TransactionEntity>> getAllTransactions() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.Double> getTotalSpentThisMonth() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertTransaction(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.entities.TransactionEntity transaction, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final kotlin.Pair<java.lang.Long, java.lang.Long> getCurrentMonthTimestamps() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.mpesa.tracker.data.local.entities.CategoryExpense>> getExpensesByCategoryThisMonth() {
        return null;
    }
}