package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.database.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * MainViewModel — منسق التطبيق (Coordinator) والمسؤول عن الحالات العامة (UI/Theme).
 * تم نقل العمليات الأخرى إلى:
 * - AuthViewModel (التراخيص والأمان)
 * - SmsViewModel (الرسائل والكروت)
 * - DistributorViewModel (نظام الموزع)
 */
@HiltViewModel
class MainViewModel @Inject constructor(private val repository: CardRepository) : ViewModel() {

    // ──────────────────────────────────────────
    // الإعدادات العامة للواجهة (UI Global State)
    // ──────────────────────────────────────────
    val isDarkTheme: StateFlow<Boolean> = repository.isDarkTheme
    fun setDarkTheme(enabled: Boolean) = repository.setDarkTheme(enabled)

    // وضع الموزع
    val isDistributorModeActive: StateFlow<Boolean> = repository.isDistributorModeActive
    fun setDistributorModeActive(active: Boolean) = repository.setDistributorModeActive(active)
}

// ملاحظة: بعد تحويل MainViewModel لـ @HiltViewModel، لم يعد MainActivity
// يستخدم هذا المصنع (يستخدم hiltViewModel() مباشرة بدلاً منه). أُبقي عليه هنا
// دون حذف تجنباً لأي كسر لأي استخدام آخر محتمل غير مرصود في هذا الفحص،
// ويمكن حذفه لاحقاً بعد التأكد الكامل من عدم استخدامه في أي مكان.
class MainViewModelFactory(private val repository: CardRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
