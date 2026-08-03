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
    // ⚠️ لا يوجد مسار احتياطي دون اتصال بعد الآن — قرار أمني صريح ومكرَّر
    // من صاحب المشروع (نفس القرار المطبَّق سابقاً في SecurityApiService.kt
    // ضمن هذه الجلسة). كانت كتلة "Offline Fallback" التي أُزيلت هنا تعتمد
    // على deterministicHash: هاش SHA-256 من (المعرّف + Salt ثابت مكشوف
    // في الكود "KayanSoftSecureSalt2026") — أي شخص يفكّ الـ APK يستخرج نفس
    // الخوارزمية ويولّد سيريالات "صالحة" محلياً بلا شراء وبلا اتصال بالسيرفر
    // إطلاقاً. كانت دوال التوليد المرتبطة (generateNewSerial،
    // generateDeterministicSerial، addCustomSerial) غير مستخدَمة في أي
    // شاشة أصلاً (تحقّقت بالبحث في كامل المشروع) فأُزيلت معها كاملة.
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
                    onResult(false, "❌ ${result.message}")
                }
                is Resource.Loading -> {}
            }
            _licenseLoading.value = false
        }
    }

    fun deleteSerial(serial: String) = repository.removeGeneratedSerial(serial)

    fun getRemainingTrialDays(): Int = repository.getRemainingTrialDays()
    fun setInitialLoginDone(done: Boolean) = repository.setInitialLoginDone(done)
    fun setPermissionDismissed(dismissed: Boolean) = repository.setPermissionDismissed(dismissed)
    fun forceExpireTrial() = repository.forceExpireTrial()
    fun refreshTrialStatus() = repository.refreshTrialStatus()
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
