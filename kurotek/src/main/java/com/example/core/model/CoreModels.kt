package com.example.core.model

/**
 * A generic class that holds a value with its loading status.
 * Used primarily for UI and ViewModel observation (Domain -> UI).
 */
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val exception: Throwable, val message: String? = exception.message) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}

/**
 * A sealed class representing Network API responses.
 * Used in the Data layer to map HTTP responses.
 */
sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(val code: Int, val message: String) : ApiResponse<Nothing>()
    data class Exception(val e: Throwable) : ApiResponse<Nothing>()
}

enum class SyncStatus {
    PENDING,
    IN_PROGRESS,
    SUCCESS,
    FAILED
}

data class SyncTask(
    val id: String,
    val type: String, // e.g., "TRANSACTION", "APPROVAL"
    val payload: String, // JSON payload
    val status: SyncStatus,
    val retryCount: Int,
    val createdAt: Long,
    val lastAttemptAt: Long?
)

enum class LicenseState {
    VALID,
    EXPIRED,
    BLOCKED,
    TRIAL,
    UNREGISTERED
}

data class LicenseStatus(
    val state: LicenseState,
    val licenseKey: String?,
    val expiryDate: Long?,
    val features: List<String>
)

data class BackupInfo(
    val id: String,
    val fileName: String,
    val sizeBytes: Long,
    val recordCount: Int,
    val createdAt: Long,
    val isEncrypted: Boolean
)

data class DeviceInfo(
    val deviceId: String,
    val androidVersion: String,
    val deviceModel: String,
    val appVersion: String,
    val signatureHash: String,
    val isRooted: Boolean
)

data class UserSession(
    val userId: String,
    val token: String?,
    val role: String, // e.g., "DISTRIBUTOR", "ADMIN"
    val loginTimestamp: Long,
    val expiryTimestamp: Long?
)
