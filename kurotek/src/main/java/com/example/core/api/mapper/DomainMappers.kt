package com.example.core.api.mapper

import com.example.core.api.dto.AuthResponseDto
import com.example.core.api.dto.LicenseResponseDto
import com.example.core.model.LicenseState
import com.example.core.model.LicenseStatus
import com.example.core.model.UserSession

/**
 * Domain Mappers
 * مسؤولة حصرياً عن تحويل الـ DTOs إلى Core Models التي يفهمها الـ UseCase
 * يمنع وصول الـ UI أو الـ ViewModel للـ DTOs مباشرة
 */
object DomainMappers {
    
    fun mapAuthResponseToUserSession(userId: String, dto: AuthResponseDto): UserSession? {
        if (!dto.success || dto.token.isNullOrEmpty()) return null
        
        return UserSession(
            userId = userId,
            token = dto.token,
            role = dto.role ?: "UNKNOWN",
            loginTimestamp = System.currentTimeMillis(),
            expiryTimestamp = dto.expiry
        )
    }

    fun mapLicenseResponseToStatus(dto: LicenseResponseDto): LicenseStatus {
        val state = when {
            dto.isFrozen -> LicenseState.BLOCKED
            !dto.isValid -> LicenseState.EXPIRED
            else -> LicenseState.VALID
        }
        
        return LicenseStatus(
            state = state,
            licenseKey = null, // Handled internally
            expiryDate = System.currentTimeMillis() + (dto.remainingDays * 86400000L),
            features = dto.features
        )
    }
}
