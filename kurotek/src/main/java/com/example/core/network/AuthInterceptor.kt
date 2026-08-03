package com.example.core.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * AuthInterceptor
 * يُضيف توكن المصادقة لكل طلب تلقائياً.
 * يسأل TokenProvider فقط — لا DataStore مباشرةً.
 * في حال انتهاء التوكن (401)، يُفوّض عملية التجديد بشكل نظيف.
 */
class AuthInterceptor(
    private val tokenProvider: TokenProvider,
    private val logger: Logger
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = kotlinx.coroutines.runBlocking { tokenProvider.getAccessToken() }
        val request = if (!token.isNullOrEmpty()) {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }

        val response = chain.proceed(request)

        // إذا كان الرد 401 → حاول تجديد التوكن وأعد الطلب مرة واحدة
        if (response.code == 401) {
            logger.i("AuthInterceptor", "Received 401 — attempting token refresh")
            val newToken = kotlinx.coroutines.runBlocking { tokenProvider.refreshAccessToken() }

            return if (!newToken.isNullOrEmpty()) {
                response.close()
                val retryRequest = chain.request().newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
                chain.proceed(retryRequest)
            } else {
                logger.e("AuthInterceptor", "Token refresh failed — triggering logout")
                kotlinx.coroutines.runBlocking { tokenProvider.onTokenRefreshFailed() }
                response
            }
        }

        return response
    }
}
