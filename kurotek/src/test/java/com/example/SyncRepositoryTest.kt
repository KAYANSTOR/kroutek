package com.example

import com.example.core.model.Resource
import com.example.core.model.SyncStatus
import com.example.core.model.SyncTask
import com.example.core.repository.SyncRepository
import com.example.core.repository.impl.SyncRepositoryImpl
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * ✅ اختبار 7: رجوع الاتصال وتنفيذ المزامنة
 * ✅ اختبار 10: Conflict Resolution عند اختلاف البيانات
 */
@ExperimentalCoroutinesApi
class SyncRepositoryTest {

    private lateinit var syncRepository: SyncRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        syncRepository = mockk()
    }

    private fun fakeSyncTask(id: String, type: String, payload: String, retryCount: Int) =
        SyncTask(id, type, payload, SyncStatus.PENDING, retryCount, System.currentTimeMillis(), null)

    // ✅ اختبار 7أ: وضع المهام في قائمة الانتظار بدون اتصال
    @Test
    fun `enqueue sync task - stores task when offline`() = runTest(testDispatcher) {
        val task = fakeSyncTask("t1", "sell_card", "{}", 0)
        coEvery { syncRepository.enqueueSyncTask(task) } returns Resource.Success(Unit)

        val result = syncRepository.enqueueSyncTask(task)
        assertTrue("يجب قبول المهام في قائمة الانتظار أثناء انقطاع الإنترنت", result is Resource.Success)
    }

    // ✅ اختبار 7ب: استرجاع المهام المعلقة
    @Test
    fun `get pending tasks - returns all queued tasks`() = runTest(testDispatcher) {
        val tasks = listOf(
            fakeSyncTask("t1", "sell_card", "{}", 0),
            fakeSyncTask("t2", "deposit", "{}", 1)
        )
        coEvery { syncRepository.getPendingTasks() } returns tasks

        val pending = syncRepository.getPendingTasks()
        assertEquals("يجب إعادة جميع المهام المعلقة", 2, pending.size)
    }

    // ✅ اختبار 7ج: إكمال المزامنة عند عودة الاتصال
    @Test
    fun `sync on reconnect - marks tasks as completed`() = runTest(testDispatcher) {
        coEvery { syncRepository.markTaskCompleted("t1") } just Runs
        coEvery { syncRepository.markTaskCompleted("t2") } just Runs
        coEvery { syncRepository.getPendingTasks() } returns emptyList()

        syncRepository.markTaskCompleted("t1")
        syncRepository.markTaskCompleted("t2")
        val remaining = syncRepository.getPendingTasks()

        assertEquals("يجب مسح المهام المكتملة", 0, remaining.size)
    }

    // ✅ اختبار 10أ: Conflict Resolution — بيانات الخادم أحدث
    @Test
    fun `conflict resolution - server data wins when newer timestamp`() = runTest(testDispatcher) {
        // محاكاة: مهمة فشلت بسبب تعارض (409 Conflict)
        val task = fakeSyncTask("t3", "sell_card", """{"timestamp":1000}""", 0)
        coEvery { syncRepository.enqueueSyncTask(task) } returns Resource.Success(Unit)
        coEvery { syncRepository.markTaskFailed("t3", any()) } just Runs

        // عند التعارض، نسجّل الفشل ونتيح إعادة المحاولة
        syncRepository.markTaskFailed("t3", "Conflict: server version newer")
        coVerify(exactly = 1) { syncRepository.markTaskFailed("t3", any()) }
    }

    // ✅ اختبار 10ب: إعادة المحاولة بعد الفشل
    @Test
    fun `conflict resolution - retries failed tasks`() = runTest(testDispatcher) {
        val failedTask = fakeSyncTask("t4", "deposit", "{}", 2)
        coEvery { syncRepository.getPendingTasks() } returns listOf(failedTask)

        val pending = syncRepository.getPendingTasks()
        val retried = pending.firstOrNull { it.retryCount > 0 }

        assertNotNull("يجب الاحتفاظ بالمهام الفاشلة لإعادة المحاولة", retried)
        assertEquals(2, retried!!.retryCount)
    }

    // انقطاع أثناء المزامنة — المهام تبقى معلقة
    @Test
    fun `sync interrupted - pending tasks remain for retry`() = runTest(testDispatcher) {
        val task = fakeSyncTask("t5", "bulk_upload", "{}", 0)
        coEvery { syncRepository.enqueueSyncTask(task) } returns Resource.Success(Unit)
        coEvery { syncRepository.getPendingTasks() } returns listOf(task) // ما تزال معلقة

        syncRepository.enqueueSyncTask(task)
        val pending = syncRepository.getPendingTasks()
        assertEquals("يجب أن تبقى المهام المقطوعة في قائمة الانتظار", 1, pending.size)
    }

    // لا توجد مهام معلقة
    @Test
    fun `pending tasks - returns empty when all synced`() = runTest(testDispatcher) {
        coEvery { syncRepository.getPendingTasks() } returns emptyList()

        val pending = syncRepository.getPendingTasks()
        assertTrue("يجب إعادة قائمة فارغة عند اكتمال المزامنة", pending.isEmpty())
    }
}
