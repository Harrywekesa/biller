# Privacy Policy for M-Pesa Tracker

**Last Updated:** March 2026

## 1. Introduction
M-Pesa Tracker ("the App") respects your privacy and is committed to protecting your personal data. This Privacy Policy outlines how we handle the sensitive SMS data required for the App to function.

## 2. Core Functionality and Permissions
The App is designed to automatically categorize your M-Pesa transactions by parsing SMS messages from Safaricom. To achieve this, the App requests the `READ_SMS` and `RECEIVE_SMS` permissions. 

**This is the core functionality of the app. Without these permissions, the app cannot operate.**

## 3. Strict Offline-First Architecture
We prioritize your privacy above all else through a strict "offline-first" architecture:
- **No Data Leaves Your Device:** Your SMS messages, financial balances, and transaction history are processed entirely locally on your Android device.
- **No Cloud Servers:** The App does not communicate with any external servers to offload SMS parsing or categorization. We use on-device Machine Learning (TensorFlow Lite) and local heuristic dictionaries.
- **Encrypted Local Backups:** If you choose to back up your data, the backup is stored locally on your device storage, fully encrypted.

## 4. How We Use Your Data
The `READ_SMS` permission is used **exclusively** to:
1. Scan for incoming messages from the sender ID "MPESA".
2. Extract the transaction amount, date, receipt number, and recipient/sender name.
3. Locally categorize the transaction for display in your dashboard and analytics pages.

We **do not** read personal messages or messages from any sender other than "MPESA".

## 5. Third-Party Sharing
Because the App operates 100% offline, your data is **never** shared with, sold to, or transmitted to any third-party analytics services, advertisers, or even our own developer environments. 

## 6. Data Retention and Deletion
Your transaction data is stored in a local SQLite database on your device. You have full control over this data. You can delete individual transactions or clear the entire App data via your Android device settings at any time, which permanently removes all parsed M-Pesa history.

## 7. Contact Us
If you have any questions or concerns regarding this privacy policy or our treatment of your SMS data, please contact the developer via the support email listed on the Google Play Store.
