import java.util.regex.Pattern

fun main(args: Array<String>) {
    val receivedMsg = "UCPE0AK52H Confirmed.You have received Ksh500.00 from AGATHA  SHIKUKU 0710***487 on 25/3/26 at 3:04 PM  New M-PESA balance is Ksh557.79. To view the Full Number, forward this message to 334."
    
    val receivedRegex = """(?<receipt>[A-Z0-9]+)\s+Confirmed\.\s*You\s+have\s+received\s+(?:Ksh|Kshs)\s?(?<amount>[\d,]+\.\d{2})\s+from\s+(?<recipient>[a-zA-Z0-9\s\-]+?)\s*(?<phone>(?:07|01|254)[\d*]{8})?\s+(?:on\s+)?(?<date>\d{1,2}/\d{1,2}/\d{2}(?:\s+at)?\s+\d{1,2}:\d{2}\s+[AP]M)\.?.*?New\s+M-PESA\s+balance\s+is\s+(?:Ksh|Kshs)\s?(?<balance>[\d,]+\.\d{2})""".toRegex(RegexOption.IGNORE_CASE)

    println("RECEIVED MATCH: " + receivedRegex.find(receivedMsg)?.let { "YES ${it.groups["recipient"]?.value} / ${it.groups["phone"]?.value}" } ?: "NO MATCH")
    
    val dataBundleMsg = "UCEIR9BIPO Confirmed. Ksh50.00 sent to SAFARICOM DATA BUNDLES for account SAFARICOM DATA BUNDLES on 14/3/26 at 5:16 PM New M-PESA balance is Ksh0.00."

    val sentToAccRegex = """(?<receipt>[A-Z0-9]+)\s+Confirmed\.\s+(?:Ksh|Kshs)\s?(?<amount>[\d,]+\.\d{2})\s+sent\s+to\s+(?<recipient>[A-Za-z\s]+)\s+for\s+account.*?on\s+(?<date>\d{1,2}/\d{1,2}/\d{2}(?:\s+at)?\s+\d{1,2}:\d{2}\s+[AP]M)\s+New\s+M-PESA\s+balance\s+is\s+(?:Ksh|Kshs)\s?(?<balance>[\d,]+\.\d{2}).*?(?:Transaction\s+cost,\s+(?:Ksh|Kshs)\s?(?<cost>[\d,]+\.\d{2}))?""".toRegex(RegexOption.IGNORE_CASE)

    println("SENT TO ACC MATCH: " + sentToAccRegex.find(dataBundleMsg)?.let { "YES ${it.groups["recipient"]?.value}" } ?: "NO MATCH")

}
