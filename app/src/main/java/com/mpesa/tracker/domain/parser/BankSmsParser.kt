package com.mpesa.tracker.domain.parser

import android.util.Log
import com.mpesa.tracker.data.local.entities.TransactionNature
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * V2 Bank SMS parser covering Kenyan institutions including Equity, I&M Bank,
 * Absa Bank, KCB, Co-op, NCBA, Stanbic, DTB, Family Bank — tested against real
 * bank alerts (PesaLink, Bank-to-M-Pesa, Digital/Branch Debit & Credit, Equity Till).
 *
 * Refless SMS use a deterministic SHA-256 fingerprint instead of random UUIDs so
 * repeated syncs remain strictly idempotent.
 */
object BankSmsParser {

    private const val TAG = "BankSmsParser"

    private val KNOWN_SENDERS_NORMALIZED = setOf(
        // Equity
        "EQUITY", "EQUITYBANK", "EAZZY", "EAZZYCASH",
        // I&M Bank
        "IM", "IMBANK", "I&MBANK", "IMBANKLIMITED",
        // Absa
        "ABSA", "ABSABANK", "ABSAKENYA",
        // KCB
        "KCB", "KCBBANK", "VOOMA", "VOOMABYKCB",
        // Co-op
        "COOP", "COOPBANK", "MCOOP", "MCOOPBK", "MCOOPCASH",
        // NCBA
        "NCBA", "NCBABANK", "NCBALOOP",
        // Other Kenyan banks
        "STANBIC", "STANBICBANK", "DTB", "DTBBANK", "FAMILYBANK", "STANCHART"
    )

    /** SQL substrings used by [SmsSyncService] for case-insensitive SMS inbox queries. */
    val INBOX_SENDER_TOKENS = listOf(
        "EQUITY", "EAZZY", "ABSA", "IMBANK", "IM BANK", "I&M", "KCB", "VOOMA",
        "COOP", "NCBA", "STANBIC", "DTB", "FAMILY"
    )

    private fun normalizeSender(sender: String): String =
        sender.trim().uppercase().replace(Regex("[^A-Z0-9&]"), "")

    fun isBankSender(sender: String): Boolean {
        val norm = normalizeSender(sender)
        if (norm.isEmpty() || norm == "MPESA") return false
        if (norm in KNOWN_SENDERS_NORMALIZED) return true
        return INBOX_SENDER_TOKENS.any { token ->
            val cleanToken = token.replace(Regex("[^A-Z0-9&]"), "")
            norm.contains(cleanToken)
        }
    }

    /** Also allows body-signature detection when carriers mask sender as shortcode/text. */
    fun looksLikeSupportedBankSms(sender: String, body: String): Boolean {
        if (isBankSender(sender)) return true
        val normSender = normalizeSender(sender)
        if (normSender == "MPESA") return false
        return body.contains("IM Bank", ignoreCase = true) ||
            body.contains("Account XX", ignoreCase = true) ||
            body.contains("Eazzy ref", ignoreCase = true) ||
            body.contains("M-PESA Ref ID:", ignoreCase = true) ||
            body.contains("MPESA payment accepted", ignoreCase = true) ||
            body.contains("Please await MPESA notification", ignoreCase = true) ||
            tillConfirmationPattern.containsMatchIn(body)
    }

    fun detectSource(sender: String, body: String = ""): AccountSource {
        val norm = normalizeSender(sender)
        return when {
            norm.contains("EQUITY") || norm.contains("EAZZY") || body.contains("Eazzy", ignoreCase = true) ||
                tillConfirmationPattern.containsMatchIn(body) -> AccountSource.EQUITY
            norm.contains("IMBANK") || norm.contains("I&M") || norm == "IM" ||
                body.contains("IM Bank", ignoreCase = true) -> AccountSource.IM_BANK
            norm.contains("ABSA") || body.contains("Account XX", ignoreCase = true) &&
                body.contains("Digital/Branch Banking", ignoreCase = true) -> AccountSource.ABSA
            norm.contains("KCB") || norm.contains("VOOMA") -> AccountSource.KCB
            norm.contains("COOP") || norm.contains("MCOOP") -> AccountSource.COOP
            norm.contains("NCBA") -> AccountSource.NCBA
            norm.contains("STANBIC") -> AccountSource.STANBIC
            norm.contains("DTB") -> AccountSource.DTB
            norm.contains("FAMILY") -> AccountSource.FAMILY_BANK
            else -> AccountSource.UNKNOWN_BANK
        }
    }

