package com.mpesa.tracker.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mpesa.tracker.data.local.entities.CategoryEntity
import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.mpesa.tracker.framework.services.SmsSyncService

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val smsSyncService: SmsSyncService
) : ViewModel() {

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterIncomeOnly = MutableStateFlow(false)
    val filterIncomeOnly: StateFlow<Boolean> = _filterIncomeOnly.asStateFlow()

    private val _filterUncategorizedOnly = MutableStateFlow(false)
    val filterUncategorizedOnly: StateFlow<Boolean> = _filterUncategorizedOnly.asStateFlow()

    private val _filterByCategoryId = MutableStateFlow<Int?>(null)
    val filterByCategoryId: StateFlow<Int?> = _filterByCategoryId.asStateFlow()

    private val _filterFulizaOnly = MutableStateFlow(false)
    val filterFulizaOnly: StateFlow<Boolean> = _filterFulizaOnly.asStateFlow()

    private data class FilterArgs(
        val query: String,
        val incomeOnly: Boolean,
        val uncategorizedOnly: Boolean,
        val categoryIdFilter: Int?,
        val fulizaOnly: Boolean
    )

    val transactions: StateFlow<List<TransactionEntity>> = combine(
        repository.getAllTransactions(),
        combine(
            _searchQuery,
            _filterIncomeOnly,
            _filterUncategorizedOnly,
            _filterByCategoryId,
            _filterFulizaOnly
        ) { q, i, u, c, f -> FilterArgs(q, i, u, c, f) }
    ) { txList, filters ->
        txList.filter { tx ->
            val matchesQuery = filters.query.isBlank() || 
                               tx.recipientName.contains(filters.query, ignoreCase = true) || 
                               tx.receiptNumber.contains(filters.query, ignoreCase = true)
            val matchesType = !filters.incomeOnly || tx.isIncome
            val matchesUncategorized = !filters.uncategorizedOnly || tx.categoryId == null
            val matchesCategoryFilter = filters.categoryIdFilter == null || tx.categoryId == filters.categoryIdFilter
            val matchesFuliza = !filters.fulizaOnly || (tx.fulizaAmount != null && tx.fulizaAmount > 0)
            
            matchesQuery && matchesType && matchesUncategorized && matchesCategoryFilter && matchesFuliza
        }.sortedByDescending { it.dateTimestamp }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val categories: StateFlow<List<CategoryEntity>> = repository.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleIncomeFilter() {
        _filterIncomeOnly.value = !_filterIncomeOnly.value
        if (_filterIncomeOnly.value) _filterUncategorizedOnly.value = false // Mutual exclusivity
    }
    
    fun toggleUncategorizedFilter() {
        _filterUncategorizedOnly.value = !_filterUncategorizedOnly.value
        if (_filterUncategorizedOnly.value) {
            _filterIncomeOnly.value = false
            _filterByCategoryId.value = null
        }
    }

    fun toggleFulizaFilter() {
        _filterFulizaOnly.value = !_filterFulizaOnly.value
        if (_filterFulizaOnly.value) {
            _filterIncomeOnly.value = false
        }
    }
    
    fun setCategoryFilter(categoryId: Int?) {
        _filterByCategoryId.value = categoryId
        if (categoryId != null) {
            _filterIncomeOnly.value = false
            _filterUncategorizedOnly.value = false
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

    fun updateTransactionCategory(receiptNumber: String, categoryId: Int, merchantName: String) {
        viewModelScope.launch {
            repository.updateTransactionCategoryAndSaveRule(receiptNumber, categoryId, merchantName)
        }
    }

    fun addCategory(name: String, colorCode: String = "#9E9E9E", iconName: String = "help_outline") {
        viewModelScope.launch {
            repository.addCategory(name, colorCode, iconName)
        }
    }
}
