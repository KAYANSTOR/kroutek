package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.CoreContainer
import com.example.models.GeneratedMikrotikCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * MikrotikViewModel — مسؤول عن توليد وإدارة كروت المايكروتك.
 * يستخدم NetworkRepository المبني بالفعل في CoreContainer.
 * مستقل تمامًا عن SmsViewModel.
 */
class MikrotikViewModel(private val coreContainer: CoreContainer) : ViewModel() {

    private val networkRepo = coreContainer.networkRepository

    val allGeneratedCards: StateFlow<List<GeneratedMikrotikCard>> =
        networkRepo.observeGeneratedCards()
            .flowOn(Dispatchers.IO)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertGeneratedCards(cards: List<GeneratedMikrotikCard>) {
        viewModelScope.launch { networkRepo.insertGeneratedCards(cards) }
    }

    fun transferGeneratedCardToAutoSales(
        id: Int, category: Int, pin: String, username: String, password: String
    ) {
        viewModelScope.launch {
            networkRepo.transferCardToAutoSales(id, category, pin, username, password)
        }
    }

    fun deleteGeneratedCard(id: Int) {
        viewModelScope.launch { networkRepo.deleteGeneratedCard(id) }
    }
}

class MikrotikViewModelFactory(private val coreContainer: CoreContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MikrotikViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MikrotikViewModel(coreContainer) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
