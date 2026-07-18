package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.CoreContainer
import com.example.core.model.LicenseState
import com.example.core.model.LicenseStatus
import com.example.core.model.Resource
import com.example.database.CardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * AuthViewModel — النسخة المحدّثة (Clean Architecture)
 *
 * القاعدة الصارمة (ADR-006):
 * ViewModel يستدعي UseCase فقط. أي منطق خارج هذا الملف.
 *
 * ┌─────────────────┐
 * │  AuthViewModel  │
 * └────────┬────────┘
 *          │ invokes
 * ┌────────▼──────────────────────────────┐
 * │  ActivateLicenseUseCase               │
 * │  ValidateLicenseUseCase               │
 * │  (ValidateSmsAmountUseCase → SmsVM)   │
 * └────────┬──────────────────────────────┘
 *          │
 * ┌────────▼────────────┐
 * │  LicenseEngine      │
 * │  SecurityEngine     │
 * │  DeviceEngine       │
 * │  NetworkClient      │
 * └─────────────────────┘
 */
class AuthViewModel(
    private val repository: CardRepository,        // يبقى مؤقتاً لبيانات PIN/شاشة الدخول المحلية
    private val coreContainer: CoreContainer
) : ViewModel() {

    // ─────────────────────────────────────────────
    // حالة الجلسة
    // ─────────────────────────────────────────────
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _licenseStatus = MutableStateFlow<LicenseStatus?>(null)
    val licenseStatus: StateFlow<LicenseStatus?> = _licenseStatus.asStateFlow()

    private val _licenseLoading = MutableStateFlow(false)
    val licenseLoading: StateFlow<Boolean> = _licenseLoading.asStateFlow()

    private val _licenseError = MutableStateFlow<String?>(null)
    val licenseError: StateFlow<String?> = _licenseError.asStateFlow()

    // بيانات محلية لا تزال تعتمد على CardRepository (PIN / Trial المحلي)
    val isActivated: StateFlow<Boolean> = repository.isActivated
    val isTrialActive: StateFlow<Boolean> = repository.isTrialActive
    val isInitialLoginDone: StateFlow<Boolean> = repository.isInitialLoginDone
    val activeSerialKey: StateFlow<String> = repository.activeSerialKey
    val isPermissionDismissed: StateFlow<Boolean> = repository.isPermissionDismissed
    val generatedSerials: StateFlow<Set<String>> = repository.generatedSerials

    private val _networkName = MutableStateFlow("شبكة الدحشة")
    val networkName: StateFlow<String> = _networkName.asStateFlow()

    fun setActivated(activated: Boolean, key: String) {
        repository.setActivated(activated, key)
    }

    // ─────────────────────────────────────────────
    // دخول / خروج (PIN محلي)
    // ─────────────────────────────────────────────
    fun verifyPassword(password: String): Boolean {
        val correct = repository.getAppPassword() == password
        if (correct) {
            _isLoggedIn.value = true
            validateLicenseFromServer() // تحقق من الترخيص عند الدخول
        }
        return correct
    }

    fun logout() {
        _isLoggedIn.value = false
        viewModelScope.launch { coreContainer.authRepository.logout() }
    }

    fun changePassword(oldPass: String, newPass: String): Boolean {
        return if (repository.getAppPassword() == oldPass) {
            repository.setAppPassword(newPass)
            true
        } else false
    }

    fun setAppPasswordDirectly(newPass: String) = repository.setAppPassword(newPass)

    // ─────────────────────────────────────────────
    // [UseCase] التحقق من الترخيص (Server First)
    // ─────────────────────────────────────────────
    fun validateLicenseFromServer() {
        viewModelScope.launch {
            _licenseLoading.value = true
            _licenseError.value = null
            val status = coreContainer.validateLicenseUseCase()
            _licenseStatus.value = status
            // مزامنة الحالة مع المحلي
            if (status.state == LicenseState.VALID || status.state == LicenseState.TRIAL) {
                repository.setActivated(status.state == LicenseState.VALID, status.licenseKey ?: "")
            }
            _licenseLoading.value = false
        }
    }

    // ─────────────────────────────────────────────
    // [UseCase] تفعيل الترخيص
    // ─────────────────────────────────────────────
    fun activateLicense(serialKey: String, onResult: (success: Boolean, message: String) -> Unit) {
        viewModelScope.launch {
            _licenseLoading.value = true
            _licenseError.value = null
            when (val result = coreContainer.activateLicenseUseCase(serialKey)) {
                is Resource.Success -> {
                    _licenseStatus.value = result.data
                    repository.setActivated(true, serialKey)
                    onResult(true, "✅ تم تفعيل الترخيص بنجاح")
                }
                is Resource.Error -> {
                    _licenseError.value = result.message
                    // Fallback: تحقق من السيريال محلياً
                    if (isValidSerialLocally(serialKey)) {
                        repository.setActivated(true, serialKey)
                        onResult(true, "✅ تم التفعيل محلياً (وضع Offline)")
                    } else {
                        onResult(false, "❌ ${result.message}")
                    }
                }
                is Resource.Loading -> {}
            }
            _licenseLoading.value = false
        }
    }

    // ─────────────────────────────────────────────
    // سيريالات محلية (للـ Offline Fallback)
    // ─────────────────────────────────────────────
    fun generateNewSerial(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random = (1..6).map { chars.random() }.joinToString("")
        val serial = "DAHSHA_$random"
        repository.addGeneratedSerial(serial)
        return serial
    }

    fun generateDeterministicSerial(identifier: String): String {
        val trimmed = identifier.trim().uppercase()
        return "$trimmed-KS${deterministicHash(trimmed)}"
    }

    fun addCustomSerial(serial: String) {
        if (serial.trim().isNotEmpty()) repository.addGeneratedSerial(serial.trim())
    }

    fun deleteSerial(serial: String) = repository.removeGeneratedSerial(serial)

    fun getRemainingTrialDays(): Int = repository.getRemainingTrialDays()
    fun setInitialLoginDone(done: Boolean) = repository.setInitialLoginDone(done)
    fun setPermissionDismissed(dismissed: Boolean) = repository.setPermissionDismissed(dismissed)
    fun forceExpireTrial() = repository.forceExpireTrial()
    fun refreshTrialStatus() = repository.refreshTrialStatus()

    private fun isValidSerialLocally(serial: String): Boolean {
        val trimmed = serial.trim().uppercase()
        if (repository.isSerialValid(trimmed)) return true
        if (trimmed.contains("-KS")) {
            val parts = trimmed.split("-KS")
            if (parts.size == 2) {
                val id = parts[0].trim()
                val hash = parts[1].trim()
                if (id.isNotEmpty() && hash.length >= 4) return hash == deterministicHash(id)
            }
        }
        return false
    }

    private fun deterministicHash(identifier: String): String {
        val raw = identifier + "KayanSoftSecureSalt2026"
        return try {
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(raw.toByteArray(Charsets.UTF_8))
            hash.joinToString("") { String.format("%02X", it) }.take(6)
        } catch (e: Exception) { "ERROR" }
    }
}

class AuthViewModelFactory(
    private val repository: CardRepository,
    private val coreContainer: CoreContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository, coreContainer) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
