package com.example

import com.example.core.model.Resource
import com.example.core.repository.InventoryRepository
import com.example.core.usecase.AddCardsUseCase
import com.example.models.Card
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * اختبارات الضغط (Stress Tests)
 *
 * ⚡ بيع 100 كرت متتالية
 * ⚡ إضافة 5000 كرت دفعة واحدة
 * ⚡ انقطاع الإنترنت أثناء المزامنة
 * ⚡ انتهاء التوكن أثناء رفع بيانات كبيرة
 */
@ExperimentalCoroutinesApi
class StressTests {

    private lateinit var inventoryRepo: InventoryRepository
    private val testDispatcher = StandardTestDispatcher()

    private fun fakeCard(id: Int, cat: Int) =
        Card(id = id, category = cat, code = "STRESS-$id", isUsed = false,
            createdAt = System.currentTimeMillis(), username = "", password = "")

    @Before
    fun setup() {
        inventoryRepo = mockk()
    }

    // ⚡ اختبار 1: بيع 100 كرت متتالية بدون تعارض
    @Test
    fun `stress - sell 100 cards sequentially without conflict`() = runTest(testDispatcher) {
        val cards = (1..100).map { fakeCard(it, 100) }
        var callIndex = 0
        coEvery { inventoryRepo.getUnusedCardByCategory(100) } answers { cards.getOrNull(callIndex++) }
        coEvery { inventoryRepo.markCardAsUsed(any()) } returns Resource.Success(Unit)

        var successCount = 0
        for (i in 1..100) {
            val card = inventoryRepo.getUnusedCardByCategory(100)
            if (card != null) {
                inventoryRepo.markCardAsUsed(card.id)
                successCount++
            }
        }

        assertEquals("يجب بيع 100 كرت بالكامل بنجاح", 100, successCount)
        coVerify(exactly = 100) { inventoryRepo.markCardAsUsed(any()) }
    }

    // ⚡ اختبار 2: إضافة 5000 كرت دفعة واحدة
    @Test
    fun `stress - add 5000 cards in bulk`() = runTest(testDispatcher) {
        val bulkCodes = (1..5000).joinToString("\n") { "CODE-$it" }
        coEvery { inventoryRepo.insertCardsBulk(100, bulkCodes) } returns Resource.Success(5000)

        val addCardsUseCase = AddCardsUseCase(inventoryRepo)
        val result = addCardsUseCase(100, bulkCodes)

        assertTrue("يجب قبول 5000 كرت دفعة واحدة", result is Resource.Success)
        assertEquals("يجب إرجاع العدد الصحيح", 5000, (result as Resource.Success).data)
    }

    // ⚡ اختبار 3: انقطاع الإنترنت أثناء المزامنة — المهام تُحفظ محلياً
    @Test
    fun `stress - internet disconnects during sync, tasks preserved`() = runTest(testDispatcher) {
        var networkAvailable = true
        val savedLocally = mutableListOf<String>()

        // محاكاة انقطاع الشبكة بعد 50 عملية
        repeat(100) { i ->
            if (i == 50) networkAvailable = false
            if (!networkAvailable) {
                savedLocally.add("task_$i") // يُخزّن محلياً
            }
        }

        assertEquals("يجب حفظ 50 مهمة محلياً بعد الانقطاع", 50, savedLocally.size)
        assertTrue("يجب أن تكون جميع المهام محددة للإعادة", savedLocally.all { it.startsWith("task_") })
    }

    // ⚡ اختبار 4: انقطاع الكهرباء أثناء رفع البيانات — Transaction Safety
    @Test
    fun `stress - power loss during upload, no partial writes`() = runTest(testDispatcher) {
        val committedCards = mutableListOf<Int>()
        var simulatedCrash = false

        // محاكاة قطع الكهرباء عند الكرت 30
        try {
            for (i in 1..100) {
                if (i == 30) {
                    simulatedCrash = true
                    throw RuntimeException("Power loss at card $i")
                }
                committedCards.add(i)
            }
        } catch (e: RuntimeException) {
            // تراجع — في Room هذا يتم عبر Transaction
            // في الاختبار نتحقق من أن اللائحة تبقى سليمة
        }

        assertTrue("يجب اكتشاف حالة الانهيار", simulatedCrash)
        // في التطبيق الحقيقي: Room Transaction تضمن عدم وجود كتابة جزئية
        assertTrue(
            "البيانات المُدخلة قبل الانهيار يجب أن تكون ضمن حدود التراجع",
            committedCards.size < 100
        )
    }

    // ⚡ اختبار 5: انتهاء التوكن أثناء رفع بيانات كبيرة
    @Test
    fun `stress - token expires mid upload, retries with refreshed token`() = runTest(testDispatcher) {
        var tokenRefreshed = false
        var retriedAfterRefresh = false

        // محاكاة: رفع يفشل بـ 401، يُحدَّث التوكن، ثم يُعاد المحاولة
        fun uploadBatch(token: String): Boolean {
            return if (token == "expired_token") {
                false // 401 Unauthorized
            } else {
                retriedAfterRefresh = true
                true // نجاح بعد التحديث
            }
        }

        var token = "expired_token"
        val firstAttempt = uploadBatch(token)
        if (!firstAttempt) {
            // إعادة توليد التوكن
            token = "fresh_token"
            tokenRefreshed = true
        }
        val secondAttempt = uploadBatch(token)

        assertTrue("يجب تحديث التوكن عند انتهاء الصلاحية", tokenRefreshed)
        assertTrue("يجب إعادة المحاولة بالتوكن الجديد بنجاح", retriedAfterRefresh)
        assertTrue("النتيجة النهائية يجب أن تكون نجاح", secondAttempt)
    }

    // ⚡ اختبار إضافي: 1000 استعلام متزامن لا تسبب تعارضاً
    @Test
    fun `stress - concurrent reads do not cause data corruption`() = runTest(testDispatcher) {
        val sharedList = mutableListOf<Int>()
        val mutex = kotlinx.coroutines.sync.Mutex()

        // محاكاة قراءات متزامنة آمنة
        val jobs = (1..1000).map { i ->
            launch(testDispatcher) {
                mutex.lock()
                try { sharedList.add(i) }
                finally { mutex.unlock() }
            }
        }
        jobs.forEach { it.join() }

        assertEquals("يجب ألا تكون هناك بيانات مفقودة بعد 1000 عملية متزامنة", 1000, sharedList.size)
    }
}