    fun parseMessage(sender: String, body: String, smsDate: Long): ParsedBankResult? {
        val cleanBody = body.replace("\n", " ").replace(Regex("\\s+"), " ").trim()
        val source = detectSource(sender, cleanBody)

        // 1. Cross-bank/gateway awaiting notice:
        //    "HARRISON WEKESA WANJALA has transferred KES80 to your MPESA ref: UJ9IR9NRDY. Please await MPESA notification."
        awaitingMpesaNoticePattern.find(cleanBody)?.let { m ->
            return ParsedBankResult(
                referenceNumber = "NOTICE-${m.groupValues[3]}",
                isIncome = true,
                amount = m.groupValues[2].replace(",", "").toDoubleOrNull() ?: 0.0,
                recipientName = m.groupValues[1].trim(),
                rawSms = body,
                timestamp = smsDate,
                source = source,
                linkedMpesaRef = m.groupValues[3].trim(),
                isTransientNotice = true,
                selfHintName = m.groupValues[1].trim(),
                isTransferCandidate = true
            )
        }

        // 2. Bank-to-M-PESA transfer with Transaction Ref + M-PESA Ref ID (I&M / mobile banking):
        //    "Bank to M-PESA transfer of KES 952.00 to 0741947264 - HARRISON WEKESA WANJALA successfully processed. Transaction Ref ID: 5938XWRV8180. M-PESA Ref ID: UKJIR2TE9Z."
        bankToMpesaPattern.find(cleanBody)?.let { m ->
            val phone = m.groupValues[2].trim()
            val name = m.groupValues[3].trim()
            return ParsedBankResult(
                referenceNumber = m.groupValues[4].trim(),
                isIncome = false,
                amount = m.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null,
                recipientName = name,
                recipientNumber = phone,
                rawSms = body,
                timestamp = smsDate,
                source = if (source == AccountSource.UNKNOWN_BANK) AccountSource.IM_BANK else source,
                linkedMpesaRef = m.groupValues[5].trim(),
                isTransferCandidate = true
            )
        }

        // 3. Gateway "MPESA payment accepted" confirmation (Absa / Bank -> M-Pesa):
        //    "MPESA payment accepted. Cust Ref: 0741947264 Amt KES: 80 Trans ID: 17915569558177 MPESA Ref: UJ9IR9NRDY Recipient: 0741947264-HARRISON WEKESA WANJALA"
        mpesaAcceptedPattern.find(cleanBody)?.let { m ->
            val custRef = m.groupValues[1].trim()
            val amount = m.groupValues[2].replace(",", "").toDoubleOrNull() ?: return null
            val transId = m.groupValues[3].trim()
            val mpesaRef = m.groupValues[4].trim()
            val rawRecipient = m.groupValues[5].trim()
            val cleanName = rawRecipient.substringAfter("-", rawRecipient).trim().ifBlank { rawRecipient }
            return ParsedBankResult(
                referenceNumber = transId,
                isIncome = false,
                amount = amount,
                recipientName = cleanName,
                recipientNumber = custRef,
                rawSms = body,
                timestamp = smsDate,
                source = if (source == AccountSource.UNKNOWN_BANK) AccountSource.ABSA else source,
                linkedMpesaRef = mpesaRef,
                isTransferCandidate = true
            )
        }

        // 4. Equity / Till payment confirmation:
        //    "Confirmed. Payment of KES. 50.00 to JONICE ENT Till No. 0711813785 has been received. Ref. UKGIR2F6XR on 16-11-2026 at 07:55. Thank you."
        tillConfirmationPattern.find(cleanBody)?.let { m ->
            val parsedTime = parseDashDate24h(m.groupValues[5].trim()) ?: smsDate
            return ParsedBankResult(
                referenceNumber = m.groupValues[4].trim(),
                isIncome = false,
                amount = m.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null,
                recipientName = m.groupValues[2].trim(),
                recipientNumber = m.groupValues[3].trim(),
                rawSms = body,
                timestamp = parsedTime,
                source = AccountSource.EQUITY,
                natureHint = TransactionNature.EXPENSE
            )
        }

        // 5. PesaLink received (I&M Bank / others):
        //    "Dear Customer,You have received KES 4500 via PesaLink into Acc 02805688956150 Tran Ref 000300572026100914392867687919. For enquiry,call 020 3221000. IM Bank."
        pesaLinkReceivedPattern.find(cleanBody)?.let { m ->
            val acc = m.groupValues[2].trim()
            val maskedAcc = if (acc.length > 6) "${acc.take(4)}...${acc.takeLast(4)}" else acc
            return ParsedBankResult(
                referenceNumber = m.groupValues[3].trim(),
                isIncome = true,
                amount = m.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null,
                recipientName = "PesaLink · Acc $maskedAcc",
                recipientNumber = acc,
                rawSms = body,
                timestamp = smsDate,
                source = if (source == AccountSource.UNKNOWN_BANK) AccountSource.IM_BANK else source,
                natureHint = TransactionNature.INCOME
            )
        }

        // 6. Absa Credit / Debit ("Dear Customer a Credit/Debit has been made for KES4600 on your Account XX3859 VIA Digital/Branch Banking...")
        absaCreditDebitPattern.find(cleanBody)?.let { m ->
            val isCredit = m.groupValues[1].equals("Credit", ignoreCase = true)
            val amount = m.groupValues[2].replace(",", "").toDoubleOrNull() ?: return null
            val accountMask = m.groupValues[3].trim()
            val channel = m.groupValues[4].trim().ifBlank { "Banking" }
            val resolvedSource = if (source == AccountSource.UNKNOWN_BANK) AccountSource.ABSA else source
            return ParsedBankResult(
                referenceNumber = deterministicRef(resolvedSource, cleanBody, smsDate),
                isIncome = isCredit,
                amount = amount,
                recipientName = "${resolvedSource.shortLabel} $accountMask · $channel",
                recipientNumber = accountMask,
                rawSms = body,
                timestamp = smsDate,
                source = resolvedSource,
                natureHint = if (isCredit) TransactionNature.INCOME else TransactionNature.EXPENSE
            )
        }

        // 7. Institution-specific fallbacks
        return when (source) {
            AccountSource.EQUITY -> parseEquity(cleanBody, smsDate)
            AccountSource.KCB -> parseKcb(cleanBody, smsDate)
            AccountSource.COOP -> parseCoop(cleanBody, smsDate)
            AccountSource.NCBA -> parseNcba(cleanBody, smsDate)
            else -> parseGenericDebitCredit(cleanBody, smsDate, source)
        }?.copy(source = source)
    }

