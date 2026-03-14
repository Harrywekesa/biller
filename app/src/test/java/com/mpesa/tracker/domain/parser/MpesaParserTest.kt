package com.mpesa.tracker.domain.parser

import com.mpesa.tracker.data.local.entities.TransactionType
import org.junit.Assert.*
import org.junit.Test

class MpesaParserTest {

    @Test
    fun `test valid Paybill message parsing`() {
        val sms = "OJM12ABCD34 Confirmed. Ksh500.00 paid to KPLC PREPAID. on 12/4/23 at 10:30 AM.New M-PESA balance is Ksh14,500.20. Transaction cost, Ksh23.00."
        val result = MpesaParser.parseMessage(sms)
        
        assertNotNull(result)
        assertEquals("OJM12ABCD34", result?.receiptNumber)
        assertEquals(500.00, result?.amount)
        assertEquals(TransactionType.PAYBILL, result?.type)
        assertEquals("KPLC PREPAID", result?.recipientName)
        assertEquals(14500.20, result?.balance)
        assertEquals(23.00, result?.transactionCost)
    }

    @Test
    fun `test valid Buy Goods message parsing`() {
        // Buy Goods often has the exact same format as Paybill in the raw SMS text, just with a till number usually missing.
        val sms = "OJM12ABCD35 Confirmed. Ksh1,250.00 paid to NAIVAS RIVERSIDE. on 13/4/23 at 6:45 PM.New M-PESA balance is Ksh13,250.20. Transaction cost, Ksh0.00."
        val result = MpesaParser.parseMessage(sms)
        
        assertNotNull(result)
        assertEquals("OJM12ABCD35", result?.receiptNumber)
        assertEquals(1250.00, result?.amount)
        assertEquals("NAIVAS RIVERSIDE", result?.recipientName)
    }

    @Test
    fun `test valid Send Money message parsing`() {
        val sms = "OJM12ABCD36 Confirmed. Ksh300.00 sent to JOHN DOE 0712345678 on 14/4/23 at 9:00 AM.New M-PESA balance is Ksh12,950.20. Transaction cost, Ksh12.00."
        val result = MpesaParser.parseMessage(sms)
        
        assertNotNull(result)
        assertEquals("OJM12ABCD36", result?.receiptNumber)
        assertEquals(300.00, result?.amount)
        assertEquals(TransactionType.SEND_MONEY, result?.type)
        assertEquals("JOHN DOE", result?.recipientName)
        assertEquals("0712345678", result?.recipientNumber)
    }

    @Test
    fun `test valid Buy Airtime message parsing`() {
        val sms = "OJM12ABCD37 Confirmed. You bought Ksh100.00 of airtime on 15/4/23 at 11:20 AM.New M-PESA balance is Ksh12,850.20."
        val result = MpesaParser.parseMessage(sms)
        
        assertNotNull(result)
        assertEquals("OJM12ABCD37", result?.receiptNumber)
        assertEquals(100.00, result?.amount)
        assertEquals(TransactionType.BUY_AIRTIME, result?.type)
        assertEquals("Safaricom Airtime/Data", result?.recipientName)
        assertEquals(0.0, result?.transactionCost) // Airtime has no fee
    }

    @Test
    fun `test valid Received Money message parsing`() {
        val sms = "QJM12ABCD34 Confirmed. You have received Ksh500.00 from Jane Doe 0712345678 on 12/04/23 at 10:30 AM. New M-PESA balance is Ksh1,500.00."
        val result = MpesaParser.parseMessage(sms)
        assertNotNull(result)
        assertEquals("QJM12ABCD34", result?.receiptNumber)
        assertEquals(500.0, result?.amount)
        assertEquals("Jane Doe", result?.recipientName)
        assertEquals("0712345678", result?.recipientNumber)
        assertEquals(TransactionType.RECEIVED_MONEY, result?.type)
        assertEquals(1500.0, result?.balance)
    }

    @Test
    fun `test valid Withdraw Cash message parsing`() {
        val sms = "QJM12ABCD34 Confirmed. on 12/04/23 at 10:30 AMWithdraw Ksh500.00 from 123456 - AGENT NAME. New M-PESA balance is Ksh1,000.00. Transaction cost, Ksh28.00."
        val result = MpesaParser.parseMessage(sms)
        assertNotNull(result)
        assertEquals("QJM12ABCD34", result?.receiptNumber)
        assertEquals(500.0, result?.amount)
        assertEquals("123456 - AGENT NAME", result?.recipientName)
        assertEquals(TransactionType.WITHDRAW_CASH, result?.type)
        assertEquals(28.0, result?.transactionCost)
    }

    @Test
    fun `test parsing fails on non-mpesa message`() {
        val result = MpesaParser.parseMessage("Hey bro, are we still meeting for lunch today at 1pm?")
        assertNull(result)
    }

    @Test
    fun `test parsing fails on promo message`() {
        val result = MpesaParser.parseMessage("Get 1GB Data valid for 3 days for only Ksh99! Dial *544# to accept.")
        assertNull(result)
    }
}
