package com.mpesa.tracker.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0013\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u0005J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0016R\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lcom/mpesa/tracker/data/local/AppDatabaseCallback;", "Landroidx/room/RoomDatabase$Callback;", "databaseProvider", "Ljavax/inject/Provider;", "Lcom/mpesa/tracker/data/local/AppDatabase;", "(Ljavax/inject/Provider;)V", "onCreate", "", "db", "Landroidx/sqlite/db/SupportSQLiteDatabase;", "app_release"})
public final class AppDatabaseCallback extends androidx.room.RoomDatabase.Callback {
    @org.jetbrains.annotations.NotNull()
    private final javax.inject.Provider<com.mpesa.tracker.data.local.AppDatabase> databaseProvider = null;
    
    public AppDatabaseCallback(@org.jetbrains.annotations.NotNull()
    javax.inject.Provider<com.mpesa.tracker.data.local.AppDatabase> databaseProvider) {
        super();
    }
    
    @java.lang.Override()
    public void onCreate(@org.jetbrains.annotations.NotNull()
    androidx.sqlite.db.SupportSQLiteDatabase db) {
    }
}