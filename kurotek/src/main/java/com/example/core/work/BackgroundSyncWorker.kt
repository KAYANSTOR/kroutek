package com.example.core.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.core.model.Resource
import com.example.core.usecase.SyncNowUseCase

class BackgroundSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val syncNowUseCase: SyncNowUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Log the start of the worker
        println("WorkManager: BackgroundSyncWorker started.")

        // Create a dummy handler for now until remote sync is fully implemented
        val handler = object : com.example.core.sync.SyncTaskHandler {
            override suspend fun handle(task: com.example.core.model.SyncTask): Boolean {
                return true // Simulate success
            }
        }

        try {
            syncNowUseCase(handler)
            println("WorkManager: BackgroundSyncWorker finished successfully.")
            return Result.success()
        } catch (e: Exception) {
            println("WorkManager: BackgroundSyncWorker failed: ${e.message}")
            return Result.retry()
        }
    }
}
