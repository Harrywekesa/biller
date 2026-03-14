package com.mpesa.tracker.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\'\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u0007\u001a\u00020\bH&J\b\u0010\t\u001a\u00020\nH&J\b\u0010\u000b\u001a\u00020\fH&\u00a8\u0006\r"}, d2 = {"Lcom/mpesa/tracker/data/local/AppDatabase;", "Landroidx/room/RoomDatabase;", "()V", "budgetDao", "Lcom/mpesa/tracker/data/local/dao/BudgetDao;", "categoryDao", "Lcom/mpesa/tracker/data/local/dao/CategoryDao;", "customRuleDao", "Lcom/mpesa/tracker/data/local/dao/CustomRuleDao;", "subscriptionDao", "Lcom/mpesa/tracker/data/local/dao/SubscriptionDao;", "transactionDao", "Lcom/mpesa/tracker/data/local/dao/TransactionDao;", "app_debug"})
@androidx.room.Database(entities = {com.mpesa.tracker.data.local.entities.TransactionEntity.class, com.mpesa.tracker.data.local.entities.CategoryEntity.class, com.mpesa.tracker.data.local.entities.SubscriptionEntity.class, com.mpesa.tracker.data.local.entities.CustomRuleEntity.class, com.mpesa.tracker.data.local.entities.BudgetEntity.class}, version = 6, exportSchema = false)
public abstract class AppDatabase extends androidx.room.RoomDatabase {
    
    public AppDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.mpesa.tracker.data.local.dao.TransactionDao transactionDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.mpesa.tracker.data.local.dao.CategoryDao categoryDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.mpesa.tracker.data.local.dao.SubscriptionDao subscriptionDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.mpesa.tracker.data.local.dao.CustomRuleDao customRuleDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.mpesa.tracker.data.local.dao.BudgetDao budgetDao();
}