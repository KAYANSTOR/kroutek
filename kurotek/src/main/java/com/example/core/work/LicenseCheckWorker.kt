package com.example.core.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.core.license.LicenseEngine
import com.example.core.model.LicenseState

class LicenseCheckWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val licenseEngine: LicenseEngine
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        println("WorkManager: LicenseCheckWorker started.")

        return try {
            val status = licenseEngine.getLicenseStatus()
            if (status.state == LicenseState.BLOCKED) {
                // If the device is blocked remotely, ensure it's frozen locally
                licenseEngine.freezeDevice()
            }
            println("WorkManager: LicenseCheckWorker finished successfully. State: ${status.state}")
            Result.success()
        } catch (e: Exception) {
            println("WorkManager: LicenseCheckWorker failed: ${e.message}")
            Result.retry()
        }
    }
}
