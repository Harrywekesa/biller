package com.mpesa.tracker.di

import android.content.Context
import androidx.room.Room
import com.mpesa.tracker.data.local.AppDatabase
import com.mpesa.tracker.data.local.AppDatabaseCallback
import com.mpesa.tracker.data.local.dao.CategoryDao
import com.mpesa.tracker.data.local.dao.CustomRuleDao
import com.mpesa.tracker.data.local.dao.SubscriptionDao
import com.mpesa.tracker.data.local.dao.TransactionDao
import com.mpesa.tracker.data.local.dao.BudgetDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE transactions ADD COLUMN simSubscriptionId INTEGER")
            }
        }

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mpesa_tracker_db"
        )
        .addMigrations(MIGRATION_6_7)
        .fallbackToDestructiveMigration() // Useful during active MVP development
        .addCallback(AppDatabaseCallback(databaseProvider)) // Triggers initial data seeding
        .build()
    }

    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideSubscriptionDao(database: AppDatabase): SubscriptionDao {
        return database.subscriptionDao()
    }

    @Provides
    fun provideCustomRuleDao(database: AppDatabase): CustomRuleDao {
        return database.customRuleDao()
    }

    @Provides
    fun provideBudgetDao(database: AppDatabase): BudgetDao {
        return database.budgetDao()
    }
}
