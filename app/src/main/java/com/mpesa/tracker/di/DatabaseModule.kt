package com.mpesa.tracker.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mpesa.tracker.data.local.AppDatabase
import com.mpesa.tracker.data.local.AppDatabaseCallback
import com.mpesa.tracker.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        databaseProvider: javax.inject.Provider<AppDatabase>
    ): AppDatabase {
        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE transactions ADD COLUMN simSubscriptionId INTEGER")
            }
        }

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mpesa_tracker_db"
        )
            .addMigrations(MIGRATION_6_7)
            .addMigrations(*AppDatabase.ALL_MIGRATIONS)
            .addCallback(AppDatabaseCallback(databaseProvider))
            .build()
    }

    @Provides fun provideTransactionDao(database: AppDatabase): TransactionDao = database.transactionDao()
    @Provides fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()
    @Provides fun provideSubscriptionDao(database: AppDatabase): SubscriptionDao = database.subscriptionDao()
    @Provides fun provideCustomRuleDao(database: AppDatabase): CustomRuleDao = database.customRuleDao()
    @Provides fun provideBudgetDao(database: AppDatabase): BudgetDao = database.budgetDao()
    @Provides fun provideTransferRuleDao(database: AppDatabase): TransferRuleDao = database.transferRuleDao()
    @Provides fun provideUnparsedSmsDao(database: AppDatabase): UnparsedSmsDao = database.unparsedSmsDao()
}
