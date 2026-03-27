package com.mpesa.tracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mpesa.tracker.data.local.dao.CategoryDao
import com.mpesa.tracker.data.local.entities.CategoryEntity
import com.mpesa.tracker.data.local.entities.SubscriptionEntity
import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.data.local.dao.SubscriptionDao
import com.mpesa.tracker.data.local.dao.TransactionDao
import com.mpesa.tracker.data.local.entities.CustomRuleEntity
import com.mpesa.tracker.data.local.dao.CustomRuleDao
import com.mpesa.tracker.data.local.entities.BudgetEntity
import com.mpesa.tracker.data.local.dao.BudgetDao

@Database(
    entities = [TransactionEntity::class, CategoryEntity::class, SubscriptionEntity::class, CustomRuleEntity::class, BudgetEntity::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun customRuleDao(): CustomRuleDao
    abstract fun budgetDao(): BudgetDao
}
