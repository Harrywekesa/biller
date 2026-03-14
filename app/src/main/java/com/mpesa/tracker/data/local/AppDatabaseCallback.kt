package com.mpesa.tracker.data.local

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mpesa.tracker.data.local.entities.CategoryEntity
import com.mpesa.tracker.data.local.entities.SubscriptionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider

class AppDatabaseCallback(
    private val databaseProvider: Provider<AppDatabase>
) : RoomDatabase.Callback() {

    // Expose these so MainActivity can manually seed the DB if destroyed by schema updates
    companion object {
        val starterCategories = listOf(
            CategoryEntity(name = "Groceries & Food", colorCode = "#4CAF50", iconName = "shopping_cart"),
            CategoryEntity(name = "Utilities (Water, Power)", colorCode = "#FF9800", iconName = "bolt"),
            CategoryEntity(name = "Transport & Fuel", colorCode = "#2196F3", iconName = "directions_car"),
            CategoryEntity(name = "Airtime & Data", colorCode = "#9C27B0", iconName = "phone_android"),
            CategoryEntity(name = "Rent & Housing", colorCode = "#795548", iconName = "home"),
            CategoryEntity(name = "Family & Friends", colorCode = "#E91E63", iconName = "people"),
            CategoryEntity(name = "Mpesa Fees", colorCode = "#F44336", iconName = "money_off"),
            CategoryEntity(name = "Subscriptions", colorCode = "#00BCD4", iconName = "subscriptions"),
            CategoryEntity(name = "Business/Till", colorCode = "#FFC107", iconName = "store"),
            CategoryEntity(name = "Uncategorized", colorCode = "#9E9E9E", iconName = "help_outline")
        )
        
        val starterSubs = listOf(
            SubscriptionEntity(name = "KPLC Tokens", merchantNameMatcher = "KPLC PREPAID", amount = 1500.0, billingCycleDays = 30),
            SubscriptionEntity(name = "Safaricom Home Fibre", merchantNameMatcher = "SAFARICOM HOME", amount = 2999.0, billingCycleDays = 30)
        )
    }

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        
        db.beginTransaction()
        try {
            starterCategories.forEach { category ->
                db.execSQL(
                    "INSERT INTO categories (name, colorCode, iconName) VALUES (?, ?, ?)",
                    arrayOf(category.name, category.colorCode, category.iconName)
                )
            }
            
            starterSubs.forEach { sub ->
                db.execSQL(
                    "INSERT INTO subscriptions (name, merchantNameMatcher, amount, isActive, billingCycleDays) VALUES (?, ?, ?, ?, ?)",
                    arrayOf(sub.name, sub.merchantNameMatcher, sub.amount, if (sub.isActive) 1 else 0, sub.billingCycleDays)
                )
            }
            
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}
