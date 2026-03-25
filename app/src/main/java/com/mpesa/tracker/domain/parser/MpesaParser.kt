package com.mpesa.tracker.domain.parser

import com.mpesa.tracker.data.local.entities.TransactionType
import java.util.Calendar
import java.util.TimeZone

object MpesaParser {

    // Regex Groups explained:
    // (?<receipt>[A-Z0-9]+) -> e.g. OJM12ABCD34
    // (?:Confirmed\\.)? -> Optional prefix
    // (?:Ksh|Kshs)\\s?(?<amount>[\\d,]+\\.\\d{2}) -> Amount
    // (?<type>paid to|sent to|gave|received|bought|withdrew|paid)
    // (?<recipient>.*?)\\.*(?:on|from)?
    // (?<date>\\d{1,2}/\\d{1,2}/\\d{2}\\s*(?:at)?\\s*\\d{1,2}:\\d{2}\\s*(?:AM|PM))
    // .*?(?:New M-PESA balance is Ksh(?<balance>[\\d,]+\\.\\d{2}))?
    // .*?(?:Transaction cost, Ksh(?<cost>[\\d,]+\\.\\d{2}))?

    private const val GENERIC_PATTERN = """^(?<receipt>[A-Z0-9]+)\s+Confirmed\.\s+(?:Ksh|Kshs)\s?(?<amount>[\d,]+\.\d{2})\s+(?<type>paid to|sent to|bought|withdrew from|received from)\s+(?<recipient>.*?)(?:\s+(?:on|from)\s+|\s+)(?<date>\d{1,2}/\d{1,2}/\d{2}\s+(?:at\s+)?\d{1,2}:\d{2}\s+(?:AM|PM))..*?(?:New\s+M-PESA\s+balance\s+is\s+(?:Ksh|Kshs)\s?(?<balance>[\d,]+\.\d{2}))..*?(?:Transaction\s+cost,\s+(?:Ksh|Kshs)\s?(?<cost>[\d,]+\.\d{2}))?"""

    // Specific Regexes for tighter matching
    
    // PAYBILL / BUY GOODS (e.g., OJM12ABCD34 Confirmed. Ksh500.00 paid to NAIVAS...)
    private val PAYBILL_REGEX = """(?<receipt>[A-Z0-9]+)\s+Confirmed\.\s+(?:Ksh|Kshs)\s?(?<amount>[\d,]+\.\d{2})\s+paid\s+to\s+(?<recipient>[^.]+)\.\s+on\s+(?<date>\d{1,2}/\d{1,2}/\d{2}(?:\s+at)?\s+\d{1,2}:\d{2}\s+[AP]M)\.(?:\s*New\s+M-PESA\s+balance\s+is\s+(?:Ksh|Kshs)\s?(?<balance>[\d,]+\.\d{2}))?(?:\.\s*Transaction\s+cost,\s+(?:Ksh|Kshs)\s?(?<cost>[\d,]+\.\d{2}))?""".toRegex(RegexOption.IGNORE_CASE)

    // SEND MONEY (e.g., OJM... Confirmed. Ksh500 sent to JOHN DOE 07123...)
    private val SEND_MONEY_REGEX = """(?<receipt>[A-Z0-9]+)\s+Confirmed\.\s+(?:Ksh|Kshs)\s?(?<amount>[\d,]+\.\d{2})\s+sent\s+to\s+(?<recipient>[A-Za-z\s]+)(?<phone>(?:07|01|254)[\d*]{8})?\s+(?:on\s+)?(?<date>\d{1,2}/\d{1,2}/\d{2}(?:\s+at)?\s+\d{1,2}:\d{2}\s+[AP]M)\..*?New\s+M-PESA\s+balance\s+is\s+(?:Ksh|Kshs)\s?(?<balance>[\d,]+\.\d{2}).*?(?:Transaction\s+cost,\s+(?:Ksh|Kshs)\s?(?<cost>[\d,]+\.\d{2}))?""".toRegex(RegexOption.IGNORE_CASE)
    
