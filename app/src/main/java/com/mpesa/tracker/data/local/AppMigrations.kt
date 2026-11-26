package com.mpesa.tracker.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room database schema migrations.
 * All migrations MUST be strictly non-destructive so existing user transactions,
 * notes, categories, custom rules, budgets, and subscriptions are always preserved.
 */
object AppMigrations {

    val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE transactions ADD COLUMN linkedReceiptNumber TEXT"
            )
        }
    }

    val MIGRATION_8_9 = object : Migration(8, 9) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE transactions ADD COLUMN userNote TEXT")
        }
    }

    val MIGRATION_9_10 = object : Migration(9, 10) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE transactions ADD COLUMN accountSource TEXT NOT NULL DEFAULT 'MPESA'")
        }
    }

    /**
     * Migration 10 -> 11 (V2 Total Redesign)
     * - Adds transactionNature, transferGroupRef, isManualEntry to transactions WITHOUT deleting any rows.
     * - Backfills transactionNature cleanly from existing type & isIncome flags.
     * - Clears legacy erroneous 'Mpesa Fees' category on WITHDRAW_CASH rows so agent/ATM cash withdrawals
     *   count only their transactionCost under Fees.
     * - Creates transfer_rules and unparsed_sms diagnostic tables.
     */
    val MIGRATION_10_11 = object : Migration(10, 11) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE transactions ADD COLUMN transactionNature TEXT NOT NULL DEFAULT 'EXPENSE'")
            db.execSQL("ALTER TABLE transactions ADD COLUMN transferGroupRef TEXT")
            db.execSQL("ALTER TABLE transactions ADD COLUMN isManualEntry INTEGER NOT NULL DEFAULT 0")

            db.execSQL(
                """
                UPDATE transactions
                SET transactionNature = CASE
                    WHEN type = 'FULIZA_DRAW' THEN 'LOAN_DRAW'
                    WHEN type = 'FULIZA_REPAYMENT' THEN 'LOAN_REPAY'
                    WHEN type = 'WITHDRAW_CASH' THEN 'TRANSFER'
                    WHEN isIncome = 1 THEN 'INCOME'
                    ELSE 'EXPENSE'
                END
                """.trimIndent()
            )

            // Fix legacy misclassification: DictionaryClassifier previously assigned WITHDRAW_CASH to 'Mpesa Fees'
            db.execSQL(
                """
                UPDATE transactions
                SET categoryId = NULL
                WHERE type = 'WITHDRAW_CASH'
                AND categoryId IN (SELECT id FROM categories WHERE name = 'Mpesa Fees')
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `transfer_rules` (
                    `merchantKey` TEXT NOT NULL,
                    `nature` TEXT NOT NULL,
                    `createdAt` INTEGER NOT NULL,
                    PRIMARY KEY(`merchantKey`)
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `unparsed_sms` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `sender` TEXT NOT NULL,
                    `body` TEXT NOT NULL,
                    `bodyHash` TEXT NOT NULL,
                    `receivedAt` INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS `index_unparsed_sms_bodyHash` ON `unparsed_sms` (`bodyHash`)"
            )
        }
    }
}
