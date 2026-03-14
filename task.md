# M-Pesa AI Expense Tracker - Development Plan

## Phase 1: Planning & Setup
- [x] Define app requirements and features (Completed)
- [x] Create development checklist (Current)
- [x] Design Technical Architecture & Implementation Plan
- [x] Initialize Android Studio Project (Kotlin)
- [x] Setup local database structure (Room/SQLite)

## Phase 2: Core Parsing Engine (The Brains)
- [x] Design regex patterns for M-Pesa SMS formats:
  - [x] Sent Money
  - [x] Received Money
  - [x] Paybill / Buy Goods
  - [x] Withdraw Cash
  - [x] Buy Airtime/Data
- [x] Create SMS Provider Reader (handling `READ_SMS` permission safely)
- [ ] Build background worker to parse new incoming messages in real-time
- [ ] Write unit tests for all regex patterns using real M-Pesa SMS examples

## Phase 3: Database & Local Models
- [x] Implement Room Database Entities (Transactions, Categories, Budgets)
- [x] Seed database with initial Kenyan business dictionary (Safaricom, KPLC, Zuku, popular supermarkets)
- [x] Build Data Access Objects (DAOs) for querying transactions by date/category
- [ ] Setup TensorFlow Lite model for text classification (offline categorization)

## Phase 4: User Interface (UI) Implementation
- [ ] App Lock Screen (Biometric/PIN)
- [ ] Dashboard View:
  - [x] Total Balance / Burn Rate widget
  - [x] Recent Transactions list
  - [ ] Spending by Category (Pie Chart)
- [ ] Transactions List View (with search and filter)
- [ ] Subscription/Recurrent Tracking View
- [ ] Analytics & Reports View (Daily, Weekly, Monthly, Yearly)

## Phase 5: Financial Intelligence & Alerts
- [ ] Implement Cash Flow forecasting logic (Daily Burn Rate calculation)
- [ ] Build pattern recognition for recurrent expenses
- [ ] Setup local notification triggers for:
  - [ ] Approaching budget limits
  - [ ] Detected bill invoices (e.g., KPLC, Water)
  - [ ] Optimization suggestions (e.g., buying data bundles vs daily data)

## Phase 6: Privacy & Polish
- [ ] Implement "Hide Balances" toggle
- [ ] Add encrypted local/cloud backup functionality
- [ ] Final UI/UX polish (Animations, Dark Mode support)
- [ ] Play Store Compliance Check (specifically focusing on SMS generic permission policy)
