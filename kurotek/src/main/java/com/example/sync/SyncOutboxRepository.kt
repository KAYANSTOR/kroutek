package com.example.sync

import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository للطابور — الطبقة الوحيدة التي يتعامل معها باقي الكود
 * لإضافة تغييرات للمزامنة. يُحقن عبر Hilt كـ Singleton.
 *
 * الاستخدام المعتاد:
 *   outboxRepository.enqueueCreate(SyncEntityType.CARD, card.id, card.toJson())
 *   outboxRepository.enqueueDelete(SyncEntityType.TRANSACTION, txId)
 */
@Singleton
class SyncOutboxRepository @Inject constructor(
    private val dao: SyncOutboxDao
) {

    // ---- واجهات الإضافة (Enqueue) ----

    /** سجّل عملية إنشاء كيان جديد */
    suspend fun enqueueCreate(entityType: String, entityId: String, payload: JSONObject) {
        dao.enqueue(
            SyncOutboxEntry(
                entityType = entityType,
                entityId   = entityId,
                operation  = SyncOperation.CREATE,
                payload    = payload.toString()
            )
        )
    }

    /** سجّل عملية تحديث كيان موجود */
    suspend fun enqueueUpdate(entityType: String, entityId: String, payload: JSONObject) {
        dao.enqueue(
            SyncOutboxEntry(
                entityType = entityType,
                entityId   = entityId,
                operation  = SyncOperation.UPDATE,
                payload    = payload.toString()
            )
        )
    }

    /** سجّل عملية حذف كيان */
    suspend fun enqueueDelete(entityType: String, entityId: String) {
        dao.enqueue(
            SyncOutboxEntry(
                entityType = entityType,
                entityId   = entityId,
                operation  = SyncOperation.DELETE,
                payload    = null
            )
        )
    }

    // ---- واجهات القراءة والتنفيذ ----

    /**
     * جلب الإدخالات الجاهزة للرفع بحد أقصى [limit] إدخال.
     * يستخدمها [SyncWorker] في كل دورة مزامنة.
     */
    suspend fun getPending(limit: Int = 50): List<SyncOutboxEntry> =
        dao.getPending(limit = limit, maxRetries = MAX_RETRIES)

    /** مراقبة عدد التغييرات المعلّقة (للعرض في الواجهة مستقبلاً) */
    fun observePendingCount(): Flow<Int> = dao.observePendingCount(maxRetries = MAX_RETRIES)

    /** احذف إدخالاً بعد نجاح رفعه */
    suspend fun markSynced(id: String) = dao.markSynced(id)

    /** احذف عدة إدخالات دفعة واحدة */
    suspend fun markSyncedBatch(ids: List<String>) = dao.markSyncedBatch(ids)

    /** سجّل فشل محاولة ورفع عداد المحاولات */
    suspend fun markFailed(id: String, error: String) = dao.markFailed(id, error)

    /** إجمالي الإدخالات في الطابور */
    suspend fun getTotalCount(): Int = dao.getTotalCount()

    /** نظّف الإدخالات الميتة (تخطّت الحد الأقصى للمحاولات) */
    suspend fun clearDeadLetters() = dao.clearDeadLetters(maxRetries = MAX_RETRIES)

    /** امسح كل الطابور (عند تسجيل الخروج) */
    suspend fun clearAll() = dao.clearAll()

    companion object {
        /** الحد الأقصى لمحاولات رفع إدخال واحد قبل نقله لـ Dead Letter */
        const val MAX_RETRIES = 5
    }
}
