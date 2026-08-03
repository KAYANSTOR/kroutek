package com.example

import com.example.core.license.LicenseEngine
import com.example.core.model.Resource
import com.example.core.usecase.ActivateLicenseUseCase
import com.example.core.usecase.ValidateLicenseUseCase
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
class LicenseUseCaseTest {

    private lateinit var licenseEngine: LicenseEngine
    private lateinit var activateLicenseUseCase: ActivateLicenseUseCase
    private lateinit var validateLicenseUseCase: ValidateLicenseUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        licenseEngine = mockk()
        activateLicenseUseCase = ActivateLicenseUseCase(licenseEngine)
        validateLicenseUseCase = ValidateLicenseUseCase(licenseEngine)
    }

    // ✅ اختبار 3: تفعيل ترخيص صحيح
    @Test
    fun `activate license - success with valid serial`() = runTest(testDispatcher) {
        coEvery { licenseEngine.activate(any()) } returns Resource.Success(Unit)

        val result = activateLicenseUseCase("VALID-SERIAL-KEY-001")
        assertTrue("يجب أن ينجح التفعيل بسيريال صحيح", result is Resource.Success)
        coVerify(exactly = 1) { licenseEngine.activate("VALID-SERIAL-KEY-001") }
    }

    // تفعيل بسيريال مزور
    @Test
    fun `activate license - fails with invalid serial`() = runTest(testDispatcher) {
        coEvery { licenseEngine.activate("FAKE-SERIAL") } returns Resource.Error("Serial key not found")

        val result = activateLicenseUseCase("FAKE-SERIAL")
        assertTrue("يجب أن يرفض السيريال المزور", result is Resource.Error)
    }

    // ✅ اختبار 4: انتهاء الترخيص
    @Test
    fun `validate license - returns expired when license past due`() = runTest(testDispatcher) {
        coEvery { licenseEngine.validate() } returns Resource.Error("License expired")

        val result = validateLicenseUseCase()
        assertTrue("يجب اكتشاف انتهاء الترخيص", result is Resource.Error)
    }

    // ترخيص نشط
    @Test
    fun `validate license - success when active`() = runTest(testDispatcher) {
        coEvery { licenseEngine.validate() } returns Resource.Success(Unit)

        val result = validateLicenseUseCase()
        assertTrue("يجب قبول الترخيص النشط", result is Resource.Success)
    }

    // فترة تجريبية
    @Test
    fun `validate license - success during trial period`() = runTest(testDispatcher) {
        coEvery { licenseEngine.validate() } returns Resource.Success(Unit)

        val result = validateLicenseUseCase()
        assertTrue("يجب قبول الترخيص خلال الفترة التجريبية", result is Resource.Success)
    }
}