    // ─── Cross-Bank / Real Sample Patterns ───────────────────────────────────
    private val awaitingMpesaNoticePattern = Regex(
        """^([A-Z0-9 .'-]+?)\s+has\s+transferred\s+(?:Ksh|KES)\.?\s*([\d,]+(?:\.\d{1,2})?)\s+to\s+your\s+MPESA\s+ref:\s*([A-Z0-9]+)\.\s*Please\s+await\s+MPESA\s+notification""",
        RegexOption.IGNORE_CASE
    )

    private val bankToMpesaPattern = Regex(
        """Bank\s+to\s+M-PESA\s+transfer\s+of\s+(?:KES|Ksh)\.?\s*([\d,]+(?:\.\d{1,2})?)\s+to\s+(\d{9,13})\s*-\s*([A-Z0-9 .'-]+?)\s+successfully\s+processed\.\s*Transaction\s+Ref\s+ID:\s*([A-Z0-9]+)\.\s*M-PESA\s+Ref\s+ID:\s*([A-Z0-9]+)""",
        RegexOption.IGNORE_CASE
    )

    private val mpesaAcceptedPattern = Regex(
        """MPESA\s+payment\s+accepted\.\s*Cust\s+Ref:\s*([A-Z0-9]+)\s+Amt\s+(?:KES|Ksh):\s*([\d,]+(?:\.\d{1,2})?)\s+Trans\s+ID:\s*([A-Z0-9]+)\s+MPESA\s+Ref:\s*([A-Z0-9]+)\s+Recipient:\s*([A-Z0-9 .'-]+)""",
        RegexOption.IGNORE_CASE
    )

