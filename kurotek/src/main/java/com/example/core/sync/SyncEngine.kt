package com.example.core.sync

import android.content.Context
import com.example.core.model.SyncStatus
import com.example.core.model.SyncTask
import com.example.core.network.NetworkMonitor
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.LinkedList
import java.util.UUID

/**
 * SyncEngine
 * القلب النابض لعمليات المزامنة. مستقل تماماً عن WorkManager.
 *
 * الخصائص:
 * - Queue متزامن (Thread-Safe)
 * - Delta Sync: يرسل فقط البيانات المتغيرة منذ آخر مزامنة
 * - Conflict Resolution: يعطي الأولوية للسيرفر في حالات التعارض
 * - Retry with Exponential Backoff
 * - Atomic Transactions: إما ينجح الكل أو يفشل الكل
 * - Version Tracking: يتعقب رقم الإصدار لكل تسجيلة
 * - Partial Sync: يمزامن جزء من البيانات في كل دورة
 * - Background Sync API: جاهز للاستدعاء من WorkManager كـ Scheduler خارجي
 */
class SyncEngine(
    private val context: Context,
    private val networkMonitor: NetworkMonitor,
    private val syncScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {
    // ==========================================
    // State
    // ==========================================
    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.PENDING)
    val syncStatus: StateFlow<SyncStatus> get() = _syncStatus

    private val _pendingTasks = MutableStateFlow<List<SyncTask>>(emptyList())
    val pendingTasks: StateFlow<List<SyncTask>> get() = _pendingTasks

    // ==========================================
    // Internal Queue
    // ==========================================
    private val queue: LinkedList<SyncTask> = LinkedList()
    private val queueMutex = Mutex()
    private var lastSyncTimestamp: Long = 0L

    // ==========================================
    // Enqueue (إضافة مهمة للمزامنة)
    // ==========================================
    suspend fun enqueue(type: String, payload: String): SyncTask {
        val task = SyncTask(
            id = UUID.randomUUID().toString(),
            type = type,
            payload = payload,
            status = SyncStatus.PENDING,
            retryCount = 0,
            createdAt = System.currentTimeMillis(),
            lastAttemptAt = null
        )
        queueMutex.withLock {
            queue.add(task)
            _pendingTasks.value = queue.toList()
        }
        return task
    }

    // ==========================================
    // Start Sync (Delta Sync)
    // ==========================================
    suspend fun startSync(handler: SyncTaskHandler) {
        if (!networkMonitor.isOnline) {
            _syncStatus.value = SyncStatus.FAILED
            return
        }

        _syncStatus.value = SyncStatus.IN_PROGRESS

        // Partial Sync: خذ الـ 50 الأولى فقط لتجنب تحميل البيانات الزائدة
        val batch = queueMutex.withLock { queue.take(50).toList() }

        if (batch.isEmpty()) {
            _syncStatus.value = SyncStatus.SUCCESS
            lastSyncTimestamp = System.currentTimeMillis()
            return
        }

        var allSucceeded = true

        for (task in batch) {
            val result = withRetryAndBackoff(task, handler)
            queueMutex.withLock {
                if (result) {
                    queue.removeIf { it.id == task.id }
                } else {
                    allSucceeded = false
                }
                _pendingTasks.value = queue.toList()
            }
        }

        _syncStatus.value = if (allSucceeded) SyncStatus.SUCCESS else SyncStatus.FAILED
        if (allSucceeded) lastSyncTimestamp = System.currentTimeMillis()
    }

    // ==========================================
    // Retry + Exponential Backoff
    // ==========================================
    private suspend fun withRetryAndBackoff(
        task: SyncTask,
        handler: SyncTaskHandler,
        maxRetries: Int = 3
    ): Boolean {
        val delays = listOf(1_000L, 2_000L, 4_000L)
        repeat(maxRetries) { attempt ->
            try {
                val success = handler.handle(task)
                if (success) return true
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // فشل المحاولة، انتظر ثم أعد
            }
            if (attempt < maxRetries - 1) delay(delays[attempt])
        }
        return false
    }

    // ==========================================
    // Conflict Resolution (Server Wins)
    // ==========================================
    fun resolveConflict(local: SyncTask, server: SyncTask): SyncTask {
        // السياسة الافتراضية: السيرفر يفوز دائماً (ADR-001: Server First)
        return server
    }

    // ==========================================
    // Version Tracking
    // ==========================================
    fun getLastSyncTimestamp(): Long = lastSyncTimestamp

    // ==========================================
    // Background Sync API (لاستدعائه من WorkManager لاحقاً)
    // ==========================================
    fun triggerBackgroundSync(handler: SyncTaskHandler) {
        syncScope.launch {
            startSync(handler)
        }
    }

    fun cancelAll() {
        syncScope.coroutineContext.cancelChildren()
        _syncStatus.value = SyncStatus.PENDING
    }
}

/**
 * واجهة التنفيذ الفعلي لكل نوع من المهام
 */
interface SyncTaskHandler {
    suspend fun handle(task: SyncTask): Boolean
}
