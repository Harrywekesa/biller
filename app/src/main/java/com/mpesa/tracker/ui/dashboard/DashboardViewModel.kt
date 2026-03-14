package com.mpesa.tracker.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject
import com.mpesa.tracker.domain.models.ReportPeriod
import com.mpesa.tracker.framework.services.SmsSyncService

data class DashboardUiState(
    val totalBalance: String = "0.00",
    val monthSpend: String = "0.00",
    val recentTransactions: List<TransactionEntity> = emptyList(),
    val isSyncing: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val smsSyncService: SmsSyncService
) : ViewModel() {

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val currencyFormatter = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    val recentTransactions: StateFlow<List<TransactionEntity>> = repository.getAllTransactions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val monthSpend: StateFlow<String> = repository.getTotalSpentForPeriod(ReportPeriod.THIS_MONTH)
        .map { total ->
            total?.let { currencyFormatter.format(it) } ?: "0.00"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "0.00"
        )
        
    val totalBalance: StateFlow<String> = repository.getAllTransactions()
        .map { transactions ->
            val latestTx = transactions.firstOrNull { it.balance != null }
            latestTx?.balance?.let { currencyFormatter.format(it) } ?: "0.00"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "0.00"
        )

    fun syncHistoricSms() {
        if (_isSyncing.value) return // Prevent multiple concurrent syncs
        
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                // Background sync process
                smsSyncService.syncHistoricMessages()
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun clearAndResyncSms() {
        if (_isSyncing.value) return
        
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                repository.deleteAllTransactions()
                smsSyncService.syncHistoricMessages()
            } finally {
                _isSyncing.value = false
            }
        }
    }
}