    private val tillConfirmationPattern = Regex(
        """Confirmed\.\s+Payment\s+of\s+(?:KES|Ksh)\.?\s*([\d,]+(?:\.\d{1,2})?)\s+to\s+([A-Z0-9 &.'-]+?)\s+Till\s+No\.\s*(\d+)\s+has\s+been\s+received\.\s*Ref\.?\s*([A-Z0-9]+)\s+on\s+(\d{2}-\d{2}-\d{4}\s+at\s+\d{2}:\d{2})""",
        RegexOption.IGNORE_CASE
    )

    private val pesaLinkReceivedPattern = Regex(
        """You\s+have\s+received\s+(?:KES|Ksh)\.?\s*([\d,]+(?:\.\d{1,2})?)\s+via\s+PesaLink\s+into\s+Acc\s+([A-Z0-9]+)\s+Tran\s+Ref\s+([A-Z0-9]+)""",
        RegexOption.IGNORE_CASE
    )

    private val absaCreditDebitPattern = Regex(
        """a\s+(Credit|Debit)\s+has\s+been\s+made\s+for\s+(?:KES|Ksh)\.?\s*([\d,]+(?:\.\d{1,2})?)\s+on\s+your\s+Account\s+([A-Z0-9]+)\s+VIA\s+([A-Z0-9/ .-]+?)(?=\s+please\s+call|\s*\.)""",
        RegexOption.IGNORE_CASE
    )

    // ─── Equity Bank ─────────────────────────────────────────────────────────
    private val equitySentPattern = Regex(
        """(?:Ksh|KES)\s*([\d,]+\.?\d*)\s+sent\s+to\s+([A-Z0-9 &.'-]+?)\s+(\d{10,12})?\s*on\s+[\d/]+\s+at\s+[\d:]+\.\s*Eazzy\s+ref[.\s]*([\w]+)\.\s*(?:Your\s+)?available\s+balance\s+is\s+(?:Ksh|KES)\s*([\d,]+\.?\d*)""",
        RegexOption.IGNORE_CASE
    )
    private val equityReceivedPattern = Regex(
        """(?:Ksh|KES)\s*([\d,]+\.?\d*)\s+received\s+from\s+([A-Z0-9 &.'-]+?)\s+(\d{10,12})?\s*on\s+[\d/]+\s+at\s+[\d:]+\.\s*Eazzy\s+ref[.\s]*([\w]+)\.\s*(?:Your\s+)?available\s+balance\s+is\s+(?:Ksh|KES)\s*([\d,]+\.?\d*)""",
        RegexOption.IGNORE_CASE
    )
    private val equityAtmPattern = Regex(
        """(?:Ksh|KES)\s*([\d,]+\.?\d*)\s+withdrawn\s+.*Eazzy\s+ref[.\s]*([\w]+)\.\s*(?:Your\s+)?available\s+balance\s+is\s+(?:Ksh|KES)\s*([\d,]+\.?\d*)""",
        RegexOption.IGNORE_CASE
    )
    private val equityPaybillPattern = Regex(
        """(?:Ksh|KES)\s*([\d,]+\.?\d*)\s+paid\s+to\s+([A-Z0-9 &.'-]+?)\s+.*Eazzy\s+ref[.\s]*([\w]+)\.\s*(?:Your\s+)?available\s+balance\s+is\s+(?:Ksh|KES)\s*([\d,]+\.?\d*)""",
        RegexOption.IGNORE_CASE
    )

