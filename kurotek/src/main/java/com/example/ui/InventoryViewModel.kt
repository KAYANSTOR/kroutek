package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.CoreContainer
import com.example.models.Card
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * InventoryViewModel
 * مسؤول عن:
 * - عرض المخزون المتوفر (أعداد الكروت حسب الفئة)
 * - إضافة كروت جديدة (مفردة، بالجملة)
 * - حذف الكروت أو مسحها بالكامل
 */
class InventoryViewModel(private val coreContainer: CoreContainer) : ViewModel() {

    private val inventoryRepo = coreContainer.inventoryRepository

    // ─────────────────────────────────────────────
    // قراءة المخزون (Real-time Flow)
    // ─────────────────────────────────────────────

    val allCards: StateFlow<List<Card>> = inventoryRepo.observeAllCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // المجموع الكلي للكروت غير المستخدمة
    val totalUnusedCount: StateFlow<Int> = allCards
        .map { cards -> cards.count { !it.used } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun getCountForCategory(category: Int): StateFlow<Int> =
        inventoryRepo.observeCountByCategory(category)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // ─────────────────────────────────────────────
    // العمليات (إضافة / حذف) عبر UseCases
    // ─────────────────────────────────────────────

    fun addCardsBulk(category: Int, codesBlock: String, onComplete: (Int) -> Unit) {
        viewModelScope.launch {
            val result = coreContainer.addCardsUseCase(category, codesBlock)
            val addedCount = if (result is com.example.core.model.Resource.Success) result.data else 0
            onComplete(addedCount)
        }
    }

    fun addSingleCard(card: Card, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = inventoryRepo.insertCard(card)
            onComplete(result is com.example.core.model.Resource.Success)
        }
    }

    fun deleteCard(cardId: String) {
        viewModelScope.launch {
            coreContainer.deleteCardUseCase(cardId)
        }
    }

    fun markCardAsUsed(cardId: String) {
        viewModelScope.launch {
            inventoryRepo.markCardAsUsed(cardId)
        }
    }

    fun clearAllCards(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = inventoryRepo.clearAllCards()
            onComplete(result is com.example.core.model.Resource.Success)
        }
    }
}

class InventoryViewModelFactory(private val coreContainer: CoreContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(coreContainer) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
