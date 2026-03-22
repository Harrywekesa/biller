fun main() {
    val sms = "UCJIR9UCU4 Confirmed. Fuliza M-PESA amount is Ksh 30.00. Access Fee charged Ksh 0.30. Total Fuliza M-PESA outstanding amount is Ksh1372.23 due on 17/04/26. To check daily charges, Dial *334#OK Select Query Charges"
    val regex = """(?<receipt>[A-Z0-9]+)\s+Confirmed\.\s+Fuliza\s+M-PESA\s+amount\s+is\s+(?:Ksh|Kshs)\s?(?<amount>[\d,]+\.\d{2})\.\s+(?:Access\s+)?Fee\s+charged\s+(?:Ksh|Kshs)\s?(?<cost>[\d,]+\.\d{2})\.\s+Total\s+Fuliza\s+M-PESA\s+outstanding\s+amount\s+is\s+(?:Ksh|Kshs)\s?(?<balance>[\d,]+\.\d{2})\s+due\s+on\s+(?<date>\d{1,2}/\d{1,2}/\d{2})""".toRegex(RegexOption.IGNORE_CASE)
    
    val match = regex.find(sms)
    if (match != null) {
        println("Matched!")
        println("receipt: " + match.groups["receipt"]?.value)
        println("balance: " + match.groups["balance"]?.value)
        println("date: " + match.groups["date"]?.value)
    } else {
        println("Not matched!")
    }
}
