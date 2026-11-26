package com.mpesa.tracker.data.repository

import com.mpesa.tracker.data.local.dao.*
import com.mpesa.tracker.data.local.entities.*
import com.mpesa.tracker.domain.classifier.CategorizationEngine
import com.mpesa.tracker.domain.models.ReportPeriod
import com.mpesa.tracker.domain.parser.AccountSource
import com.mpesa.tracker.framework.services.NotificationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val customRuleDao: CustomRuleDao,
    private val budgetDao: BudgetDao,
    private val transferRuleDao: TransferRuleDao,
    private val unparsedSmsDao: UnparsedSmsDao,
    private val subscriptionDao: SubscriptionDao,
    private val categorizationEngine: CategorizationEngine,
    private val userPreferences: UserPreferencesRepository,
    private val notificationService: NotificationService
) {

    fun getAllTransactions(simId: Int? = null): Flow<List<TransactionEntity>> =
        transactionDao.getAllTransactions(simId)

    fun getActiveSimIds(): Flow<List<Int>> = transactionDao.getActiveSimIds()

    fun getActiveAccountSources(): Flow<List<String>> = transactionDao.getActiveAccountSources()

    fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    fun getAllCustomRules(): Flow<List<CustomRuleEntity>> = customRuleDao.getAllRules()

    fun getAllTransferRules(): Flow<List<TransferRuleEntity>> = transferRuleDao.getAllRules()

    fun getUnparsedSmsLogs(): Flow<List<UnparsedSmsEntity>> = unparsedSmsDao.getAll()

    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>> = subscriptionDao.getAllSubscriptions()

    suspend fun deleteAllTransactions() {
        transactionDao.deleteAllTransactions()
    }

    suspend fun deleteTransaction(receiptNumber: String) {
        transactionDao.deleteTransaction(receiptNumber)
    }

    suspend fun deleteTransactions(receiptNumbers: List<String>) {
        if (receiptNumbers.isNotEmpty()) {
            transactionDao.deleteTransactions(receiptNumbers)
        }
    }

    suspend fun updateUserNote(receiptNumber: String, note: String?) {
        val cleaned = note?.trim()?.takeIf { it.isNotEmpty() }
        transactionDao.updateUserNote(receiptNumber, cleaned)
    }

    suspend fun addCategory(name: String, colorCode: String = "#00C853", iconName: String = "category") {
        categoryDao.insertCategory(CategoryEntity(name = name, colorCode = colorCode, iconName = iconName))
    }

    suspend fun updateCategory(category: CategoryEntity) {
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(categoryId: Int) {
        categoryDao.deleteCategory(categoryId)
    }

    suspend fun deleteCustomRule(merchantName: String) {
        customRuleDao.deleteRule(merchantName)
    }

    suspend fun deleteTransferRule(merchantKey: String) {
        transferRuleDao.deleteRule(merchantKey)
    }

    suspend fun clearUnparsedSmsLogs() {
        unparsedSmsDao.clearAll()
    }

    suspend fun logUnparsedSms(sender: String, body: String, bodyHash: String, receivedAt: Long) {
        unparsedSmsDao.insert(
            UnparsedSmsEntity(
                sender = sender,
                body = body,
                bodyHash = bodyHash,
                receivedAt = receivedAt
            )
        )
    }

    suspend fun recordMpesaRefTransferHint(mpesaRef: String) {
        val cleanRef = mpesaRef.trim().uppercase()
        if (cleanRef.isBlank()) return
        transferRuleDao.insertOrUpdateRule(
            TransferRuleEntity(merchantKey = "REF:$cleanRef", nature = TransactionNature.TRANSFER.name)
        )
        // If that M-Pesa receipt is already stored in DB, convert it cleanly without deleting data
        transactionDao.getTransactionByReceipt(cleanRef)?.let { existing ->
            if (existing.transactionNature != TransactionNature.TRANSFER.name || existing.transferGroupRef != cleanRef) {
                transactionDao.updateTransaction(
                    existing.copy(
                        transactionNature = TransactionNature.TRANSFER.name,
                        transferGroupRef = cleanRef,
                        categoryId = null
                    )
                )
            }
        }
        transactionDao.getTransactionsByTransferRef(cleanRef).forEach { leg ->
            if (leg.transactionNature != TransactionNature.TRANSFER.name) {
                transactionDao.updateTransaction(
                    leg.copy(
                        transactionNature = TransactionNature.TRANSFER.name,
                        transferGroupRef = cleanRef,
                        categoryId = null
                    )
                )
            }
        }
    }

    suspend fun setCategoryBudget(categoryId: Int, limit: Double) {
        budgetDao.insertOrUpdateBudget(BudgetEntity(categoryId = categoryId, monthlyLimit = limit))
    }

    suspend fun clearCategoryBudget(categoryId: Int) {
        budgetDao.deleteBudgetForCategory(categoryId)
    }

    fun getTotalSpentForPeriod(period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null, simId: Int? = null): Flow<Double?> {
        val (start, end) = getTimestampRangeForPeriod(period, customStart, customEnd)
        return transactionDao.getTotalSpentBetween(start, end, simId)
    }

    fun getTotalIncomeForPeriod(period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null, simId: Int? = null): Flow<Double?> {
        val (start, end) = getTimestampRangeForPeriod(period, customStart, customEnd)
        return transactionDao.getTotalIncomeBetween(start, end, simId)
    }

    fun getTotalTransfersForPeriod(period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null, simId: Int? = null): Flow<Double?> {
        val (start, end) = getTimestampRangeForPeriod(period, customStart, customEnd)
        return transactionDao.getTotalTransfersBetween(start, end, simId)
    }

    fun getTotalFeesForPeriod(period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null, simId: Int? = null): Flow<Double?> {
        val (start, end) = getTimestampRangeForPeriod(period, customStart, customEnd)
        return transactionDao.getTotalFeesBetween(start, end, simId)
    }

    fun getSpentForPerson(personName: String, period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null, simId: Int? = null): Flow<Double?> {
        val (start, end) = getTimestampRangeForPeriod(period, customStart, customEnd)
        return transactionDao.getSpentForPersonBetween(personName, start, end, simId)
    }

    fun getIncomeFromPerson(personName: String, period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null, simId: Int? = null): Flow<Double?> {
        val (start, end) = getTimestampRangeForPeriod(period, customStart, customEnd)
        return transactionDao.getIncomeFromPersonBetween(personName, start, end, simId)
    }

    fun getFeesForPerson(personName: String, period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null, simId: Int? = null): Flow<Double?> {
        val (start, end) = getTimestampRangeForPeriod(period, customStart, customEnd)
        return transactionDao.getFeesForPersonBetween(personName, start, end, simId)
    }

    /**
     * Unified ingestion pipeline used by SmsSyncService, MpesaSmsWorker, BankSmsWorker, and Manual Entry:
     * 1) Deduplicates via primary key / cross-bank echo correlation.
     * 2) Resolves [TransactionNature] (Reversals, Fuliza debt, Internal Transfers vs True Income/Expense).
     * 3) Categorizes ONLY true [TransactionNature.EXPENSE] entries with CustomRule FIRST, then Dictionary.
     * 4) Triggers Budget 80%/100% threshold alerts when applicable.
     */
    suspend fun ingestTransaction(
        rawTx: TransactionEntity,
        linkedMpesaRef: String? = null,
        isTransferCandidate: Boolean = false
    ) {
        var tx = rawTx

        // 1. Bidirectional Reversal linking (order-independent so spend & fees always zero out)
        val linkedReceipt = tx.linkedReceiptNumber
        if (tx.natureEnum == TransactionNature.REVERSAL && !linkedReceipt.isNullOrBlank()) {
            markOriginalTransactionReversed(linkedReceipt, tx.receiptNumber)
        } else if (tx.natureEnum != TransactionNature.REVERSAL) {
            transactionDao.findReversalPointingTo(tx.receiptNumber)?.let { reversalTx ->
                tx = tx.copy(
                    transactionNature = TransactionNature.REVERSAL.name,
                    transferGroupRef = reversalTx.receiptNumber,
                    categoryId = null
                )
            }
        }

        // 2. Check explicit Transfer Rules override (merchant or ref hint)
        val refRule = transferRuleDao.getNatureForMerchant("REF:${tx.receiptNumber.uppercase()}")
        val linkedRefRule = linkedMpesaRef?.trim()?.uppercase()?.takeIf { it.isNotEmpty() }
            ?.let { transferRuleDao.getNatureForMerchant("REF:$it") }
        val merchantRule = transferRuleDao.getNatureForMerchant(tx.recipientName.uppercase())
        val explicitNature = refRule ?: linkedRefRule ?: merchantRule

        var resolvedNature = if (explicitNature != null) {
            TransactionNature.fromName(explicitNature)
        } else {
            resolveDefaultNature(tx, isTransferCandidate)
        }

        var groupRef = tx.transferGroupRef ?: linkedMpesaRef

        // 3. Bank <-> M-Pesa two-leg reconciliation!
        val isGatewayAccepted = tx.rawSmsBody.contains("MPESA payment accepted", ignoreCase = true)
        val isGenericBankDebit = !tx.isIncome && tx.rawSmsBody.contains("Digital/Branch Banking", ignoreCase = true)

        if (!linkedMpesaRef.isNullOrBlank()) {
            // Bank SMS carrying an M-PESA Ref ID (e.g. UKJIR2TE9Z / UJ9IR9NRDY)
            val mpesaLeg = transactionDao.getTransactionByReceipt(linkedMpesaRef)
            if (mpesaLeg != null && mpesaLeg.isIncome) {
                // Incoming M-Pesa leg exists in user's own inbox => confirmed self-transfer!
                resolvedNature = TransactionNature.TRANSFER
                groupRef = linkedMpesaRef
                transactionDao.updateTransaction(
                    mpesaLeg.copy(
                        transactionNature = TransactionNature.TRANSFER.name,
                        transferGroupRef = linkedMpesaRef,
                        categoryId = null
                    )
                )
            }

            // Correlate gateway "MPESA payment accepted" with a nearby Absa/Bank Account generic debit of exact amount
            if (isGatewayAccepted) {
                val nearbyBankDebit = transactionDao.findNearbyMatchingTransaction(
                    source = tx.accountSource,
                    amount = tx.amount,
                    nature = TransactionNature.EXPENSE.name,
                    timestampMs = tx.dateTimestamp
                )
                if (nearbyBankDebit != null &&
                    nearbyBankDebit.receiptNumber != tx.receiptNumber &&
                    nearbyBankDebit.rawSmsBody.contains("Digital/Branch Banking", ignoreCase = true)
                ) {
                    // Merge into the canonical bank debit as a TRANSFER and skip storing redundant gateway echo
                    transactionDao.updateTransaction(
                        nearbyBankDebit.copy(
                            transactionNature = TransactionNature.TRANSFER.name,
                            transferGroupRef = linkedMpesaRef,
                            categoryId = null,
                            userNote = nearbyBankDebit.userNote ?: "To M-Pesa ($linkedMpesaRef · ${tx.recipientName})"
                        )
                    )
                    return
                }
            }
        } else if (isGenericBankDebit) {
            // Reverse arrival order: generic bank debit arrived AFTER "MPESA payment accepted" was already ingested
            val nearbyGatewayTransfer = transactionDao.findNearbyMatchingTransaction(
                source = tx.accountSource,
                amount = tx.amount,
                nature = TransactionNature.TRANSFER.name,
                timestampMs = tx.dateTimestamp
            )
            if (nearbyGatewayTransfer != null &&
                nearbyGatewayTransfer.receiptNumber != tx.receiptNumber &&
                nearbyGatewayTransfer.rawSmsBody.contains("MPESA payment accepted", ignoreCase = true)
            ) {
                resolvedNature = TransactionNature.TRANSFER
                groupRef = nearbyGatewayTransfer.transferGroupRef
                if (nearbyGatewayTransfer.userNote.isNullOrBlank()) {
                    tx = tx.copy(userNote = tx.userNote ?: "To M-Pesa (${nearbyGatewayTransfer.transferGroupRef ?: ""} · ${nearbyGatewayTransfer.recipientName})".trim())
                }
                // Remove redundant gateway echo now that canonical bank debit owns the transfer group
                transactionDao.deleteTransaction(nearbyGatewayTransfer.receiptNumber)
            }
        }

        // Conversely, if incoming M-Pesa leg arrives AFTER Bank-to-M-PESA leg:
        if (tx.accountSource == AccountSource.MPESA.name && tx.isIncome) {
            val pairedBankLegs = transactionDao.getTransactionsByTransferRef(tx.receiptNumber)
            if (pairedBankLegs.isNotEmpty()) {
                resolvedNature = TransactionNature.TRANSFER
                groupRef = tx.receiptNumber
                pairedBankLegs.forEach { bankLeg ->
                    if (bankLeg.transactionNature != TransactionNature.TRANSFER.name) {
                        transactionDao.updateTransaction(
                            bankLeg.copy(
                                transactionNature = TransactionNature.TRANSFER.name,
                                transferGroupRef = tx.receiptNumber,
                                categoryId = null
                            )
                        )
                    }
                }
            }
        }

        tx = tx.copy(
            transactionNature = resolvedNature.name,
            transferGroupRef = groupRef
        )

        // 4. Categorize strictly if EXPENSE (CustomRule ALWAYS precedes Dictionary!)
        if (tx.natureEnum == TransactionNature.EXPENSE && tx.categoryId == null) {
            val catId = categorizationEngine.categorize(tx)
            tx = tx.copy(categoryId = catId)
        } else if (tx.natureEnum != TransactionNature.EXPENSE) {
            tx = tx.copy(categoryId = null)
        }

        val insertedRowId = transactionDao.insertTransaction(tx)

        // 5. Real-time Budget Alert check (for new EXPENSE insertions)
        val finalCatId = tx.categoryId
        if (insertedRowId != -1L && tx.natureEnum == TransactionNature.EXPENSE && finalCatId != null) {
            checkAndNotifyBudgetThreshold(finalCatId)
        }
    }

    /** Backwards-compatible wrapper for callers passing raw TransactionEntity. */
    suspend fun insertTransaction(transaction: TransactionEntity) {
        ingestTransaction(transaction)
    }

    private fun resolveDefaultNature(
        tx: TransactionEntity,
        isTransferCandidate: Boolean
    ): TransactionNature {
        if (tx.natureEnum == TransactionNature.REVERSAL) return TransactionNature.REVERSAL
        if (tx.type == TransactionType.FULIZA_DRAW) return TransactionNature.LOAN_DRAW
        if (tx.type == TransactionType.FULIZA_REPAYMENT) return TransactionNature.LOAN_REPAY
        if (tx.type == TransactionType.WITHDRAW_CASH) return TransactionNature.TRANSFER

        val upperName = tx.recipientName.uppercase()
        val upperBody = tx.rawSmsBody.uppercase()
        val selfIds = userPreferences.selfIdentifiers.value

        val matchesSelfIdentifier = selfIds.any { id ->
            upperName.contains(id) || (tx.recipientNumber?.uppercase()?.contains(id) == true) || upperBody.contains(id)
        }

        if (matchesSelfIdentifier && (isTransferCandidate || tx.type == TransactionType.SEND_MONEY || tx.isIncome)) {
            return TransactionNature.TRANSFER
        }

        // Known Kenyan self-wallet / Bank-to-M-Pesa inflow strings (non-destructive: user can still flip 1-tap!)
        val selfWalletTokens = listOf("M-SHWARI", "MSHWARI", "KCB M-PESA", "ZIIDI", "LOCK SAVINGS")
        if (selfWalletTokens.any { upperName.contains(it) }) {
            return TransactionNature.TRANSFER
        }

        // Incoming M-Pesa from known own bank apps (Equity, I&M, Absa) are treated as internal transfers
        // so Bank Credit + Bank-to-M-Pesa doesn't double-count income. User can always flip via 1-tap toggle!
        if (tx.isIncome && tx.accountSource == AccountSource.MPESA.name) {
            val ownBankInboundTokens = listOf("IM BANK LIMITED", "I&M BANK", "ABSA BANK", "EQUITY BANK", "EQUITY BULK")
            if (ownBankInboundTokens.any { upperName.contains(it) }) {
                return TransactionNature.TRANSFER
            }
        }

        return if (tx.isIncome) TransactionNature.INCOME else TransactionNature.EXPENSE
    }

    private suspend fun markOriginalTransactionReversed(originalReceipt: String, reversalReceipt: String) {
        transactionDao.getTransactionByReceipt(originalReceipt)?.let { original ->
            if (original.natureEnum != TransactionNature.REVERSAL || original.transferGroupRef != reversalReceipt) {
                transactionDao.updateTransaction(
                    original.copy(
                        transactionNature = TransactionNature.REVERSAL.name,
                        transferGroupRef = reversalReceipt,
                        categoryId = null
                    )
                )
            }
        }
    }

    /** Reconciles any existing M-Pesa reversal records against their original transactions safely. */
    suspend fun reconcileExistingReversals() {
        transactionDao.getAllReversals().forEach { rev ->
            val target = rev.linkedReceiptNumber?.trim()?.takeIf { it.isNotEmpty() } ?: return@forEach
            markOriginalTransactionReversed(target, rev.receiptNumber)
        }
    }

    /**
     * One-time safe, non-destructive V2 reconciliation pass across existing persisted transactions.
     * Links existing Bank <-> M-Pesa transfer pairs and removes stale WITHDRAW_CASH fee pollution
     * without ever deleting a single transaction, note, budget, or rule.
     */
    suspend fun reconcileExistingDataOnceIfNeeded() {
        reconcileExistingReversals()
        if (userPreferences.wasTransferReconciledV2()) return
        val all = transactionDao.getAllTransactionsOnce()
        val byReceipt = all.associateBy { it.receiptNumber }

        for (tx in all) {
            val groupRef = tx.transferGroupRef ?: tx.linkedReceiptNumber
            if (!groupRef.isNullOrBlank()) {
                val other = byReceipt[groupRef]
                if (other != null && other.isIncome && other.accountSource == AccountSource.MPESA.name) {
                    transactionDao.updateTransaction(
                        tx.copy(transactionNature = TransactionNature.TRANSFER.name, transferGroupRef = groupRef, categoryId = null)
                    )
                    transactionDao.updateTransaction(
                        other.copy(transactionNature = TransactionNature.TRANSFER.name, transferGroupRef = groupRef, categoryId = null)
                    )
                }
            } else if (tx.isIncome && tx.accountSource == AccountSource.MPESA.name) {
                val upper = tx.recipientName.uppercase()
                if (upper.contains("IM BANK LIMITED") || upper.contains("ABSA BANK") || upper.contains("EQUITY BANK")) {
                    transactionDao.updateTransaction(
                        tx.copy(transactionNature = TransactionNature.TRANSFER.name, categoryId = null)
                    )
                }
            }
        }
        userPreferences.markTransferReconciledV2()
    }

    /** Updates transaction nature (e.g. marking as Internal Transfer or Expense) with optional rule persistence. */
    suspend fun updateTransactionNature(
        receiptNumber: String,
        newNature: TransactionNature,
        saveRuleForMerchant: Boolean = false
    ) {
        val tx = transactionDao.getTransactionByReceipt(receiptNumber) ?: return
        val newCategoryId = if (newNature == TransactionNature.EXPENSE) {
            tx.categoryId ?: categorizationEngine.categorize(tx.copy(transactionNature = newNature.name))
        } else null

        if (saveRuleForMerchant) {
            transferRuleDao.insertOrUpdateRule(
                TransferRuleEntity(merchantKey = tx.recipientName.uppercase(), nature = newNature.name)
            )
            transactionDao.updateNatureForMerchant(tx.recipientName, newNature.name)
        } else {
            transactionDao.updateTransaction(
                tx.copy(transactionNature = newNature.name, categoryId = newCategoryId)
            )
        }
    }

    suspend fun updateTransactionCategoryAndSaveRule(
        receiptNumber: String,
        newCategoryId: Int,
        merchantName: String,
        applyToAllFromMerchant: Boolean = true
    ) {
        val tx = transactionDao.getTransactionByReceipt(receiptNumber) ?: return
        if (applyToAllFromMerchant) {
            transactionDao.updateCategoryForMerchant(merchantName, newCategoryId, tx.transactionNature)
            customRuleDao.insertOrUpdateRule(CustomRuleEntity(merchantName = merchantName, categoryId = newCategoryId))

            val businessCode = tx.recipientNumber?.trim()?.takeIf {
                it.isNotEmpty() && (tx.type == TransactionType.PAYBILL || tx.type == TransactionType.BUY_GOODS)
            }
            if (businessCode != null) {
                transactionDao.updateCategoryForCode(businessCode, newCategoryId, tx.transactionNature)
                customRuleDao.insertOrUpdateRule(CustomRuleEntity(merchantName = "CODE:$businessCode", categoryId = newCategoryId))
            }
        } else {
            transactionDao.updateSingleTransactionCategory(receiptNumber, newCategoryId)
        }
        checkAndNotifyBudgetThreshold(newCategoryId)
    }

    /** Registers or updates a Paybill/Till code categorization rule and applies it to matching existing expenses. */
    suspend fun savePaybillCodeRule(code: String, categoryId: Int) {
        val cleanCode = code.trim()
        if (cleanCode.isEmpty()) return
        customRuleDao.insertOrUpdateRule(CustomRuleEntity(merchantName = "CODE:$cleanCode", categoryId = categoryId))
        transactionDao.updateCategoryForCode(cleanCode, categoryId, TransactionNature.EXPENSE.name)
        checkAndNotifyBudgetThreshold(categoryId)
    }

    /** Single shared entry point for inserting manual cash / wallet transactions cleanly. */
    suspend fun insertManualTransaction(
        merchant: String,
        amount: Double,
        fee: Double,
        nature: TransactionNature,
        source: AccountSource,
        categoryId: Int?,
        note: String?,
        dateTimestamp: Long = System.currentTimeMillis(),
        businessCode: String? = null
    ) {
        val type = when (nature) {
            TransactionNature.INCOME -> TransactionType.RECEIVED_MONEY
            TransactionNature.TRANSFER -> TransactionType.UNKNOWN
            else -> TransactionType.BUY_GOODS
        }
        val cleanCode = businessCode?.trim()?.takeIf { it.isNotEmpty() }
        val tx = TransactionEntity(
            receiptNumber = "MAN-${java.util.UUID.randomUUID().toString().take(8).uppercase()}",
            type = type,
            amount = amount,
            transactionCost = fee,
            dateTimestamp = dateTimestamp,
            recipientName = merchant.trim(),
            recipientNumber = cleanCode,
            categoryId = if (nature == TransactionNature.EXPENSE) categoryId else null,
            isIncome = nature == TransactionNature.INCOME,
            rawSmsBody = "Manual Entry",
            accountSource = source.name,
            transactionNature = nature.name,
            userNote = note?.trim()?.takeIf { it.isNotEmpty() },
            isManualEntry = true
        )
        ingestTransaction(tx)
    }

    suspend fun updateSingleTransactionCategory(receiptNumber: String, categoryId: Int) {
        transactionDao.updateSingleTransactionCategory(receiptNumber, categoryId)
        checkAndNotifyBudgetThreshold(categoryId)
    }

    private suspend fun checkAndNotifyBudgetThreshold(categoryId: Int) {
        if (!userPreferences.budgetAlertsEnabled.value) return
        val budget = budgetDao.getBudgetForCategory(categoryId) ?: return
        if (budget.monthlyLimit <= 0.0) return

        val expenses = getExpensesByCategory(ReportPeriod.THIS_MONTH).firstOrNull() ?: return
        val catExpense = expenses.firstOrNull { it.categoryId == categoryId } ?: return
        val pct = ((catExpense.totalAmount / budget.monthlyLimit) * 100.0).toInt()
        val monthKey = SimpleDateFormat("yyyy-MM", Locale.US).format(Date())
        val lastNotified = userPreferences.getNotifiedBudgetThreshold(monthKey, categoryId)

        val fmt = NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 }
        val catName = catExpense.categoryName ?: "Category"

        if (pct >= 100 && lastNotified < 100) {
            userPreferences.setNotifiedBudgetThreshold(monthKey, categoryId, 100)
            notificationService.showBudgetAlert(
                "$catName has exceeded its Ksh ${fmt.format(budget.monthlyLimit)} monthly budget (Ksh ${fmt.format(catExpense.totalAmount)} spent)."
            )
        } else if (pct in 80..99 && lastNotified < 80) {
            userPreferences.setNotifiedBudgetThreshold(monthKey, categoryId, 80)
            notificationService.showBudgetAlert(
                "$catName is at $pct% of its Ksh ${fmt.format(budget.monthlyLimit)} monthly budget."
            )
        }
    }

    /**
     * Persists a recurring subscription to SQLite so [PlanScreen] and [BillReminderWorker]
     * share a single authoritative schedule.
     */
    suspend fun saveTrackedSubscription(
        name: String,
        merchantMatcher: String,
        amount: Double,
        billingCycleDays: Int = 30
    ) {
        val categories = categoryDao.getAllCategories().firstOrNull() ?: emptyList()
        val subCatId = categories.firstOrNull { it.name.equals("Subscriptions", ignoreCase = true) }?.id

        // Find last payment timestamp for this merchant to compute real expectedNextPaymentDate
        val allTxs = transactionDao.getAllTransactionsOnce()
        val matchingTxs = allTxs
            .filter { it.recipientName.contains(merchantMatcher, ignoreCase = true) && it.natureEnum == TransactionNature.EXPENSE }
            .sortedBy { it.dateTimestamp }

        val lastDate = matchingTxs.lastOrNull()?.dateTimestamp ?: System.currentTimeMillis()
        var nextDue = lastDate + billingCycleDays * 24L * 60L * 60L * 1000L
        val now = System.currentTimeMillis()
        while (nextDue < now) {
            nextDue += billingCycleDays * 24L * 60L * 60L * 1000L
        }

        val existing = subscriptionDao.getSubscriptionByMatcher(merchantMatcher)
        if (existing != null) {
            subscriptionDao.updateSubscription(
                existing.copy(
                    amount = amount,
                    billingCycleDays = billingCycleDays,
                    categoryId = subCatId,
                    isActive = true,
                    expectedNextPaymentDate = nextDue
                )
            )
        } else {
            subscriptionDao.insertSubscription(
                SubscriptionEntity(
                    name = name.take(28),
                    merchantNameMatcher = merchantMatcher,
                    amount = amount,
                    isActive = true,
                    billingCycleDays = billingCycleDays,
                    categoryId = subCatId,
                    expectedNextPaymentDate = nextDue
                )
            )
        }

        if (subCatId != null && matchingTxs.isNotEmpty()) {
            transactionDao.updateCategoryForMerchant(matchingTxs.first().recipientName, subCatId, TransactionNature.EXPENSE.name)
            customRuleDao.insertOrUpdateRule(CustomRuleEntity(matchingTxs.first().recipientName, subCatId))
        }
    }

    suspend fun deleteSubscription(subscriptionId: Int) {
        subscriptionDao.deleteSubscription(subscriptionId)
    }

    suspend fun toggleSubscriptionActive(subscription: SubscriptionEntity) {
        subscriptionDao.updateSubscription(subscription.copy(isActive = !subscription.isActive))
    }

    private fun getTimestampRangeForPeriod(period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        var endTimestamp = System.currentTimeMillis()
        var startTimestamp = 0L

        when (period) {
            ReportPeriod.THIS_WEEK -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                startTimestamp = calendar.timeInMillis

                val endCal = Calendar.getInstance().apply {
                    timeInMillis = startTimestamp
                    add(Calendar.DAY_OF_YEAR, 7)
                    add(Calendar.MILLISECOND, -1)
                }
                endTimestamp = endCal.timeInMillis
            }
            ReportPeriod.THIS_MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                startTimestamp = calendar.timeInMillis

                val endCal = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                    set(Calendar.MILLISECOND, 999)
                }
                endTimestamp = endCal.timeInMillis
            }
            ReportPeriod.LAST_MONTH -> {
                calendar.add(Calendar.MONTH, -1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                startTimestamp = calendar.timeInMillis

                val endCalendar = Calendar.getInstance().apply {
                    add(Calendar.MONTH, -1)
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                    set(Calendar.MILLISECOND, 999)
                }
                return Pair(startTimestamp, endCalendar.timeInMillis)
            }
            ReportPeriod.ALL_TIME -> {
                startTimestamp = 0L
                endTimestamp = Long.MAX_VALUE
            }
            ReportPeriod.CUSTOM -> {
                return Pair(customStart ?: 0L, customEnd ?: endTimestamp)
            }
        }
        return Pair(startTimestamp, endTimestamp)
    }

    fun getExpensesByCategory(period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null, simId: Int? = null): Flow<List<CategoryExpense>> {
        val (start, end) = getTimestampRangeForPeriod(period, customStart, customEnd)
        return transactionDao.getExpensesByCategory(start, end, simId)
    }

    fun getExpensesByCategoryForLastNMonths(monthsAgo: Int, simId: Int? = null): Flow<List<CategoryExpense>> {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.MONTH, -monthsAgo)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = calendar.timeInMillis

        val endCal = Calendar.getInstance().apply {
            add(Calendar.MONTH, -monthsAgo)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return transactionDao.getExpensesByCategory(start, endCal.timeInMillis, simId)
    }

    fun getDailySpendingTrend(period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null, simId: Int? = null): Flow<List<DailySpend>> {
        val (start, end) = getTimestampRangeForPeriod(period, customStart, customEnd)
        return transactionDao.getDailySpendingTrend(start, end, simId)
    }

    fun getSpendingByCategoryAndDayOfWeek(period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null, simId: Int? = null): Flow<List<CategoryDaySpend>> {
        val (start, end) = getTimestampRangeForPeriod(period, customStart, customEnd)
        return transactionDao.getSpendingByCategoryAndDayOfWeek(start, end, simId)
    }

    fun getTopMerchants(limit: Int = 10, simId: Int? = null): Flow<List<MerchantSummary>> =
        transactionDao.getTopMerchants(limit, simId)

    fun getFulizaUsageByMonth(simId: Int? = null): Flow<List<MonthlyTotal>> =
        transactionDao.getFulizaUsageByMonth(simId)

    fun getMonthlyExpenseTotals(monthsBack: Int = 4, simId: Int? = null): Flow<List<MonthlyTotal>> =
        transactionDao.getMonthlyExpenseTotals(monthWindowStart(monthsBack), simId)

    fun getMonthlyIncomeTotals(monthsBack: Int = 4, simId: Int? = null): Flow<List<MonthlyTotal>> =
        transactionDao.getMonthlyIncomeTotals(monthWindowStart(monthsBack), simId)

    fun getExpensesByAccountSource(period: ReportPeriod, customStart: Long? = null, customEnd: Long? = null): Flow<List<MonthlyTotal>> {
        val (start, end) = getTimestampRangeForPeriod(period, customStart, customEnd)
        return transactionDao.getExpensesByAccountSource(start, end)
    }

    private fun monthWindowStart(monthsBack: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -(monthsBack - 1).coerceAtLeast(0))
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
