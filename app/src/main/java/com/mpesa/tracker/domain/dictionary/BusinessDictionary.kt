package com.mpesa.tracker.domain.dictionary

/**
 * A comprehensive dictionary of Kenyan M-Pesa merchants, Paybill numbers, and Till numbers
 * mapped to their spending categories.
 *
 * Category names MUST match exactly what is seeded in AppDatabaseCallback.starterCategories.
 * Current valid categories:
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

    // ── PAYBILL NUMBERS ──────────────────────────────────────────────────────
    // Source: Safaricom official Paybill directory + common Kenyan services

    private val paybillMappings = mapOf(
        // Utilities — Power
        "888888" to "Utilities (Water, Power)", // KPLC Prepaid
        "888880" to "Utilities (Water, Power)", // KPLC Postpaid
        "880100" to "Utilities (Water, Power)", // Kenya Power (alternative)

        // Utilities — Water
        "247247" to "Utilities (Water, Power)", // Nairobi Water & Sewerage
        "444400" to "Utilities (Water, Power)", // Mombasa Water
        "326326" to "Utilities (Water, Power)", // NWSC Water

        // Utilities — Internet / TV
        "200999" to "Subscriptions",  // Zuku Fiber
        "400400" to "Subscriptions",  // Faiba Home Internet (JTL)
        "325325" to "Subscriptions",  // DSTV
        "290290" to "Subscriptions",  // Showmax
        "249249" to "Subscriptions",  // StarTimes

        // Safaricom Services
        "999991" to "Airtime & Data", // Safaricom Postpay
        "333300" to "Airtime & Data", // Safaricom Data Bundles Paybill
        "272727" to "Airtime & Data", // Safaricom Home Fibre

        // Banking & Financial
        "522522" to "Business/Till",  // Equity Bank
        "100100" to "Business/Till",  // KCB Bank
        "522200" to "Business/Till",  // Standard Chartered
        "700800" to "Business/Till",  // Cooperative Bank
        "329329" to "Business/Till",  // Family Bank
        "400200" to "Business/Till",  // M-Shwari (KCB-MPESA)
        "552100" to "Business/Till",  // HF Group
        "912200" to "Business/Till",  // NCBA Loop

        // Insurance
        "223300" to "Business/Till",  // Britam Insurance
        "510300" to "Business/Till",  // Jubilee Insurance
        "327600" to "Business/Till",  // UAP Insurance
        "911911" to "Business/Till",  // AAR Insurance

        // Government / NTSA / KRA
        "222222" to "Business/Till",  // eCitizen (Government services)
        "572572" to "Business/Till",  // NTSA (Driving licence, etc.)
        "572000" to "Business/Till",  // KRA (Tax payments)
        "000000" to "Business/Till",  // HELB (Student loans)

        // Hospitals
        "800200" to "Business/Till",  // Aga Khan Hospital
        "520200" to "Business/Till",  // Nairobi Hospital
        "501501" to "Business/Till",  // Kenyatta National Hospital
        "215215" to "Business/Till",  // MP Shah Hospital

        // Supermarkets / Retail
        "290700" to "Groceries & Food", // Naivas Online
        "851852" to "Groceries & Food", // Carrefour

        // Fuel / Petrol Stations (Paybill)
        "606060" to "Transport & Fuel", // Shell/Vivo Energy Pay
        "350350" to "Transport & Fuel", // TotalEnergies Paybill
        "200200" to "Transport & Fuel", // Rubis Energy Paybill

        // Ride-hailing / Transport
        "310300" to "Transport & Fuel", // Little Cab Pay
        "891300" to "Transport & Fuel", // Bolt Business

        // Education
        "303030" to "Business/Till",  // KNEC (Exam fees)
        "500600" to "Business/Till",  // University of Nairobi fees
        "220220" to "Business/Till",  // Strathmore University
        "247100" to "Business/Till",  // USIU Africa

        // Airtime Top-Up (Rival networks)
        "601600" to "Airtime & Data", // Airtel Money Transfer
        "444123" to "Airtime & Data"  // Telkom Kenya
    )

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
     * Returns the category name for the given recipient name and/or number.
     * Checks Paybill number first (exact match), then name (partial, case-insensitive).
     */
    fun getCategoryForRecipient(recipientName: String, recipientNumber: String?): String? {
        // 1. Exact Paybill/Till number match (fastest, most reliable)
        if (recipientNumber != null) {
            val paybillMatch = paybillMappings[recipientNumber]
            if (paybillMatch != null) return paybillMatch
        }

        val upperName = recipientName.uppercase()

        // 2. Exact name match
        val exactMatch = nameMappings[recipientName]
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