    // SEND TO PAYBILL VER. 2 (Data Bundles / Safaricom)
    // E.g. "UCEIR9BIPO Confirmed. Ksh50.00 sent to SAFARICOM DATA BUNDLES for account SAFARICOM DATA BUNDLES on 14/3/26 at 5:16 PM New M-PESA balance is Ksh0.00."
    private val SENT_TO_ACCOUNT_REGEX = """(?<receipt>[A-Z0-9]+)\s+Confirmed\.\s+(?:Ksh|Kshs)\s?(?<amount>[\d,]+\.\d{2})\s+sent\s+to\s+(?<recipient>[A-Za-z\s]+)\s+for\s+account.*?on\s+(?<date>\d{1,2}/\d{1,2}/\d{2}(?:\s+at)?\s+\d{1,2}:\d{2}\s+[AP]M)\s+New\s+M-PESA\s+balance\s+is\s+(?:Ksh|Kshs)\s?(?<balance>[\d,]+\.\d{2}).*?(?:Transaction\s+cost,\s+(?:Ksh|Kshs)\s?(?<cost>[\d,]+\.\d{2}))?""".toRegex(RegexOption.IGNORE_CASE)

    // BUY AIRTIME
    private val BUY_AIRTIME_REGEX = """(?<receipt>[A-Z0-9]+)\s+Confirmed\.\s+You\s+bought\s+(?:Ksh|Kshs)\s?(?<amount>[\d,]+\.\d{2})\s+of\s+airtime\s+(?:on\s+)?(?<date>\d{1,2}/\d{1,2}/\d{2}(?:\s+at)?\s+\d{1,2}:\d{2}\s+[AP]M).*?New\s+M-PESA\s+balance\s+is\s+(?:Ksh|Kshs)\s?(?<balance>[\d,]+\.\d{2})""".toRegex(RegexOption.IGNORE_CASE)


    // RECEIVED MONEY
    private val RECEIVED_MONEY_REGEX = """(?<receipt>[A-Z0-9]+)\s+Confirmed\.\s*You\s+have\s+received\s+(?:Ksh|Kshs)\s?(?<amount>[\d,]+\.\d{2})\s+from\s+(?<recipient>[a-zA-Z0-9\s\-]+?)\s*(?<phone>(?:07|01|254)[\d*]{8})?\s+(?:on\s+)?(?<date>\d{1,2}/\d{1,2}/\d{2}(?:\s+at)?\s+\d{1,2}:\d{2}\s+[AP]M)\.?.*?New\s+M-PESA\s+balance\s+is\s+(?:Ksh|Kshs)\s?(?<balance>[\d,]+\.\d{2})""".toRegex(RegexOption.IGNORE_CASE)
    
    // AGENT WITHDRAWAL (e.g. OJM... Confirmed. on 12/4/23 at 10:30 AMWithdraw Ksh500.00 from 123456 - AGENT NAME New M-PESA balance is Ksh...)
    private val WITHDRAW_CASH_REGEX = """(?<receipt>[A-Z0-9]+)\s+Confirmed\.\s+(?:on\s+)?(?<date>\d{1,2}/\d{1,2}/\d{2}(?:\s+at)?\s+\d{1,2}:\d{2}\s+[AP]M)(?:\s*)?[Ww]ithdraw\s+(?:Ksh|Kshs)\s?(?<amount>[\d,]+\.\d{2})\s+from\s+(?<recipient>[^.]+)\.?[^\d]*New\s+M-PESA\s+balance\s+is\s+(?:Ksh|Kshs)\s?(?<balance>[\d,]+\.\d{2}).*?(?:Transaction\s+cost,\s+(?:Ksh|Kshs)\s?(?<cost>[\d,]+\.\d{2}))?""".toRegex(RegexOption.IGNORE_CASE)

