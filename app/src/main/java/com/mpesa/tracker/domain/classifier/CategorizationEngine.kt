package com.mpesa.tracker.domain.classifier

import com.mpesa.tracker.data.local.entities.TransactionEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategorizationEngine @Inject constructor(
    private val dictionaryClassifier: DictionaryClassifier,
    private val mlClassifier: TensorFlowLiteClassifier
) {

    /**
     * Determines the optimal category for a transaction.
     * It first attempts a strict heuristic rule match (fastest and 100% precise).
     * If that fails, it falls back to the Machine Learning NLP model.
     * Finally, it defaults to a generic category or null.
     */
    suspend fun categorize(transaction: TransactionEntity): Int? {
        
        // 1. Pass through heuristic rules (e.g. KPLC -> Utilities)
        val dictionaryMatch = dictionaryClassifier.classify(transaction)
        if (dictionaryMatch != null) {
            return dictionaryMatch
        }

        // 2. Pass through TFLite Model (e.g. "John Doe Send Money" -> Transfers)
        val mlMatch = mlClassifier.classify(transaction)
        if (mlMatch != null) {
            return mlMatch
        }

        // 3. Fallback (Leave Uncategorized)
        return null
    }
}
