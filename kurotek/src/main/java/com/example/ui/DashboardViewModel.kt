package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.CoreContainer
import com.example.models.PendingApproval
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * DashboardViewModel
 * مسؤول عن:
 * - جلب إحصائيات اللوحة الرئيسية (المبيعات اليومية، عدد الموافقات المعلقة، المخزون المنخفض)
 * - مراقبة الموافقات المعلقة وإدارتها المبدئية
 */
class DashboardViewModel(private val coreContainer: CoreContainer) : ViewModel() {

    private val dashboardRepo = coreContainer.dashboardRepository
    private val approvalsRepo = coreContainer.approvalsRepository

    // ─────────────────────────────────────────────
    // إحصائيات الشاشة الرئيسية
    // ─────────────────────────────────────────────
    
    // عدد الموافقات المعلقة (Live)
    val pendingApprovalsCount: StateFlow<Int> = approvalsRepo.observePendingApprovals()
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // الموافقات المعلقة نفسها
    val pendingApprovals: StateFlow<List<PendingApproval>> = approvalsRepo.observePendingApprovals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ─────────────────────────────────────────────
    // عمليات سريعة من اللوحة (قبول / رفض)
    // ─────────────────────────────────────────────

    fun approvePending(pendingId: Int, onResult: (success: Boolean, message: String) -> Unit) {
        viewModelScope.launch {
            val pending = approvalsRepo.getPendingApproval(pendingId)
            if (pending == null) {
                onResult(false, "الطلب غير موجود")
                return@launch
            }

            // الاعتماد على UseCase لبيع الكرت وإرسال الـ SMS
            val result = coreContainer.sellCardUseCase(
                phone = pending.phone,
                amount = pending.amount,
                walletType = pending.walletType,
                onSmsSend = { recipient, msg ->
                    com.example.utils.SmsSender.sendSmsInBackground(coreContainer.deviceEngine.context, recipient, msg)
                }
            )

            if (result is com.example.core.model.Resource.Success) {
                approvalsRepo.deletePendingApproval(pendingId)
                
                // مزامنة فورية في الخلفية
                val payload = """{"phone":"${pending.phone}","amount":${pending.amount},"walletType":"${pending.walletType}"}"""
                coreContainer.syncTransactionsUseCase(payload)
                
                onResult(true, "تم قبول الطلب وإرسال الكرت بنجاح")
            } else {
                onResult(false, result.message ?: "حدث خطأ غير معروف")
            }
        }
    }

    fun rejectPending(pendingId: Int) {
        viewModelScope.launch {
            val pending = approvalsRepo.getPendingApproval(pendingId) ?: return@launch
            coreContainer.salesRepository.insertTransaction(
                phone = pending.phone,
                amount = pending.amount,
                cardCode = "تم رفض الطلب يدوياً من قِبل المشرف",
                walletType = pending.walletType
            )
            approvalsRepo.deletePendingApproval(pendingId)
        }
    fun updatePendingApprovalPhone(pendingId: Int, newPhone: String) {
        viewModelScope.launch {
            approvalsRepo.updatePendingPhone(pendingId, newPhone)
        }
    }
}

class DashboardViewModelFactory(private val coreContainer: CoreContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(coreContainer) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
