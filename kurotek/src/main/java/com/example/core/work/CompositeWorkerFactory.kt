package com.example.core.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

/**
 * CompositeWorkerFactory
 * Delegates worker creation to multiple factories in order.
 * Tries each factory and returns the first non-null worker.
 */
class CompositeWorkerFactory(
    private val factories: List<WorkerFactory>
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        for (factory in factories) {
            try {
                val worker = factory.createWorker(appContext, workerClassName, workerParameters)
                if (worker != null) return worker
            } catch (e: Throwable) {
                // Log and continue trying other factories
                android.util.Log.w("CompositeWorkerFactory", "Factory ${factory.javaClass.name} failed to create $workerClassName: ${e.message}")
            }
        }
        return null
    }
}
