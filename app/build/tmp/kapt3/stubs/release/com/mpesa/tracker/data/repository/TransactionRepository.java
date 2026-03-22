package com.mpesa.tracker.data.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000~\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\r\b\u0007\u0018\u00002\u00020\u0001B\'\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ&\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u0011J\u0016\u0010\u0012\u001a\u00020\f2\u0006\u0010\u0013\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010\u0015J\u000e\u0010\u0016\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u0010\u0017J\u0012\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001b0\u001a0\u0019J\u0012\u0010\u001c\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001d0\u001a0\u0019J\u0012\u0010\u001e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001f0\u001a0\u0019J7\u0010 \u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020!0\u001a0\u00192\u0006\u0010\"\u001a\u00020#2\n\b\u0002\u0010$\u001a\u0004\u0018\u00010%2\n\b\u0002\u0010&\u001a\u0004\u0018\u00010%\u00a2\u0006\u0002\u0010\'J7\u0010(\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020)0\u001a0\u00192\u0006\u0010\"\u001a\u00020#2\n\b\u0002\u0010$\u001a\u0004\u0018\u00010%2\n\b\u0002\u0010&\u001a\u0004\u0018\u00010%\u00a2\u0006\u0002\u0010\'J9\u0010*\u001a\u000e\u0012\u0004\u0012\u00020%\u0012\u0004\u0012\u00020%0+2\u0006\u0010\"\u001a\u00020#2\n\b\u0002\u0010$\u001a\u0004\u0018\u00010%2\n\b\u0002\u0010&\u001a\u0004\u0018\u00010%H\u0002\u00a2\u0006\u0002\u0010,J3\u0010-\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010.0\u00192\u0006\u0010\"\u001a\u00020#2\n\b\u0002\u0010$\u001a\u0004\u0018\u00010%2\n\b\u0002\u0010&\u001a\u0004\u0018\u00010%\u00a2\u0006\u0002\u0010\'J3\u0010/\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010.0\u00192\u0006\u0010\"\u001a\u00020#2\n\b\u0002\u0010$\u001a\u0004\u0018\u00010%2\n\b\u0002\u0010&\u001a\u0004\u0018\u00010%\u00a2\u0006\u0002\u0010\'J\u0016\u00100\u001a\u00020\f2\u0006\u00101\u001a\u00020\u001dH\u0086@\u00a2\u0006\u0002\u00102J\u001e\u00103\u001a\u00020\f2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u00104\u001a\u00020.H\u0086@\u00a2\u0006\u0002\u00105J&\u00106\u001a\u00020\f2\u0006\u00107\u001a\u00020\u000e2\u0006\u00108\u001a\u00020\u00142\u0006\u00109\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010:R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006;"}, d2 = {"Lcom/mpesa/tracker/data/repository/TransactionRepository;", "", "transactionDao", "Lcom/mpesa/tracker/data/local/dao/TransactionDao;", "categoryDao", "Lcom/mpesa/tracker/data/local/dao/CategoryDao;", "customRuleDao", "Lcom/mpesa/tracker/data/local/dao/CustomRuleDao;", "budgetDao", "Lcom/mpesa/tracker/data/local/dao/BudgetDao;", "(Lcom/mpesa/tracker/data/local/dao/TransactionDao;Lcom/mpesa/tracker/data/local/dao/CategoryDao;Lcom/mpesa/tracker/data/local/dao/CustomRuleDao;Lcom/mpesa/tracker/data/local/dao/BudgetDao;)V", "addCategory", "", "name", "", "colorCode", "iconName", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "clearCategoryBudget", "categoryId", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAllTransactions", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllCategories", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/mpesa/tracker/data/local/entities/CategoryEntity;", "getAllTransactions", "Lcom/mpesa/tracker/data/local/entities/TransactionEntity;", "getAutoDetectedSubscriptions", "Lcom/mpesa/tracker/data/local/entities/SubscriptionEntity;", "getDailySpendingTrend", "Lcom/mpesa/tracker/data/local/entities/DailySpend;", "period", "Lcom/mpesa/tracker/domain/models/ReportPeriod;", "customStart", "", "customEnd", "(Lcom/mpesa/tracker/domain/models/ReportPeriod;Ljava/lang/Long;Ljava/lang/Long;)Lkotlinx/coroutines/flow/Flow;", "getExpensesByCategory", "Lcom/mpesa/tracker/data/local/entities/CategoryExpense;", "getTimestampRangeForPeriod", "Lkotlin/Pair;", "(Lcom/mpesa/tracker/domain/models/ReportPeriod;Ljava/lang/Long;Ljava/lang/Long;)Lkotlin/Pair;", "getTotalIncomeForPeriod", "", "getTotalSpentForPeriod", "insertTransaction", "transaction", "(Lcom/mpesa/tracker/data/local/entities/TransactionEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setCategoryBudget", "limit", "(IDLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateTransactionCategoryAndSaveRule", "receiptNumber", "newCategoryId", "merchantName", "(Ljava/lang/String;ILjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_release"})
public final class TransactionRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.mpesa.tracker.data.local.dao.TransactionDao transactionDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.mpesa.tracker.data.local.dao.CategoryDao categoryDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.mpesa.tracker.data.local.dao.CustomRuleDao customRuleDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.mpesa.tracker.data.local.dao.BudgetDao budgetDao = null;
    
    @javax.inject.Inject()
    public TransactionRepository(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.dao.TransactionDao transactionDao, @org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.dao.CategoryDao categoryDao, @org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.dao.CustomRuleDao customRuleDao, @org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.dao.BudgetDao budgetDao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.mpesa.tracker.data.local.entities.TransactionEntity>> getAllTransactions() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.mpesa.tracker.data.local.entities.CategoryEntity>> getAllCategories() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteAllTransactions(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addCategory(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String colorCode, @org.jetbrains.annotations.NotNull()
    java.lang.String iconName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setCategoryBudget(int categoryId, double limit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object clearCategoryBudget(int categoryId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.Double> getTotalSpentForPeriod(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.domain.models.ReportPeriod period, @org.jetbrains.annotations.Nullable()
    java.lang.Long customStart, @org.jetbrains.annotations.Nullable()
    java.lang.Long customEnd) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.Double> getTotalIncomeForPeriod(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.domain.models.ReportPeriod period, @org.jetbrains.annotations.Nullable()
    java.lang.Long customStart, @org.jetbrains.annotations.Nullable()
    java.lang.Long customEnd) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertTransaction(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.entities.TransactionEntity transaction, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateTransactionCategoryAndSaveRule(@org.jetbrains.annotations.NotNull()
    java.lang.String receiptNumber, int newCategoryId, @org.jetbrains.annotations.NotNull()
    java.lang.String merchantName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final kotlin.Pair<java.lang.Long, java.lang.Long> getTimestampRangeForPeriod(com.mpesa.tracker.domain.models.ReportPeriod period, java.lang.Long customStart, java.lang.Long customEnd) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.mpesa.tracker.data.local.entities.CategoryExpense>> getExpensesByCategory(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.domain.models.ReportPeriod period, @org.jetbrains.annotations.Nullable()
    java.lang.Long customStart, @org.jetbrains.annotations.Nullable()
    java.lang.Long customEnd) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.mpesa.tracker.data.local.entities.DailySpend>> getDailySpendingTrend(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.domain.models.ReportPeriod period, @org.jetbrains.annotations.Nullable()
    java.lang.Long customStart, @org.jetbrains.annotations.Nullable()
    java.lang.Long customEnd) {
        return null;
    }
    
    /**
     * Replaced auto-detection with explicit categorization. 
     * Users assign bills to the "Subscriptions" category, and this function reads those merchants.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.mpesa.tracker.data.local.entities.SubscriptionEntity>> getAutoDetectedSubscriptions() {
        return null;
    }
}