package com.example.core.repository

import com.example.core.model.*

/**
 * Interface for License checking, fetching, and validating
 */
interface LicenseRepository {
    suspend fun getLicenseStatus(): LicenseStatus
    suspend fun activateLicense(key: String): Resource<LicenseStatus>
    suspend fun deactivateLicense(): Resource<Unit>
    suspend fun validateLocalLicense(): Boolean
}

/**
 * Interface for synchronization operations
 */
interface SyncRepository {
    suspend fun enqueueSyncTask(task: SyncTask): Resource<Unit>
    suspend fun getPendingTasks(): List<SyncTask>
    suspend fun markTaskCompleted(taskId: String)
    suspend fun markTaskFailed(taskId: String, error: String)
}

/**
 * Interface for Database/App Backup Operations
 */
interface BackupRepository {
    suspend fun createBackup(): Resource<BackupInfo>
    suspend fun restoreBackup(backupId: String): Resource<Unit>
    suspend fun listAvailableBackups(): List<BackupInfo>
    suspend fun deleteBackup(backupId: String): Resource<Unit>
}

/**
 * Interface for purely UI and App Settings (Theme, Lang, etc.)
 */
interface SettingsRepository {
    suspend fun isDarkModeEnabled(): Boolean
    suspend fun setDarkMode(enabled: Boolean)
    
    suspend fun getLanguage(): String
    suspend fun setLanguage(lang: String)

    suspend fun getLastSyncTimestamp(): Long
    suspend fun setLastSyncTimestamp(timestamp: Long)
}

/**
 * Interface for Authentication & Session
 */
interface AuthRepository {
    suspend fun login(userId: String, passwordHash: String): Resource<UserSession>
    suspend fun logout(): Resource<Unit>
    suspend fun getCurrentSession(): UserSession?
    suspend fun requireValidSession(): Boolean
}

/**
 * Interface for cryptography operations wrapping SecurityEngine
 */
interface SecurityRepository {
    fun encryptData(data: String): String
    fun decryptData(encryptedData: String): String
    fun hashData(data: String): String
    fun generateSecureRandomString(length: Int): String
}
