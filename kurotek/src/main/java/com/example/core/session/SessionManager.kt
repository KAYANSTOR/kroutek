package com.example.core.session

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.core.model.UserSession
import com.example.core.network.TokenProvider
import com.example.core.security.SecurityEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * SessionManager
 * مدير الجلسة المركزي. يُخزّن التوكن في EncryptedSharedPreferences (مشفر بـ Android Keystore).
 * هو التطبيق الوحيد المسموح له بحفظ وقراءة التوكنات والسيريالات الحساسة.
 * يُطبّق واجهة TokenProvider لربطه بـ AuthInterceptor.
 */
class SessionManager(
    private val context: Context,
    private val onLogout: suspend () -> Unit
) : TokenProvider {

    private val securePrefs = try {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
            
        EncryptedSharedPreferences.create(
            context,
            "secure_session",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        context.getSharedPreferences("secure_session", Context.MODE_PRIVATE).edit().clear().commit()
        context.getSharedPreferences("secure_session", Context.MODE_PRIVATE)
    }

    private val _currentSession = MutableStateFlow<UserSession?>(null)
    val currentSession: StateFlow<UserSession?> get() = _currentSession

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_EXPIRY = "expiry"
    }

    fun saveSession(session: UserSession) {
        securePrefs.edit()
            .putString(KEY_ACCESS_TOKEN, session.token)
            .putString(KEY_USER_ID, session.userId)
            .putString(KEY_USER_ROLE, session.role)
            .putLong(KEY_EXPIRY, session.expiryTimestamp ?: 0L)
            .apply()
        _currentSession.value = session
    }

    fun getSession(): UserSession? {
        val userId = securePrefs.getString(KEY_USER_ID, null) ?: return null
        val token = securePrefs.getString(KEY_ACCESS_TOKEN, null)
        val role = securePrefs.getString(KEY_USER_ROLE, "UNKNOWN") ?: "UNKNOWN"
        val expiry = securePrefs.getLong(KEY_EXPIRY, 0L)
        return UserSession(
            userId = userId,
            token = token,
            role = role,
            loginTimestamp = 0L,
            expiryTimestamp = expiry
        ).also { _currentSession.value = it }
    }

    fun clearSession() {
        securePrefs.edit().clear().apply()
        _currentSession.value = null
    }

    fun isSessionValid(): Boolean {
        val expiry = securePrefs.getLong(KEY_EXPIRY, 0L)
        return expiry == 0L || System.currentTimeMillis() < expiry
    }

    // ==========================================
    // TokenProvider Implementation (للـ AuthInterceptor)
    // ==========================================
    override suspend fun getAccessToken(): String? =
        securePrefs.getString(KEY_ACCESS_TOKEN, null)

    override suspend fun getRefreshToken(): String? =
        securePrefs.getString(KEY_REFRESH_TOKEN, null)

    override suspend fun refreshAccessToken(): String? {
        // TODO: استدعاء الـ Network للحصول على توكن جديد باستخدام Refresh Token
        // يتم تطبيق هذا الجزء بعد اكتمال Repository Implementation
        return null
    }

    override suspend fun onTokenRefreshFailed() {
        clearSession()
        onLogout()
    }
}
