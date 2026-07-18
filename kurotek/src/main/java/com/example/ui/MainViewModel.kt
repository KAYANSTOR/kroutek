package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.database.CardRepository
import kotlinx.coroutines.flow.StateFlow

/**
 * MainViewModel — منسق التطبيق (Coordinator) والمسؤول عن الحالات العامة (UI/Theme).
 * تم نقل العمليات الأخرى إلى:
 * - AuthViewModel (التراخيص والأمان)
 * - SmsViewModel (الرسائل والكروت)
 * - DistributorViewModel (نظام الموزع)
 */
class MainViewModel(private val repository: CardRepository) : ViewModel() {

    // ──────────────────────────────────────────
    // الإعدادات العامة للواجهة (UI Global State)
    // ──────────────────────────────────────────
    val isDarkTheme: StateFlow<Boolean> = repository.isDarkTheme
    fun setDarkTheme(enabled: Boolean) = repository.setDarkTheme(enabled)

    // وضع الموزع
    val isDistributorModeActive: StateFlow<Boolean> = repository.isDistributorModeActive
    fun setDistributorModeActive(active: Boolean) = repository.setDistributorModeActive(active)
}

class MainViewModelFactory(private val repository: CardRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
