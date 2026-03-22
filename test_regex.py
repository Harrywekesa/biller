import re

sms = "UCJIR9UCU4 Confirmed. Fuliza M-PESA amount is Ksh 30.00. Access Fee charged Ksh 0.30. Total Fuliza M-PESA outstanding amount is Ksh1372.23 due on 17/04/26. To check daily charges, Dial *334#OK Select Query Charges"

regex = r"(?P<receipt>[A-Z0-9]+)\s+Confirmed\.\s+Fuliza\s+M-PESA\s+amount\s+is\s+(?:Ksh|Kshs)\s?(?P<amount>[\d,]+\.\d{2})\.\s+(?:Access\s+)?Fee\s+charged\s+(?:Ksh|Kshs)\s?(?P<cost>[\d,]+\.\d{2})\.\s+Total\s+Fuliza\s+M-PESA\s+outstanding\s+amount\s+is\s+(?:Ksh|Kshs)\s?(?P<balance>[\d,]+\.\d{2})\s+due\s+on\s+(?P<date>\d{1,2}/\d{1,2}/\d{2})"

match = re.search(regex, sms, re.IGNORECASE)
if match:
    print("MATCHED!")
    print("Balance:", match.group("balance"))
    print("Date:", match.group("date"))
else:
    print("NO MATCH")
