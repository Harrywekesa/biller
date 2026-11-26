package com.mpesa.tracker.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mpesa.tracker.data.local.entities.CategoryEntity
import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.data.local.entities.TransactionNature
import com.mpesa.tracker.data.local.entities.TransactionType
import com.mpesa.tracker.data.repository.TransactionRepository
import com.mpesa.tracker.data.repository.UserPreferencesRepository
import com.mpesa.tracker.domain.models.ReportPeriod
import com.mpesa.tracker.domain.parser.AccountSource
import com.mpesa.tracker.framework.services.SmsSyncService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

data class WalletSummaryUi(
    val source: AccountSource,
    val balanceLabel: String?,
    val txCount: Int
)

data class OverviewUiState(
    val mpesaBalance: String = "0.00",
    val isNegativeBalance: Boolean = false,
    val monthIncome: String = "0.00",
    val monthSpend: String = "0.00",
    val monthTransfers: String = "0.00",
    val monthFees: String = "0.00",
    val dailyAveragePace: String = "0.00",
    val todayTxCount: Int = 0,
    val uncategorizedCount: Int = 0,
    val overBudgetCount: Int = 0,
    val upcomingBillsCount: Int = 0,
    val wallets: List<WalletSummaryUi> = emptyList(),
    val recentTransactions: List<TransactionEntity> = emptyList()
)

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val userPreferences: UserPreferencesRepository,
    private val smsSyncService: SmsSyncService
) : ViewModel() {

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _selectedSimId = MutableStateFlow<Int?>(null)
    val selectedSimId: StateFlow<Int?> = _selectedSimId.asStateFlow()

    val isPrivacyMode: StateFlow<Boolean> = userPreferences.isPrivacyMode
    val selectedAccountFilter: StateFlow<String?> = userPreferences.selectedAccountFilter

    val activeSimIds: StateFlow<List<Int>> = repository.getActiveSimIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val fmt = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    private data class PeriodMetrics(
        val income: Double,
        val spend: Double,
        val transfers: Double,
        val fees: Double
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val periodMetricsFlow = _selectedSimId.flatMapLatest { simId ->
        combine(
            repository.getTotalIncomeForPeriod(ReportPeriod.THIS_MONTH, simId = simId),
            repository.getTotalSpentForPeriod(ReportPeriod.THIS_MONTH, simId = simId),
            repository.getTotalTransfersForPeriod(ReportPeriod.THIS_MONTH, simId = simId),
            repository.getTotalFeesForPeriod(ReportPeriod.THIS_MONTH, simId = simId)
        ) { inc, sp, tr, fee ->
            PeriodMetrics(inc ?: 0.0, sp ?: 0.0, tr ?: 0.0, fee ?: 0.0)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<OverviewUiState> = combine(
        _selectedSimId.flatMapLatest { simId -> repository.getAllTransactions(simId) },
        periodMetricsFlow,
        repository.getExpensesByCategory(ReportPeriod.THIS_MONTH),
        repository.getAllSubscriptions(),
        selectedAccountFilter
    ) { allTxs, metrics, categoryExpenses, subscriptions, accountFilter ->
        val mpesaTxs = allTxs.filter { it.accountSource == AccountSource.MPESA.name }
        val rawMpesaBal = computeMpesaBalance(mpesaTxs)

        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val elapsedDays = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).coerceAtLeast(1)

        val groupedWallets = allTxs.groupBy { it.accountSource }.toMutableMap()
        val estimatedCash = computeEstimatedCashBalance(allTxs)

        val wallets = groupedWallets.entries.map { (srcName, txs) ->
            val src = AccountSource.fromName(srcName)
            val bal: Double? = when (src) {
                AccountSource.MPESA -> computeMpesaBalance(txs)
                AccountSource.CASH -> estimatedCash
                else -> txs.firstOrNull { it.balance != null }?.balance
            }
            val label = bal?.let {
                if (it < 0) "-Ksh ${fmt.format(-it)}" else "Ksh ${fmt.format(it)}"
            }
            WalletSummaryUi(source = src, balanceLabel = label, txCount = txs.size)
        }.let { list ->
            if (estimatedCash != null && list.none { it.source == AccountSource.CASH }) {
                val cashLabel = if (estimatedCash < 0) "-Ksh ${fmt.format(-estimatedCash)}" else "Ksh ${fmt.format(estimatedCash)}"
                list + WalletSummaryUi(source = AccountSource.CASH, balanceLabel = cashLabel, txCount = 0)
            } else {
                list
            }
        }.sortedBy {
            when (it.source) {
                AccountSource.MPESA -> 0
                AccountSource.CASH -> 1
                else -> 2
            }
        }

        val now = System.currentTimeMillis()
        val threeDaysLater = now + 3L * 24 * 60 * 60 * 1000L
        val upcomingBills = subscriptions.count {
            it.isActive && it.expectedNextPaymentDate != null && it.expectedNextPaymentDate in now..threeDaysLater
        }

        val overBudgets = categoryExpenses.count {
            it.budgetLimit != null && it.budgetLimit > 0 && it.totalAmount > it.budgetLimit
        }

        val filteredRecent = if (accountFilter == null) allTxs
        else allTxs.filter { it.accountSource == accountFilter }

        OverviewUiState(
            mpesaBalance = if (rawMpesaBal < 0) "-${fmt.format(-rawMpesaBal)}" else fmt.format(rawMpesaBal),
            isNegativeBalance = rawMpesaBal < 0,
            monthIncome = fmt.format(metrics.income),
            monthSpend = fmt.format(metrics.spend),
            monthTransfers = fmt.format(metrics.transfers),
            monthFees = fmt.format(metrics.fees),
            dailyAveragePace = fmt.format(metrics.spend / elapsedDays),
            todayTxCount = allTxs.count { it.dateTimestamp >= todayStart },
            uncategorizedCount = allTxs.count {
                it.natureEnum == TransactionNature.EXPENSE && it.categoryId == null
            },
            overBudgetCount = overBudgets,
            upcomingBillsCount = upcomingBills,
            wallets = wallets,
            recentTransactions = filteredRecent.take(8)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OverviewUiState())

    fun togglePrivacyMode() = userPreferences.togglePrivacyMode()
    fun setSimFilter(simId: Int?) { _selectedSimId.value = simId }
    fun setAccountFilter(source: String?) = userPreferences.setSelectedAccountFilter(source)

    fun syncNow() {
        if (_isSyncing.value) return
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                smsSyncService.syncHistoricMessages()
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun updateTransactionNature(receiptNumber: String, newNature: TransactionNature, saveRule: Boolean) {
        viewModelScope.launch {
            repository.updateTransactionNature(receiptNumber, newNature, saveRule)
        }
    }

    fun updateTransactionCategory(receiptNumber: String, categoryId: Int, merchantName: String, applyToAll: Boolean) {
        viewModelScope.launch {
            repository.updateTransactionCategoryAndSaveRule(receiptNumber, categoryId, merchantName, applyToAll)
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch { repository.addCategory(name) }
    }

    fun updateUserNote(receiptNumber: String, note: String?) {
        viewModelScope.launch { repository.updateUserNote(receiptNumber, note) }
    }

    fun deleteTransaction(receiptNumber: String) {
        viewModelScope.launch { repository.deleteTransaction(receiptNumber) }
    }

    fun addManualTransaction(
        merchant: String,
        amount: Double,
        fee: Double,
        nature: TransactionNature,
        source: AccountSource,
        categoryId: Int?,
        note: String?,
        dateTimestamp: Long,
        businessCode: String?
    ) {
        viewModelScope.launch {
            repository.insertManualTransaction(
                merchant = merchant,
                amount = amount,
                fee = fee,
                nature = nature,
                source = source,
                categoryId = categoryId,
                note = note,
                dateTimestamp = dateTimestamp,
                businessCode = businessCode
            )
        }
    }

    /** Computes closed-loop estimated Cash on Hand from M-Pesa cash withdrawals + manual Cash entries. */
    private fun computeEstimatedCashBalance(allTxs: List<TransactionEntity>): Double? {
        val withdrawnCash = allTxs.filter {
            it.type == TransactionType.WITHDRAW_CASH && it.natureEnum != TransactionNature.REVERSAL
        }.sumOf { it.amount }
        val cashTxs = allTxs.filter {
            it.accountSource == AccountSource.CASH.name && it.natureEnum != TransactionNature.REVERSAL
        }
        if (withdrawnCash == 0.0 && cashTxs.isEmpty()) return null
        val cashIn = cashTxs.filter {
            it.natureEnum == TransactionNature.INCOME || it.natureEnum == TransactionNature.TRANSFER
        }.sumOf { it.amount }
        val cashOut = cashTxs.filter {
            it.natureEnum == TransactionNature.EXPENSE
        }.sumOf { it.amount + it.transactionCost }
        return withdrawnCash + cashIn - cashOut
    }

    private fun computeMpesaBalance(mpesaTxs: List<TransactionEntity>): Double {
        if (mpesaTxs.isEmpty()) return 0.0
        val knownSimTxs = mpesaTxs.filter { it.simSubscriptionId != null }
        return if (knownSimTxs.isNotEmpty()) {
            knownSimTxs.groupBy { it.simSubscriptionId }.values.sumOf { singleSimBalance(it) }
        } else {
            singleSimBalance(mpesaTxs)
        }
    }

    private fun singleSimBalance(txs: List<TransactionEntity>): Double {
        val latest = txs.firstOrNull { it.balance != null } ?: return 0.0
        return when {
            latest.recipientName == "Fuliza M-PESA" && latest.balance != null -> -(latest.balance)
            latest.balance == 0.0 -> {
                val recentFuliza = txs.firstOrNull { it.recipientName == "Fuliza M-PESA" && it.balance != null }
                when {
                    recentFuliza?.balance != null && recentFuliza.balance > 0 -> -(recentFuliza.balance)
                    latest.fulizaAmount != null -> -(latest.fulizaAmount)
                    else -> 0.0
                }
            }
            else -> latest.balance ?: 0.0
        }
    }
}
