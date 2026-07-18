package com.example.core.api.dto

/**
 * Request DTOs (Data Transfer Objects)
 * يتم استخدامها حصرياً لإرسال البيانات إلى الـ API
 */
data class AuthRequestDto(
    val deviceId: String,
    val serialKey: String,
    val signature: String
)

data class SyncTransactionsRequestDto(
    val transactions: List<TransactionDto>,
    val lastSyncTimestamp: Long
)

data class TransactionDto(
    val localId: String,
    val amount: Int,
    val phone: String,
    val walletType: String,
    val timestamp: Long
)

/**
 * Response DTOs
 * يتم استخدامها حصرياً لاستقبال البيانات من الـ API
 */
data class AuthResponseDto(
    val success: Boolean,
    val token: String?,
    val role: String?,
    val expiry: Long?,
    val message: String?
)

data class SyncResponseDto(
    val success: Boolean,
    val serverTimestamp: Long,
    val conflicts: List<String>
)

data class LicenseResponseDto(
    val isValid: Boolean,
    val isFrozen: Boolean,
    val remainingDays: Int,
    val features: List<String>
)