    // FULIZA OVERDRAFT IDENTIFIER (Standalone)
    // E.g., "UCEIR9COVD Confirmed. Fuliza M-PESA amount is Ksh 100.00. Access Fee charged Ksh 1.00. Total Fuliza M-PESA outstanding amount is Ksh1660.84 due on 05/04/26."
    private val STANDALONE_FULIZA_REGEX = """(?<receipt>[A-Z0-9]+)\s+Confirmed\.\s+Fuliza\s+M-PESA\s+amount\s+is\s+(?:Ksh|Kshs)\s?(?<amount>[\d,]+\.\d{2})\.\s+(?:Access\s+)?Fee\s+charged\s+(?:Ksh|Kshs)\s?(?<cost>[\d,]+\.\d{2})\.\s+Total\s+Fuliza\s+M-PESA\s+outstanding\s+amount\s+is\s+(?:Ksh|Kshs)\s?(?<balance>[\d,]+\.\d{2})\s+due\s+on\s+(?<date>\d{1,2}/\d{1,2}/\d{2})""".toRegex(RegexOption.IGNORE_CASE)

    // FULIZA REPAYMENT (Deduction from M-Pesa to pay Fuliza)
    // E.g., "UCCIR94WN8 Confirmed. Ksh 750.00 from your M-PESA has been used to partially pay your outstanding Fuliza M-PESA. Your available Fuliza M-PESA limit is Ksh 1711.40. M-PESA balance is Ksh0.00."
    private val FULIZA_REPAY_REGEX = """(?<receipt>[A-Z0-9]+)\s+Confirmed\.\s+(?:Ksh|Kshs)\s?(?<amount>[\d,]+\.\d{2})\s+from\s+your\s+M-PESA\s+has\s+been\s+used\s+to\s+(?:partially|fully)\s+pay\s+your\s+outstanding\s+Fuliza.*?M-PESA\s+balance\s+is\s+(?:(?:Ksh|Kshs)\s?)?(?<balance>[\d,]+\.\d{2})""".toRegex(RegexOption.IGNORE_CASE)


    fun parseMessage(smsBody: String, smsTimestamp: Long? = null): ParsedMpesaResult? {
        val cleanBody = smsBody.replace("\n", " ").trim()

        return parsePaybill(cleanBody) 
            ?: parseSentToAccount(cleanBody)
            ?: parseSendMoney(cleanBody) 
            ?: parseReceivedMoney(cleanBody)
            ?: parseWithdrawCash(cleanBody)
            ?: parseAirtime(cleanBody)
            ?: parseFulizaRepayment(cleanBody, smsTimestamp)
            ?: parseStandaloneFuliza(cleanBody, smsTimestamp)
    }

    private fun parseFulizaRepayment(sms: String, smsTimestamp: Long?): ParsedMpesaResult? {
        val match = FULIZA_REPAY_REGEX.find(sms) ?: return null
        
        val amountStr = match.groups["amount"]?.value?.replace(",", "") ?: "0.0"
        
        var balance: Double? = null
        try {
            val balStr = match.groups["balance"]?.value?.replace(",", "")
            if (balStr != null) balance = balStr.toDouble()
        } catch(e: Exception) {}

        return ParsedMpesaResult(
            receiptNumber = (match.groups["receipt"]?.value ?: "") + "-REPAY",
            type = TransactionType.PAYBILL,
            amount = amountStr.toDouble(),
            transactionCost = 0.0,
            timestamp = smsTimestamp ?: System.currentTimeMillis(), // Use real SMS received time or fallback to now
            recipientName = "Fuliza Repayment",
            balance = balance,
            rawSms = sms
        )
    }

