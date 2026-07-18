package com.example.core.network

import com.example.core.api.error.ApiError

/**
 * الواجهة الأساسية التي تخفي أي تفاصيل عن Retrofit أو غيره.
 * المستودعات (Repositories) ستستخدم فقط هذا العقد.
 */
interface NetworkClient {
    /**
     * ينفذ الطلب المعطى، يطبق سياسات إعادة المحاولة (Retry Policy)، 
     * ويرصد الأخطاء لتحويلها إلى `ApiError`.
     */
    suspend fun <T> executeRequest(request: suspend () -> T): Result<T>
}

/**
 * يوفر تفاصيل الجلسة الحالية وتجديد التوكن دون ربط مباشر بـ DataStore داخل المحرك.
 */
interface TokenProvider {
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    /**
     * يحاول تجديد التوكن. يرجع null إذا فشل التجديد (مما يستدعي تسجيل الخروج)
     */
    suspend fun refreshAccessToken(): String? 
    suspend fun onTokenRefreshFailed()
}

/**
 * واجهة لمراقبة حالة الشبكة لتسهيل عمل الـ Sync Engine ومحرك الشبكة.
 */
interface NetworkMonitor {
    val isOnline: Boolean
    val isMetered: Boolean
    val isWifi: Boolean
    fun startMonitoring()
    fun stopMonitoring()
}

/**
 * طابور الطلبات المعلقة (مثلاً في حال الـ Offline أو الفشل المتكرر).
 */
interface PendingRequestQueue {
    suspend fun enqueue(requestData: String, endpointName: String)
    suspend fun dequeue(): String?
    suspend fun clear()
}

/**
 * واجهة مستقلة لتسجيل الأحداث (Logcat / File / Firebase Crashlytics).
 */
interface Logger {
    fun d(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable? = null)
    fun i(tag: String, message: String)
}

/**
 * خطاف (Hook) لقياس وتسجيل الإحصائيات (Metrics) مستقبلاً.
 */
interface MetricsHook {
    fun onRequestStart(endpoint: String)
    fun onRequestSuccess(endpoint: String, durationMs: Long, bytesReceived: Long)
    fun onRequestError(endpoint: String, durationMs: Long, errorType: String)
}
