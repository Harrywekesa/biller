package com.mpesa.tracker

import com.mpesa.tracker.domain.parser.MpesaParser
import org.junit.Test
import org.junit.Assert.*

class ParserTest {
    @Test
    fun testParseFuliza() {
        val sms = "UCJIR9UCU4 Confirmed. Fuliza M-PESA amount is Ksh 30.00. Access Fee charged Ksh 0.30. Total Fuliza M-PESA outstanding amount is Ksh1372.23 due on 17/04/26. To check daily charges, Dial *334#OK Select Query Charges"
        val result = MpesaParser.parseMessage(sms, 1710863000000L) // Some mock timestamp
        
        assertNotNull("Result should not be null", result)
        assertEquals(1372.23, result?.balance)
        println("MATCHED: " + result?.balance)
    }
}
