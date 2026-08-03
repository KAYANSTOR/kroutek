package com.example

import com.example.core.model.Resource
import com.example.core.repository.InventoryRepository
import com.example.core.repository.SalesRepository
import com.example.core.usecase.SellCardUseCase
import com.example.core.usecase.ValidateSmsAmountUseCase
import com.example.models.Card
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * ✅ اختبار 5: بيع كرت أثناء الاتصال
 * ✅ اختبار 6: بيع كرت بدون اتصال (Offline-first)
 * ✅ اختبار 8: عدم بيع نفس الكرت مرتين (Race Condition)
 */
@ExperimentalCoroutinesApi
class SellCardUseCaseTest {

    private lateinit var inventoryRepo: InventoryRepository
    private lateinit var salesRepo: SalesRepository
    private lateinit var useCase: SellCardUseCase
    private val testDispatcher = StandardTestDispatcher()

    // كرت اختباري افتراضي
    private fun fakeCard(id: Int, cat: Int, code: String) =
        Card(id = id, category = cat, code = code, isUsed = false,
            createdAt = System.currentTimeMillis(), username = "", password = "")

    @Before
    fun setup() {
        inventoryRepo = mockk()
        salesRepo = mockk()
        useCase = SellCardUseCase(inventoryRepo, salesRepo, ValidateSmsAmountUseCase())
    }

    // ✅ اختبار 5: بيع ناجح مع الاتصال
    @Test
    fun `sell card online - success when card available`() = runTest(testDispatcher) {
        val card = fakeCard(1, 100, "CARD-001")
        coEvery { inventoryRepo.getUnusedCardByCategory(100) } returns card
        coEvery { inventoryRepo.markCardAsUsed(1) } returns Resource.Success(Unit)
        coEvery { salesRepo.insertTransaction(any(), any(), any(), any()) } returns Resource.Success(Unit)
        coEvery { salesRepo.insertDeposit(any(), any(), any(), any(), any()) } returns Resource.Success(1L)

        val result = useCase("777123456", 100, "جيب") { _, _ -> true }

        assertTrue("البيع يجب أن ينجح عند توفر الكرت والاتصال", result is Resource.Success)
        coVerify(exactly = 1) { inventoryRepo.markCardAsUsed(1) }
        coVerify(exactly = 1) { salesRepo.insertTransaction("777123456", 100, any(), "جيب") }
    }

    // ✅ اختبار 6: بيع بدون إنترنت — يُحفظ محلياً (SMS تفشل لكن الصفقة تُسجَّل)
    @Test
    fun `sell card offline - records transaction locally when SMS fails`() = runTest(testDispatcher) {
        val card = fakeCard(2, 200, "CARD-002")
        coEvery { inventoryRepo.getUnusedCardByCategory(200) } returns card
        coEvery { inventoryRepo.markCardAsUsed(2) } returns Resource.Success(Unit)
        coEvery { salesRepo.insertTransaction(any(), any(), any(), any()) } returns Resource.Success(Unit)
        coEvery { salesRepo.insertDeposit(any(), any(), any(), any(), any()) } returns Resource.Success(2L)

        // onSmsSend تُعيد false (انقطاع إنترنت/SMS)
        val result = useCase("778000000", 200, "كريمي") { _, _ -> false }

        assertTrue("يجب حفظ الصفقة محلياً حتى لو فشل إرسال SMS", result is Resource.Success)
        // التحقق من التسجيل بـ ✖ للإشارة إلى فشل الإرسال
        coVerify { salesRepo.insertTransaction("778000000", 200, match { it.contains("✖") }, "كريمي") }
    }

    // ✅ اختبار 8: Race Condition — لا يُباع نفس الكرت مرتين
    @Test
    fun `sell card race condition - each sale gets unique card`() = runTest(testDispatcher) {
        val card1 = fakeCard(1, 100, "CARD-001")
        val card2 = fakeCard(2, 100, "CARD-002")
        coEvery { inventoryRepo.getUnusedCardByCategory(100) } returnsMany listOf(card1, card2)
        coEvery { inventoryRepo.markCardAsUsed(any()) } returns Resource.Success(Unit)
        coEvery { salesRepo.insertTransaction(any(), any(), any(), any()) } returns Resource.Success(Unit)
        coEvery { salesRepo.insertDeposit(any(), any(), any(), any(), any()) } returns Resource.Success(1L)

        val result1 = useCase("777111111", 100, "جيب") { _, _ -> true }
        val result2 = useCase("777222222", 100, "جيب") { _, _ -> true }

        assertTrue("البيع الأول يجب أن ينجح", result1 is Resource.Success)
        assertTrue("البيع الثاني يجب أن ينجح بكرت مختلف", result2 is Resource.Success)
        coVerify(exactly = 1) { inventoryRepo.markCardAsUsed(1) }
        coVerify(exactly = 1) { inventoryRepo.markCardAsUsed(2) }
    }

    // بيع بمبلغ غير مدعوم
    @Test
    fun `sell card - rejects unsupported amount`() = runTest(testDispatcher) {
        val result = useCase("777000000", 150, "جيب") { _, _ -> true }
        assertTrue("يجب رفض المبلغ 150 غير الموجود في القائمة", result is Resource.Error)
        coVerify(exactly = 0) { inventoryRepo.getUnusedCardByCategory(any()) }
    }

    // بيع بدون مخزون
    @Test
    fun `sell card - fails when no stock`() = runTest(testDispatcher) {
        coEvery { inventoryRepo.getUnusedCardByCategory(100) } returns null

        val result = useCase("777000001", 100, "جيب") { _, _ -> true }
        assertTrue("يجب الفشل عند نفاد المخزون", result is Resource.Error)
        coVerify(exactly = 0) { salesRepo.insertTransaction(any(), any(), any(), any()) }
    }
}
