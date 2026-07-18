package com.example

import com.example.core.model.LicenseState
import com.example.core.model.LicenseStatus
import com.example.core.model.Resource
import com.example.core.repository.LicenseRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * ✅ اختبار 3: تفعيل الترخيص
 * ✅ اختبار 4: انتهاء الترخيص
 */
@ExperimentalCoroutinesApi
class LicenseRepositoryTest {

    private lateinit var licenseRepository: LicenseRepository
    private val testDispatcher = StandardTestDispatcher()

    private fun licenseStatus(state: LicenseState, expiry: Long? = null) =
        LicenseStatus(state = state, licenseKey = "KEY-001", expiryDate = expiry, features = emptyList())

    @Before
    fun setup() {
        licenseRepository = mockk()
    }

    // ✅ اختبار 3أ: تفعيل ترخيص بسيريال صحيح
    @Test
    fun `activate license - returns VALID state on success`() = runTest(testDispatcher) {
        val validStatus = licenseStatus(LicenseState.VALID, System.currentTimeMillis() + 30 * 86_400_000L)
        coEvery { licenseRepository.activateLicense("VALID-SERIAL-001") } returns Resource.Success(validStatus)

        val result = licenseRepository.activateLicense("VALID-SERIAL-001")

        assertTrue("يجب أن ينجح التفعيل", result is Resource.Success)
        assertEquals(LicenseState.VALID, (result as Resource.Success).data.state)
    }

    // ✅ اختبار 3ب: تفعيل بسيريال مزيف
    @Test
    fun `activate license - returns error for fake serial`() = runTest(testDispatcher) {
        coEvery { licenseRepository.activateLicense("FAKE-SERIAL") } returns Resource.Error("Activation failed")

        val result = licenseRepository.activateLicense("FAKE-SERIAL")
        assertTrue("يجب رفض السيريال المزيف", result is Resource.Error)
    }

    // ✅ اختبار 3ج: تفعيل بسيريال مستخدم سابقاً على جهاز آخر
    @Test
    fun `activate license - rejects serial already bound to another device`() = runTest(testDispatcher) {
        coEvery { licenseRepository.activateLicense("USED-SERIAL") } returns Resource.Error("Serial bound to different device")

        val result = licenseRepository.activateLicense("USED-SERIAL")
        assertTrue("يجب رفض السيريال المرتبط بجهاز آخر", result is Resource.Error)
    }

    // ✅ اختبار 4أ: اكتشاف انتهاء الترخيص
    @Test
    fun `license status - returns EXPIRED when past expiry date`() = runTest(testDispatcher) {
        val expiredStatus = licenseStatus(LicenseState.EXPIRED, System.currentTimeMillis() - 86_400_000L)
        coEvery { licenseRepository.getLicenseStatus() } returns expiredStatus

        val status = licenseRepository.getLicenseStatus()
        assertEquals("يجب اكتشاف انتهاء الترخيص", LicenseState.EXPIRED, status.state)
    }

    // ✅ اختبار 4ب: فترة السماح (Grace Period)
    @Test
    fun `license status - treats as VALID during grace period offline`() = runTest(testDispatcher) {
        // انتهى الترخيص منذ 12 ساعة فقط (ضمن فترة السماح 24 ساعة)
        val inGraceStatus = licenseStatus(LicenseState.VALID, System.currentTimeMillis() - 12 * 3600_000L)
        coEvery { licenseRepository.getLicenseStatus() } returns inGraceStatus

        val status = licenseRepository.getLicenseStatus()
        assertEquals("يجب قبول الترخيص ضمن فترة السماح", LicenseState.VALID, status.state)
    }

    // ✅ اختبار 4ج: ترخيص نشط
    @Test
    fun `license status - returns VALID for active license`() = runTest(testDispatcher) {
        val validStatus = licenseStatus(LicenseState.VALID, System.currentTimeMillis() + 30 * 86_400_000L)
        coEvery { licenseRepository.getLicenseStatus() } returns validStatus

        val status = licenseRepository.getLicenseStatus()
        assertEquals("يجب قبول الترخيص النشط", LicenseState.VALID, status.state)
    }

    // التحقق المحلي السريع
    @Test
    fun `local validation - returns true when license is cached as valid`() = runTest(testDispatcher) {
        coEvery { licenseRepository.validateLocalLicense() } returns true

        assertTrue("التحقق المحلي يجب أن يقبل الترخيص الصالح", licenseRepository.validateLocalLicense())
    }

    @Test
    fun `local validation - returns false when license expired locally`() = runTest(testDispatcher) {
        coEvery { licenseRepository.validateLocalLicense() } returns false

        assertFalse("التحقق المحلي يجب أن يرفض الترخيص المنتهي", licenseRepository.validateLocalLicense())
    }

    // جهاز مجمّد من السيرفر
    @Test
    fun `license status - returns BLOCKED for frozen device`() = runTest(testDispatcher) {
        val blockedStatus = licenseStatus(LicenseState.BLOCKED)
        coEvery { licenseRepository.getLicenseStatus() } returns blockedStatus

        val status = licenseRepository.getLicenseStatus()
        assertEquals("يجب حجب الجهاز المجمّد", LicenseState.BLOCKED, status.state)
    }
}
