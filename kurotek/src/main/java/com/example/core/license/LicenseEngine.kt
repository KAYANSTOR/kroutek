package com.example.core.license

import android.content.Context
import com.example.core.api.endpoint.ApiEndpoints
import com.example.core.api.mapper.DomainMappers
import com.example.core.device.DeviceEngine
import com.example.core.model.LicenseState
import com.example.core.model.LicenseStatus
import com.example.core.model.Resource
import com.example.core.network.NetworkClient
import com.example.core.repository.LicenseRepository
import com.example.core.security.SecurityEngine
import com.example.core.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * LicenseEngine (LicenseRepositoryImpl)
 * يعتمد على: DeviceEngine + SecurityEngine + NetworkEngine + SessionManager.
 *
 * المسؤوليات:
 * - Trial: التحقق من انقضاء الفترة التجريبية محلياً
 * - Activation: تفعيل الترخيص عبر السيرفر (مع Device Binding)
 * - Renewal: تجديد الترخيص
 * - Suspend/Freeze: الاستجابة لأوامر السيرفر بتجميد الجهاز
 * - Grace Period: منح مهلة إضافية في حال انقطاع الشبكة
 * - License Validation: التحقق المحلي والسيرفر معاً
 */
class LicenseEngine(
    private val context: Context,
    private val networkClient: NetworkClient,
    private val apiEndpoints: ApiEndpoints,
    private val deviceEngine: DeviceEngine,
    private val sessionManager: SessionManager
) : LicenseRepository {

    // الحالة الحالية للترخيص (مرئية للـ ViewModel عبر StateFlow)
    private val _licenseStatus = MutableStateFlow(loadCachedStatus())
    val licenseStatus: StateFlow<LicenseStatus> get() = _licenseStatus

    companion object {
        private const val TRIAL_DURATION_MS = 5 * 24 * 60 * 60 * 1000L // 5 أيام
        private const val GRACE_PERIOD_MS = 24 * 60 * 60 * 1000L       // 24 ساعة
        private const val PREFS_NAME = "license_data"
        private const val KEY_INSTALL_TIME = "install_time"
        private const val KEY_SERIAL = "license_serial"
        private const val KEY_EXPIRY = "license_expiry"
        private const val KEY_FROZEN = "is_frozen"
    }

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // ==========================================
    // 1. Trial Management
    // ==========================================
    private fun getInstallTime(): Long {
        var t = prefs.getLong(KEY_INSTALL_TIME, 0L)
        if (t == 0L) {
            t = System.currentTimeMillis()
            prefs.edit().putLong(KEY_INSTALL_TIME, t).apply()
        }
        return t
    }

    private fun isTrialValid(): Boolean {
        val elapsed = System.currentTimeMillis() - getInstallTime()
        return elapsed < TRIAL_DURATION_MS
    }

    // ==========================================
    // 2. Local Cache Read
    // ==========================================
    private fun loadCachedStatus(): LicenseStatus {
        val isFrozen = prefs.getBoolean(KEY_FROZEN, false)
        val expiry = prefs.getLong(KEY_EXPIRY, 0L)
        val serial = prefs.getString(KEY_SERIAL, null)

        val state = when {
            isFrozen -> LicenseState.BLOCKED
            serial.isNullOrEmpty() -> if (isTrialValid()) LicenseState.TRIAL else LicenseState.UNREGISTERED
            expiry > 0 && System.currentTimeMillis() > expiry + GRACE_PERIOD_MS -> LicenseState.EXPIRED
            else -> LicenseState.VALID
        }
        return LicenseStatus(state = state, licenseKey = serial, expiryDate = expiry, features = emptyList())
    }

    // ==========================================
    // 3. Remote Validation (Server First)
    // ==========================================
    override suspend fun getLicenseStatus(): LicenseStatus {
        val deviceInfo = deviceEngine.getDeviceInfo()

        val result = networkClient.executeRequest {
            apiEndpoints.checkLicenseStatus(deviceInfo.deviceId)
        }

        return result.fold(
            onSuccess = { response ->
                val body = response.body()
                if (body != null) {
                    val status = DomainMappers.mapLicenseResponseToStatus(body)
                    cacheStatus(status)
                    _licenseStatus.value = status
                    status
                } else {
                    // Fallback to cached status with Grace Period
                    applyGracePeriod()
                }
            },
            onFailure = {
                // Offline → Grace Period
                applyGracePeriod()
            }
        )
    }

    // ==========================================
    // 4. Activation (Device Binding)
    // ==========================================
    override suspend fun activateLicense(key: String): Resource<LicenseStatus> {
        val deviceInfo = deviceEngine.getDeviceInfo()
        val aesKey = SecurityEngine.generateAesKey("KUROTEK_KEY_SEED_v1")
        val encryptedKey = SecurityEngine.encryptAES(key, aesKey)
        val signature = SecurityEngine.generateHMAC(
            data = "${deviceInfo.deviceId}:$encryptedKey",
            secret = "KUROTEK_HMAC_SECRET_v1"
        )

        // حفظ السيريال محلياً مشفراً
        prefs.edit()
            .putString(KEY_SERIAL, SecurityEngine.encryptAES(key, aesKey))
            .apply()

        // التحقق من السيرفر
        val freshStatus = getLicenseStatus()
        return if (freshStatus.state == LicenseState.VALID) {
            Resource.Success(freshStatus)
        } else {
            Resource.Error(Exception("Activation failed: ${freshStatus.state}"))
        }
    }

    // ==========================================
    // 5. Deactivation / Suspension
    // ==========================================
    override suspend fun deactivateLicense(): Resource<Unit> {
        prefs.edit()
            .remove(KEY_SERIAL)
            .remove(KEY_EXPIRY)
            .putBoolean(KEY_FROZEN, false)
            .apply()
        _licenseStatus.value = loadCachedStatus()
        return Resource.Success(Unit)
    }

    // ==========================================
    // 6. Local Validation (Fast Path)
    // ==========================================
    override suspend fun validateLocalLicense(): Boolean {
        val status = loadCachedStatus()
        return status.state == LicenseState.VALID || status.state == LicenseState.TRIAL
    }

    // ==========================================
    // Helpers
    // ==========================================
    private fun cacheStatus(status: LicenseStatus) {
        prefs.edit()
            .putBoolean(KEY_FROZEN, status.state == LicenseState.BLOCKED)
            .putLong(KEY_EXPIRY, status.expiryDate ?: 0L)
            .apply()
    }

    private fun applyGracePeriod(): LicenseStatus {
        val cached = loadCachedStatus()
        // إذا كان صالحاً ضمن فترة السماح → نعامله كـ VALID محلياً
        val isInGrace = cached.expiryDate?.let {
            System.currentTimeMillis() < it + GRACE_PERIOD_MS
        } ?: false

        return if (isInGrace || cached.state == LicenseState.VALID || cached.state == LicenseState.TRIAL) {
            cached
        } else {
            cached.copy(state = LicenseState.EXPIRED)
        }
    }

    fun freezeDevice() {
        prefs.edit().putBoolean(KEY_FROZEN, true).apply()
        _licenseStatus.value = loadCachedStatus()
    }
}
