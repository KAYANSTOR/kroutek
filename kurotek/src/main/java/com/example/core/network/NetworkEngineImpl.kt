package com.example.core.network

import com.example.core.api.error.ApiError
import com.example.core.security.SecurityEngine
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * NetworkConfig
 * مسؤولة عن إنشاء مثيل OkHttpClient + Retrofit. القيم قابلة للتغيير من خارج المحرك.
 */
data class NetworkConfig(
    val baseUrl: String,
    val connectTimeoutSecs: Long = 30L,
    val readTimeoutSecs: Long = 30L,
    val writeTimeoutSecs: Long = 30L,
    val enableLogging: Boolean = true,
    // Certificate Pinning Placeholder — فعّله عند الإنتاج
    val pinnedCertificates: List<String> = emptyList()
)

/**
 * NetworkEngineImpl
 * التطبيق الرئيسي لـ NetworkClient Interface.
 * المحرك لا يعرف أي Endpoint وليس لديه أي Business Logic.
 * هو فقط يُنفّذ الطلبات (Execute Requests).
 */
class NetworkEngineImpl(
    private val config: NetworkConfig,
    private val tokenProvider: TokenProvider,
    private val networkMonitor: NetworkMonitor,
    private val pendingQueue: PendingRequestQueue,
    private val logger: Logger,
    private val metricsHook: MetricsHook,
    private val securityEngine: com.example.core.security.SecurityEngine?
) : NetworkClient {

    private val authInterceptor = AuthInterceptor(tokenProvider, logger)

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        logger.d("OkHttp", message)
    }.apply {
        level = if (config.enableLogging)
            HttpLoggingInterceptor.Level.BODY
        else
            HttpLoggingInterceptor.Level.NONE
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(config.connectTimeoutSecs, TimeUnit.SECONDS)
        .readTimeout(config.readTimeoutSecs, TimeUnit.SECONDS)
        .writeTimeout(config.writeTimeoutSecs, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        // Certificate Pinning Placeholder
        // .certificatePinner(buildCertificatePinner())
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(config.baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * الدالة الوحيدة في المحرك: تُنفّذ أي طلب وتطبق عليه:
     * - Offline Guard
     * - Retry (Exponential Backoff)
     * - Error Mapping
     * - Metrics Hooks
     */
    override suspend fun <T> executeRequest(request: suspend () -> T): Result<T> {
        val endpoint = "UNKNOWN" // سيُستبدل بـ method name في الـ profiling المتقدم
        val startTime = System.currentTimeMillis()

        if (!networkMonitor.isOnline) {
            logger.i("NetworkEngine", "Device is offline. Routing to PendingQueue.")
            metricsHook.onRequestError(endpoint, 0, "OFFLINE")
            return Result.failure(ApiError.NetworkError("لا يوجد اتصال بالإنترنت"))
        }

        metricsHook.onRequestStart(endpoint)

        return try {
            val result = RetryPolicy.withRetry { request() }
            val duration = System.currentTimeMillis() - startTime
            metricsHook.onRequestSuccess(endpoint, duration, -1)
            logger.d("NetworkEngine", "✅ Request succeeded in ${duration}ms")
            Result.success(result)
        } catch (e: Throwable) {
            val duration = System.currentTimeMillis() - startTime
            val error = ErrorMapper.map(e)
            metricsHook.onRequestError(endpoint, duration, error::class.simpleName ?: "Unknown")
            logger.e("NetworkEngine", "❌ Request failed: ${error.message}", error)
            Result.failure(error)
        }
    }
}
