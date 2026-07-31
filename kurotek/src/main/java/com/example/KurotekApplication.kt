package com.example

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import com.example.core.CoreContainer
import com.example.core.work.KurotekWorkerFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KurotekApplication : Application(), Configuration.Provider {

    val coreContainer: CoreContainer by lazy {
        Log.e("STARTUP", "STEP 1a: KurotekApplication.coreContainer lazy init START")
        try {
            val instance = CoreContainer.getInstance(this)
            Log.e("STARTUP", "STEP 1a: KurotekApplication.coreContainer lazy init SUCCESS")
            instance
        } catch (e: Throwable) {
            Log.e("STARTUP", "STEP 1a FAILED: KurotekApplication.coreContainer init", e)
            throw e
        }
    }

    init {
        Log.e("STARTUP", "STEP 1: KurotekApplication init block START")
    }

    override fun onCreate() {
        Log.e("STARTUP", "STEP 1: KurotekApplication.onCreate() START")
        try {
            super.onCreate()
            Log.e("STARTUP", "STEP 1: KurotekApplication.onCreate() super.onCreate() DONE")
        } catch (e: Throwable) {
            Log.e("STARTUP", "STEP 1 FAILED: KurotekApplication.onCreate() super.onCreate()", e)
            throw e
        }
        Log.e("STARTUP", "STEP 1: KurotekApplication.onCreate() END")
    }

    override val workManagerConfiguration: Configuration
        get() = try {
            Log.e("STARTUP", "STEP 1b: KurotekApplication.workManagerConfiguration START")
            val config = Configuration.Builder()
                .setWorkerFactory(KurotekWorkerFactory(coreContainer))
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .build()
            Log.e("STARTUP", "STEP 1b: KurotekApplication.workManagerConfiguration SUCCESS")
            config
        } catch (e: Throwable) {
            Log.e("STARTUP", "STEP 1b FAILED: KurotekApplication.workManagerConfiguration", e)
            throw e
        }
}
