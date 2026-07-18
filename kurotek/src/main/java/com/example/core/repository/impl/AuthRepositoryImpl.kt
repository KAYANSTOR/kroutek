package com.example.core.repository.impl

import com.example.core.api.endpoint.ApiEndpoints
import com.example.core.api.dto.AuthRequestDto
import com.example.core.api.mapper.DomainMappers
import com.example.core.device.DeviceEngine
import com.example.core.model.Resource
import com.example.core.model.UserSession
import com.example.core.network.NetworkClient
import com.example.core.repository.AuthRepository
import com.example.core.security.SecurityEngine
import com.example.core.session.SessionManager

/**
 * AuthRepositoryImpl
 * يُنسّق بين Network Engine و Session Manager و Security Engine و Device Engine.
 * الـ ViewModel لا يعلم أي شيء عن هذه التفاصيل.
 */
class AuthRepositoryImpl(
    private val networkClient: NetworkClient,
    private val apiEndpoints: ApiEndpoints,
    private val sessionManager: SessionManager,
    private val deviceEngine: DeviceEngine,
    private val securityEngine: SecurityEngine? = null
) : AuthRepository {

    override suspend fun login(userId: String, passwordHash: String): Resource<UserSession> {
        val deviceInfo = deviceEngine.getDeviceInfo()
        val aesKey = SecurityEngine.generateAesKey("KUROTEK_KEY_SEED_v1")
        val encryptedSerial = SecurityEngine.encryptAES(passwordHash, aesKey)
        val signature = SecurityEngine.hashSHA256("${deviceInfo.deviceId}:$encryptedSerial")

        val request = AuthRequestDto(
            deviceId = deviceInfo.deviceId,
            serialKey = encryptedSerial,
            signature = signature
        )

        val result = networkClient.executeRequest {
            apiEndpoints.login(request)
        }

        return result.fold(
            onSuccess = { response ->
                val body = response.body()
                if (body != null && body.success) {
                    val session = DomainMappers.mapAuthResponseToUserSession(userId, body)
                    if (session != null) {
                        sessionManager.saveSession(session)
                        Resource.Success(session)
                    } else {
                        Resource.Error(Exception("Invalid server response"))
                    }
                } else {
                    Resource.Error(Exception(body?.message ?: "Login failed"))
                }
            },
            onFailure = { Resource.Error(it) }
        )
    }

    override suspend fun logout(): Resource<Unit> {
        sessionManager.clearSession()
        return Resource.Success(Unit)
    }

    override suspend fun getCurrentSession(): UserSession? =
        sessionManager.getSession()

    override suspend fun requireValidSession(): Boolean =
        sessionManager.isSessionValid() && sessionManager.getSession() != null
}
