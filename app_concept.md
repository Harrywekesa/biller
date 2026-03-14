# M-Pesa AI Expense Tracker - Concept Note

## 1. App Concept & Value Proposition
*   **The Problem:** M-Pesa is the primary spending method in Kenya, but tracking *where* the money goes requires scrolling through hundreds of SMS messages or manually entering data into budgeting apps.
*   **The Solution:** An automated, AI-powered expense tracker that securely reads M-Pesa SMS messages locally on the device, extracting the transaction details and automatically categorizing expenses.
*   **Value Proposition:** "Know exactly where your money goes, effortlessly."

## 2. Core Features (Minimum Viable Product - MVP)
*   **SMS Parsing Engine:** Accurately reads M-Pesa confirmation texts to extract:
    *   Transaction Type (Send Money, Paybill, Buy Goods, Withdraw, Airtime/Data).
    *   Amount & Transaction Cost.
    *   Recipient Name (e.g., "Safaricom", "Naivas Supermarket", "KPLC").
    *   Date and Time.
    *   New M-Pesa Balance.
*   **Smart Categorization:**
    *   *Auto-categorization:* Recognizing common names using a pre-built dictionary (e.g., "Safaricom" -> Utilities/Airtime, "Naivas" -> Groceries).
    *   *Custom Rules:* Allowing the user to set personal rules (e.g., "Any payment to 'John Doe' is 'Rent'").
*   **Dashboard & Analytics:**
    *   A simple home screen showing Total Spent.
    *   Visualizations (Pie charts/bar graphs) showing spending by category.
*   **Recurrent Expense Tracker:**
    *   A dedicated view to track recurring payments like data bundles, subscriptions, or monthly rent.
*   **Comprehensive Reporting:**
    *   Daily, Weekly, Monthly, and Yearly spending reports to track long-term trends.

## 3. Financial Intelligence & AI (Advanced Features)
*   **Local ML Classification:** Uses an On-Device Machine Learning model (like TensorFlow Lite) to smartly categorize new or unknown businesses based on learned user behavior, without sending data to the cloud.
*   **Smart Spending Insights & Alerts:** 
    *   Analyzes spending patterns and provides actionable advice (e.g., *"You buy 100/- Safaricom Data every day. Buying a 3,000/- monthly bundle would save you money."*).
    *   Alerts when spending in a specific category spikes abnormally against the 30-day average.
*   **Cash Flow Forecasting:** Calculates the user's "daily burn rate" and predicts how many days until their current M-Pesa balance reaches zero.
*   **Bill Reminders:** Scans SMS for invoices (Zuku, KPLC, Nairobi Water) and creates predictive calendar reminders for upcoming due dates.
*   **Hidden Costs Analyzer:** Tracks and aggregates total money wasted purely on M-Pesa transaction and withdrawal fees, suggesting cheaper transaction methods.

## 4. Privacy & Security Architecture
*   **100% Offline Processing:** All SMS parsing and AI categorization happens **locally on the device**. No SMS data is ever sent to external servers.
*   **Biometric Security:** App requires Fingerprint/FaceID or PIN to open, protecting sensitive financial data.
*   **Local Database:** Uses a secure local Android database (Room/SQLite) to store extracted data.
*   **Privacy Mode:** A quick toggle to blur out total balances and amounts when opening the app in public.
*   **Encrypted Backups:** Optional, encrypted backups to the user's personal Google Drive in case of device loss.

## 5. Technical Strategy
*   **Platform:** Native Android (Kotlin). Essential for robust background SMS reading and efficient on-device ML execution.
*   **Permissions:** Requires `READ_SMS` permission (requires careful justification for Google Play Store approval).
*   **Data Bootstrapping:** Requires building a comprehensive localized dictionary mapping popular Kenyan Paybill/Till numbers to their respective categories before launch to ensure the app is "smart" on day one.

## 6. Potential App Names
*   *M-Track*
*   *PesaSense*
*   *Matumizi* (Swahili for expenses)
*   *PesaFlow*
*   *SpendSafi*
