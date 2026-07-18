package com.example.core.repository.impl

import com.example.core.model.Resource
import com.example.core.repository.ApprovalsRepository
import com.example.core.repository.DashboardRepository
import com.example.core.repository.DistributorRepository
import com.example.core.repository.NetworkRepository
import com.example.core.repository.ReportsRepository
import com.example.core.repository.WalletRepository
import com.example.database.CardRepository
import com.example.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

// ─────────────────────────────────────────────────────────────────────
// Helper — Wraps any suspend call into Resource
// ─────────────────────────────────────────────────────────────────────
internal suspend fun <T> wrap(block: suspend () -> T): Resource<Unit> {
    return try { block(); Resource.Success(Unit) }
    catch (e: Exception) { Resource.Error(e) }
}

// ─────────────────────────────────────────────────────────────────────
// WalletRepositoryImpl
// ─────────────────────────────────────────────────────────────────────
class WalletRepositoryImpl(private val db: CardRepository) : WalletRepository {
    override suspend fun isJeebEnabled() = db.isJeebEnabled.value
    override suspend fun setJeebEnabled(enabled: Boolean) = db.setJeebEnabled(enabled)
    override suspend fun isJawaliEnabled() = db.isJawaliEnabled.value
    override suspend fun setJawaliEnabled(enabled: Boolean) = db.setJawaliEnabled(enabled)
    override suspend fun isKuraimiEnabled() = db.isKuraimiEnabled.value
    override suspend fun setKuraimiEnabled(enabled: Boolean) = db.setKuraimiEnabled(enabled)
    override suspend fun isHasebEnabled() = db.isHasebEnabled.value
    override suspend fun setHasebEnabled(enabled: Boolean) = db.setHasebEnabled(enabled)
    override suspend fun isOneCashEnabled() = db.isOneCashEnabled.value
    override suspend fun setOneCashEnabled(enabled: Boolean) = db.setOneCashEnabled(enabled)
    override suspend fun isMFloosEnabled() = db.isMFloosEnabled.value
    override suspend fun setMFloosEnabled(enabled: Boolean) = db.setMFloosEnabled(enabled)
    override suspend fun getApprovedSmsTemplates() = db.approvedSmsTemplates.value
    override suspend fun addApprovedSmsTemplate(template: String) = db.addApprovedSmsTemplate(template)
    override suspend fun removeApprovedSmsTemplate(template: String) = db.removeApprovedSmsTemplate(template)
    override suspend fun getGeneralSmsTemplate() = db.generalSmsTemplate.value
    override suspend fun setGeneralSmsTemplate(template: String) = db.setGeneralSmsTemplate(template)
    override suspend fun getNetworkName() = db.networkName.value
    override suspend fun setNetworkName(name: String) = db.setNetworkName(name)
}

// ─────────────────────────────────────────────────────────────────────
// ReportsRepositoryImpl
// ─────────────────────────────────────────────────────────────────────
class ReportsRepositoryImpl(private val db: CardRepository) : ReportsRepository {
    override suspend fun getTransactionsByDateRange(from: Long, to: Long): List<Transaction> =
        db.getAllTransactions().firstOrNull()
            ?.filter { it.createdAt in from..to } ?: emptyList()

    override suspend fun getTotalRevenue(): Double =
        db.getAllTransactions().firstOrNull()
            ?.sumOf { it.amount.toDouble() } ?: 0.0

    override suspend fun getRevenueByWallet(walletType: String): Double =
        db.getAllTransactions().firstOrNull()
            ?.filter { it.walletType == walletType }
            ?.sumOf { it.amount.toDouble() } ?: 0.0

    override suspend fun getDepositsByDateRange(from: Long, to: Long): List<Deposit> =
        db.getAllDeposits().firstOrNull()
            ?.filter { it.createdAt in from..to } ?: emptyList()

    override suspend fun getMappings(): List<CustomerMapping> =
        db.getAllMappings().firstOrNull() ?: emptyList()

    override suspend fun insertMapping(uniqueId: String, phone: String, name: String, walletType: String) =
        wrap { db.insertMapping(uniqueId, phone, name, walletType) }

    override suspend fun deleteMapping(id: Int) = wrap { db.deleteMapping(id) }
}

// ─────────────────────────────────────────────────────────────────────
// DashboardRepositoryImpl
// ─────────────────────────────────────────────────────────────────────
class DashboardRepositoryImpl(private val db: CardRepository) : DashboardRepository {
    override suspend fun getPendingApprovalsCount() =
        db.getAllPendingApprovals().firstOrNull()?.size ?: 0

    override suspend fun getLowStockCategories(threshold: Int): List<Int> {
        val cats = db.categories.value
        return cats.filter { cat -> db.getUnusedCountByCategoryDirect(cat) <= threshold }
    }

    override suspend fun getTodayTransactionsCount(): Int {
        val dayStart = System.currentTimeMillis() - 86_400_000L
        return db.getAllTransactions().firstOrNull()
            ?.filter { it.createdAt >= dayStart }?.size ?: 0
    }

