package com.mpesa.tracker.data.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class ThemeMode(val label: String) {
    SYSTEM("System Default"),
    DARK("Dark"),
    LIGHT("Light")
}

/**
 * Reactive global user preferences backed by SharedPreferences.
 * Persists Privacy Mode, Adaptive Material 3 Theme settings, Security Lock,
 * Notifications, and Self-Transfer detection identifiers across all tabs & restarts.
 */
@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("biller_user_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(
        ThemeMode.entries.firstOrNull { it.name == prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) }
            ?: ThemeMode.SYSTEM
    )
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _dynamicColorEnabled = MutableStateFlow(prefs.getBoolean(KEY_DYNAMIC_COLOR, true))
    val dynamicColorEnabled: StateFlow<Boolean> = _dynamicColorEnabled.asStateFlow()

    private val _isPrivacyMode = MutableStateFlow(prefs.getBoolean(KEY_PRIVACY_MODE, false))
    val isPrivacyMode: StateFlow<Boolean> = _isPrivacyMode.asStateFlow()

    private val _appLockEnabled = MutableStateFlow(prefs.getBoolean(KEY_APP_LOCK, true))
    val appLockEnabled: StateFlow<Boolean> = _appLockEnabled.asStateFlow()

    private val _budgetAlertsEnabled = MutableStateFlow(prefs.getBoolean(KEY_BUDGET_ALERTS, true))
    val budgetAlertsEnabled: StateFlow<Boolean> = _budgetAlertsEnabled.asStateFlow()

    private val _billRemindersEnabled = MutableStateFlow(prefs.getBoolean(KEY_BILL_REMINDERS, true))
    val billRemindersEnabled: StateFlow<Boolean> = _billRemindersEnabled.asStateFlow()

    private val _dailyAiAdviceEnabled = MutableStateFlow(prefs.getBoolean(KEY_DAILY_AI_ADVICE, false))
    val dailyAiAdviceEnabled: StateFlow<Boolean> = _dailyAiAdviceEnabled.asStateFlow()

    private val _selfIdentifiers = MutableStateFlow(
        prefs.getStringSet(KEY_SELF_IDENTIFIERS, emptySet())?.toSet() ?: emptySet()
    )
    val selfIdentifiers: StateFlow<Set<String>> = _selfIdentifiers.asStateFlow()

    private val _selectedAccountFilter = MutableStateFlow<String?>(null)
    val selectedAccountFilter: StateFlow<String?> = _selectedAccountFilter.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _themeMode.value = mode
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DYNAMIC_COLOR, enabled).apply()
        _dynamicColorEnabled.value = enabled
    }

    fun togglePrivacyMode() {
        setPrivacyMode(!_isPrivacyMode.value)
    }

    fun setPrivacyMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_PRIVACY_MODE, enabled).apply()
        _isPrivacyMode.value = enabled
    }

    fun setAppLockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_APP_LOCK, enabled).apply()
        _appLockEnabled.value = enabled
    }

    fun setBudgetAlertsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BUDGET_ALERTS, enabled).apply()
        _budgetAlertsEnabled.value = enabled
    }

    fun setBillRemindersEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BILL_REMINDERS, enabled).apply()
        _billRemindersEnabled.value = enabled
    }

    fun setDailyAiAdviceEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DAILY_AI_ADVICE, enabled).apply()
        _dailyAiAdviceEnabled.value = enabled
    }

    fun addSelfIdentifier(raw: String) {
        val cleaned = raw.trim().uppercase()
        if (cleaned.length < 3) return
        val updated = _selfIdentifiers.value + cleaned
        prefs.edit().putStringSet(KEY_SELF_IDENTIFIERS, updated).apply()
        _selfIdentifiers.value = updated
    }

    fun removeSelfIdentifier(value: String) {
        val updated = _selfIdentifiers.value - value
        prefs.edit().putStringSet(KEY_SELF_IDENTIFIERS, updated).apply()
        _selfIdentifiers.value = updated
    }

    fun setSelectedAccountFilter(source: String?) {
        _selectedAccountFilter.value = source
    }

    fun wasTransferReconciledV2(): Boolean = prefs.getBoolean(KEY_V2_RECONCILED, false)

    fun markTransferReconciledV2() {
        prefs.edit().putBoolean(KEY_V2_RECONCILED, true).apply()
    }

    /** Tracks last alert threshold (80 or 100) notified per categoryId for a YYYY-MM month key. */
    fun getNotifiedBudgetThreshold(monthKey: String, categoryId: Int): Int =
        prefs.getInt("budget_alert_${monthKey}_$categoryId", 0)

    fun setNotifiedBudgetThreshold(monthKey: String, categoryId: Int, threshold: Int) {
        prefs.edit().putInt("budget_alert_${monthKey}_$categoryId", threshold).apply()
    }

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_DYNAMIC_COLOR = "dynamic_color"
        private const val KEY_PRIVACY_MODE = "privacy_mode"
        private const val KEY_APP_LOCK = "app_lock"
        private const val KEY_BUDGET_ALERTS = "budget_alerts"
        private const val KEY_BILL_REMINDERS = "bill_reminders"
        private const val KEY_DAILY_AI_ADVICE = "daily_ai_advice"
        private const val KEY_SELF_IDENTIFIERS = "self_identifiers"
        private const val KEY_V2_RECONCILED = "v2_transfers_reconciled"
    }
}
