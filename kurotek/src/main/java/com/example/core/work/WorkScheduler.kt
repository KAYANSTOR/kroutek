package com.example.core.work

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * WorkScheduler
 * واجهة برمجية مساعدة لجدولة جميع العمال (Workers) بسهولة من الـ UI أو Application.
 */
object WorkScheduler {

    private const val SYNC_WORK_NAME = "BackgroundSyncWork"
    private const val LICENSE_WORK_NAME = "LicenseCheckWork"
    private const val RETRY_WORK_NAME = "RetryFailedTasksWork"

    fun scheduleAllWorkers(context: Context) {
        val workManager = WorkManager.getInstance(context)

        // 1. المزامنة الدورية كل 15 دقيقة (أقل وقت مسموح به في WorkManager)
        val syncConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<BackgroundSyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(syncConstraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // لا تعيد التشغيل إذا كانت مجدولة مسبقاً
            syncRequest
        )

        // 2. تحديث الترخيص كل 24 ساعة
        val licenseConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val licenseRequest = PeriodicWorkRequestBuilder<LicenseCheckWorker>(24, TimeUnit.HOURS)
            .setConstraints(licenseConstraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            LICENSE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            licenseRequest
        )

        // 3. إعادة محاولة إرسال الطلبات الفاشلة كل ساعة
        val retryRequest = PeriodicWorkRequestBuilder<RetryFailedTasksWorker>(1, TimeUnit.HOURS)
            .setConstraints(syncConstraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            RETRY_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            retryRequest
        )
    }

    /**
     * تشغيل المزامنة الفورية (One-Time) عند عودة الاتصال مثلاً
     */
    fun enqueueImmediateSync(context: Context) {
        val syncRequest = OneTimeWorkRequestBuilder<BackgroundSyncWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
        
        WorkManager.getInstance(context).enqueueUniqueWork(
            "ImmediateSync",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }
}
