package com.mpesa.tracker.domain.classifier

import android.util.Log
import com.mpesa.tracker.data.local.dao.CategoryDao
import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.data.local.entities.TransactionNature
import com.mpesa.tracker.data.local.entities.TransactionType
import com.mpesa.tracker.domain.dictionary.BusinessDictionary
import javax.inject.Inject

/**
 * Deterministic Kenyan merchant & Paybill/Till dictionary classifier.
 *
 * Strictly skips [TransactionNature.TRANSFER], [TransactionNature.LOAN_DRAW],
 * [TransactionNature.LOAN_REPAY], [TransactionNature.REVERSAL], and [TransactionNature.INCOME]
 * so transfers and ATM/agent cash withdrawals NEVER pollute spending categories like "Mpesa Fees".
 */
class DictionaryClassifier @Inject constructor(
    private val categoryDao: CategoryDao
) : TransactionClassifier {

    override suspend fun classify(transaction: TransactionEntity): Int? {
        val nature = transaction.natureEnum
        if (nature != TransactionNature.EXPENSE || transaction.isIncome) {
            return null
        }

        if (transaction.type == TransactionType.FULIZA_DRAW ||
            transaction.type == TransactionType.FULIZA_REPAYMENT ||
            transaction.type == TransactionType.WITHDRAW_CASH) {
            return null
        }

        if (transaction.type == TransactionType.BUY_AIRTIME) {
            return categoryDao.getCategoryByName("Airtime & Data")?.id
        }

        val categoryName = BusinessDictionary.getCategoryForRecipient(
            recipientName = transaction.recipientName,
            recipientNumber = transaction.recipientNumber
        ) ?: return null

        val category = categoryDao.getCategoryByName(categoryName)
        if (category == null) {
            Log.w("DictionaryClassifier", "Category '$categoryName' not found in DB for '${transaction.recipientName}'")
        }
        return category?.id
    }
}
