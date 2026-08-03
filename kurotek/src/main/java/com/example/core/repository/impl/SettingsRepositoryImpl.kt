package com.example.core.repository.impl

import com.example.core.repository.SettingsRepository
import com.example.core.settings.SettingsEngine
import kotlinx.coroutines.flow.first

/**
 * SettingsRepositoryImpl
 * يعتمد على SettingsEngine (DataStore) فقط. لا يحتوي على أي منطق شبكة.
 */
class SettingsRepositoryImpl(
    private val settingsEngine: SettingsEngine
) : SettingsRepository {

    override suspend fun isDarkModeEnabled(): Boolean =
        settingsEngine.isDarkTheme.first()

    override suspend fun setDarkMode(enabled: Boolean) =
        settingsEngine.setDarkTheme(enabled)

    override suspend fun getLanguage(): String =
        settingsEngine.appLanguage.first()

    override suspend fun setLanguage(lang: String) =
        settingsEngine.setAppLanguage(lang)

    override suspend fun getLastSyncTimestamp(): Long =
        settingsEngine.lastSyncTimestamp.first()

    override suspend fun setLastSyncTimestamp(timestamp: Long) =
        settingsEngine.setLastSyncTimestamp(timestamp)
}