    private fun parseStandaloneFuliza(sms: String, smsTimestamp: Long?): ParsedMpesaResult? {
        val match = STANDALONE_FULIZA_REGEX.find(sms) ?: return null
        
        val amountStr = match.groups["amount"]?.value?.replace(",", "") ?: "0.0"
        val costStr = match.groups["cost"]?.value?.replace(",", "") ?: "0.0"
        val fee = costStr.toDoubleOrNull() ?: 0.0
        
        var balance: Double? = null
        try {
            val balStr = match.groups["balance"]?.value?.replace(",", "")
            if (balStr != null) balance = balStr.toDouble()
        } catch(e: Exception) {}

        val timestamp = smsTimestamp ?: System.currentTimeMillis() // Use actual SMS time over Due Date
        
        return ParsedMpesaResult(
            receiptNumber = (match.groups["receipt"]?.value ?: "") + "-FULIZA",
            type = TransactionType.PAYBILL, // We can categorize it as PAYBILL/BUY_GOODS or create a new FULIZA type
            amount = amountStr.toDouble(),
            transactionCost = fee,
            timestamp = timestamp,
            recipientName = "Fuliza M-PESA",
            balance = balance, // Note: This is outstanding balance, not M-Pesa balance. But we'll leave it as balance for now.
            fulizaAmount = amountStr.toDouble(),
            fulizaFee = fee,
            rawSms = sms
        )
    }

    private fun parsePaybill(sms: String): ParsedMpesaResult? {
        val match = PAYBILL_REGEX.find(sms) ?: return null
        return buildResult(match, TransactionType.PAYBILL, sms)
    }
    
    private fun parseSendMoney(sms: String): ParsedMpesaResult? {
        val match = SEND_MONEY_REGEX.find(sms) ?: return null
        // Include phone number in recipient if available
        val recipientName = match.groups["recipient"]?.value?.trim() ?: "Unknown"
        val phone = try { match.groups["phone"]?.value?.trim() } catch(e: Exception) { null }
        return buildResult(match, TransactionType.SEND_MONEY, sms).copy(
            recipientName = recipientName,
            recipientNumber = phone
        )
    }
    
    private fun parseSentToAccount(sms: String): ParsedMpesaResult? {
        val match = SENT_TO_ACCOUNT_REGEX.find(sms) ?: return null
        val recipientName = match.groups["recipient"]?.value?.trim() ?: "Unknown"
        return buildResult(match, TransactionType.PAYBILL, sms).copy(
            recipientName = recipientName
        )
    }

    private fun parseAirtime(sms: String): ParsedMpesaResult? {
        val match = BUY_AIRTIME_REGEX.find(sms) ?: return null
        
        // Airtime doesn't have a recipient group in the regex, hardcode it when building the result
        val amountStr = match.groups["amount"]?.value?.replace(",", "") ?: "0.0"
        
        var balance: Double? = null
        try {
            val balStr = match.groups["balance"]?.value?.replace(",", "")
            if (balStr != null) balance = balStr.toDouble()
        } catch(e: Exception) {}

        val dateStr = match.groups["date"]?.value ?: ""
        val timestamp = parseRobustDate(dateStr)

        return ParsedMpesaResult(
            receiptNumber = match.groups["receipt"]?.value ?: "",
            type = TransactionType.BUY_AIRTIME,
            amount = amountStr.toDouble(),
            transactionCost = 0.0,
            timestamp = timestamp,
            recipientName = "Safaricom Airtime/Data",
            balance = balance,
            rawSms = sms
        )
    }

    private fun parseReceivedMoney(sms: String): ParsedMpesaResult? {
        val match = RECEIVED_MONEY_REGEX.find(sms) ?: return null
        val recipientName = match.groups["recipient"]?.value?.trim() ?: "Unknown"
        val phone = try { match.groups["phone"]?.value?.trim() } catch(e: Exception) { null }
        return buildResult(match, TransactionType.RECEIVED_MONEY, sms).copy(
            recipientName = recipientName,
            recipientNumber = phone
        )
    }

    private fun parseWithdrawCash(sms: String): ParsedMpesaResult? {
        val match = WITHDRAW_CASH_REGEX.find(sms) ?: return null
        val recipientName = match.groups["recipient"]?.value?.trim() ?: "Unknown"
        // Withdraw cost sometimes does not match standard group if format changes slightly
        var cost = match.groups["cost"]?.value?.replace(",", "")?.toDoubleOrNull() ?: 0.0
        
        if (cost == 0.0) {
           val costRegex = """Transaction\s+cost,\s+(?:Ksh|Kshs)\s?([\d,]+\.\d{2})""".toRegex(RegexOption.IGNORE_CASE)
           costRegex.find(sms)?.groups?.get(1)?.value?.replace(",", "")?.toDoubleOrNull()?.let { 
               cost = it 
           }
        }

        return buildResult(match, TransactionType.WITHDRAW_CASH, sms).copy(
            recipientName = recipientName,
            transactionCost = cost
        )
    }

