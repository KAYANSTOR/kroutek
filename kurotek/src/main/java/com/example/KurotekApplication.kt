package com.example

import android.app.Application
import androidx.work.Configuration
import com.example.core.CoreContainer
import com.example.core.work.KurotekWorkerFactory
import com.example.core.work.WorkScheduler
import com.example.core.work.CompositeWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import androidx.hilt.work.HiltWorkerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class KurotekApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    // Singleton Container
    val coreContainer: CoreContainer by lazy {
        CoreContainer.getInstance(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(CompositeWorkerFactory(listOf(hiltWorkerFactory, KurotekWorkerFactory(coreContainer))))
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        try {
            // Schedule workers in background to avoid blocking startup
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    WorkScheduler.scheduleAllWorkers(this@KurotekApplication)
                } catch (e: Exception) {
                    android.util.Log.e("KurotekApplication", "Failed scheduling workers: ${e.message}")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("KurotekApplication", "فشل في جدولة المهام الخلفية: ${e.message}")
        }
    }
}
