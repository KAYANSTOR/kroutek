package com.example.core.api.endpoint

import com.example.core.api.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * ApiEndpoints
 * واجهة الـ API الأساسية التي سيتم 구현ها باستخدام Retrofit
 * جميع المسارات (Endpoints) يجب أن تتطابق مع خادم Kurotek TypeScript/Node.js
 * الفعلي في server/src/ (وليس Go — انظر ADR-004 المُحدَّث في
 * ARCHITECTURE_DECISIONS.md). ⚠️ المسارات أدناه لا تطابق مسارات الخادم
 * الفعلية حالياً (توحيدها هو الخطوة التالية الصريحة المتفَق عليها؛ لا تُعِد
 * كتابة هذا الملف قبل تلك المهمة المخصَّصة لها).
 */
interface ApiEndpoints {

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: AuthRequestDto): Response<AuthResponseDto>

    @GET("api/v1/license/status/{deviceId}")
    suspend fun checkLicenseStatus(@Path("deviceId") deviceId: String): Response<LicenseResponseDto>

    @POST("api/v1/sync/transactions")
    suspend fun syncTransactions(@Body request: SyncTransactionsRequestDto): Response<SyncResponseDto>
}
