package com.example

import com.example.core.model.Resource
import com.example.core.repository.ApprovalsRepository
import com.example.core.repository.InventoryRepository
import com.example.core.repository.SalesRepository
import com.example.core.usecase.ApprovePendingUseCase
import com.example.core.usecase.RejectPendingUseCase
import com.example.core.usecase.SellCardUseCase
import com.example.core.usecase.ValidateSmsAmountUseCase
import com.example.models.Card
import com.example.models.PendingApproval
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * اختبارات ApprovePendingUseCase و RejectPendingUseCase
 * يتحقق من صحة استخراج Business Logic من DashboardViewModel
 */
@ExperimentalCoroutinesApi
class ApprovePendingUseCaseTest {

    private lateinit var approvalsRepo: ApprovalsRepository
    private lateinit var inventoryRepo: InventoryRepository
    private lateinit var salesRepo: SalesRepository
    private lateinit var sellCardUseCase: SellCardUseCase
    private lateinit var approveUseCase: ApprovePendingUseCase
    private lateinit var rejectUseCase: RejectPendingUseCase
    private val testDispatcher = StandardTestDispatcher()

    private fun fakePending(id: Int = 1, amount: Int = 100, wallet: String = "جيب") =
        PendingApproval(id = id, phone = "777111222", amount = amount,
            walletType = wallet, isAccountCode = false, depositId = 0,
            createdAt = System.currentTimeMillis())

    private fun fakeCard(id: Int = 10, cat: Int = 100) =
        Card(id = id, category = cat, code = "CODE-$id", isUsed = false,
            createdAt = System.currentTimeMillis(), username = "", password = "")

    @Before
    fun setup() {
        approvalsRepo = mockk()
        inventoryRepo = mockk()
        salesRepo = mockk()
        sellCardUseCase = SellCardUseCase(inventoryRepo, salesRepo, ValidateSmsAmountUseCase())
        approveUseCase = ApprovePendingUseCase(approvalsRepo, sellCardUseCase)
        rejectUseCase = RejectPendingUseCase(approvalsRepo, salesRepo)
    }

    // ✅ موافقة على طلب موجود — ينجح
    @Test
    fun `approve pending - success when card available and pending exists`() = runTest(testDispatcher) {
        val pending = fakePending(id = 1)
        val card = fakeCard(id = 10, cat = 100)

        coEvery { approvalsRepo.getPendingApproval(1) } returns pending
        coEvery { inventoryRepo.getUnusedCardByCategory(100) } returns card
        coEvery { inventoryRepo.markCardAsUsed(10) } returns Resource.Success(Unit)
        coEvery { salesRepo.insertTransaction(any(), any(), any(), any()) } returns Resource.Success(Unit)
        coEvery { salesRepo.insertDeposit(any(), any(), any(), any(), any()) } returns Resource.Success(1L)
        coEvery { approvalsRepo.deletePendingApproval(1) } returns Resource.Success(Unit)

        val result = approveUseCase(1) { _, _ -> true }

        assertTrue("يجب أن تنجح الموافقة", result is Resource.Success)
        coVerify(exactly = 1) { approvalsRepo.deletePendingApproval(1) }
        coVerify(exactly = 1) { inventoryRepo.markCardAsUsed(10) }
    }

    // ✅ موافقة على طلب غير موجود
    @Test
    fun `approve pending - returns error when pending not found`() = runTest(testDispatcher) {
        coEvery { approvalsRepo.getPendingApproval(999) } returns null

        val result = approveUseCase(999) { _, _ -> true }

        assertTrue("يجب إعادة خطأ عند عدم وجود الطلب", result is Resource.Error)
        coVerify(exactly = 0) { inventoryRepo.getUnusedCardByCategory(any()) }
    }

    // ✅ موافقة بدون مخزون
    @Test
    fun `approve pending - fails when no card stock`() = runTest(testDispatcher) {
        val pending = fakePending(amount = 100)
        coEvery { approvalsRepo.getPendingApproval(1) } returns pending
        coEvery { inventoryRepo.getUnusedCardByCategory(100) } returns null

        val result = approveUseCase(1) { _, _ -> true }

        assertTrue("يجب الفشل عند نفاد المخزون", result is Resource.Error)
        // الطلب لا يُحذف إذا فشل البيع
        coVerify(exactly = 0) { approvalsRepo.deletePendingApproval(any()) }
    }

    // ✅ موافقة مع فشل إرسال SMS — تُسجَّل مع ✖
    @Test
    fun `approve pending - succeeds locally even if SMS fails`() = runTest(testDispatcher) {
        val pending = fakePending()
        val card = fakeCard()

        coEvery { approvalsRepo.getPendingApproval(1) } returns pending
        coEvery { inventoryRepo.getUnusedCardByCategory(100) } returns card
        coEvery { inventoryRepo.markCardAsUsed(any()) } returns Resource.Success(Unit)
        coEvery { salesRepo.insertTransaction(any(), any(), any(), any()) } returns Resource.Success(Unit)
        coEvery { salesRepo.insertDeposit(any(), any(), any(), any(), any()) } returns Resource.Success(1L)
        coEvery { approvalsRepo.deletePendingApproval(1) } returns Resource.Success(Unit)

        val result = approveUseCase(1) { _, _ -> false } // SMS فشل

        assertTrue("يجب نجاح الموافقة حتى لو فشل الـ SMS", result is Resource.Success)
        coVerify(exactly = 1) { salesRepo.insertTransaction(any(), any(), match { it.contains("✖") }, any()) }
    }

    // ✅ رفض طلب موجود — يُسجَّل ويُحذف
    @Test
    fun `reject pending - records rejection and removes from list`() = runTest(testDispatcher) {
        val pending = fakePending(id = 2)
        coEvery { approvalsRepo.getPendingApproval(2) } returns pending
        coEvery { salesRepo.insertTransaction(any(), any(), any(), any()) } returns Resource.Success(Unit)
        coEvery { approvalsRepo.deletePendingApproval(2) } returns Resource.Success(Unit)

        val result = rejectUseCase(2)

        assertTrue("يجب أن ينجح الرفض", result is Resource.Success)
        coVerify(exactly = 1) { salesRepo.insertTransaction("777111222", 100, "مرفوض يدوياً", "جيب") }
        coVerify(exactly = 1) { approvalsRepo.deletePendingApproval(2) }
    }

    // ✅ رفض طلب غير موجود
    @Test
    fun `reject pending - returns error when not found`() = runTest(testDispatcher) {
        coEvery { approvalsRepo.getPendingApproval(404) } returns null

        val result = rejectUseCase(404)

        assertTrue("يجب إعادة خطأ", result is Resource.Error)
        coVerify(exactly = 0) { salesRepo.insertTransaction(any(), any(), any(), any()) }
    }
}
