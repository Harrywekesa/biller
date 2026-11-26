package com.mpesa.tracker.domain.dictionary

data class PaybillRegistryEntry(
    val code: String,
    val merchantName: String,
    val categoryName: String
)

/**
 * Kenyan Paybill / Till registry and deterministic merchant dictionary mapped to
 * the app's seeded spending categories.
 *
 * Lookup order:
 * 1) Exact Paybill / Till code lookup via [lookupCode]
 * 2) Exact or partial normalized merchant name matching
 * 3) Inline 5-8 digit Paybill/Till shortcode extraction from recipient text
 *
 * Category names MUST match what is seeded in AppDatabaseCallback.starterCategories:
 *   - "Groceries & Food"
 *   - "Utilities (Water, Power)"
 *   - "Transport & Fuel"
 *   - "Airtime & Data"
 *   - "Rent & Housing"
 *   - "Family & Friends"
 *   - "Mpesa Fees"
 *   - "Subscriptions"
 *   - "Business/Till"
 *   - "Uncategorized"
 */
object BusinessDictionary {

    // ── PAYBILL / TILL REGISTRY ───────────────────────────────────────────────
    // Source: Safaricom official Paybill directory + common Kenyan services

    private val registryEntries: List<PaybillRegistryEntry> = listOf(
        // Utilities — Power
        PaybillRegistryEntry("888888", "KPLC Prepaid", "Utilities (Water, Power)"),
        PaybillRegistryEntry("888880", "KPLC Postpaid", "Utilities (Water, Power)"),
        PaybillRegistryEntry("880100", "Kenya Power", "Utilities (Water, Power)"),

        // Utilities — Water
        PaybillRegistryEntry("444000", "Nairobi Water", "Utilities (Water, Power)"),
        PaybillRegistryEntry("444400", "Mombasa Water", "Utilities (Water, Power)"),
        PaybillRegistryEntry("326326", "NWSC Water", "Utilities (Water, Power)"),

        // Utilities — Internet / TV / Streaming
        PaybillRegistryEntry("200999", "Zuku Fiber", "Subscriptions"),
        PaybillRegistryEntry("400400", "Faiba Home (JTL)", "Subscriptions"),
        PaybillRegistryEntry("325325", "DStv", "Subscriptions"),
        PaybillRegistryEntry("290290", "Showmax", "Subscriptions"),
        PaybillRegistryEntry("249249", "StarTimes", "Subscriptions"),

        // Safaricom Services
        PaybillRegistryEntry("999991", "Safaricom Postpay", "Airtime & Data"),
        PaybillRegistryEntry("333300", "Safaricom Data Bundles", "Airtime & Data"),
        PaybillRegistryEntry("272727", "Safaricom Home Fibre", "Subscriptions"),
        PaybillRegistryEntry("150", "Safaricom Care", "Airtime & Data"),

        // Banking & Financial
        PaybillRegistryEntry("247247", "Equity Bank (Eazzy)", "Business/Till"),
        PaybillRegistryEntry("522522", "KCB Bank", "Business/Till"),
        PaybillRegistryEntry("542542", "I&M Bank", "Business/Till"),
        PaybillRegistryEntry("400200", "Co-operative Bank", "Business/Till"),
        PaybillRegistryEntry("303030", "Absa Bank / KNEC", "Business/Till"),
        PaybillRegistryEntry("880100", "NCBA Bank", "Business/Till"),
        PaybillRegistryEntry("329329", "Standard Chartered", "Business/Till"),
        PaybillRegistryEntry("222111", "Family Bank", "Business/Till"),
        PaybillRegistryEntry("552100", "HF Group", "Business/Till"),
        PaybillRegistryEntry("912200", "NCBA Loop", "Business/Till"),
        PaybillRegistryEntry("516600", "DTB Bank", "Business/Till"),
        PaybillRegistryEntry("600100", "Stanbic Bank", "Business/Till"),

        // Insurance
        PaybillRegistryEntry("223300", "Britam Insurance", "Business/Till"),
        PaybillRegistryEntry("510300", "Jubilee Insurance", "Business/Till"),
        PaybillRegistryEntry("327600", "UAP Old Mutual", "Business/Till"),
        PaybillRegistryEntry("911911", "AAR Insurance", "Business/Till"),

        // Government / NTSA / KRA
        PaybillRegistryEntry("222222", "eCitizen", "Business/Till"),
        PaybillRegistryEntry("572572", "NTSA", "Business/Till"),
        PaybillRegistryEntry("572000", "KRA iTax", "Business/Till"),
        PaybillRegistryEntry("200800", "HELB", "Business/Till"),
        PaybillRegistryEntry("200222", "SHA / NHIF", "Business/Till"),

        // Hospitals & Healthcare
        PaybillRegistryEntry("800200", "Aga Khan Hospital", "Business/Till"),
        PaybillRegistryEntry("520200", "Nairobi Hospital", "Business/Till"),
        PaybillRegistryEntry("501501", "Kenyatta National Hospital", "Business/Till"),
        PaybillRegistryEntry("215215", "MP Shah Hospital", "Business/Till"),

        // Supermarkets / Retail
        PaybillRegistryEntry("290700", "Naivas Online", "Groceries & Food"),
        PaybillRegistryEntry("851852", "Carrefour Kenya", "Groceries & Food"),

        // Fuel / Petrol Stations
        PaybillRegistryEntry("606060", "Shell / Vivo Energy", "Transport & Fuel"),
        PaybillRegistryEntry("350350", "TotalEnergies", "Transport & Fuel"),
        PaybillRegistryEntry("200200", "Rubis Energy", "Transport & Fuel"),

        // Ride-hailing / Transport
        PaybillRegistryEntry("310300", "Little Cab", "Transport & Fuel"),
        PaybillRegistryEntry("891300", "Bolt Business", "Transport & Fuel"),

        // Education
        PaybillRegistryEntry("500600", "University of Nairobi", "Business/Till"),
        PaybillRegistryEntry("220220", "Strathmore University", "Business/Till"),
        PaybillRegistryEntry("247100", "USIU Africa", "Business/Till"),

        // Telecom / Other
        PaybillRegistryEntry("601600", "Airtel Money", "Airtime & Data"),
        PaybillRegistryEntry("444123", "Telkom Kenya", "Airtime & Data")
    )