    private fun parseEquity(body: String, timestamp: Long): ParsedBankResult? {
        equitySentPattern.find(body)?.let { m ->
            return ParsedBankResult(
                referenceNumber = m.groupValues[4].ifBlank { deterministicRef(AccountSource.EQUITY, body, timestamp) },
                isIncome = false,
                amount = m.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null,
                recipientName = m.groupValues[2].trim(),
                recipientNumber = m.groupValues[3].ifBlank { null },
                balance = m.groupValues[5].replace(",", "").toDoubleOrNull(),
                rawSms = body,
                timestamp = timestamp,
                source = AccountSource.EQUITY
            )
        }
        equityReceivedPattern.find(body)?.let { m ->
            return ParsedBankResult(
                referenceNumber = m.groupValues[4].ifBlank { deterministicRef(AccountSource.EQUITY, body, timestamp) },
                isIncome = true,
                amount = m.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null,
                recipientName = m.groupValues[2].trim(),
                recipientNumber = m.groupValues[3].ifBlank { null },
                balance = m.groupValues[5].replace(",", "").toDoubleOrNull(),
                rawSms = body,
                timestamp = timestamp,
                source = AccountSource.EQUITY
            )
        }
        equityAtmPattern.find(body)?.let { m ->
            return ParsedBankResult(
                referenceNumber = m.groupValues[2].ifBlank { deterministicRef(AccountSource.EQUITY, body, timestamp) },
                isIncome = false,
                amount = m.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null,
                recipientName = "ATM Withdrawal",
                balance = m.groupValues[3].replace(",", "").toDoubleOrNull(),
                rawSms = body,
                timestamp = timestamp,
                source = AccountSource.EQUITY,
                natureHint = TransactionNature.TRANSFER
            )
        }
        equityPaybillPattern.find(body)?.let { m ->
            return ParsedBankResult(
                referenceNumber = m.groupValues[3].ifBlank { deterministicRef(AccountSource.EQUITY, body, timestamp) },
                isIncome = false,
                amount = m.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null,
                recipientName = m.groupValues[2].trim(),
                balance = m.groupValues[4].replace(",", "").toDoubleOrNull(),
                rawSms = body,
                timestamp = timestamp,
                source = AccountSource.EQUITY
            )
        }
        Log.d(TAG, "Equity SMS not parsed: ${body.take(60)}")
        return null
    }

    // ─── KCB Bank ────────────────────────────────────────────────────────────
    private val kcbDebitPattern = Regex(
        """(?:Ksh|KES)\s*([\d,]+\.?\d*)\s+debited\s+from\s+your\s+account.*?Ref[:\s]+([\w]+).*?Bal[:\s]+(?:Ksh|KES)\s*([\d,]+\.?\d*)""",
        RegexOption.IGNORE_CASE
    )
    private val kcbCreditPattern = Regex(
        """(?:Ksh|KES)\s*([\d,]+\.?\d*)\s+credited\s+to\s+your\s+account\s+from\s+([A-Z0-9 &.'-]+?)\.\s*Ref[:\s]+([\w]+).*?Bal[:\s]+(?:Ksh|KES)\s*([\d,]+\.?\d*)""",
        RegexOption.IGNORE_CASE
    )
    private val voomaDebitPattern = Regex(
        """You sent\s+(?:Ksh|KES)\s*([\d,]+\.?\d*)\s+to\s+(\d{10,12})?\s*([A-Z0-9 &.'-]+?)?\.\s*Ref\s+([\w]+)\.\s*Balance[:\s]+(?:Ksh|KES)\s*([\d,]+\.?\d*)""",
        RegexOption.IGNORE_CASE
    )

