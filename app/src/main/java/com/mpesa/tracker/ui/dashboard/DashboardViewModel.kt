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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject
import com.mpesa.tracker.domain.models.ReportPeriod
import com.mpesa.tracker.framework.services.SmsSyncService

data class DashboardUiState(
    val totalBalance: String = "0.00",
    val monthSpend: String = "0.00",
    val monthIncome: String = "0.00",
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

    private val _selectedSimId = MutableStateFlow<Int?>(null)
    val selectedSimId: StateFlow<Int?> = _selectedSimId.asStateFlow()

    private val currencyFormatter = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    val recentTransactions: StateFlow<List<TransactionEntity>> = _selectedSimId
        .flatMapLatest { simId ->
            repository.getAllTransactions(simId = simId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activeSimIds: StateFlow<List<Int>> = repository.getActiveSimIds()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val monthSpend: StateFlow<String> = _selectedSimId
        .flatMapLatest { simId ->
            repository.getTotalSpentForPeriod(ReportPeriod.THIS_MONTH, simId = simId)
        }
        .map { total ->
            total?.let { currencyFormatter.format(it) } ?: "0.00"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "0.00"
        )
        
    @OptIn(ExperimentalCoroutinesApi::class)
    val monthIncome: StateFlow<String> = _selectedSimId
        .flatMapLatest { simId ->
            repository.getTotalIncomeForPeriod(ReportPeriod.THIS_MONTH, simId = simId)
        }
        .map { total ->
            total?.let { currencyFormatter.format(it) } ?: "0.00"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "0.00"
        )
        
    @OptIn(ExperimentalCoroutinesApi::class)
    val totalBalance: StateFlow<String> = _selectedSimId
        .flatMapLatest { simId ->
            repository.getAllTransactions(simId = simId)
        }
        .map { transactions ->
            val latestTxWithBalance = transactions.firstOrNull { it.balance != null }
            
            if (latestTxWithBalance != null) {
                // If the most recent transaction was a standalone Fuliza query/update, 
                // the "balance" parsed in it is the outstanding Fuliza debt.
                if (latestTxWithBalance.recipientName == "Fuliza M-PESA" && latestTxWithBalance.balance != null) {
                    "-${currencyFormatter.format(latestTxWithBalance.balance)}"
                } 
                // If M-Pesa balance is 0.00, they might have an outstanding Fuliza debt
                else if (latestTxWithBalance.balance == 0.0) {
                    // Find the most recent standalone Fuliza message to capture the true debt
                    val recentFuliza = transactions.firstOrNull { it.recipientName == "Fuliza M-PESA" && it.balance != null }
                    
                    if (recentFuliza != null && recentFuliza.balance != null && recentFuliza.balance > 0) {
                        "-${currencyFormatter.format(recentFuliza.balance)}"
                    } 
                    else if (latestTxWithBalance.fulizaAmount != null) {
                        "-${currencyFormatter.format(latestTxWithBalance.fulizaAmount)}"
                    } 
                    else {
                        "0.00"
                    }
                }  
                else {
                    currencyFormatter.format(latestTxWithBalance.balance)
                }
            } else {
                "0.00"
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "0.00"
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalTransacted: StateFlow<String> = _selectedSimId
        .flatMapLatest { simId ->
            combine(
                repository.getTotalSpentForPeriod(ReportPeriod.THIS_MONTH, simId = simId),
                repository.getTotalIncomeForPeriod(ReportPeriod.THIS_MONTH, simId = simId)
            ) { spend, income ->
                val total = (spend ?: 0.0) + (income ?: 0.0)
                currencyFormatter.format(total)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "0.00"
        )

    fun setSimFilter(simId: Int?) {
        _selectedSimId.value = simId
    }

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