    override suspend fun getTodayRevenue(): Double {
        val dayStart = System.currentTimeMillis() - 86_400_000L
        return db.getAllTransactions().firstOrNull()
            ?.filter { it.createdAt >= dayStart }
            ?.sumOf { it.amount.toDouble() } ?: 0.0
    }

    override fun observePendingApprovals(): Flow<List<PendingApproval>> = db.getAllPendingApprovals()
}

// ─────────────────────────────────────────────────────────────────────
// ApprovalsRepositoryImpl
// ─────────────────────────────────────────────────────────────────────
class ApprovalsRepositoryImpl(private val db: CardRepository) : ApprovalsRepository {
    override suspend fun getAllPendingApprovals() = db.getAllPendingApprovals().firstOrNull() ?: emptyList()
    override suspend fun getPendingApproval(id: Int) = db.getPendingApproval(id)
    override suspend fun insertPendingApproval(phone: String, amount: Int, walletType: String, isAccountCode: Boolean, depositId: Int): Resource<Long> {
        val id = db.insertPendingApproval(phone, amount, walletType, isAccountCode, depositId)
        return Resource.Success(id)
    }
    override suspend fun deletePendingApproval(id: Int) = wrap { db.deletePendingApproval(id) }
    override suspend fun updatePendingPhone(id: Int, newPhone: String) = wrap { db.updatePendingApprovalPhone(id, newPhone) }
    override fun observePendingApprovals(): Flow<List<PendingApproval>> = db.getAllPendingApprovals()
}

// ─────────────────────────────────────────────────────────────────────
// NetworkRepositoryImpl (Mikrotik/NAS cards)
// ─────────────────────────────────────────────────────────────────────
class NetworkRepositoryImpl(private val db: CardRepository) : NetworkRepository {
    override suspend fun getAllGeneratedCards() = db.getAllGeneratedCards().firstOrNull() ?: emptyList()
    override suspend fun insertGeneratedCard(card: GeneratedMikrotikCard) = wrap { db.insertGeneratedCard(card) }
    override suspend fun insertGeneratedCards(cards: List<GeneratedMikrotikCard>) = wrap { db.insertGeneratedCards(cards) }
    override suspend fun markCardAsPrinted(id: Int, printed: Boolean) = wrap { db.markGeneratedCardAsPrinted(id, printed) }
    override suspend fun transferCardToAutoSales(id: Int, category: Int, pin: String, username: String, password: String) =
        wrap { db.transferGeneratedCardToAutoSales(id, category, pin, username, password) }
    override suspend fun deleteGeneratedCard(id: Int) = wrap { db.deleteGeneratedCard(id) }
    override suspend fun clearAllGeneratedCards() = wrap { db.clearAllGeneratedCards() }
    override fun observeGeneratedCards(): Flow<List<GeneratedMikrotikCard>> = db.getAllGeneratedCards()
}

// ─────────────────────────────────────────────────────────────────────
// DistributorRepositoryImpl
// ─────────────────────────────────────────────────────────────────────
class DistributorRepositoryImpl(private val db: CardRepository) : DistributorRepository {
    override suspend fun getCustomers() = db.getDistributorCustomers().firstOrNull() ?: emptyList()
    override suspend fun getCustomerById(id: String) = db.getDistributorCustomerByIdDirect(id)
    override suspend fun insertCustomer(customer: DistributorCustomer) = wrap { db.insertDistributorCustomer(customer) }
    override suspend fun deleteCustomer(id: String) = wrap { db.deleteDistributorCustomer(id) }
    override suspend fun getTransactions() = db.getDistributorTransactions().firstOrNull() ?: emptyList()
    override suspend fun insertTransaction(tx: DistributorTransaction) = wrap { db.insertDistributorTransaction(tx) }
    override suspend fun deleteTransaction(id: String, customerId: String) = wrap { db.deleteDistributorTransaction(id, customerId) }
    override suspend fun getExpenses() = db.getDistributorExpenses().firstOrNull() ?: emptyList()
    override suspend fun insertExpense(expense: DistributorExpense) = wrap { db.insertDistributorExpense(expense) }
    override suspend fun deleteExpense(id: String) = wrap { db.deleteDistributorExpense(id) }
    override suspend fun getCapitals() = db.getDistributorCapitals().firstOrNull() ?: emptyList()
    override suspend fun insertCapital(capital: DistributorCapital) = wrap { db.insertDistributorCapital(capital) }
    override suspend fun deleteCapital(id: String) = wrap { db.deleteDistributorCapital(id) }
    override suspend fun clearAllData() = wrap { db.clearAllDistributorData() }
    override fun observeCustomers(): Flow<List<DistributorCustomer>> = db.getDistributorCustomers()
    override fun observeTransactions(): Flow<List<DistributorTransaction>> = db.getDistributorTransactions()
    override fun observeExpenses(): Flow<List<DistributorExpense>> = db.getDistributorExpenses()
    override fun observeCapitals(): Flow<List<DistributorCapital>> = db.getDistributorCapitals()
}
