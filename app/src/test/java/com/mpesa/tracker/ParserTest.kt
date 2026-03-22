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

    @Test
    fun testParseFulizaFullyPaid() {
        val sms = "UCMIRA49P4 Confirmed. Ksh 1981.21 from your M-PESA has been used to fully pay your outstanding Fuliza M-PESA. Available Fuliza M-PESA limit is Ksh 2000.00. Your M-PESA balance is 868.79."
        val result = MpesaParser.parseMessage(sms, 1710863000000L)
        
        assertNotNull("Result should not be null", result)
        assertEquals(1981.21, result?.amount)
        assertEquals(868.79, result?.balance)
    }

    @Test
    fun testParseReceivedFromBankWithHyphen() {
        val sms = "UCMIRA46Y3 Confirmed. You have received Ksh2,850.00 from IM BANK LIMITED- APP on 22/3/26 at 10:48 AM. New M-PESA balance is Ksh2,850.00. Buy goods with M-PESA."
        val result = MpesaParser.parseMessage(sms, 1710863000000L)
        
        assertNotNull("Result should not be null", result)
        assertEquals(2850.00, result?.amount)
        assertEquals(2850.00, result?.balance)
        assertEquals("IM BANK LIMITED- APP", result?.recipientName)
    }
}
