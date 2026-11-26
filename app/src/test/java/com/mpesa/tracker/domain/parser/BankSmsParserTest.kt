package com.mpesa.tracker.domain.parser

import com.mpesa.tracker.data.local.entities.TransactionNature
import com.mpesa.tracker.domain.parser.BankSmsParser.parseMessage
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class BankSmsParserTest {

    @Test
    fun `parses real IM Bank to M-PESA transfer with both ref IDs`() {
        val sms = "Bank to M-PESA transfer of KES 952.00 to 0741947264 - HARRISON WEKESA WANJALA successfully processed. Transaction Ref ID: 5938XWRV8180. M-PESA Ref ID: UKJIR2TE9Z."
        val result = parseMessage("IMBANK", sms, 1700000000000L)

        assertNotNull(result)
        assertEquals("5938XWRV8180", result!!.referenceNumber)
        assertEquals("UKJIR2TE9Z", result.linkedMpesaRef)
        assertEquals(952.0, result.amount, 0.01)
        assertEquals("0741947264", result.recipientNumber)
        assertEquals("HARRISON WEKESA WANJALA", result.recipientName)
        assertFalse(result.isIncome)
        assertTrue(result.isTransferCandidate)
        assertEquals(AccountSource.IM_BANK, result.source)
    }

    @Test
    fun `parses real IM Bank PesaLink received SMS without decimals or balance`() {
        val sms = "Dear Customer,You have received KES 4500 via PesaLink into Acc 02805688956150 Tran Ref 000300572026100914392867687919. For enquiry,call 020 3221000. IM Bank."
        val result = parseMessage("IM Bank", sms, 1700000000000L)

        assertNotNull(result)
        assertTrue(result!!.isIncome)
        assertEquals(4500.0, result.amount, 0.01)
        assertEquals("000300572026100914392867687919", result.referenceNumber)
        assertEquals("02805688956150", result.recipientNumber)
        assertEquals(AccountSource.IM_BANK, result.source)
        assertEquals(TransactionNature.INCOME, result.natureHint)
    }

    @Test
    fun `parses real Absa Credit and Debit with deterministic idempotent refs`() {
        val ts = 1795596720000L
        val creditSms = "Dear Customer a Credit has been made for KES4600 on your Account XX3859 VIA Digital/Branch Banking please call 0722130120 for any enquiries"
        val credit1 = parseMessage("ABSA", creditSms, ts)
        val credit2 = parseMessage("ABSA", creditSms, ts + 5_000L) // Same minute bucket

        assertNotNull(credit1)
        assertTrue(credit1!!.isIncome)
        assertEquals(4600.0, credit1.amount, 0.01)
        assertEquals("XX3859", credit1.recipientNumber)
        assertEquals(AccountSource.ABSA, credit1.source)
        assertEquals("Idempotent ref must match across resyncs", credit1.referenceNumber, credit2!!.referenceNumber)

        val debitSms = "Dear Customer a Debit has been made for KES80 on your Account XX3859 VIA Digital/Branch Banking please call 0722130120 for any enquiries"
        val debit = parseMessage("ABSA", debitSms, ts)
        assertNotNull(debit)
        assertFalse(debit!!.isIncome)
        assertEquals(80.0, debit.amount, 0.01)
        assertEquals(AccountSource.ABSA, debit.source)
        assertNotEquals(credit1.referenceNumber, debit.referenceNumber)
    }

    @Test
    fun `flags transient awaiting MPESA notice without inserting duplicate`() {
        val sms = "HARRISON WEKESA WANJALA has transferred KES80 to your MPESA ref: UJ9IR9NRDY. Please await MPESA notification."
        val result = parseMessage("ABSA", sms, 1795596720000L)

        assertNotNull(result)
        assertTrue(result!!.isTransientNotice)
        assertEquals("UJ9IR9NRDY", result.linkedMpesaRef)
        assertEquals("HARRISON WEKESA WANJALA", result.selfHintName)
    }

    @Test
    fun `parses MPESA payment accepted bank gateway confirmation`() {
        val sms = "MPESA payment accepted.  Cust Ref: 0741947264 Amt KES: 80 Trans ID: 17915569558177 MPESA Ref: UJ9IR9NRDY Recipient: 0741947264-HARRISON WEKESA WANJALA"
        val result = parseMessage("ABSA", sms, 1795596720000L)

        assertNotNull(result)
        assertEquals("17915569558177", result!!.referenceNumber)
        assertEquals("UJ9IR9NRDY", result.linkedMpesaRef)
        assertEquals(80.0, result.amount, 0.01)
        assertEquals("0741947264", result.recipientNumber)
        assertEquals("HARRISON WEKESA WANJALA", result.recipientName)
        assertTrue(result.isTransferCandidate)
    }

    @Test
    fun `parses real Equity Till payments with KES dot and 24h Nairobi date`() {
        val sms1 = "Confirmed. Payment of KES. 50.00 to JONICE ENT Till No. 0711813785 has been received. Ref. UKGIR2F6XR on 16-11-2026 at 07:55. Thank you."
        val r1 = parseMessage("EQUITY", sms1, 1700000000000L)

        assertNotNull(r1)
        assertEquals("UKGIR2F6XR", r1!!.referenceNumber)
        assertEquals(50.0, r1.amount, 0.01)
        assertEquals("JONICE ENT", r1.recipientName)
        assertEquals("0711813785", r1.recipientNumber)
        assertFalse(r1.isIncome)
        assertEquals(TransactionNature.EXPENSE, r1.natureHint)

        val cal = Calendar.getInstance(TimeZone.getTimeZone("Africa/Nairobi")).apply {
            timeInMillis = r1.timestamp
        }
        assertEquals(2026, cal.get(Calendar.YEAR))
        assertEquals(10, cal.get(Calendar.MONTH)) // November (0-indexed)
        assertEquals(16, cal.get(Calendar.DAY_OF_MONTH))
        assertEquals(7, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(55, cal.get(Calendar.MINUTE))

        val sms2 = "Confirmed. Payment of KES. 280.00 to BOUNTIFUL AGRO SUPPLIES Till No. 0717137404 has been received. Ref. UK7IR1ASKZ on 07-11-2026 at 12:21. Thank you."
        val r2 = parseMessage("EquityBank", sms2, 1700000000000L)
        assertNotNull(r2)
        assertEquals("UK7IR1ASKZ", r2!!.referenceNumber)
        assertEquals(280.0, r2.amount, 0.01)
        assertEquals("BOUNTIFUL AGRO SUPPLIES", r2.recipientName)
    }

    @Test
    fun `parses Equity sent and received and KCB and Co-op and NCBA`() {
        val eqSms = "Confirmed. Ksh1,500.00 sent to JOHN DOE 0712345678 on 25/11/2026 at 10:00. Eazzy ref.EQTY12345. Your available balance is Ksh 23,450.00."
        val eq = parseMessage("EQUITY", eqSms, 1700000000000L)
        assertNotNull(eq)
        assertEquals(1500.0, eq!!.amount, 0.01)
        assertEquals(23450.0, eq.balance!!, 0.01)

        val coopSms = "MCo-op Cash:Ksh 2,500.00 received from JANE WANJIKU on 25/11/2026. TXN:COOP654321. Bal:Ksh 10,950.00"
        val coop = parseMessage("COOPBANK", coopSms, 1700000000000L)
        assertNotNull(coop)
        assertEquals("JANE WANJIKU", coop!!.recipientName)
        assertEquals(2500.0, coop.amount, 0.01)
    }

    @Test
    fun `sender normalization matches carrier variations safely`() {
        assertFalse(BankSmsParser.isBankSender("MPESA"))
        assertFalse(BankSmsParser.isBankSender("M-PESA"))
        assertTrue(BankSmsParser.isBankSender("I&M Bank"))
        assertTrue(BankSmsParser.isBankSender("IM Bank"))
        assertTrue(BankSmsParser.isBankSender("AbsaKenya"))
        assertTrue(BankSmsParser.isBankSender("EquityBank"))
    }

    @Test
    fun `body fallback detects carrier shortcode bank SMS and ignores await notice in MpesaParser`() {
        val bankToMpesa = "Bank to M-PESA transfer of KES 952.00 to 0741947264 - HARRISON WEKESA WANJALA successfully processed. Transaction Ref ID: 5938XWRV8180. M-PESA Ref ID: UKJIR2TE9Z."
        val absaDebit = "Dear Customer a Debit has been made for KES80 on your Account XX3859 VIA Digital/Branch Banking please call 0722130120 for any enquiries"
        val awaitNotice = "HARRISON WEKESA WANJALA has transferred KES80 to your MPESA ref: UJ9IR9NRDY. Please await MPESA notification."
        val tillNoDecimals = "Confirmed. Payment of KES 150 to JONICE ENT Till No. 0711813785 has been received. Ref. UKGIR2F999 on 16-11-2026 at 09:10. Thank you."

        assertTrue(BankSmsParser.looksLikeSupportedBankSms("20123", bankToMpesa))
        assertTrue(BankSmsParser.looksLikeSupportedBankSms("22222", absaDebit))
        assertTrue(BankSmsParser.looksLikeSupportedBankSms("22222", awaitNotice))
        assertNull(MpesaParser.parseMessage(awaitNotice))

        val tillParsed = MpesaParser.parseMessage(tillNoDecimals)
        assertNotNull(tillParsed)
        assertEquals(150.0, tillParsed!!.amount, 0.01)
        assertEquals("UKGIR2F999", tillParsed.receiptNumber)
    }
}
