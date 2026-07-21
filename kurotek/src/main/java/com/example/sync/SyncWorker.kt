package com.example.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * WorkManager Worker مسؤول عن رفع محتوى طابور Outbox للسيرفر.
 *
 * يُشغَّل:
 *   - دورياً كل 15 دقيقة (الحد الأدنى لـ WorkManager).
 *   - فوراً عند الاتصال بالإنترنت (باستخدام Constraints.CONNECTED).
 *
 * آلية العمل:
 *   1. يقرأ دفعة من الطابور (حتى 50 إدخال).
 *   2. يجمّعها في طلب Push واحد ويرسلها للسيرفر.
 *   3. عند نجاح الدفعة → يحذفها من الطابور.
 *   4. عند فشل إدخال بعينه → يزيد عداد المحاولات فقط، ويكمل الباقي.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val outboxRepository: SyncOutboxRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val pending = outboxRepository.getPending(limit = 50)
            if (pending.isEmpty()) return@withContext Result.success()

            val serverUrl = inputData.getString(KEY_SERVER_URL)
                ?: return@withContext Result.failure(
                    workDataOf("error" to "SERVER_URL not configured")
                )
            val deviceToken = inputData.getString(KEY_DEVICE_TOKEN)
                ?: return@withContext Result.failure(
                    workDataOf("error" to "DEVICE_TOKEN not configured")
                )

            val successIds = mutableListOf<String>()

            // نجمّع الإدخالات في payload واحدة لتقليل عدد الطلبات
            val changes = JSONArray()
            for (entry in pending) {
                val change = JSONObject().apply {
                    put("entityType", entry.entityType)
                    put("entityId", entry.entityId)
                    put("operation", entry.operation)
                    put("payload", if (entry.payload != null) JSONObject(entry.payload) else JSONObject.NULL)
                    put("clientTimestamp", entry.createdAt)
                }
                changes.put(change)
            }

            val requestBody = JSONObject().apply {
                put("changes", changes)
            }.toString()

            // إرسال الطلب للسيرفر
            val url = URL("$serverUrl/sync/push")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod  = "POST"
                connectTimeout = 15_000
                readTimeout    = 30_000
                doOutput       = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Authorization", "Bearer $deviceToken")
            }

            val responseCode = connection.responseCode

            if (responseCode in 200..299) {
                // نجاح: نقرأ الـ IDs التي قبلها السيرفر
                val responseText = connection.inputStream.bufferedReader().readText()
                val responseJson = JSONObject(responseText)
                val syncedIds    = responseJson.optJSONArray("syncedIds")

                if (syncedIds != null) {
                    val ids = (0 until syncedIds.length()).map { syncedIds.getString(it) }
                    // نحذف فقط الإدخالات التي أكد السيرفر استلامها
                    val confirmedIds = pending.filter { it.entityId in ids }.map { it.id }
                    if (confirmedIds.isNotEmpty()) {
                        outboxRepository.markSyncedBatch(confirmedIds)
                        successIds.addAll(confirmedIds)
                    }
                } else {
                    // السيرفر أجاب بنجاح دون قائمة → نعتبر الكل منجزاً
                    outboxRepository.markSyncedBatch(pending.map { it.id })
                    successIds.addAll(pending.map { it.id })
                }
            } else {
                // فشل كلي: نسجّل الخطأ على جميع الإدخالات ونعيد المحاولة لاحقاً
                val error = "HTTP $responseCode"
                for (entry in pending) {
                    outboxRepository.markFailed(entry.id, error)
                }
                return@withContext Result.retry()
            }

            connection.disconnect()

            // تنظيف الإدخالات الميتة
            outboxRepository.clearDeadLetters()

            Result.success(workDataOf("syncedCount" to successIds.size))

        } catch (e: Exception) {
            // خطأ في الشبكة أو أي استثناء آخر → أعد المحاولة
            for (entry in outboxRepository.getPending(limit = 50)) {
                outboxRepository.markFailed(entry.id, e.message ?: "Unknown error")
            }
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME         = "SyncOutboxWorker"
        const val KEY_SERVER_URL    = "server_url"
        const val KEY_DEVICE_TOKEN  = "device_token"

        /**
         * جدوِل SyncWorker ليعمل دورياً عند توفر الإنترنت.
         * استدعِ هذه الدالة من Application.onCreate() أو بعد التفعيل.
         */
        fun schedule(context: Context, serverUrl: String, deviceToken: String) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val inputData = workDataOf(
                KEY_SERVER_URL   to serverUrl,
                KEY_DEVICE_TOKEN to deviceToken
            )

            val request = PeriodicWorkRequestBuilder<SyncWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setInputData(inputData)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }

        /** شغّل المزامنة فوراً مرة واحدة (مثلاً بعد أي تغيير محلي مهم) */
        fun runNow(context: Context, serverUrl: String, deviceToken: String) {
            val inputData = workDataOf(
                KEY_SERVER_URL   to serverUrl,
                KEY_DEVICE_TOKEN to deviceToken
            )

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context).enqueue(request)
        }

        /** أوقف جدولة المزامنة (عند تسجيل الخروج) */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