    private fun buildResult(match: MatchResult, type: TransactionType, raw: String): ParsedMpesaResult {
        val amountStr = match.groups["amount"]?.value?.replace(",", "") ?: "0.0"
        
        var cost = 0.0
        try {
            val costStr = match.groups["cost"]?.value?.replace(",", "")
            if (costStr != null) cost = costStr.toDouble()
        } catch(e: Exception) {}

        var balance: Double? = null
        try {
            val balStr = match.groups["balance"]?.value?.replace(",", "")
            if (balStr != null) balance = balStr.toDouble()
        } catch(e: Exception) {}
        
        // Secondary Pass for Fuliza Overdraft (Legacy embedded format)
        var fulizaAmount: Double? = null
        var fulizaFee: Double? = null
        try {
            val FULIZA_LEGACY_REGEX = """Fuliza\s+M-Pesa\s+amount\s+is\s+(?:Ksh|Kshs)\s?(?<fAmount>[\d,]+(?:\.\d{2})?).*?Fee\s+charged\s+(?:Ksh|Kshs)\s?(?<fFee>[\d,]+(?:\.\d{2})?)""".toRegex(RegexOption.IGNORE_CASE)
            val fulizaMatch = FULIZA_LEGACY_REGEX.find(raw)
            if (fulizaMatch != null) {
                fulizaAmount = fulizaMatch.groups["fAmount"]?.value?.replace(",", "")?.toDoubleOrNull()
                fulizaFee = fulizaMatch.groups["fFee"]?.value?.replace(",", "")?.toDoubleOrNull()
            }
        } catch(e: Exception) {}

        val dateStr = match.groups["date"]?.value ?: ""
        val timestamp = parseRobustDate(dateStr)

        return ParsedMpesaResult(
            receiptNumber = match.groups["receipt"]?.value ?: "",
            type = type,
            amount = amountStr.toDouble(),
            transactionCost = cost,
            timestamp = timestamp,
            recipientName = match.groups["recipient"]?.value?.trim() ?: "Unknown",
            balance = balance,
            fulizaAmount = fulizaAmount,
            fulizaFee = fulizaFee,
            rawSms = raw
        )
    }

    private fun parseRobustDate(dateString: String): Long {
        try {
            // Examples: "12/4/23 10:30 AM", "12/04/23 at 10:30 AM"
            val cleanStr = dateString.replace("at", "", ignoreCase = true)
                .replace("\\s+".toRegex(), " ")
                .trim()
            
            // Expected parts: ["12", "4", "23"], ["10:30", "AM"]
            val spaceSplit = cleanStr.split(" ")
            if (spaceSplit.size >= 3) {
                val dateParts = spaceSplit[0].split("/")
                val timeParts = spaceSplit[1].split(":")
                val amPm = spaceSplit[2]

                if (dateParts.size == 3 && timeParts.size == 2) {
                    val day = dateParts[0].toInt()
                    val month = dateParts[1].toInt() - 1 // Calendar months are 0-indexed
                    var year = dateParts[2].toInt()
                    if (year < 100) year += 2000

                    var hour = timeParts[0].toInt()
                    val minute = timeParts[1].toInt()

                    if (amPm.equals("PM", ignoreCase = true) && hour < 12) {
                        hour += 12
                    } else if (amPm.equals("AM", ignoreCase = true) && hour == 12) {
                        hour = 0
                    }

                    val cal = Calendar.getInstance()
                    cal.set(year, month, day, hour, minute, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    return cal.timeInMillis
                }
            }
        } catch (e: Exception) {
            // fallback
        }
        return System.currentTimeMillis()
    }
}
