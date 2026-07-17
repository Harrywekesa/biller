package com.mpesa.tracker.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mpesa.tracker.data.repository.TransactionRepository
import com.mpesa.tracker.domain.intelligence.FinancialIntelligenceEngine
import com.mpesa.tracker.domain.models.ReportPeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AiReportState {
    object Idle : AiReportState()
    object Loading : AiReportState()
    data class Success(val reportMarkdown: String) : AiReportState()
    data class Error(val message: String) : AiReportState()
}

@HiltViewModel
class AiInsightsViewModel @Inject constructor(
    private val financialEngine: FinancialIntelligenceEngine,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AiReportState>(AiReportState.Idle)
    val uiState: StateFlow<AiReportState> = _uiState.asStateFlow()

    fun generateInsights(period: ReportPeriod) {
        viewModelScope.launch {
            _uiState.value = AiReportState.Loading

            try {
                // Fetch required data
                val expensesByCategory = transactionRepository.getExpensesByCategory(period).firstOrNull() ?: emptyList()
                val dailyTrend = transactionRepository.getDailySpendingTrend(period).firstOrNull() ?: emptyList()
                val spendingByDayAndCategory = transactionRepository.getSpendingByCategoryAndDayOfWeek(period).firstOrNull() ?: emptyList()

                val periodName = when(period) {
                    ReportPeriod.THIS_WEEK -> "This Week"
                    ReportPeriod.THIS_MONTH -> "This Month"
                    ReportPeriod.LAST_MONTH -> "Last Month"
                    ReportPeriod.ALL_TIME -> "All Time"
                    ReportPeriod.CUSTOM -> "Custom Range"
                }

                // Call AI Engine
                val report = financialEngine.generateFinancialReport(
                    expensesByCategory = expensesByCategory,
                    dailySpendingTrend = dailyTrend,
                    spendingByDayAndCategory = spendingByDayAndCategory,
                    periodName = periodName
                )

                if (report != null && !report.startsWith("Failed")) {
                    _uiState.value = AiReportState.Success(report)
                } else {
                    _uiState.value = AiReportState.Error(report ?: "Unknown error occurred.")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = AiReportState.Error("An error occurred: ${e.localizedMessage}")
            }
        }
    }

    fun resetState() {
        _uiState.value = AiReportState.Idle
    }
}
