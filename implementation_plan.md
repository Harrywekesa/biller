# Implementation Plan: M-Pesa AI Expense Tracker

## Goal Description
Build a native Android application using Kotlin that automatically reads M-Pesa SMS receipts, parses the data (amount, date, merchant, transaction cost, balances), and categorizes them using local rules and lightweight Machine Learning. The app will provide data visualization, recurring expense tracking, and financial insights without ever sending the user's SMS data to the cloud.

## User Review Required
> [!IMPORTANT]
> **SMS Permissions (Play Store Policy):** Google Play actively restricts the `READ_SMS` permission. While we can easily build and use this app locally/via APK, publishing to the Play Store will require a rigorous justification process (showing a video of the core functionality relying on this permission). 
> **Are you planning to publish this on the Play Store, or is this primarily a personal/internal tool?**

> [!WARNING]
> **TFLite vs Heuristics:** For MVP, we will start with a local dictionary (heuristics/rules) for the most common 100 Kenyan businesses. Full TensorFlow Lite categorization will be introduced in Phase 3 after the core SMS parsing is 100% accurate.

## Proposed Changes

We are starting from scratch. The architecture will follow standard Android MVVM (Model-View-ViewModel) using Kotlin Coroutines for async tasks and Room for local database management.

### [Core App Setup]
#### [NEW] [Android Project Initialization](file:///C:/Users/Pi/Videos/biller)
- Create a new Android Studio project with Kotlin DSL and Jetpack Compose for the UI.
- Setup Hilt/Dagger for Dependency Injection.

### [Data Layer (Room Database)]
#### [NEW] [AppDatabase.kt](file:///C:/Users/Pi/Videos/biller/app/src/main/java/com/mpesa/tracker/data/local/AppDatabase.kt)
- Room Database setup.
#### [NEW] [TransactionEntity.kt](file:///C:/Users/Pi/Videos/biller/app/src/main/java/com/mpesa/tracker/data/local/entities/TransactionEntity.kt)
- Table for storing parsed SMS data (ID, M-Pesa Receipt Number, Type, SubType, Amount, TransactionCost, Date, RecipientName, RecipientNumber, Balance, CategoryId, IsRecurring).
#### [NEW] [CategoryEntity.kt](file:///C:/Users/Pi/Videos/biller/app/src/main/java/com/mpesa/tracker/data/local/entities/CategoryEntity.kt)
- Pre-populated categories (Food, Rent, Utilities, Transport, Transfers, Fees).
#### [NEW] [DictionaryEntity.kt](file:///C:/Users/Pi/Videos/biller/app/src/main/java/com/mpesa/tracker/data/local/entities/DictionaryEntity.kt)
- Mapping known business names to Category IDs (e.g., "Safaricom" -> Utilities).

### [Domain Layer (Parsing Engine)]
#### [NEW] [MpesaRegexPatterns.kt](file:///C:/Users/Pi/Videos/biller/app/src/main/java/com/mpesa/tracker/domain/parser/MpesaRegexPatterns.kt)
- Complex Regex patterns for distinct M-Pesa message types (Paybill, Buy Goods, Send Money, Receive Money, Withdraw).
#### [NEW] [SmsParserService.kt](file:///C:/Users/Pi/Videos/biller/app/src/main/java/com/mpesa/tracker/domain/parser/SmsParserService.kt)
- Service to run regex against SMS, extract captured groups, and map to `TransactionEntity`.

### [Android Framework Layer]
#### [NEW] [SmsReceiver.kt](file:///C:/Users/Pi/Videos/biller/app/src/main/java/com/mpesa/tracker/framework/receivers/SmsReceiver.kt)
- `BroadcastReceiver` listening for `android.provider.Telephony.SMS_RECEIVED` to trigger parsing on newly arrived M-Pesa messages in the background.

## Verification Plan
Given this is a parser-heavy application, failing to parse a specific M-Pesa message type breaks the core user experience.

### Automated Tests
1.  **Regex Unit Testing (Crucial!):**
    - We will create a `MpesaParserTest` suite containing dozens of *real, anonymized* M-Pesa text messages spanning the last 3-4 years (as Safaricom occasionally changes their format).
    - Tests will assert that Amount, Recipient, Date, and Balance are extracted perfectly for EVERY message type.
    - Command: `./gradlew test`
2.  **Room Database Tests:**
    - Local unit tests to ensure transactions are inserted correctly and category relations map successfully.

### Manual Verification
1.  **Emulator SMS Simulation:**
    - Using Android Studio's Emulator controls to simulate sending an incoming SMS formatting exactly like M-Pesa to trigger the `BroadcastReceiver` and ensure the database updates in real-time.
2.  **Physical Device Testing:**
    - Deploy the APK to a physical Android device to verify that historic (already existing) M-Pesa messages can be successfully read and ingested.
