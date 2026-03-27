package com.mpesa.tracker.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mpesa.tracker.data.local.entities.CategoryExpense
import com.mpesa.tracker.data.local.entities.DailySpend
import com.mpesa.tracker.data.repository.TransactionRepository
import com.mpesa.tracker.domain.models.ReportPeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import com.mpesa.tracker.domain.export.CsvExporter

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow(ReportPeriod.THIS_MONTH)
    val selectedPeriod: StateFlow<ReportPeriod> = _selectedPeriod.asStateFlow()
    
    private val _selectedSimId = MutableStateFlow<Int?>(null)
    val selectedSimId: StateFlow<Int?> = _selectedSimId.asStateFlow()

    val activeSimIds: StateFlow<List<Int>> = repository.getActiveSimIds()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private data class AnalyticsArgs(
        val period: ReportPeriod,
        val start: Long?,
        val end: Long?,
        val simId: Int?
    )
    
    // Custom date overrides
    private val _customStartDate = MutableStateFlow<Long?>(null)
    val customStartDate: StateFlow<Long?> = _customStartDate.asStateFlow()
    private val _customEndDate = MutableStateFlow<Long?>(null)
    val customEndDate: StateFlow<Long?> = _customEndDate.asStateFlow()

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val expensesByCategory: StateFlow<List<CategoryExpense>> = combine(
        _selectedPeriod,
        _customStartDate,
        _customEndDate,
        _selectedSimId
    ) { period, start, end, simId -> AnalyticsArgs(period, start, end, simId) }
        .flatMapLatest { args -> 
            repository.getExpensesByCategory(args.period, args.start, args.end, args.simId) 
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val dailySpendTrend: StateFlow<List<DailySpend>> = combine(
        _selectedPeriod,
        _customStartDate,
        _customEndDate,
        _selectedSimId
    ) { period, start, end, simId -> AnalyticsArgs(period, start, end, simId) }
        .flatMapLatest { args -> 
            repository.getDailySpendingTrend(args.period, args.start, args.end, args.simId) 
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onPeriodSelected(period: ReportPeriod) {
        _selectedPeriod.value = period
        if (period != ReportPeriod.CUSTOM) {
            _customStartDate.value = null
            _customEndDate.value = null
        }
    }
    
    fun setSimFilter(simId: Int?) {
        _selectedSimId.value = simId
    }
    
    fun setCustomDateRange(start: Long?, end: Long?) {
        _customStartDate.value = start
        _customEndDate.value = end
        _selectedPeriod.value = ReportPeriod.CUSTOM
    }

    fun exportData(context: Context) {
        viewModelScope.launch {
            _exportState.value = ExportState.Exporting
            try {
                // Fetch all transactions for export (or just the selected period if preferred)
                val allTx = repository.getAllTransactions().first()
                val exporter = CsvExporter(context)
                val result = exporter.exportTransactionsToCsv(allTx)
                
                if (result.isSuccess) {
                    _exportState.value = ExportState.Success(result.getOrDefault("Exported to Downloads folder."))
                } else {
                    _exportState.value = ExportState.Error(result.exceptionOrNull()?.message ?: "Export failed")
                }
            } catch (e: Exception) {
                _exportState.value = ExportState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
    
    fun resetExportState() {
        _exportState.value = ExportState.Idle
    }

    fun setBudget(categoryId: Int, limit: Double) {
        viewModelScope.launch {
            repository.setCategoryBudget(categoryId, limit)
        }
    }
}

sealed class ExportState {
    object Idle : ExportState()
    object Exporting : ExportState()
    data class Success(val message: String) : ExportState()
    data class Error(val message: String) : ExportState()
}
