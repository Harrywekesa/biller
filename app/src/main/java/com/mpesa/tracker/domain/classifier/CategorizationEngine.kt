package com.mpesa.tracker.domain.classifier

import com.mpesa.tracker.data.local.dao.CustomRuleDao
import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.data.local.entities.TransactionNature
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Unified categorization pipeline with corrected precedence:
 * 1) Non-EXPENSE transactions (TRANSFER, LOAN_DRAW, LOAN_REPAY, REVERSAL, INCOME) stay uncategorized
 * 2) User's explicit [CustomRuleDao] overrides ALWAYS take highest precedence
 * 3) Deterministic [DictionaryClassifier] (Paybill/Till registry + keyword map)
 */
@Singleton
class CategorizationEngine @Inject constructor(
    private val customRuleDao: CustomRuleDao,
    private val dictionaryClassifier: DictionaryClassifier
) {
    suspend fun categorize(transaction: TransactionEntity): Int? {
        if (transaction.natureEnum != TransactionNature.EXPENSE || transaction.isIncome) {
            return null
        }

        val customCategoryId = customRuleDao.getCategoryIdForMerchant(transaction.recipientName)
        if (customCategoryId != null) {
            return customCategoryId
        }

        return dictionaryClassifier.classify(transaction)
    }
}
