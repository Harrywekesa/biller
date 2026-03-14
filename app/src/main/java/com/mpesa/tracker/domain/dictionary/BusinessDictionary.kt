package com.mpesa.tracker.domain.dictionary

object BusinessDictionary {
    
    // Map of known names/numbers to Category Match Strings
    // These strings should correspond to the starter categories in AppDatabaseCallback
    
    val mappings = mapOf(
        // Utilities
        "KPLC" to "Utilities (Water, Power)",
        "888888" to "Utilities (Water, Power)",
        "NAIROBI WATER" to "Utilities (Water, Power)",
        
        // Groceries & Food
        "NAIVAS" to "Groceries & Food",
        "CARREFOUR" to "Groceries & Food",
        "QUICKMART" to "Groceries & Food",
        "CHANDARANA" to "Groceries & Food",
        "KFC" to "Groceries & Food",
        "JAVA HOUSE" to "Groceries & Food",
        "ARTCAFE" to "Groceries & Food",
        "UBER EATS" to "Groceries & Food",
        "GLOVO" to "Groceries & Food",

        // Transport & Fuel
        "UBER" to "Transport & Fuel",
        "BOLT" to "Transport & Fuel",
        "LITTLE CAB" to "Transport & Fuel",
        "SHELL" to "Transport & Fuel",
        "TOTALENERGIES" to "Transport & Fuel",
        "RUBIS" to "Transport & Fuel",
        
        // Subscriptions
        "ZUKU" to "Subscriptions",
        "Safaricom Home" to "Subscriptions",
        "NETFLIX" to "Subscriptions",
        "DSTV" to "Subscriptions",
        "SHOWMAX" to "Subscriptions"
    )

    fun getCategoryForRecipient(recipientName: String, recipientNumber: String?): String? {
        val upperName = recipientName.uppercase()
        
        // Check exact or partial name matches
        for ((key, category) in mappings) {
            if (upperName.contains(key.uppercase())) {
                return category
            }
        }
        
        // Check number match if available
        if (recipientNumber != null && mappings.containsKey(recipientNumber)) {
            return mappings[recipientNumber]
        }
        
        return null
    }
}
