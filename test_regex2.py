import re

test_str1 = "UCEIR99V2M Confirmed.You have received Ksh100.00 from HARRISON  WANJALA 0741947264 on 14/3/26 at 8:21 AM  New M-PESA balance is Ksh100.00"

p4_new = r"(?i)(?P<receipt>[A-Z0-9]+)\s+Confirmed\.\s*You\s+have\s+received\s+(?:Ksh|Kshs)\s?(?P<amount>[\d,]+\.\d{2})\s+from\s+(?P<recipient>[A-Za-z\s]+)(?P<phone>(?:07|01|254)\d{8})?\s+(?:on\s+)?(?P<date>\d{1,2}/\d{1,2}/\d{2}(?:\s+at)?\s+\d{1,2}:\d{2}\s+[AP]M)\.?.*?New\s+M-PESA\s+balance\s+is\s+(?:Ksh|Kshs)\s?(?P<balance>[\d,]+\.\d{2})"

print("P4 New:", bool(re.search(p4_new, test_str1)))
