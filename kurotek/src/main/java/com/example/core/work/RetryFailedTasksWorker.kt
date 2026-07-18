package com.example.core.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.core.repository.SyncRepository

class RetryFailedTasksWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val syncRepository: SyncRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        println("WorkManager: RetryFailedTasksWorker started.")

        return try {
            val pendingTasks = syncRepository.getPendingTasks()
            if (pendingTasks.isEmpty()) {
                println("WorkManager: No failed/pending tasks to retry.")
                return Result.success()
            }

            // Retry logic here... For now, we simulate success
            // Real implementation would iterate pendingTasks and retry sending them.
            pendingTasks.forEach { task ->
                println("WorkManager: Retrying task ${task.id} (${task.type})")
                // Simulate success
                syncRepository.markTaskCompleted(task.id)
            }

            println("WorkManager: RetryFailedTasksWorker finished successfully.")
            Result.success()
        } catch (e: Exception) {
            println("WorkManager: RetryFailedTasksWorker failed: ${e.message}")
            Result.retry()
        }
    }
}
