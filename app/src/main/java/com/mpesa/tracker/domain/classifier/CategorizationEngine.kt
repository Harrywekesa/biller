package com.mpesa.tracker.domain.classifier

import com.mpesa.tracker.data.local.dao.CustomRuleDao
import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.data.local.entities.TransactionNature
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Unified categorization pipeline with corrected precedence:
 * 1) Non-EXPENSE transactions (TRANSFER, LOAN_DRAW, LOAN_REPAY, REVERSAL, INCOME) stay uncategorized
 * 2) User's explicit [CustomRuleDao] overrides ALWAYS take highest precedence (by merchant name OR Paybill/Till code)
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

        val customByMerchant = customRuleDao.getCategoryIdForMerchant(transaction.recipientName)
        if (customByMerchant != null) {
            return customByMerchant
        }

        val code = transaction.recipientNumber?.trim()?.takeIf { it.isNotEmpty() }
        if (code != null) {
            val customByCode = customRuleDao.getCategoryIdForMerchant("CODE:$code")
            if (customByCode != null) {
                return customByCode
            }
        }

        return dictionaryClassifier.classify(transaction)
    }
}
