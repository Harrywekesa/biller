package com.mpesa.tracker.ui.analytics

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mpesa.tracker.data.local.entities.CategoryEntity
import com.mpesa.tracker.data.local.entities.CategoryExpense
import com.mpesa.tracker.data.local.entities.DailySpend
import com.mpesa.tracker.data.local.entities.MerchantSummary
import com.mpesa.tracker.data.local.entities.MonthlyTotal
import com.mpesa.tracker.data.repository.TransactionRepository
import com.mpesa.tracker.data.repository.UserPreferencesRepository
import com.mpesa.tracker.domain.export.CsvExporter
import com.mpesa.tracker.domain.intelligence.FinancialIntelligenceEngine
import com.mpesa.tracker.domain.intelligence.FinancialScore
import com.mpesa.tracker.domain.intelligence.SpendingAnomaly
import com.mpesa.tracker.domain.models.ReportPeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class MonthlyComparisonPoint(
    val monthKey: String,
    val income: Double,
    val expense: Double
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val userPreferences: UserPreferencesRepository,
    private val financialEngine: FinancialIntelligenceEngine
) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow(ReportPeriod.THIS_MONTH)
    val selectedPeriod: StateFlow<ReportPeriod> = _selectedPeriod.asStateFlow()

    private val _selectedSimId = MutableStateFlow<Int?>(null)
    val selectedSimId: StateFlow<Int?> = _selectedSimId.asStateFlow()

    val isPrivacyMode: StateFlow<Boolean> = userPreferences.isPrivacyMode

    val activeSimIds: StateFlow<List<Int>> = repository.getActiveSimIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private data class AnalyticsArgs(
        val period: ReportPeriod,
        val start: Long?,
        val end: Long?,
        val simId: Int?
    )

    private val _customStartDate = MutableStateFlow<Long?>(null)
    private val _customEndDate = MutableStateFlow<Long?>(null)

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()

    private val argsFlow = combine(
        _selectedPeriod,
        _customStartDate,
        _customEndDate,
        _selectedSimId
    ) { p, s, e, sim -> AnalyticsArgs(p, s, e, sim) }

    @OptIn(ExperimentalCoroutinesApi::class)
    val expensesByCategory: StateFlow<List<CategoryExpense>> = argsFlow
        .flatMapLatest { repository.getExpensesByCategory(it.period, it.start, it.end, it.simId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val dailySpendTrend: StateFlow<List<DailySpend>> = argsFlow
        .flatMapLatest { repository.getDailySpendingTrend(it.period, it.start, it.end, it.simId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalFees: StateFlow<Double?> = argsFlow
        .flatMapLatest { repository.getTotalFeesForPeriod(it.period, it.start, it.end, it.simId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalTransfers: StateFlow<Double?> = argsFlow
        .flatMapLatest { repository.getTotalTransfersForPeriod(it.period, it.start, it.end, it.simId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val accountSourceExpenses: StateFlow<List<MonthlyTotal>> = argsFlow
        .flatMapLatest { repository.getExpensesByAccountSource(it.period, it.start, it.end) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val monthlyComparison: StateFlow<List<MonthlyComparisonPoint>> = _selectedSimId
        .flatMapLatest { simId ->
            combine(
                repository.getMonthlyIncomeTotals(4, simId),
                repository.getMonthlyExpenseTotals(4, simId)
            ) { incomes, expenses ->
                val keys = (incomes.map { it.monthString } + expenses.map { it.monthString }).distinct().sorted()
                val incMap = incomes.associate { it.monthString to it.totalAmount }
                val expMap = expenses.associate { it.monthString to it.totalAmount }
                keys.takeLast(4).map { key ->
                    MonthlyComparisonPoint(
                        monthKey = key,
                        income = incMap[key] ?: 0.0,
                        expense = expMap[key] ?: 0.0
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val personArgsFlow = combine(_searchQuery, argsFlow) { q, args -> q to args }

    @OptIn(ExperimentalCoroutinesApi::class)
    val personTotalSpent: StateFlow<Double?> = personArgsFlow
        .flatMapLatest { (q, a) ->
            if (q.isBlank()) flowOf(null) else repository.getSpentForPerson(q, a.period, a.start, a.end, a.simId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val personTotalIncome: StateFlow<Double?> = personArgsFlow
        .flatMapLatest { (q, a) ->
            if (q.isBlank()) flowOf(null) else repository.getIncomeFromPerson(q, a.period, a.start, a.end, a.simId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val personTotalFees: StateFlow<Double?> = personArgsFlow
        .flatMapLatest { (q, a) ->
            if (q.isBlank()) flowOf(null) else repository.getFeesForPerson(q, a.period, a.start, a.end, a.simId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val topMerchants: StateFlow<List<MerchantSummary>> = _selectedSimId
        .flatMapLatest { repository.getTopMerchants(8, it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _spendingAnomalies = MutableStateFlow<List<SpendingAnomaly>>(emptyList())
    val spendingAnomalies: StateFlow<List<SpendingAnomaly>> = _spendingAnomalies.asStateFlow()

    private val _financialScore = MutableStateFlow<FinancialScore?>(null)
    val financialScore: StateFlow<FinancialScore?> = _financialScore.asStateFlow()

    init {
        viewModelScope.launch {
            _selectedSimId.collect { simId -> refreshAnalytics(simId) }
        }
    }

    fun togglePrivacyMode() = userPreferences.togglePrivacyMode()

    fun onPeriodSelected(period: ReportPeriod) {
        _selectedPeriod.value = period
        if (period != ReportPeriod.CUSTOM) {
            _customStartDate.value = null
            _customEndDate.value = null
        }
    }

    fun setSimFilter(simId: Int?) { _selectedSimId.value = simId }

    fun setCustomDateRange(start: Long?, end: Long?) {
        _customStartDate.value = start
        _customEndDate.value = end
        _selectedPeriod.value = ReportPeriod.CUSTOM
    }

    fun setSearchQuery(query: String) { _searchQuery.value = query }

    fun setBudget(categoryId: Int, limit: Double) {
        viewModelScope.launch { repository.setCategoryBudget(categoryId, limit) }
    }

    fun exportData(context: Context) {
        viewModelScope.launch {
            _exportState.value = ExportState.Exporting
            try {
                val allTx = repository.getAllTransactions().first()
                val result = CsvExporter(context).exportTransactionsToCsv(allTx)
                _exportState.value = if (result.isSuccess) {
                    ExportState.Success(result.getOrDefault("Exported to Downloads."))
                } else {
                    ExportState.Error(result.exceptionOrNull()?.message ?: "Export failed")
                }
            } catch (e: Exception) {
                _exportState.value = ExportState.Error(e.message ?: "Export failed")
            }
        }
    }

    fun resetExportState() { _exportState.value = ExportState.Idle }

    fun refreshAnalytics(simId: Int? = null) {
        viewModelScope.launch {
            val thisMonth = repository.getExpensesByCategory(ReportPeriod.THIS_MONTH, simId = simId).firstOrNull() ?: emptyList()
            val lastMonth = repository.getExpensesByCategoryForLastNMonths(1, simId).firstOrNull() ?: emptyList()
            val twoMonthsAgo = repository.getExpensesByCategoryForLastNMonths(2, simId).firstOrNull() ?: emptyList()
            _spendingAnomalies.value = financialEngine.detectSpendingAnomalies(thisMonth, lastMonth, twoMonthsAgo)

            val totalIncome = repository.getTotalIncomeForPeriod(ReportPeriod.THIS_MONTH, simId = simId).firstOrNull() ?: 0.0
            val totalSpent = repository.getTotalSpentForPeriod(ReportPeriod.THIS_MONTH, simId = simId).firstOrNull() ?: 0.0
            val fulizaFlow = repository.getFulizaUsageByMonth(simId).firstOrNull() ?: emptyList()
            val currentMonthKey = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
            val thisMonthFuliza = fulizaFlow.firstOrNull { it.monthString == currentMonthKey }?.totalAmount ?: 0.0
            _financialScore.value = financialEngine.calculateFinancialScore(totalIncome, totalSpent, thisMonthFuliza, thisMonth)
        }
    }
}

sealed class ExportState {
    object Idle : ExportState()
    object Exporting : ExportState()
    data class Success(val message: String) : ExportState()
    data class Error(val message: String) : ExportState()
}
