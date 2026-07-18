package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.CoreContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * WalletViewModel
 * مسؤول عن:
 * - تفعيل وإلغاء تفعيل المحافظ الإلكترونية المدعومة (جيب، جوالي، الكريمي، الخ)
 * - قوالب رسائل الـ SMS لكل محفظة
 */
class WalletViewModel(private val coreContainer: CoreContainer) : ViewModel() {

    private val walletRepo = coreContainer.walletRepository

    // ─────────────────────────────────────────────
    // حالة المحافظ (Toggles)
    // ─────────────────────────────────────────────

    private val _isJeebEnabled = MutableStateFlow(false)
    val isJeebEnabled: StateFlow<Boolean> = _isJeebEnabled.asStateFlow()

    private val _isJawaliEnabled = MutableStateFlow(false)
    val isJawaliEnabled: StateFlow<Boolean> = _isJawaliEnabled.asStateFlow()

    private val _isKuraimiEnabled = MutableStateFlow(false)
    val isKuraimiEnabled: StateFlow<Boolean> = _isKuraimiEnabled.asStateFlow()

    private val _isHasebEnabled = MutableStateFlow(false)
    val isHasebEnabled: StateFlow<Boolean> = _isHasebEnabled.asStateFlow()

    private val _isOneCashEnabled = MutableStateFlow(false)
    val isOneCashEnabled: StateFlow<Boolean> = _isOneCashEnabled.asStateFlow()

    private val _isMFloosEnabled = MutableStateFlow(false)
    val isMFloosEnabled: StateFlow<Boolean> = _isMFloosEnabled.asStateFlow()

    init {
        loadWalletStates()
    }

    private fun loadWalletStates() {
        viewModelScope.launch {
            _isJeebEnabled.value = walletRepo.isJeebEnabled()
            _isJawaliEnabled.value = walletRepo.isJawaliEnabled()
            _isKuraimiEnabled.value = walletRepo.isKuraimiEnabled()
            _isHasebEnabled.value = walletRepo.isHasebEnabled()
            _isOneCashEnabled.value = walletRepo.isOneCashEnabled()
            _isMFloosEnabled.value = walletRepo.isMFloosEnabled()
        }
    }

    fun toggleJeeb(enabled: Boolean) {
        viewModelScope.launch { walletRepo.setJeebEnabled(enabled); _isJeebEnabled.value = enabled }
    }
    fun toggleJawali(enabled: Boolean) {
        viewModelScope.launch { walletRepo.setJawaliEnabled(enabled); _isJawaliEnabled.value = enabled }
    }
    fun toggleKuraimi(enabled: Boolean) {
        viewModelScope.launch { walletRepo.setKuraimiEnabled(enabled); _isKuraimiEnabled.value = enabled }
    }
    fun toggleHaseb(enabled: Boolean) {
        viewModelScope.launch { walletRepo.setHasebEnabled(enabled); _isHasebEnabled.value = enabled }
    }
    fun toggleOneCash(enabled: Boolean) {
        viewModelScope.launch { walletRepo.setOneCashEnabled(enabled); _isOneCashEnabled.value = enabled }
    }
    fun toggleMFloos(enabled: Boolean) {
        viewModelScope.launch { walletRepo.setMFloosEnabled(enabled); _isMFloosEnabled.value = enabled }
    }

    // ─────────────────────────────────────────────
    // قوالب وإعدادات الرسائل
    // ─────────────────────────────────────────────

    private val _approvedSmsTemplates = MutableStateFlow<List<String>>(emptyList())
    val approvedSmsTemplates: StateFlow<List<String>> = _approvedSmsTemplates.asStateFlow()

    fun loadTemplates() {
        viewModelScope.launch {
            _approvedSmsTemplates.value = walletRepo.getApprovedSmsTemplates()
        }
    }

    fun addTemplate(template: String) {
        viewModelScope.launch {
            walletRepo.addApprovedSmsTemplate(template)
            loadTemplates()
        }
    }

    fun removeTemplate(template: String) {
        viewModelScope.launch {
            walletRepo.removeApprovedSmsTemplate(template)
            loadTemplates()
        }
    }
}

class WalletViewModelFactory(private val coreContainer: CoreContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WalletViewModel(coreContainer) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
