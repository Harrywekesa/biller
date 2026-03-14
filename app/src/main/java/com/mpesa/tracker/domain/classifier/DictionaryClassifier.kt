package com.mpesa.tracker.domain.classifier

import com.mpesa.tracker.data.local.entities.TransactionEntity
import javax.inject.Inject

/**
 * A fast, exact-match heuristic classifier based on a local dictionary of known Kenyan merchants.
 * This runs before the heavy TFLite ML model as a 'first pass' for extreme accuracy on common bills.
 */
class DictionaryClassifier @Inject constructor() : TransactionClassifier {

    // In a real app, this would be queried from the DictionaryEntity Room table.
    // We are hardcoding the initial seed data here for the MVP.
    // 1 = Rent, 2 = Food/Groceries, 3 = Utilities, 4 = Transport, ...
    private val ruleMap = mapOf(
        "KPLC PREPAID" to 3, // Utilities
        "KPLC POSTPAID" to 3,
        "NAIROBI WATER" to 3,
        "ZUKU" to 3,
        "SAFARICOM HOME" to 3,
        "NAIVAS" to 2, // Groceries
        "CARREFOUR" to 2,
        "QUICKMART" to 2,
        "CLEANSHELF" to 2,
        "CHANDARANA" to 2,
        "KFC" to 2, // Food
        "JAVA HOUSE" to 2,
        "ARTCAFE" to 2,
        "UBER" to 4, // Transport
        "BOLT" to 4,
        "LITTLE CAB" to 4,
        "SENDY" to 4,
        "Safaricom Airtime/Data" to 4, // 4 = Airtime array index 3+1 (mapped loosely) 
        "SAFARICOM DATA BUNDLES" to 4, // Airtime category ID
        "AIRTEL" to 4
    )

    override suspend fun classify(transaction: TransactionEntity): Int? {
        val upperRecipient = transaction.recipientName.uppercase()
        
        // Direct match
        if (ruleMap.containsKey(upperRecipient)) {
            return ruleMap[upperRecipient]
        }

        // Partial Match (e.g. "NAIVAS RIVERSIDE" -> matches "NAIVAS")
        for ((key, categoryId) in ruleMap) {
            if (upperRecipient.contains(key)) {
                return categoryId
            }
        }

        return null // Move on to ML model if heuristic fails
    }
}
