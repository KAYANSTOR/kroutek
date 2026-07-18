package com.example.core.repository.impl

import com.example.core.model.Resource
import com.example.core.repository.InventoryRepository
import com.example.database.CardRepository
import com.example.models.Card
import kotlinx.coroutines.flow.Flow

class InventoryRepositoryImpl(private val db: CardRepository) : InventoryRepository {
    override suspend fun insertCard(card: Card) = wrap { db.insertCardsList(listOf(card)) }
    override suspend fun insertCardsBulk(category: Int, codesBlock: String): Resource<Int> {
        val count = db.insertCardsBulk(category, codesBlock)
        return Resource.Success(count)
    }
    override suspend fun insertCardsList(cards: List<Card>): Resource<Int> =
        Resource.Success(db.insertCardsList(cards))
    override suspend fun deleteCard(cardId: Int) = wrap { db.deleteCard(cardId) }
    override suspend fun markCardAsUsed(cardId: Int) = wrap { db.markCardAsUsed(cardId) }
    override suspend fun getUnusedCardByCategory(category: Int): Card? = db.getUnusedCardByCategory(category)
    override suspend fun getUnusedCountByCategory(category: Int): Int = db.getUnusedCountByCategoryDirect(category)
    override suspend fun getTotalUnusedCount(): Int = 0 // TODO: add direct count to CardRepository
    override suspend fun getAllCards(): List<Card> = db.getAllCards().replayCache.firstOrNull() ?: emptyList()
    override suspend fun clearAllCards() = wrap { db.clearAllCards() }
    override fun observeAllCards(): Flow<List<Card>> = db.getAllCards()
    override fun observeCountByCategory(category: Int): Flow<Int> = db.getUnusedCountByCategory(category)
}
