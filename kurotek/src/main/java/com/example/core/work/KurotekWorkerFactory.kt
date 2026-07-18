package com.example.core.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.core.CoreContainer

/**
 * KurotekWorkerFactory
 * يقوم بإنشاء Workers المخصصة وتمرير CoreContainer أو UseCases/Repositories المناسبة لها.
 * يحل مشكلة الـ Dependency Injection داخل WorkManager بدون استخدام Hilt.
 */
class KurotekWorkerFactory(
    private val coreContainer: CoreContainer
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            BackgroundSyncWorker::class.java.name ->
                BackgroundSyncWorker(appContext, workerParameters, coreContainer.syncNowUseCase)
            
            LicenseCheckWorker::class.java.name ->
                LicenseCheckWorker(appContext, workerParameters, coreContainer.licenseEngine)
                
            RetryFailedTasksWorker::class.java.name ->
                RetryFailedTasksWorker(appContext, workerParameters, coreContainer.syncRepository)

            else -> null
        }
    }
}
