package com.example.core.repository.impl

import com.example.core.model.Resource
import com.example.core.model.SyncTask
import com.example.core.repository.SyncRepository
import com.example.core.sync.SyncEngine
import com.example.core.sync.SyncTaskHandler
import com.example.core.model.SyncStatus

/**
 * SyncRepositoryImpl
 * بوابة (Gateway) نظيفة بين الـ UseCases والـ SyncEngine القلب النابض.
 */
class SyncRepositoryImpl(
    private val syncEngine: SyncEngine
) : SyncRepository {

    override suspend fun enqueueSyncTask(task: SyncTask): Resource<Unit> {
        return try {
            syncEngine.enqueue(type = task.type, payload = task.payload)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getPendingTasks(): List<SyncTask> =
        syncEngine.pendingTasks.value

    override suspend fun markTaskCompleted(taskId: String) {
        // الـ SyncEngine يُزيل المهام تلقائياً عند النجاح في startSync
        // هذه الدالة متاحة لإيقاف التوافق اليدوي إذا احتجنا
    }

    override suspend fun markTaskFailed(taskId: String, error: String) {
        // تسجيل الفشل (سيُتكامل مع Logger لاحقاً)
    }

    /**
     * تشغيل المزامنة الكاملة (يُستدعى من WorkManager أو UseCase)
     */
    suspend fun runSync(handler: SyncTaskHandler) {
        syncEngine.startSync(handler)
    }

    fun getSyncStatus() = syncEngine.syncStatus
    fun getLastSyncTimestamp() = syncEngine.getLastSyncTimestamp()
}
