package com.example

import android.app.Application
import android.util.Log
import androidx.annotation.Keep
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.core.CoreContainer
import com.example.core.work.KurotekWorkerFactory
import com.example.core.work.WorkScheduler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
@Keep
class KurotekApplication : Application(), Configuration.Provider {

    init {
        Log.e("STARTUP", "STEP 0 START: KurotekApplication init block")
    }

    init {
        Log.e("STARTUP", "STEP 0 END: KurotekApplication init block")
    }

    // Lazy-initialized core container
    val coreContainer: CoreContainer by lazy {
        Log.e("STARTUP", "STEP 2 START: KurotekApplication.coreContainer lazy init")
        try {
            CoreContainer.getInstance(this).also {
                Log.e("STARTUP", "STEP 2 END: KurotekApplication.coreContainer lazy init SUCCESS")
            }
        } catch (e: Throwable) {
            Log.e("STARTUP", "STEP 2 FAILED: KurotekApplication.coreContainer lazy init", e)
            throw e
        }
    }

    override fun onCreate() {
        Log.e("STARTUP", "STEP 1 START: KurotekApplication.onCreate()")

        try {
            super.onCreate()
            Log.e("STARTUP", "STEP 1 MID: KurotekApplication.onCreate() super.onCreate() DONE")
        } catch (e: Throwable) {
            Log.e("STARTUP", "STEP 1 FAILED: KurotekApplication.onCreate() super.onCreate()", e)
            throw e
        }

        try {
            Log.e("STARTUP", "STEP 3b START: WorkManager manual initialize")
            WorkManager.initialize(
                this,
                Configuration.Builder()
                    .setWorkerFactory(KurotekWorkerFactory(coreContainer))
                    .setMinimumLoggingLevel(Log.INFO)
                    .build()
            )
            Log.e("STARTUP", "STEP 3b END: WorkManager manual initialize SUCCESS")
        } catch (e: Throwable) {
            Log.e("STARTUP", "STEP 3b FAILED: WorkManager manual initialize", e)
        }

        try {
            Log.e("STARTUP", "STEP 3 START: WorkScheduler.scheduleAllWorkers()")
            WorkScheduler.scheduleAllWorkers(this)
            Log.e("STARTUP", "STEP 3 END: WorkScheduler.scheduleAllWorkers() SUCCESS")
        } catch (e: Throwable) {
            Log.e("STARTUP", "STEP 3 FAILED: WorkScheduler.scheduleAllWorkers()", e)
        }

        Log.e("STARTUP", "STEP 1 END: KurotekApplication.onCreate()")
    }

    override val workManagerConfiguration: Configuration
        get() = try {
            Log.e("STARTUP", "STEP 4 START: KurotekApplication.workManagerConfiguration getter")
            Configuration.Builder()
                .setWorkerFactory(KurotekWorkerFactory(coreContainer))
                .build()
                .also { Log.e("STARTUP", "STEP 4 END: KurotekApplication.workManagerConfiguration getter SUCCESS") }
        } catch (e: Throwable) {
            Log.e("STARTUP", "STEP 4 FAILED: KurotekApplication.workManagerConfiguration getter", e)
            throw e
        }
}

