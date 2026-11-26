package com.mpesa.tracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mpesa.tracker.data.local.entities.CategoryEntity
import com.mpesa.tracker.data.local.entities.CustomRuleEntity
import com.mpesa.tracker.data.local.entities.TransferRuleEntity
import com.mpesa.tracker.data.local.entities.UnparsedSmsEntity
import com.mpesa.tracker.data.repository.ThemeMode
import com.mpesa.tracker.data.repository.TransactionRepository
import com.mpesa.tracker.data.repository.UserPreferencesRepository
import com.mpesa.tracker.framework.services.BackupManager
import com.mpesa.tracker.framework.services.SmsSyncService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val userPreferences: UserPreferencesRepository,
    private val smsSyncService: SmsSyncService,
    private val backupManager: BackupManager
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = userPreferences.themeMode
    val dynamicColorEnabled: StateFlow<Boolean> = userPreferences.dynamicColorEnabled
    val isPrivacyMode: StateFlow<Boolean> = userPreferences.isPrivacyMode
    val appLockEnabled: StateFlow<Boolean> = userPreferences.appLockEnabled
    val budgetAlertsEnabled: StateFlow<Boolean> = userPreferences.budgetAlertsEnabled
    val billRemindersEnabled: StateFlow<Boolean> = userPreferences.billRemindersEnabled
    val dailyAiAdviceEnabled: StateFlow<Boolean> = userPreferences.dailyAiAdviceEnabled
    val selfIdentifiers: StateFlow<Set<String>> = userPreferences.selfIdentifiers

    val categories: StateFlow<List<CategoryEntity>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customRules: StateFlow<List<CustomRuleEntity>> = repository.getAllCustomRules()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transferRules: StateFlow<List<TransferRuleEntity>> = repository.getAllTransferRules()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unparsedSms: StateFlow<List<UnparsedSmsEntity>> = repository.getUnparsedSmsLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _backups = MutableStateFlow<List<File>>(emptyList())
    val backups: StateFlow<List<File>> = _backups.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _isRescanning = MutableStateFlow(false)
    val isRescanning: StateFlow<Boolean> = _isRescanning.asStateFlow()

    // Live rescan progress: null = idle, non-null = current phase message
    private val _rescanProgress = MutableStateFlow<RescanProgress>(RescanProgress.Idle)
    val rescanProgress: StateFlow<RescanProgress> = _rescanProgress.asStateFlow()

    init {
        refreshBackups()
    }

    fun setThemeMode(mode: ThemeMode) = userPreferences.setThemeMode(mode)
    fun setDynamicColor(enabled: Boolean) = userPreferences.setDynamicColorEnabled(enabled)
    fun setPrivacyMode(enabled: Boolean) = userPreferences.setPrivacyMode(enabled)
    fun setAppLockEnabled(enabled: Boolean) = userPreferences.setAppLockEnabled(enabled)
    fun setBudgetAlertsEnabled(enabled: Boolean) = userPreferences.setBudgetAlertsEnabled(enabled)
    fun setBillRemindersEnabled(enabled: Boolean) = userPreferences.setBillRemindersEnabled(enabled)
    fun setDailyAiAdviceEnabled(enabled: Boolean) = userPreferences.setDailyAiAdviceEnabled(enabled)

    fun addSelfIdentifier(value: String) = userPreferences.addSelfIdentifier(value)
    fun removeSelfIdentifier(value: String) = userPreferences.removeSelfIdentifier(value)

    fun addCategory(name: String, iconName: String, colorHex: String) {
        viewModelScope.launch { repository.addCategory(name, colorHex, iconName) }
    }

    fun updateCategory(category: CategoryEntity) {
        viewModelScope.launch { repository.updateCategory(category) }
    }

    fun deleteCategory(id: Int) {
        viewModelScope.launch { repository.deleteCategory(id) }
    }

    fun deleteCustomRule(merchantName: String) {
        viewModelScope.launch { repository.deleteCustomRule(merchantName) }
    }

    fun savePaybillCodeRule(code: String, categoryId: Int) {
        viewModelScope.launch {
            repository.savePaybillCodeRule(code, categoryId)
            _statusMessage.value = "Paybill/Till rule saved for code $code"
        }
    }

    fun deleteTransferRule(merchantKey: String) {
        viewModelScope.launch { repository.deleteTransferRule(merchantKey) }
    }

    fun clearUnparsedSms() {
        viewModelScope.launch { repository.clearUnparsedSmsLogs() }
    }

    fun runNonDestructiveRescan() {
        if (_isRescanning.value) return
        viewModelScope.launch {
            _isRescanning.value = true
            _rescanProgress.value = RescanProgress.Phase("Resetting sync cursor…")
            try {
                smsSyncService.resetSyncTimestamp()
                _rescanProgress.value = RescanProgress.Phase("Scanning M-Pesa SMS inbox…")
                val mpesaCount = withContext(Dispatchers.IO) { smsSyncService.syncMpesa() }
                _rescanProgress.value = RescanProgress.Phase("Scanning bank SMS (I&M · Absa · Equity)… ($mpesaCount M-Pesa found)")
                val bankCount = withContext(Dispatchers.IO) { smsSyncService.syncBanks() }
                val total = mpesaCount + bankCount
                _rescanProgress.value = RescanProgress.Done(
                    mpesa = mpesaCount,
                    bank = bankCount,
                    total = total
                )
            } catch (e: Exception) {
                _rescanProgress.value = RescanProgress.Error("Scan failed: ${e.localizedMessage}")
            } finally {
                _isRescanning.value = false
            }
        }
    }

    fun refreshBackups() {
        viewModelScope.launch {
            _backups.value = withContext(Dispatchers.IO) { backupManager.listBackups() }
        }
    }

    fun createBackup() {
        viewModelScope.launch {
            val file = withContext(Dispatchers.IO) { backupManager.createLocalBackup() }
            if (file != null) {
                refreshBackups()
                _statusMessage.value = "Encrypted backup saved: ${file.name}"
            } else {
                _statusMessage.value = "Backup failed."
            }
        }
    }

    fun restoreBackup(file: File) {
        viewModelScope.launch {
            val ok = withContext(Dispatchers.IO) { backupManager.restoreBackup(file) }
            _statusMessage.value = if (ok) "Backup restored. Restart app to reload database cleanly." else "Restore failed."
        }
    }

    fun clearStatusMessage() { _statusMessage.value = null }

    fun clearRescanProgress() { _rescanProgress.value = RescanProgress.Idle }
}

sealed class RescanProgress {
    object Idle : RescanProgress()
    data class Phase(val message: String) : RescanProgress()
    data class Done(val mpesa: Int, val bank: Int, val total: Int) : RescanProgress()
    data class Error(val message: String) : RescanProgress()
}
