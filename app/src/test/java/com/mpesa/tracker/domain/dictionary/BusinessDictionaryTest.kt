package com.mpesa.tracker.domain.dictionary

import org.junit.Assert.*
import org.junit.Test

class BusinessDictionaryTest {

    @Test
    fun `lookupCode resolves major Kenyan Paybills cleanly`() {
        val kplc = BusinessDictionary.lookupCode("888888")
        assertNotNull(kplc)
        assertEquals("KPLC Prepaid", kplc!!.merchantName)
        assertEquals("Utilities (Water, Power)", kplc.categoryName)

        val imBank = BusinessDictionary.lookupCode(" 542542 ")
        assertNotNull(imBank)
        assertEquals("I&M Bank", imBank!!.merchantName)
        assertEquals("Business/Till", imBank.categoryName)

        val equity = BusinessDictionary.lookupCode("247247")
        assertNotNull(equity)
        assertEquals("Equity Bank (Eazzy)", equity!!.merchantName)
    }

    @Test
    fun `getCategoryForRecipient prioritizes Paybill code and extracts embedded shortcodes`() {
        assertEquals(
            "Utilities (Water, Power)",
            BusinessDictionary.getCategoryForRecipient("UNKNOWN VENDOR", "888888")
        )
        assertEquals(
            "Subscriptions",
            BusinessDictionary.getCategoryForRecipient("200999 - ZUKU WANANCHI", null)
        )
        assertEquals(
            "Groceries & Food",
            BusinessDictionary.getCategoryForRecipient("NAIVAS RIVERSIDE", null)
        )
    }

    @Test
    fun `searchRegistry matches by code merchant or category`() {
        val kplcMatches = BusinessDictionary.searchRegistry("kplc")
        assertTrue(kplcMatches.any { it.code == "888888" })

        val codeMatches = BusinessDictionary.searchRegistry("2907")
        assertTrue(codeMatches.any { it.merchantName.contains("Naivas", ignoreCase = true) })
    }
}
