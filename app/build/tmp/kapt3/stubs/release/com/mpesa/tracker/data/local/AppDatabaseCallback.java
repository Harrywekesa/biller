package com.mpesa.tracker.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \n2\u00020\u0001:\u0001\nB\u0013\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u0005J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0016R\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/mpesa/tracker/data/local/AppDatabaseCallback;", "Landroidx/room/RoomDatabase$Callback;", "databaseProvider", "Ljavax/inject/Provider;", "Lcom/mpesa/tracker/data/local/AppDatabase;", "(Ljavax/inject/Provider;)V", "onCreate", "", "db", "Landroidx/sqlite/db/SupportSQLiteDatabase;", "Companion", "app_release"})
public final class AppDatabaseCallback extends androidx.room.RoomDatabase.Callback {
    @org.jetbrains.annotations.NotNull()
    private final javax.inject.Provider<com.mpesa.tracker.data.local.AppDatabase> databaseProvider = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<com.mpesa.tracker.data.local.entities.CategoryEntity> starterCategories = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<com.mpesa.tracker.data.local.entities.SubscriptionEntity> starterSubs = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.mpesa.tracker.data.local.AppDatabaseCallback.Companion Companion = null;
    
    public AppDatabaseCallback(@org.jetbrains.annotations.NotNull()
    javax.inject.Provider<com.mpesa.tracker.data.local.AppDatabase> databaseProvider) {
        super();
    }
    
    @java.lang.Override()
    public void onCreate(@org.jetbrains.annotations.NotNull()
    androidx.sqlite.db.SupportSQLiteDatabase db) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0007\u00a8\u0006\u000b"}, d2 = {"Lcom/mpesa/tracker/data/local/AppDatabaseCallback$Companion;", "", "()V", "starterCategories", "", "Lcom/mpesa/tracker/data/local/entities/CategoryEntity;", "getStarterCategories", "()Ljava/util/List;", "starterSubs", "Lcom/mpesa/tracker/data/local/entities/SubscriptionEntity;", "getStarterSubs", "app_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<com.mpesa.tracker.data.local.entities.CategoryEntity> getStarterCategories() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<com.mpesa.tracker.data.local.entities.SubscriptionEntity> getStarterSubs() {
            return null;
        }
    }
}