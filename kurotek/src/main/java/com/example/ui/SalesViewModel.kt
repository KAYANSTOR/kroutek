package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.CoreContainer
import com.example.models.Transaction
import com.example.models.Deposit
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * SalesViewModel
 * مسؤول عن:
 * - عرض سجل المبيعات (Transactions)
 * - عرض سجل الإيداعات (Deposits)
 * - إدخال معاملة بيع يدوية
 */
class SalesViewModel(private val coreContainer: CoreContainer) : ViewModel() {

    private val salesRepo = coreContainer.salesRepository

    // ─────────────────────────────────────────────
    // قراءة البيانات (StateFlows)
    // ─────────────────────────────────────────────

    val transactions: StateFlow<List<Transaction>> = salesRepo.observeTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deposits: StateFlow<List<Deposit>> = salesRepo.observeDeposits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ─────────────────────────────────────────────
    // العمليات اليدوية
    // ─────────────────────────────────────────────

    fun addManualTransaction(phone: String, amount: Int, cardCode: String, walletType: String) {
        viewModelScope.launch {
            salesRepo.insertTransaction(phone, amount, cardCode, walletType)
            // مزامنة فورية في الخلفية عبر UseCase
            val payload = """{"phone":"$phone","amount":$amount,"walletType":"$walletType","type":"MANUAL"}"""
            coreContainer.syncTransactionsUseCase(payload)
        }
    }

    fun clearTransactions() {
        viewModelScope.launch {
            salesRepo.clearAllTransactions()
        }
    }

    fun clearDeposits() {
        viewModelScope.launch {
            salesRepo.clearAllDeposits()
        }
    }
}

class SalesViewModelFactory(private val coreContainer: CoreContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SalesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SalesViewModel(coreContainer) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
