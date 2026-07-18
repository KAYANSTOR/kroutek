package com.example.core.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "kurotek_settings")

/**
 * Settings Engine
 * مسؤول حصرياً عن الإعدادات غير الحساسة (المظهر، اللغة، توقيتات المزامنة، التفضيلات).
 * لا يحتوي على أي توكنات، أو سيريالات، أو مفاتيح سرية (يتم حفظها في SecureStorage).
 */
class SettingsEngine(private val context: Context) {

    companion object {
        private val THEME_KEY = booleanPreferencesKey("is_dark_theme")
        private val LANGUAGE_KEY = stringPreferencesKey("app_language")
        private val LAST_SYNC_KEY = longPreferencesKey("last_sync_timestamp")
        private val AUTO_SMS_KEY = booleanPreferencesKey("auto_sms_enabled")
        private val NOTIFICATION_COMPOSE_KEY = booleanPreferencesKey("notification_compose_enabled")
    }

    // ==========================================
    // Theme (المظهر)
    // ==========================================
    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[THEME_KEY] ?: true // Default to Dark Theme
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[THEME_KEY] = isDark
        }
    }

    // ==========================================
    // Language (اللغة)
    // ==========================================
    val appLanguage: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LANGUAGE_KEY] ?: "ar" // Default Arabic
    }

    suspend fun setAppLanguage(lang: String) {
        context.dataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = lang
        }
    }

    // ==========================================
    // Last Sync (آخر مزامنة)
    // ==========================================
    val lastSyncTimestamp: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[LAST_SYNC_KEY] ?: 0L
    }

    suspend fun setLastSyncTimestamp(timestamp: Long) {
        context.dataStore.edit { prefs ->
            prefs[LAST_SYNC_KEY] = timestamp
        }
    }

    // ==========================================
    // UI Local Preferences (تفضيلات الواجهة والأدوات)
    // ==========================================
    val isAutoSmsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[AUTO_SMS_KEY] ?: false
    }

    suspend fun setAutoSmsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[AUTO_SMS_KEY] = enabled
        }
    }

    val isNotificationComposeEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[NOTIFICATION_COMPOSE_KEY] ?: true
    }

    suspend fun setNotificationComposeEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATION_COMPOSE_KEY] = enabled
        }
    }
}
