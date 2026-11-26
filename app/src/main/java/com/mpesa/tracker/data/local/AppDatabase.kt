package com.mpesa.tracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mpesa.tracker.data.local.dao.*
import com.mpesa.tracker.data.local.entities.*

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        SubscriptionEntity::class,
        CustomRuleEntity::class,
        BudgetEntity::class,
        TransferRuleEntity::class,
        UnparsedSmsEntity::class
    ],
    version = 11,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun customRuleDao(): CustomRuleDao
    abstract fun budgetDao(): BudgetDao
    abstract fun transferRuleDao(): TransferRuleDao
    abstract fun unparsedSmsDao(): UnparsedSmsDao

    companion object {
        val ALL_MIGRATIONS = arrayOf(
            AppMigrations.MIGRATION_7_8,
            AppMigrations.MIGRATION_8_9,
            AppMigrations.MIGRATION_9_10,
            AppMigrations.MIGRATION_10_11
        )
    }
}
