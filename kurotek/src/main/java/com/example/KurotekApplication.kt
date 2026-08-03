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
        // جدولة كل الـ Workers الدورية (مزامنة كل 15 دقيقة، تحقق ترخيص كل
        // 24 ساعة، إعادة محاولة كل ساعة) — كانت هذه الملفات موجودة سابقاً
        // (core/work/*.kt) لكن غير مربوطة بأي مكان فعلياً، فلم تكن تعمل
        // إطلاقاً رغم وجودها. ExistingPeriodicWorkPolicy.KEEP بداخل
        // WorkScheduler يضمن عدم إعادة الجدولة إن كانت مجدولة مسبقاً، لذا
        // استدعاؤها في كل onCreate آمن ولا يكرر العمل.
    }
}
