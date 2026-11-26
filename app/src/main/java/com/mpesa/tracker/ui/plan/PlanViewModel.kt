package com.mpesa.tracker.ui.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mpesa.tracker.data.local.entities.SubscriptionEntity
import com.mpesa.tracker.data.repository.TransactionRepository
import com.mpesa.tracker.data.repository.UserPreferencesRepository
import com.mpesa.tracker.domain.intelligence.FinancialIntelligenceEngine
import com.mpesa.tracker.domain.intelligence.PotentialSubscription
import com.mpesa.tracker.domain.models.ReportPeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PlanSubTab(val label: String) {
    BUDGETS("Budgets"),
    BILLS("Bills & Subs")
}

enum class BudgetFilter {
    ALL,
    ACTIVE_ONLY,
    OVER_BUDGET
}

data class BudgetItemUi(
    val categoryId: Int,
    val categoryName: String,
    val iconName: String,
    val colorCode: String,
    val spentThisMonth: Double,
    val monthlyLimit: Double?,
    val hasBudget: Boolean,
    val progress: Float,
    val isOverBudget: Boolean,
    val remainingAmount: Double
)

data class BudgetSummaryUi(
    val totalBudgetCap: Double = 0.0,
    val totalSpentInBudgeted: Double = 0.0,
    val totalRemaining: Double = 0.0,
    val overallProgress: Float = 0f,
    val activeBudgetCount: Int = 0,
    val overBudgetCount: Int = 0
)

@HiltViewModel
class PlanViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val userPreferences: UserPreferencesRepository,
    private val financialEngine: FinancialIntelligenceEngine
) : ViewModel() {

    private val _activeSubTab = MutableStateFlow(PlanSubTab.BUDGETS)
    val activeSubTab: StateFlow<PlanSubTab> = _activeSubTab.asStateFlow()

    private val _filter = MutableStateFlow(BudgetFilter.ALL)
    val filter: StateFlow<BudgetFilter> = _filter.asStateFlow()

    val isPrivacyMode: StateFlow<Boolean> = userPreferences.isPrivacyMode

    private val rawExpenses = repository.getExpensesByCategory(ReportPeriod.THIS_MONTH)
    private val allCategories = repository.getAllCategories()

    val budgetItems: StateFlow<List<BudgetItemUi>> = combine(
        rawExpenses,
        allCategories,
        _filter
    ) { expenses, categories, currentFilter ->
        val expenseMap = expenses.filter { it.categoryId != null }.associateBy { it.categoryId!! }
        val items = categories.map { cat ->
            val expense = expenseMap[cat.id]
            val spent = expense?.totalAmount ?: 0.0
            val limit = expense?.budgetLimit
            val hasBudget = limit != null && limit > 0.0
            val progress = if (hasBudget) (spent / limit!!).toFloat() else 0f
            val isOver = hasBudget && spent > limit!!
            val remaining = if (hasBudget) limit!! - spent else 0.0

            BudgetItemUi(
                categoryId = cat.id,
                categoryName = cat.name,
                iconName = cat.iconName,
                colorCode = cat.colorCode,
                spentThisMonth = spent,
                monthlyLimit = limit,
                hasBudget = hasBudget,
                progress = progress,
                isOverBudget = isOver,
                remainingAmount = remaining
            )
        }.sortedWith(
            compareByDescending<BudgetItemUi> { it.isOverBudget }
                .thenByDescending { it.hasBudget }
                .thenByDescending { it.spentThisMonth }
        )

        when (currentFilter) {
            BudgetFilter.ALL -> items
            BudgetFilter.ACTIVE_ONLY -> items.filter { it.hasBudget }
            BudgetFilter.OVER_BUDGET -> items.filter { it.isOverBudget }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val summary: StateFlow<BudgetSummaryUi> = combine(rawExpenses, allCategories) { expenses, categories ->
        val expenseMap = expenses.filter { it.categoryId != null }.associateBy { it.categoryId!! }
        var totalCap = 0.0
        var totalSpent = 0.0
        var activeCount = 0
        var overCount = 0

        categories.forEach { cat ->
            val exp = expenseMap[cat.id]
            val limit = exp?.budgetLimit
            if (limit != null && limit > 0.0) {
                val spent = exp.totalAmount
                totalCap += limit
                totalSpent += spent
                activeCount++
                if (spent > limit) overCount++
            }
        }
        BudgetSummaryUi(
            totalBudgetCap = totalCap,
            totalSpentInBudgeted = totalSpent,
            totalRemaining = (totalCap - totalSpent).coerceAtLeast(0.0),
            overallProgress = if (totalCap > 0.0) (totalSpent / totalCap).toFloat().coerceIn(0f, 1f) else 0f,
            activeBudgetCount = activeCount,
            overBudgetCount = overCount
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BudgetSummaryUi())

    val subscriptions: StateFlow<List<SubscriptionEntity>> = repository.getAllSubscriptions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val detectedPatterns: StateFlow<List<PotentialSubscription>> = combine(
        repository.getAllTransactions(),
        subscriptions
    ) { txs, savedSubs ->
        val savedMatchers = savedSubs.map { it.merchantNameMatcher.lowercase() }.toSet()
        financialEngine.detectRecurrentPatterns(txs)
            .filter { p -> p.merchantName.lowercase() !in savedMatchers }
            .take(8)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectSubTab(tab: PlanSubTab) { _activeSubTab.value = tab }
    fun setFilter(newFilter: BudgetFilter) { _filter.value = newFilter }
    fun togglePrivacyMode() = userPreferences.togglePrivacyMode()

    fun setBudget(categoryId: Int, limit: Double) {
        viewModelScope.launch { repository.setCategoryBudget(categoryId, limit) }
    }

    fun removeBudget(categoryId: Int) {
        viewModelScope.launch { repository.clearCategoryBudget(categoryId) }
    }

    fun trackRecurringBill(name: String, matcher: String, amount: Double, cycleDays: Int = 30) {
        viewModelScope.launch {
            repository.saveTrackedSubscription(name, matcher, amount, cycleDays)
        }
    }

    fun toggleSubscriptionActive(sub: SubscriptionEntity) {
        viewModelScope.launch { repository.toggleSubscriptionActive(sub) }
    }

    fun deleteSubscription(id: Int) {
        viewModelScope.launch { repository.deleteSubscription(id) }
    }
}
