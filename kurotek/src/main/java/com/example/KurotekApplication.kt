package com.example

import android.app.Application
import androidx.work.Configuration
import com.example.core.CoreContainer
import com.example.core.work.KurotekWorkerFactory
import com.example.core.work.WorkScheduler
import dagger.hilt.android.HiltAndroidApp

/**
 * KurotekApplication
 * يتم فيه تهيئة CoreContainer كـ Singleton لجميع أجزاء التطبيق.
 * ويتم تهيئة WorkManager مع Custom WorkerFactory لحقن التبعيات.
 * نقطة انطلاق Hilt لكامل التطبيق أيضاً.
 */
@HiltAndroidApp
class KurotekApplication : Application(), Configuration.Provider {

    // Singleton Container 
    val coreContainer: CoreContainer by lazy {
        CoreContainer.getInstance(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(KurotekWorkerFactory(coreContainer))
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        try {
            // جدولة كل الـ Workers الدورية عند انطلاق التطبيق لضمان استمرارية العمل في الخلفية
            WorkScheduler.scheduleAllWorkers(this)
        } catch (e: Exception) {
            android.util.Log.e("KurotekApplication", "فشل في جدولة المهام الخلفية: ${e.message}")
        }
    }
}
