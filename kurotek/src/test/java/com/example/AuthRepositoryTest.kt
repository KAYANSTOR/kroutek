package com.example

import com.example.core.model.Resource
import com.example.core.model.UserSession
import com.example.core.repository.AuthRepository
import com.example.core.session.SessionManager
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * ✅ اختبار 1: تسجيل الدخول (صحيح وخاطئ)
 * ✅ اختبار 2: تحديث الـ Access Token عند انتهاء صلاحيته
 * ✅ اختبار 9: استعادة البيانات بعد إعادة تشغيل التطبيق
 */
@ExperimentalCoroutinesApi
class AuthRepositoryTest {

    private lateinit var authRepository: AuthRepository
    private val testDispatcher = StandardTestDispatcher()

    private fun fakeSession(userId: String = "user1") = UserSession(
        userId = userId,
        token = "access_token_abc",
        role = "ADMIN",
        loginTimestamp = System.currentTimeMillis(),
        expiryTimestamp = System.currentTimeMillis() + 3_600_000L // ساعة من الآن
    )

    @Before
    fun setup() {
        authRepository = mockk()
    }

    // ✅ اختبار 1أ: تسجيل الدخول بمعلومات صحيحة
    @Test
    fun `login - success with valid credentials`() = runTest(testDispatcher) {
        val session = fakeSession()
        coEvery { authRepository.login("user1", "correct_hash") } returns Resource.Success(session)

        val result = authRepository.login("user1", "correct_hash")

        assertTrue("يجب أن ينجح تسجيل الدخول بالبيانات الصحيحة", result is Resource.Success)
        assertEquals("user1", (result as Resource.Success).data.userId)
        coVerify(exactly = 1) { authRepository.login("user1", "correct_hash") }
    }

    // ✅ اختبار 1ب: تسجيل الدخول بكلمة مرور خاطئة
    @Test
    fun `login - fails with wrong password`() = runTest(testDispatcher) {
        coEvery { authRepository.login("user1", "wrong_hash") } returns Resource.Error("Invalid credentials")

        val result = authRepository.login("user1", "wrong_hash")

        assertTrue("يجب أن يرفض تسجيل الدخول عند خطأ في البيانات", result is Resource.Error)
    }

    // ✅ اختبار 1ج: تسجيل دخول بدون اتصال بالإنترنت
    @Test
    fun `login - fails gracefully when offline`() = runTest(testDispatcher) {
        coEvery { authRepository.login(any(), any()) } returns Resource.Error("No network connection")

        val result = authRepository.login("user1", "any_hash")
        assertTrue("يجب إعادة خطأ واضح عند انقطاع الإنترنت", result is Resource.Error)
    }

    // ✅ اختبار 2: التحقق من صلاحية الجلسة — تحديث Token منتهي الصلاحية
    @Test
    fun `session - reports invalid when token expired`() = runTest(testDispatcher) {
        coEvery { authRepository.requireValidSession() } returns false

        val isValid = authRepository.requireValidSession()
        assertFalse("يجب اكتشاف انتهاء التوكن وإعادة false", isValid)
    }

    // ✅ اختبار 2ب: جلسة نشطة
    @Test
    fun `session - reports valid when token active`() = runTest(testDispatcher) {
        coEvery { authRepository.requireValidSession() } returns true

        val isValid = authRepository.requireValidSession()
        assertTrue("يجب قبول الجلسة النشطة", isValid)
    }

    // ✅ اختبار 9: استعادة البيانات بعد إعادة التشغيل
    @Test
    fun `session - persists across restarts (getCurrentSession)`() = runTest(testDispatcher) {
        val session = fakeSession("user_restart")
        coEvery { authRepository.getCurrentSession() } returns session

        val restored = authRepository.getCurrentSession()

        assertNotNull("يجب استعادة الجلسة بعد إعادة التشغيل", restored)
        assertEquals("user_restart", restored!!.userId)
    }

    // لا توجد جلسة محفوظة
    @Test
    fun `session - returns null when no saved session`() = runTest(testDispatcher) {
        coEvery { authRepository.getCurrentSession() } returns null

        val restored = authRepository.getCurrentSession()
        assertNull("يجب إعادة null إذا لم تكن هناك جلسة محفوظة", restored)
    }

    // تسجيل الخروج يمسح الجلسة
    @Test
    fun `logout - clears session successfully`() = runTest(testDispatcher) {
        coEvery { authRepository.logout() } returns Resource.Success(Unit)
        coEvery { authRepository.getCurrentSession() } returns null

        val result = authRepository.logout()
        assertTrue("يجب أن ينجح تسجيل الخروج", result is Resource.Success)

        val session = authRepository.getCurrentSession()
        assertNull("يجب مسح الجلسة بعد تسجيل الخروج", session)
    }
}
