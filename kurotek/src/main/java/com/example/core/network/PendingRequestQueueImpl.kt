package com.example.core.network

import android.content.Context
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * PendingRequestQueueImpl
 * طابور بسيط ومتزامن (Thread-Safe) لحفظ الطلبات الفاشلة بسبب الـ Offline.
 * الـ Sync Engine سيقوم بتفريغه عند عودة الاتصال.
 */
class PendingRequestQueueImpl(private val context: Context) : PendingRequestQueue {

    private val prefs = context.getSharedPreferences("pending_queue", Context.MODE_PRIVATE)
    private val mutex = Mutex()
    private var counter: Int
        get() = prefs.getInt("counter", 0)
        set(value) = prefs.edit().putInt("counter", value).apply()

    override suspend fun enqueue(requestData: String, endpointName: String) = mutex.withLock {
        val key = "req_${System.currentTimeMillis()}_${counter}"
        prefs.edit().putString(key, "$endpointName||$requestData").apply()
        counter++
        Unit
    }

    override suspend fun dequeue(): String? = mutex.withLock {
        val key = prefs.all.keys.filter { it.startsWith("req_") }.minOrNull() ?: return@withLock null
        val value = prefs.getString(key, null)
        prefs.edit().remove(key).apply()
        value
    }

    override suspend fun clear() = mutex.withLock {
        val editor = prefs.edit()
        prefs.all.keys.filter { it.startsWith("req_") }.forEach { editor.remove(it) }
        editor.putInt("counter", 0).apply()
    }
}