    private fun parseKcb(body: String, timestamp: Long): ParsedBankResult? {
        kcbDebitPattern.find(body)?.let { m ->
            return ParsedBankResult(
                referenceNumber = m.groupValues[2].ifBlank { deterministicRef(AccountSource.KCB, body, timestamp) },
                isIncome = false,
                amount = m.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null,
                recipientName = "KCB Payment",
                balance = m.groupValues[3].replace(",", "").toDoubleOrNull(),
                rawSms = body,
                timestamp = timestamp,
                source = AccountSource.KCB
            )
        }
        kcbCreditPattern.find(body)?.let { m ->
            return ParsedBankResult(
                referenceNumber = m.groupValues[3].ifBlank { deterministicRef(AccountSource.KCB, body, timestamp) },
                isIncome = true,
                amount = m.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null,
                recipientName = m.groupValues[2].trim(),
                balance = m.groupValues[4].replace(",", "").toDoubleOrNull(),
                rawSms = body,
                timestamp = timestamp,
                source = AccountSource.KCB
            )
        }
        voomaDebitPattern.find(body)?.let { m ->
            val name = m.groupValues[3].trim().ifBlank { m.groupValues[2].ifBlank { "Vooma Transfer" } }
            return ParsedBankResult(
                referenceNumber = m.groupValues[4].ifBlank { deterministicRef(AccountSource.KCB, body, timestamp) },
                isIncome = false,
                amount = m.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null,
                recipientName = name,
                recipientNumber = m.groupValues[2].ifBlank { null },
                balance = m.groupValues[5].replace(",", "").toDoubleOrNull(),
                rawSms = body,
                timestamp = timestamp,
                source = AccountSource.KCB
            )
        }
        return null
    }

    // ─── Co-op Bank ──────────────────────────────────────────────────────────
    private val coopSentPattern = Regex(
        """(?:Ksh|KES)\s*([\d,]+\.?\d*)\s+sent\s+to\s+([A-Z0-9 &.'-]+?)(?=\s+on\s+|\s*\.\s*TXN|\s+TXN).*?TXN[:\s]+([\w]+).*?Bal[:\s]+(?:Ksh|KES)\s*([\d,]+\.?\d*)""",
        RegexOption.IGNORE_CASE
    )
    private val coopRecvPattern = Regex(
        """(?:Ksh|KES)\s*([\d,]+\.?\d*)\s+received\s+from\s+([A-Z0-9 &.'-]+?)(?=\s+on\s+|\s*\.\s*TXN|\s+TXN).*?TXN[:\s]+([\w]+).*?Bal[:\s]+(?:Ksh|KES)\s*([\d,]+\.?\d*)""",
        RegexOption.IGNORE_CASE
    )

    private fun parseCoop(body: String, timestamp: Long): ParsedBankResult? {
        coopSentPattern.find(body)?.let { m ->
            return ParsedBankResult(
                referenceNumber = m.groupValues[3].ifBlank { deterministicRef(AccountSource.COOP, body, timestamp) },
                isIncome = false,
                amount = m.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null,
                recipientName = m.groupValues[2].trim(),
                balance = m.groupValues[4].replace(",", "").toDoubleOrNull(),
                rawSms = body,
                timestamp = timestamp,
                source = AccountSource.COOP
            )
        }
        coopRecvPattern.find(body)?.let { m ->
            return ParsedBankResult(
                referenceNumber = m.groupValues[3].ifBlank { deterministicRef(AccountSource.COOP, body, timestamp) },
                isIncome = true,
                amount = m.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null,
                recipientName = m.groupValues[2].trim(),
                balance = m.groupValues[4].replace(",", "").toDoubleOrNull(),
                rawSms = body,
                timestamp = timestamp,
                source = AccountSource.COOP
            )
        }
        return null
    }

    // ─── NCBA ────────────────────────────────────────────────────────────────
    private val ncbaDebitPattern = Regex(
        """(?:Ksh|KES)\s*([\d,]+\.?\d*)\s+has been debited.*?Ref[:\s]+([\w]+).*?Bal[:\s]+(?:Ksh|KES)\s*([\d,]+\.?\d*)""",
        RegexOption.IGNORE_CASE
    )
    private val ncbaCreditPattern = Regex(
        """(?:Ksh|KES)\s*([\d,]+\.?\d*)\s+credited to your account.*?Ref[:\s]+([\w]+).*?Bal[:\s]+(?:Ksh|KES)\s*([\d,]+\.?\d*)""",
        RegexOption.IGNORE_CASE
    )

