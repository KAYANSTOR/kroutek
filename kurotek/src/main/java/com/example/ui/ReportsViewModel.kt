package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.CoreContainer
import com.example.models.CustomerMapping
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ReportsViewModel
 * مسؤول عن:
 * - توليد التقارير المالية والإيرادات
 * - إدارة أسماء ومطابقة العملاء (Mappings) لتحسين أسماء العملاء في التقارير
 */
class ReportsViewModel(private val coreContainer: CoreContainer) : ViewModel() {

    private val reportsRepo = coreContainer.reportsRepository

    // ─────────────────────────────────────────────
    // التقارير والمبيعات
    // ─────────────────────────────────────────────

    private val _reportData = MutableStateFlow<Map<String, Any>?>(null)
    val reportData: StateFlow<Map<String, Any>?> = _reportData.asStateFlow()

    fun generateReport(fromTimestamp: Long, toTimestamp: Long) {
        viewModelScope.launch {
            val data = coreContainer.generateReportUseCase(fromTimestamp, toTimestamp)
            _reportData.value = data
        }
    }

    // ─────────────────────────────────────────────
    // مطابقة العملاء (Mappings)
    // ─────────────────────────────────────────────

    private val _mappings = MutableStateFlow<List<CustomerMapping>>(emptyList())
    val mappings: StateFlow<List<CustomerMapping>> = _mappings.asStateFlow()

    fun loadMappings() {
        viewModelScope.launch {
            _mappings.value = reportsRepo.getMappings()
        }
    }

    fun addMapping(uniqueId: String, phone: String, name: String, walletType: String) {
        viewModelScope.launch {
            reportsRepo.insertMapping(uniqueId, phone, name, walletType)
            loadMappings() // تحديث القائمة
        }
    }

    fun deleteMapping(id: String) {
        viewModelScope.launch {
            reportsRepo.deleteMapping(id)
            loadMappings()
        }
    }
}

class ReportsViewModelFactory(private val coreContainer: CoreContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportsViewModel(coreContainer) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
