package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.CoreContainer
import com.example.core.model.Resource
import com.example.database.CardRepository
import com.example.models.DistributorCapital
import com.example.models.DistributorCustomer
import com.example.models.DistributorExpense
import com.example.models.DistributorTransaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * DistributorViewModel — النسخة المحدّثة (Clean Architecture)
 *
 * القاعدة الصارمة (ADR-006):
 * عمليات البيع والتحقق من المخزون تبقى هنا مؤقتاً حتى يكتمل
 * DistributorSaleUseCase في المرحلة القادمة.
 *
 * ┌────────────────────────────┐
 * │  DistributorViewModel      │
 * └────────────┬───────────────┘
 *              ├─ CreateBackupUseCase   (Core)
 *              ├─ RestoreBackupUseCase  (Core)
 *              └─ CardRepository       (Data, مؤقت)
 */
class DistributorViewModel(
    private val repository: CardRepository,
    private val coreContainer: CoreContainer
) : ViewModel() {

    // ─────────────────────────────────────────────
    // وضع الموزع
    // ─────────────────────────────────────────────
    val isDistributorModeActive: StateFlow<Boolean> = repository.isDistributorModeActive
    fun setDistributorModeActive(active: Boolean) = repository.setDistributorModeActive(active)

    // ─────────────────────────────────────────────
    // بيانات العملاء والمعاملات والمصروفات ورأس المال
    // ─────────────────────────────────────────────
    val distributorCustomers: StateFlow<List<DistributorCustomer>> = repository.getDistributorCustomers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val distributorTransactions: StateFlow<List<DistributorTransaction>> = repository.getDistributorTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val distributorExpenses: StateFlow<List<DistributorExpense>> = repository.getDistributorExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val distributorCapitals: StateFlow<List<DistributorCapital>> = repository.getDistributorCapitals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ─────────────────────────────────────────────
    // إدارة العملاء
    // ─────────────────────────────────────────────
    fun insertDistributorCustomer(name: String, customId: String? = null) {
        viewModelScope.launch {
            val customer = DistributorCustomer(id = customId ?: UUID.randomUUID().toString(), name = name)
            repository.insertDistributorCustomer(customer)
        }
    }

    fun deleteDistributorCustomer(id: String) {
        viewModelScope.launch { repository.deleteDistributorCustomer(id) }
    }

    // ─────────────────────────────────────────────
    // تنفيذ مبيعات الموزع
    // ─────────────────────────────────────────────
    fun performDistributorSale(
        customerId: String,
        quantities: Map<Int, Int>,
        totalAmount: Double,
        totalBuyingCost: Double,
        calcProfits: Double,
        receivedAmount: Double,
        onComplete: (success: Boolean, message: String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (customerId == "CASH" && repository.getDistributorCustomerByIdDirect("CASH") == null) {
                    repository.insertDistributorCustomer(DistributorCustomer(id = "CASH", name = "زبون كاش مباشر"))
                }

                val insufficient = mutableListOf<Int>()
                quantities.forEach { (cat, qty) ->
                    if (qty > 0 && repository.getUnusedCountByCategoryDirect(cat) < qty)
                        insufficient.add(cat)
                }

                quantities.forEach { (cat, qty) ->
                    if (qty > 0) repeat(qty) {
                        repository.getUnusedCardByCategory(cat)?.let { repository.markCardAsUsed(it.id) }
                    }
                }

                val notes = "التكلفة: $totalBuyingCost | الأرباح: $calcProfits | كروت: ${
                    quantities.filter { it.value > 0 }.map { "${it.value}×${it.key}" }.joinToString(", ")
                }"
                repository.insertDistributorTransaction(
                    DistributorTransaction(UUID.randomUUID().toString(), customerId, "sale", totalAmount, notes)
                )

                if (receivedAmount > 0) {
                    repository.insertDistributorTransaction(
                        DistributorTransaction(UUID.randomUUID().toString(), customerId, "payment", receivedAmount, "دفعة مسددة نقداً")
                    )
                }

                val customerName = if (customerId == "CASH") "زبون كاش مباشر"
                else repository.getDistributorCustomerByIdDirect(customerId)?.name ?: "عميل موزع"

                repository.insertTransaction(
                    phone = customerName,
                    amount = totalAmount.toInt(),
                    cardCode = "فاتورة بيع كروت - المسدد: $receivedAmount ر.ي | أرباح: $calcProfits ر.ي",
                    walletType = "حاسبة الموزع"
                )

                onComplete(
                    true,
                    if (insufficient.isEmpty()) "🟢 تم تسجيل المبيعات وخصم الكروت بنجاح!"
                    else "⚠️ تم تسجيل البيع، المخزون غير كافٍ لبعض الفئات!"
                )
            } catch (e: Exception) {
                android.util.Log.e("DistributorViewModel", "performSale failed", e)
                onComplete(false, "🔴 فشل: ${e.localizedMessage}")
            }
        }
    }

    // ─────────────────────────────────────────────
    // الحركات المالية
    // ─────────────────────────────────────────────
    fun insertDistributorTransaction(customerId: String, type: String, amount: Double, notes: String) {
        viewModelScope.launch {
            repository.insertDistributorTransaction(
                DistributorTransaction(UUID.randomUUID().toString(), customerId, type, amount, notes)
            )
        }
    }
    fun deleteDistributorTransaction(id: String, customerId: String) {
        viewModelScope.launch { repository.deleteDistributorTransaction(id, customerId) }
    }

    // ─────────────────────────────────────────────
    // المصروفات
    // ─────────────────────────────────────────────
    fun insertDistributorExpense(category: String, amount: Double, description: String) {
        viewModelScope.launch {
            repository.insertDistributorExpense(
                DistributorExpense(UUID.randomUUID().toString(), category, amount, description)
            )
        }
    }
    fun deleteDistributorExpense(id: String) { viewModelScope.launch { repository.deleteDistributorExpense(id) } }

    // ─────────────────────────────────────────────
    // رأس المال
    // ─────────────────────────────────────────────
    fun insertDistributorCapital(type: String, amount: Double, description: String) {
        viewModelScope.launch {
            repository.insertDistributorCapital(
                DistributorCapital(UUID.randomUUID().toString(), type, amount, description)
            )
        }
    }
    fun deleteDistributorCapital(id: String) { viewModelScope.launch { repository.deleteDistributorCapital(id) } }

    // ─────────────────────────────────────────────
    // [UseCase] النسخ الاحتياطية
    // ─────────────────────────────────────────────
    private val _backupState = MutableStateFlow<String?>(null)
    val backupState: StateFlow<String?> = _backupState.asStateFlow()

    fun createBackup() {
        viewModelScope.launch {
            when (val result = coreContainer.createBackupUseCase()) {
                is Resource.Success -> _backupState.value = "✅ تم إنشاء النسخة: ${result.data.fileName}"
                is Resource.Error -> _backupState.value = "❌ فشل النسخ: ${result.message}"
                else -> {}
            }
        }
    }

    fun restoreBackup(backupId: String) {
        viewModelScope.launch {
            when (val result = coreContainer.restoreBackupUseCase(backupId)) {
                is Resource.Success -> _backupState.value = "✅ تم استعادة النسخة بنجاح"
                is Resource.Error -> _backupState.value = "❌ فشل الاستعادة: ${result.message}"
                else -> {}
            }
        }
    }

    // ─────────────────────────────────────────────
    // تهيئة البيانات
    // ─────────────────────────────────────────────
    fun clearAllDistributorData() { viewModelScope.launch { repository.clearAllDistributorData() } }
}

class DistributorViewModelFactory(
    private val repository: CardRepository,
    private val coreContainer: CoreContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DistributorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DistributorViewModel(repository, coreContainer) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
