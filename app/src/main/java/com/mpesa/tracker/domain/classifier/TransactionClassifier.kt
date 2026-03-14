package com.mpesa.tracker.domain.classifier

import com.mpesa.tracker.data.local.entities.TransactionEntity

/**
 * Interface defining the categorization logic for an M-Pesa transaction.
 * Different strategies (e.g., Dictionary Match, Machine Learning) will implement this.
 */
interface TransactionClassifier {
    
    /**
     * Attempts to classify a transaction into a category.
     * @param transaction The parsed M-Pesa transaction entity.
     * @return The Category ID if a match is confidently found, or null otherwise.
     */
    suspend fun classify(transaction: TransactionEntity): Int?
}
