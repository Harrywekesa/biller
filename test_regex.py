import re

received_msg = "UCPE0AK52H Confirmed.You have received Ksh500.00 from AGATHA  SHIKUKU 0710***487 on 25/3/26 at 3:04 PM  New M-PESA balance is Ksh557.79. To view the Full Number, forward this message to 334."
received_regex = re.compile(r"""(?P<receipt>[A-Z0-9]+)\s+Confirmed\.\s*You\s+have\s+received\s+(?:Ksh|Kshs)\s?(?P<amount>[\d,]+\.\d{2})\s+from\s+(?P<recipient>[a-zA-Z0-9\s\-]+?)\s*(?P<phone>(?:07|01|254)[\d*]{8})?\s+(?:on\s+)?(?P<date>\d{1,2}/\d{1,2}/\d{2}(?:\s+at)?\s+\d{1,2}:\d{2}\s+[AP]M)\.?.*?New\s+M-PESA\s+balance\s+is\s+(?:Ksh|Kshs)\s?(?P<balance>[\d,]+\.\d{2})""", re.IGNORECASE)

m1 = received_regex.search(received_msg)
if m1:
    print(f"RECEIVED MATCH: {m1.group('recipient')} / {m1.group('phone')}")
else:
    print("RECEIVED NO MATCH")


databundle_msg = "UCEIR9BIPO Confirmed. Ksh50.00 sent to SAFARICOM DATA BUNDLES for account SAFARICOM DATA BUNDLES on 14/3/26 at 5:16 PM New M-PESA balance is Ksh0.00."
sent_to_acc_regex = re.compile(r"""(?P<receipt>[A-Z0-9]+)\s+Confirmed\.\s+(?:Ksh|Kshs)\s?(?P<amount>[\d,]+\.\d{2})\s+sent\s+to\s+(?P<recipient>[A-Za-z\s]+)\s+for\s+account.*?on\s+(?P<date>\d{1,2}/\d{1,2}/\d{2}(?:\s+at)?\s+\d{1,2}:\d{2}\s+[AP]M)\s+New\s+M-PESA\s+balance\s+is\s+(?:Ksh|Kshs)\s?(?P<balance>[\d,]+\.\d{2}).*?(?:Transaction\s+cost,\s+(?:Ksh|Kshs)\s?(?P<cost>[\d,]+\.\d{2}))?""", re.IGNORECASE)

m2 = sent_to_acc_regex.search(databundle_msg)
if m2:
    print(f"SENT TO ACC MATCH: {m2.group('recipient')}")
else:
    print("SENT TO ACC NO MATCH")
