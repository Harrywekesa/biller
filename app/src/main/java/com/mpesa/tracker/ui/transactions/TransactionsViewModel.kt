package com.mpesa.tracker.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mpesa.tracker.data.local.entities.CategoryEntity
import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.data.local.entities.TransactionNature
import com.mpesa.tracker.data.repository.TransactionRepository
import com.mpesa.tracker.data.repository.UserPreferencesRepository
import com.mpesa.tracker.domain.parser.AccountSource
import com.mpesa.tracker.framework.services.SmsSyncService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class NatureFilter(val label: String) {
    ALL("All"),
    EXPENSES("Expenses"),
    INCOME("Income"),
    TRANSFERS("Transfers"),
    FULIZA("Fuliza")
}

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val userPreferences: UserPreferencesRepository,
    private val smsSyncService: SmsSyncService
) : ViewModel() {

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _selectedSimId = MutableStateFlow<Int?>(null)
    val selectedSimId: StateFlow<Int?> = _selectedSimId.asStateFlow()

    private val _selectedAccountSource = MutableStateFlow<String?>(null)
    val selectedAccountSource: StateFlow<String?> = _selectedAccountSource.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _natureFilter = MutableStateFlow(NatureFilter.ALL)
    val natureFilter: StateFlow<NatureFilter> = _natureFilter.asStateFlow()

    private val _filterUncategorizedOnly = MutableStateFlow(false)
    val filterUncategorizedOnly: StateFlow<Boolean> = _filterUncategorizedOnly.asStateFlow()

    private val _filterByCategoryId = MutableStateFlow<Int?>(null)
    val filterByCategoryId: StateFlow<Int?> = _filterByCategoryId.asStateFlow()

    private val _selectedTransactionIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedTransactionIds: StateFlow<Set<String>> = _selectedTransactionIds.asStateFlow()

    val isPrivacyMode: StateFlow<Boolean> = userPreferences.isPrivacyMode

    val activeSimIds: StateFlow<List<Int>> = repository.getActiveSimIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeAccountSources: StateFlow<List<String>> = repository.getActiveAccountSources()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private data class FilterArgs(
        val query: String,
        val nature: NatureFilter,
        val uncategorizedOnly: Boolean,
        val categoryIdFilter: Int?,
        val accountSource: String?
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions: StateFlow<List<TransactionEntity>> = _selectedSimId
        .flatMapLatest { simId -> repository.getAllTransactions(simId) }
        .combine(
            combine(
                combine(_searchQuery, _natureFilter, _filterUncategorizedOnly) { q, n, u -> Triple(q, n, u) },
                combine(_filterByCategoryId, _selectedAccountSource) { c, s -> Pair(c, s) }
            ) { left, right ->
                FilterArgs(left.first, left.second, left.third, right.first, right.second)
            }
        ) { txList, filters ->
            txList.filter { tx ->
                val matchesQuery = filters.query.isBlank() ||
                    tx.recipientName.contains(filters.query, ignoreCase = true) ||
                    tx.receiptNumber.contains(filters.query, ignoreCase = true) ||
                    (tx.recipientNumber?.contains(filters.query, ignoreCase = true) == true) ||
                    (tx.userNote?.contains(filters.query, ignoreCase = true) == true)

                val matchesNature = when (filters.nature) {
                    NatureFilter.ALL -> true
                    NatureFilter.EXPENSES -> tx.natureEnum == TransactionNature.EXPENSE
                    NatureFilter.INCOME -> tx.natureEnum == TransactionNature.INCOME
                    NatureFilter.TRANSFERS -> tx.natureEnum == TransactionNature.TRANSFER
                    NatureFilter.FULIZA -> (tx.fulizaAmount != null && tx.fulizaAmount > 0) ||
                        tx.natureEnum == TransactionNature.LOAN_DRAW ||
                        tx.natureEnum == TransactionNature.LOAN_REPAY
                }

                val matchesUncat = !filters.uncategorizedOnly ||
                    (tx.natureEnum == TransactionNature.EXPENSE && tx.categoryId == null)
                val matchesCat = filters.categoryIdFilter == null || tx.categoryId == filters.categoryIdFilter
                val matchesSrc = filters.accountSource == null || tx.accountSource == filters.accountSource

                matchesQuery && matchesNature && matchesUncat && matchesCat && matchesSrc
            }.sortedByDescending { it.dateTimestamp }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun togglePrivacyMode() = userPreferences.togglePrivacyMode()
    fun onSearchQueryChange(query: String) { _searchQuery.value = query }
    fun setSimFilter(simId: Int?) { _selectedSimId.value = simId }
    fun setAccountSourceFilter(source: String?) { _selectedAccountSource.value = source }

    fun setNatureFilter(filter: NatureFilter) {
        _natureFilter.value = filter
        if (filter == NatureFilter.INCOME || filter == NatureFilter.TRANSFERS) {
            _filterUncategorizedOnly.value = false
        }
    }

    fun toggleUncategorizedFilter() {
        _filterUncategorizedOnly.value = !_filterUncategorizedOnly.value
        if (_filterUncategorizedOnly.value) {
            _natureFilter.value = NatureFilter.EXPENSES
            _filterByCategoryId.value = null
        }
    }

    fun applyUncategorizedFilter() {
        _filterUncategorizedOnly.value = true
        _natureFilter.value = NatureFilter.EXPENSES
        _filterByCategoryId.value = null
    }

    fun applyNatureDeepLink(nature: NatureFilter) {
        _filterUncategorizedOnly.value = false
        _filterByCategoryId.value = null
        _natureFilter.value = nature
    }

    fun setCategoryFilter(categoryId: Int?) {
        _filterByCategoryId.value = categoryId
        if (categoryId != null) {
            _natureFilter.value = NatureFilter.EXPENSES
            _filterUncategorizedOnly.value = false
        }
    }

    fun clearAllFilters() {
        _searchQuery.value = ""
        _natureFilter.value = NatureFilter.ALL
        _filterUncategorizedOnly.value = false
        _filterByCategoryId.value = null
        _selectedAccountSource.value = null
        _selectedSimId.value = null
    }

    fun syncNewSms() {
        if (_isSyncing.value) return
        viewModelScope.launch {
            _isSyncing.value = true
            try { smsSyncService.syncHistoricMessages() } finally { _isSyncing.value = false }
        }
    }

    fun addManualTransaction(
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

    fun updateTransactionNature(receiptNumber: String, newNature: TransactionNature, saveRule: Boolean) {
        viewModelScope.launch { repository.updateTransactionNature(receiptNumber, newNature, saveRule) }
    }

    fun updateTransactionCategory(receiptNumber: String, categoryId: Int, merchantName: String, applyToAll: Boolean = true) {
        viewModelScope.launch {
            repository.updateTransactionCategoryAndSaveRule(receiptNumber, categoryId, merchantName, applyToAll)
        }
    }

    fun updateUserNote(receiptNumber: String, note: String?) {
        viewModelScope.launch { repository.updateUserNote(receiptNumber, note) }
    }

    fun deleteTransaction(receiptNumber: String) {
        viewModelScope.launch { repository.deleteTransaction(receiptNumber) }
    }

    fun addCategory(name: String) {
        viewModelScope.launch { repository.addCategory(name) }
    }

    fun toggleSelection(receiptNumber: String) {
        val current = _selectedTransactionIds.value.toMutableSet()
        if (!current.add(receiptNumber)) current.remove(receiptNumber)
        _selectedTransactionIds.value = current
    }

    fun clearSelection() { _selectedTransactionIds.value = emptySet() }

    fun categorizeSelected(categoryId: Int) {
        viewModelScope.launch {
            val ids = _selectedTransactionIds.value
            transactions.value.filter { it.receiptNumber in ids }.forEach { tx ->
                repository.updateTransactionCategoryAndSaveRule(tx.receiptNumber, categoryId, tx.recipientName, true)
            }
            clearSelection()
        }
    }

    fun markSelectedAsTransfers() {
        viewModelScope.launch {
            val ids = _selectedTransactionIds.value
            transactions.value.filter { it.receiptNumber in ids }.forEach { tx ->
                repository.updateTransactionNature(tx.receiptNumber, TransactionNature.TRANSFER, false)
            }
            clearSelection()
        }
    }

    fun deleteSelected() {
        viewModelScope.launch {
            repository.deleteTransactions(_selectedTransactionIds.value.toList())
            clearSelection()
        }
    }
}
