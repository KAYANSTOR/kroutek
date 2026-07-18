package com.example.core.network

import com.example.core.api.error.ApiError
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLHandshakeException

/**
 * ErrorMapper
 * تحويل أي استثناء خارجي إلى ApiError معروف. لا تتسرب أخطاء Retrofit خارج هذا الملف.
 */
object ErrorMapper {
    fun map(throwable: Throwable): ApiError {
        return when (throwable) {
            is CancellationException -> throw throwable // لا نلتقط إلغاء الـ Coroutines
            is SSLHandshakeException -> ApiError.NetworkError("SSL Error: ${throwable.message}")
            is SocketTimeoutException -> ApiError.TimeoutError()
            is IOException -> ApiError.NetworkError(throwable.message ?: "IO Error")
            is HttpException -> when (throwable.code()) {
                400 -> ApiError.ServerError(400, "Bad Request: ${throwable.message()}")
                401 -> ApiError.UnauthorizedError()
                403 -> ApiError.ServerError(403, "Forbidden")
                409 -> ApiError.ServerError(409, "Conflict")
                422 -> ApiError.ServerError(422, "Validation Error")
                in 500..599 -> ApiError.ServerError(throwable.code(), "Server Error")
                else -> ApiError.ServerError(throwable.code(), throwable.message())
            }
            is ApiError -> throwable
            else -> ApiError.UnknownError(throwable.message ?: "Unknown Error")
        }
    }
}

/**
 * RetryPolicy - Exponential Backoff
 */
object RetryPolicy {
    private const val MAX_RETRIES = 5
    private val DELAYS_MS = listOf(1_000L, 2_000L, 4_000L, 8_000L, 16_000L)

    suspend fun <T> withRetry(
        attempt: Int = 0,
        block: suspend () -> T
    ): T {
        return try {
            block()
        } catch (e: Throwable) {
            val mapped = ErrorMapper.map(e)
            val isRetryable = mapped is ApiError.NetworkError || mapped is ApiError.TimeoutError
                    || (mapped is ApiError.ServerError && mapped.code in 500..599)

            if (isRetryable && attempt < MAX_RETRIES - 1) {
                kotlinx.coroutines.delay(DELAYS_MS[attempt])
                withRetry(attempt + 1, block)
            } else {
                throw mapped
            }
        }
    }
}
