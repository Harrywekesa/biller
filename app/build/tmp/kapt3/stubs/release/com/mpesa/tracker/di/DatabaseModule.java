package com.mpesa.tracker.di;

@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J \u0010\u0003\u001a\u00020\u00042\b\b\u0001\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00040\bH\u0007J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0004H\u0007J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u000b\u001a\u00020\u0004H\u0007J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u000b\u001a\u00020\u0004H\u0007J\u0010\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u000b\u001a\u00020\u0004H\u0007J\u0010\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u000b\u001a\u00020\u0004H\u0007\u00a8\u0006\u0014"}, d2 = {"Lcom/mpesa/tracker/di/DatabaseModule;", "", "()V", "provideAppDatabase", "Lcom/mpesa/tracker/data/local/AppDatabase;", "context", "Landroid/content/Context;", "databaseProvider", "Ljavax/inject/Provider;", "provideBudgetDao", "Lcom/mpesa/tracker/data/local/dao/BudgetDao;", "database", "provideCategoryDao", "Lcom/mpesa/tracker/data/local/dao/CategoryDao;", "provideCustomRuleDao", "Lcom/mpesa/tracker/data/local/dao/CustomRuleDao;", "provideSubscriptionDao", "Lcom/mpesa/tracker/data/local/dao/SubscriptionDao;", "provideTransactionDao", "Lcom/mpesa/tracker/data/local/dao/TransactionDao;", "app_release"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class DatabaseModule {
    @org.jetbrains.annotations.NotNull()
    public static final com.mpesa.tracker.di.DatabaseModule INSTANCE = null;
    
    private DatabaseModule() {
        super();
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.mpesa.tracker.data.local.AppDatabase provideAppDatabase(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    javax.inject.Provider<com.mpesa.tracker.data.local.AppDatabase> databaseProvider) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.mpesa.tracker.data.local.dao.TransactionDao provideTransactionDao(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.AppDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.mpesa.tracker.data.local.dao.CategoryDao provideCategoryDao(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.AppDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.mpesa.tracker.data.local.dao.SubscriptionDao provideSubscriptionDao(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.AppDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.mpesa.tracker.data.local.dao.CustomRuleDao provideCustomRuleDao(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.AppDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.mpesa.tracker.data.local.dao.BudgetDao provideBudgetDao(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.AppDatabase database) {
        return null;
    }
}