    private fun parseNcba(body: String, timestamp: Long): ParsedBankResult? {
        ncbaDebitPattern.find(body)?.let { m ->
            return ParsedBankResult(
                referenceNumber = m.groupValues[2].ifBlank { deterministicRef(AccountSource.NCBA, body, timestamp) },
                isIncome = false,
                amount = m.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null,
                recipientName = "NCBA Payment",
                balance = m.groupValues[3].replace(",", "").toDoubleOrNull(),
                rawSms = body,
                timestamp = timestamp,
                source = AccountSource.NCBA
            )
        }
        ncbaCreditPattern.find(body)?.let { m ->
            return ParsedBankResult(
                referenceNumber = m.groupValues[2].ifBlank { deterministicRef(AccountSource.NCBA, body, timestamp) },
                isIncome = true,
                amount = m.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null,
                recipientName = "NCBA Credit",
                balance = m.groupValues[3].replace(",", "").toDoubleOrNull(),
                rawSms = body,
                timestamp = timestamp,
                source = AccountSource.NCBA
            )
        }
        return null
    }

    // ─── Generic Debit/Credit Fallback (with optional Balance & Ref) ─────────
    private val genericDebitPattern = Regex(
        """(?:Ksh|KES)\.?\s*([\d,]+(?:\.\d{1,2})?)\s+(?:has\s+been\s+)?(?:debited|withdrawn|sent|paid)(?:.*?(?:Ref|Tran\s+Ref|ID)[:.\s]+([A-Z0-9]+))?(?:.*?(?:Bal(?:ance)?|Avail)[:.\s]+(?:Ksh|KES)\.?\s*([\d,]+(?:\.\d{1,2})?))?""",
        RegexOption.IGNORE_CASE
    )
    private val genericCreditPattern = Regex(
        """(?:Ksh|KES)\.?\s*([\d,]+(?:\.\d{1,2})?)\s+(?:has\s+been\s+)?(?:credited|received|deposited)(?:.*?(?:Ref|Tran\s+Ref|ID)[:.\s]+([A-Z0-9]+))?(?:.*?(?:Bal(?:ance)?|Avail)[:.\s]+(?:Ksh|KES)\.?\s*([\d,]+(?:\.\d{1,2})?))?""",
        RegexOption.IGNORE_CASE
    )

    private fun parseGenericDebitCredit(body: String, timestamp: Long, source: AccountSource): ParsedBankResult? {
        genericDebitPattern.find(body)?.let { m ->
            val amt = m.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null
            val ref = m.groupValues[2].trim().ifBlank { deterministicRef(source, body, timestamp) }
            val bal = m.groupValues[3].replace(",", "").toDoubleOrNull()
            return ParsedBankResult(
                referenceNumber = ref,
                isIncome = false,
                amount = amt,
                recipientName = "${source.displayName} Debit",
                balance = bal,
                rawSms = body,
                timestamp = timestamp,
                source = source
            )
        }
        genericCreditPattern.find(body)?.let { m ->
            val amt = m.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null
            val ref = m.groupValues[2].trim().ifBlank { deterministicRef(source, body, timestamp) }
            val bal = m.groupValues[3].replace(",", "").toDoubleOrNull()
            return ParsedBankResult(
                referenceNumber = ref,
                isIncome = true,
                amount = amt,
                recipientName = "${source.displayName} Credit",
                balance = bal,
                rawSms = body,
                timestamp = timestamp,
                source = source
            )
        }
        return null
    }

    private fun parseDashDate24h(dateStr: String): Long? = try {
        SimpleDateFormat("dd-MM-yyyy 'at' HH:mm", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("Africa/Nairobi")
        }.parse(dateStr.trim())?.time
    } catch (_: Exception) {
        null
    }

    /** Deterministic ID for refless SMS (e.g., Absa Debit/Credit alerts) so sync is idempotent. */
    fun deterministicRef(source: AccountSource, normalizedBody: String, timestampMs: Long): String {
        val minuteBucket = timestampMs / 60_000L
        val hash = sha256("${source.name}|${normalizedBody.trim()}|$minuteBucket")
        return "${source.name}-${hash.take(12).uppercase()}"
    }

    fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
