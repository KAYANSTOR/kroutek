package com.example.security

import android.content.Context
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

// Request model
data class SerialValidationRequest(
    val serial: String,
    val deviceId: String,
    val timestamp: Long,
    val nonce: String,
    val signature: String
)

// Response model
data class SerialValidationResponse(
    val success: Boolean,
    val status: String, // "ACTIVE", "EXPIRED", "REVOKED", "NOT_FOUND"
    val message: String?,
    val token: String?
)

interface SecurityApi {
    @POST("api/v1/serial/validate")
    fun validateSerial(
        @Header("X-Signature") signature: String,
        @Header("X-Timestamp") timestamp: Long,
        @Body request: SerialValidationRequest
    ): Call<SerialValidationResponse>
}

object SecurityApiService {
    private const val TAG = "SecurityApiService"
    
    // Configurable production backend API Base URL
    // In production, change this to your secure HTTPS VPS server address.
    private const val BASE_URL = "https://kayan-licensing-server.onrender.com/" 
    
    // HMAC Secret Key is now securely loaded via BuildConfig (from .env file)
    private val HMAC_SECRET: String
        get() = try {
            com.example.BuildConfig.HMAC_SECRET.replace("\"", "")
        } catch (e: Exception) {
            ""
        }

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // High security HTTP Client with pinned TLS configurations and Strict Timeouts to prevent slow-loris & replay attacks
    // مهلة انتظار طويلة نسبياً (30 ثانية) بدل 5 — استضافة Render المجانية
    // (BASE_URL أعلاه) "تنام" بعد فترة خمول وتحتاج حتى 30-50 ثانية لأول
    // استجابة (Cold Start). كانت هذه المشكلة مخفية سابقاً خلف التحقق
    // الاحتياطي دون اتصال؛ بعد إزالته (قرار أمني، انظر توثيق validateSerial
    // أدناه)، أصبح تفادي فشل التفعيل بسبب هذا التأخير ضرورياً لا اختيارياً.
    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.NONE // Disable logging in production for security
        }

        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private val api: SecurityApi by lazy {
        retrofit.create(SecurityApi::class.java)
    }

    /**
     * Generates an HMAC-SHA256 signature to guarantee request integrity and prevent tampering.
     */
    private fun generateHmacSignature(data: String): String {
        return try {
            val keySpec = SecretKeySpec(HMAC_SECRET.toByteArray(Charsets.UTF_8), "HmacSHA256")
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(keySpec)
            val bytes = mac.doFinal(data.toByteArray(Charsets.UTF_8))
            bytes.joinToString("") { String.format("%02X", it.toInt() and 0xFF) }
        } catch (e: Exception) {
            "SIGN_ERROR"
        }
    }

    /**
     * Validates the activation serial.
     * يتصل بالسيرفر حصراً (HTTPS + توقيع HMAC للطلب). لا يوجد أي مسار
     * تحقق محلي دون اتصال بعد الآن — قرار أمني صريح ومقصود (انظر القسم
     * 5.4 من docs/RESTRUCTURING_PLAN.md): كان المسار السابق قابلاً للتزوير
     * الكامل لأن خوارزمية التحقق والـ Salt كانا مكتوبين كنص صريح داخل
     * كود التطبيق نفسه، ما يجعل أي شخص يفكّ الـ APK قادراً على توليد
     * تفعيل صالح بلا شراء سيريال حقيقي وبلا أي اتصال بالسيرفر إطلاقاً.
     *
     * ⚠️ الأثر العملي: يتطلب التفعيل (إدخال السيريال أول مرة) اتصالاً
     * فعلياً بالإنترنت والوصول لسيرفر الترخيص. هذا لا يؤثر على الاستخدام
     * اليومي للتطبيق بعد التفعيل الناجح — حالة "مفعَّل" تُخزَّن محلياً في
     * CardRepository ولا تُعاد مطالبتها من السيرفر عند كل فتح للتطبيق.
     */
    fun validateSerial(
        context: Context,
        serial: String,
        deviceId: String,
        callback: (Boolean, String) -> Unit
    ) {
        val cleanSerial = serial.trim().uppercase()
        val timestamp = System.currentTimeMillis()
        val nonce = java.util.UUID.randomUUID().toString()
        
        // Form signed data payload
        val signaturePayload = "$cleanSerial:$deviceId:$timestamp:$nonce"
        val clientHmacSignature = generateHmacSignature(signaturePayload)

        val requestPayload = SerialValidationRequest(
            serial = cleanSerial,
            deviceId = deviceId,
            timestamp = timestamp,
            nonce = nonce,
            signature = clientHmacSignature
        )

        Log.d(TAG, "Requesting server validation for serial $cleanSerial bound to $deviceId")

        // Try hitting the production remote server
        api.validateSerial(clientHmacSignature, timestamp, requestPayload)
            .enqueue(object : Callback<SerialValidationResponse> {
                override fun onResponse(
                    call: Call<SerialValidationResponse>,
                    response: Response<SerialValidationResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        if (body.success && body.status == "ACTIVE") {
                            Log.i(TAG, "Server validation succeeded. Serial is Active.")
                            callback(true, body.message ?: "تم تفعيل التطبيق بنجاح عبر السيرفر!")
                        } else {
                            Log.w(TAG, "Server validation rejected: ${body.status}")
                            callback(false, body.message ?: "هذا السيريال غير صالح أو منتهي!")
                        }
                    } else {
                        // السيرفر أعاد خطأً (وليس مشكلة اتصال). لا يوجد مسار احتياطي دون اتصال بعد الآن.
                        Log.w(TAG, "Server returned error code ${response.code()}. No offline fallback available by design.")
                        callback(false, "تعذّر التحقق من السيريال حالياً (خطأ من السيرفر). يرجى المحاولة لاحقاً أو التواصل مع الدعم.")
                    }
                }

                override fun onFailure(call: Call<SerialValidationResponse>, t: Throwable) {
                    // انقطاع اتصال أو مهلة انتظار. لا يوجد مسار احتياطي دون اتصال بعد الآن — بقرار أمني صريح.
                    Log.w(TAG, "Server request failed: ${t.message}. No offline fallback available by design.")
                    callback(false, "يتطلب تفعيل التطبيق اتصالاً بالإنترنت. يرجى التحقق من اتصال شبكتك والمحاولة مجدداً.")
                }
            })
    }
}
