package com.example.core.repository.impl

import com.example.core.model.Resource
import com.example.core.repository.SalesRepository
import com.example.database.CardRepository
import com.example.models.Deposit
import com.example.models.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class SalesRepositoryImpl(private val db: CardRepository) : SalesRepository {
    override suspend fun insertTransaction(phone: String, amount: Int, cardCode: String, walletType: String) =
        wrap { db.insertTransaction(phone, amount, cardCode, walletType) }

    override suspend fun getAllTransactions(): List<Transaction> =
        db.getAllTransactions().firstOrNull() ?: emptyList()

    override suspend fun clearAllTransactions() = wrap { db.clearAllTransactions() }

    override suspend fun insertDeposit(phone: String, amount: Int, walletType: String, isShared: Boolean, cardDetails: String): Resource<Long> {
        val id = db.insertDeposit(phone, amount, walletType, isShared = isShared, cardDetails = cardDetails)
        return Resource.Success(id)
    }

    override suspend fun getAllDeposits(): List<Deposit> =
        db.getAllDeposits().firstOrNull() ?: emptyList()

    override suspend fun clearAllDeposits() = wrap { db.clearAllDeposits() }

    override suspend fun updateDepositSharing(depositId: Int, isShared: Boolean, cardDetails: String) =
        wrap { db.updateDepositSharing(depositId, isShared = isShared, cardDetails = cardDetails) }

    override fun observeTransactions(): Flow<List<Transaction>> = db.getAllTransactions()
    override fun observeDeposits(): Flow<List<Deposit>> = db.getAllDeposits()
}