    private val codeIndex: Map<String, PaybillRegistryEntry> =
        registryEntries.associateBy { normalizeCode(it.code) }

    private fun normalizeCode(code: String): String = code.trim().replace(" ", "")

    fun getAllRegistryEntries(): List<PaybillRegistryEntry> = registryEntries

    fun lookupCode(code: String?): PaybillRegistryEntry? {
        if (code.isNullOrBlank()) return null
        return codeIndex[normalizeCode(code)]
    }

    fun searchRegistry(query: String, limit: Int = 25): List<PaybillRegistryEntry> {
        val q = query.trim()
        if (q.isEmpty()) return registryEntries.take(limit)
        return registryEntries.filter { entry ->
            entry.code.contains(q, ignoreCase = true) ||
                entry.merchantName.contains(q, ignoreCase = true) ||
                entry.categoryName.contains(q, ignoreCase = true)
        }.take(limit)
    }

    // ── NAME-BASED MAPPINGS ──────────────────────────────────────────────────
    // Partial string matches against recipient names (case-insensitive)

    private val nameMappings = mapOf(
        // Power & Water Utilities
        "KPLC" to "Utilities (Water, Power)",
        "KENYA POWER" to "Utilities (Water, Power)",
        "NAIROBI WATER" to "Utilities (Water, Power)",
        "NWSC" to "Utilities (Water, Power)",
        "MOMBASA WATER" to "Utilities (Water, Power)",
        "RIFT VALLEY WATER" to "Utilities (Water, Power)",
        "KISUMU WATER" to "Utilities (Water, Power)",

        // Internet & Subscriptions
        "ZUKU" to "Subscriptions",
        "FAIBA" to "Subscriptions",
        "SAFARICOM HOME" to "Subscriptions",
        "STARLINK" to "Subscriptions",
        "NETFLIX" to "Subscriptions",
        "DSTV" to "Subscriptions",
        "SHOWMAX" to "Subscriptions",
        "STARTIMES" to "Subscriptions",
        "SPOTIFY" to "Subscriptions",
        "YOUTUBE PREMIUM" to "Subscriptions",
        "CANAL+" to "Subscriptions",

        // Groceries & Supermarkets
        "NAIVAS" to "Groceries & Food",
        "CARREFOUR" to "Groceries & Food",
        "QUICKMART" to "Groceries & Food",
        "CLEANSHELF" to "Groceries & Food",
        "CHANDARANA" to "Groceries & Food",
        "EASTMATT" to "Groceries & Food",
        "MARKET" to "Groceries & Food",
        "TUMAINI" to "Groceries & Food",

        // Restaurants & Food Delivery
        "KFC" to "Groceries & Food",
        "JAVA HOUSE" to "Groceries & Food",
        "ARTCAFE" to "Groceries & Food",
        "STEERS" to "Groceries & Food",
        "GALITO" to "Groceries & Food",
        "CHICKEN INN" to "Groceries & Food",
        "PIZZA INN" to "Groceries & Food",
        "DOMINOS" to "Groceries & Food",
        "SUBWAY" to "Groceries & Food",
        "BURGER KING" to "Groceries & Food",
        "UBER EATS" to "Groceries & Food",
        "GLOVO" to "Groceries & Food",
        "JUMIA FOOD" to "Groceries & Food",
        "SENDY FOOD" to "Groceries & Food",

        // Transport & Fuel
        "UBER" to "Transport & Fuel",
        "BOLT" to "Transport & Fuel",
        "LITTLE CAB" to "Transport & Fuel",
        "LITTLE RIDE" to "Transport & Fuel",
        "FARAS" to "Transport & Fuel",
        "SHELL" to "Transport & Fuel",
        "VIVO ENERGY" to "Transport & Fuel",
        "TOTALENERGIES" to "Transport & Fuel",
        "TOTAL ENERGY" to "Transport & Fuel",
        "RUBIS" to "Transport & Fuel",
        "OIL LIBYA" to "Transport & Fuel",
        "KENOL" to "Transport & Fuel",
        "PETRO" to "Transport & Fuel",
        "NATIONAL OIL" to "Transport & Fuel",
        "GALANA" to "Transport & Fuel",
        "CALTEX" to "Transport & Fuel",
        "KOBIL" to "Transport & Fuel",

        // Airtime & Data
        "Safaricom Airtime/Data" to "Airtime & Data", // Parser hardcoded name
        "SAFARICOM DATA" to "Airtime & Data",
        "AIRTEL" to "Airtime & Data",
        "TELKOM" to "Airtime & Data",

        // Fuliza (handled specially — DEBT not spending, but still categorize)
        "Fuliza M-PESA" to "Uncategorized",
        "Fuliza Repayment" to "Uncategorized",

        // Banking & Financial Services
        "EQUITY" to "Business/Till",
        "KCB" to "Business/Till",
        "M-SHWARI" to "Business/Till",
        "MSHWARI" to "Business/Till",
        "COOPERATIVE" to "Business/Till",
        "CO-OP BANK" to "Business/Till",
        "STANCHART" to "Business/Till",
        "STANDARD CHARTERED" to "Business/Till",
        "FAMILY BANK" to "Business/Till",
        "ABSA" to "Business/Till",
        "NCBA" to "Business/Till",
        "DTB" to "Business/Till",
        "I&M BANK" to "Business/Till",
        "SIDIAN" to "Business/Till",
        "SPIRE BANK" to "Business/Till",
        "EAZZY" to "Business/Till",

        // Rent & Housing
        "RENT" to "Rent & Housing",
        "LANDLORD" to "Rent & Housing",
        "CARETAKER" to "Rent & Housing",

        // Government & Education
        "ECITIZEN" to "Business/Till",
        "E-CITIZEN" to "Business/Till",
        "NTSA" to "Business/Till",
        "KRA" to "Business/Till",
        "HELB" to "Business/Till",
        "NHIF" to "Business/Till",
        "NSSF" to "Business/Till",

        // Hospitals & Pharmacy
        "HOSPITAL" to "Business/Till",
        "PHARMACY" to "Business/Till",
        "CLINIC" to "Business/Till",
        "DAWA" to "Business/Till",
        "GOODLIFE" to "Business/Till",
        "HALTONS" to "Business/Till",

        // M-Pesa Fees category (handled by TransactionType, but in case of named entries)
        "MPESA FEES" to "Mpesa Fees"
    )

    /**
     * Returns the category name for the given recipient name and/or Paybill/Till number.
     * Checks Paybill/Till registry first (exact match or inline code), then exact/partial name.
     */
    fun getCategoryForRecipient(recipientName: String, recipientNumber: String?): String? {
        // 1. Exact Paybill/Till number match (fastest, most reliable)
        lookupCode(recipientNumber)?.let { return it.categoryName }

        // 1b. Inline 5-8 digit business code embedded in recipient text (e.g. "888888 - KPLC")
        Regex("""\b(\d{5,8})\b""").find(recipientName)?.groupValues?.getOrNull(1)?.let { embeddedCode ->
            lookupCode(embeddedCode)?.let { return it.categoryName }
        }

        val upperName = recipientName.uppercase()

        // 2. Exact name match
        val exactMatch = nameMappings[recipientName] ?: nameMappings[upperName]
        if (exactMatch != null) return exactMatch

        // 3. Partial name match (e.g. "NAIVAS RIVERSIDE" → matches "NAIVAS")
        for ((key, category) in nameMappings) {
            if (upperName.contains(key.uppercase())) {
                return category
            }
        }

        return null
    }
}
