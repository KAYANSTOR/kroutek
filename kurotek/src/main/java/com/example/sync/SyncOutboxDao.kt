package com.example.sync

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncOutboxDao {

    /** أضف إدخالاً جديداً للطابور */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(entry: SyncOutboxEntry)

    /** أضف عدة إدخالات دفعة واحدة */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueueAll(entries: List<SyncOutboxEntry>)

    /**
     * اجلب الإدخالات الجاهزة للرفع مرتّبة حسب createdAt.
     * [maxRetries] الحد الأقصى لعدد المحاولات قبل التخلي.
     */
    @Query("""
        SELECT * FROM sync_outbox
        WHERE retryCount < :maxRetries
        ORDER BY createdAt ASC
        LIMIT :limit
    """)
    suspend fun getPending(limit: Int = 50, maxRetries: Int = 5): List<SyncOutboxEntry>

    /** نفس الاستعلام لكن كـ Flow للمراقبة المستمرة */
    @Query("""
        SELECT COUNT(*) FROM sync_outbox
        WHERE retryCount < :maxRetries
    """)
    fun observePendingCount(maxRetries: Int = 5): Flow<Int>

    /** احذف إدخالاً بعد نجاح رفعه */
    @Query("DELETE FROM sync_outbox WHERE id = :id")
    suspend fun markSynced(id: String)

    /** احذف عدة إدخالات دفعة واحدة بعد نجاح رفعها */
    @Query("DELETE FROM sync_outbox WHERE id IN (:ids)")
    suspend fun markSyncedBatch(ids: List<String>)

    /** سجّل فشل محاولة رفع */
    @Query("""
        UPDATE sync_outbox
        SET retryCount = retryCount + 1,
            lastAttemptAt = :now,
            lastError = :error
        WHERE id = :id
    """)
    suspend fun markFailed(id: String, error: String, now: Long = System.currentTimeMillis())

    /** عدد الإدخالات الكلي في الطابور (للتشخيص) */
    @Query("SELECT COUNT(*) FROM sync_outbox")
    suspend fun getTotalCount(): Int

    /** احذف إدخالات تخطّت الحد الأقصى للمحاولات (للتنظيف الدوري) */
    @Query("DELETE FROM sync_outbox WHERE retryCount >= :maxRetries")
    suspend fun clearDeadLetters(maxRetries: Int = 5)

    /** امسح الطابور كاملاً (عند تسجيل الخروج أو إعادة الضبط) */
    @Query("DELETE FROM sync_outbox")
    suspend fun clearAll()
}
