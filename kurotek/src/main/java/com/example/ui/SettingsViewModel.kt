package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.CoreContainer
import com.example.database.CardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * SettingsViewModel — Clean Architecture Settings Coordinator
 * Responsible for all application settings, wallet toggles, SMS templates, and configuration.
 */
class SettingsViewModel(
    private val repository: CardRepository,
    private val coreContainer: CoreContainer
) : ViewModel() {

    // ─────────────────────────────────────────────
    // إعدادات الخدمة والمحافظ
    // ─────────────────────────────────────────────
    val isServiceEnabled: StateFlow<Boolean> = repository.isServiceEnabled
    fun toggleService(enabled: Boolean) = repository.setServiceEnabled(enabled)

    val isJeebEnabled: StateFlow<Boolean> = repository.isJeebEnabled
    val isJawaliEnabled: StateFlow<Boolean> = repository.isJawaliEnabled
    val isKuraimiEnabled: StateFlow<Boolean> = repository.isKuraimiEnabled
    val isHasebEnabled: StateFlow<Boolean> = repository.isHasebEnabled
    val isOneCashEnabled: StateFlow<Boolean> = repository.isOneCashEnabled
    val isMFloosEnabled: StateFlow<Boolean> = repository.isMFloosEnabled

    fun toggleJeeb(enabled: Boolean) = repository.setJeebEnabled(enabled)
    fun toggleJawali(enabled: Boolean) = repository.setJawaliEnabled(enabled)
    fun toggleKuraimi(enabled: Boolean) = repository.setKuraimiEnabled(enabled)
    fun toggleHaseb(enabled: Boolean) = repository.setHasebEnabled(enabled)
    fun toggleOneCash(enabled: Boolean) = repository.setOneCashEnabled(enabled)
    fun toggleMFloos(enabled: Boolean) = repository.setMFloosEnabled(enabled)

    // ─────────────────────────────────────────────
    // إعدادات الرد التلقائي
    // ─────────────────────────────────────────────
    val isAutoSendSmsEnabled: StateFlow<Boolean> = repository.isAutoSendSmsEnabled
    val isNotificationClickComposeEnabled: StateFlow<Boolean> = repository.isNotificationClickComposeEnabled
    val approvedSmsTemplates: StateFlow<List<String>> = repository.approvedSmsTemplates
    val accountCodeSmsTemplate: StateFlow<String> = repository.accountCodeSmsTemplate
    val accountCodeSmsPhone: StateFlow<String> = repository.accountCodeSmsPhone
    val generalSmsTemplate: StateFlow<String> = repository.generalSmsTemplate
    val networkName: StateFlow<String> = repository.networkName
    val customGeminiApiKey: StateFlow<String> = repository.customGeminiApiKey
    val cardFormatMode: StateFlow<String> = repository.cardFormatMode

    fun toggleAutoSendSms(enabled: Boolean) = repository.setAutoSendSmsEnabled(enabled)
    fun toggleNotificationClickCompose(enabled: Boolean) = repository.setNotificationClickComposeEnabled(enabled)
    fun addApprovedSmsTemplate(template: String) = repository.addApprovedSmsTemplate(template)
    fun removeApprovedSmsTemplate(template: String) = repository.removeApprovedSmsTemplate(template)
    fun updateAccountCodeSmsTemplate(template: String) = repository.setAccountCodeSmsTemplate(template)
    fun updateAccountCodeSmsPhone(phone: String) = repository.setAccountCodeSmsPhone(phone)
    fun updateGeneralSmsTemplate(template: String) = repository.setGeneralSmsTemplate(template)
    fun updateNetworkName(name: String) = repository.setNetworkName(name)
    fun updateCustomGeminiApiKey(apiKey: String) = repository.setCustomGeminiApiKey(apiKey)
    fun getActiveGeminiApiKey(): String = repository.getActiveGeminiApiKey()
    fun updateCardFormatMode(mode: String) = repository.setCardFormatMode(mode)

    // ─────────────────────────────────────────────
    // إدارة الفئات (Categories)
    // ─────────────────────────────────────────────
    val categories: StateFlow<List<Int>> = repository.categories
    fun addCategory(cat: Int) = repository.addCategory(cat)
    fun removeCategory(cat: Int) { viewModelScope.launch { repository.removeCategory(cat) } }

}

class SettingsViewModelFactory(
    private val repository: CardRepository,
    private val coreContainer: CoreContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository, coreContainer) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
