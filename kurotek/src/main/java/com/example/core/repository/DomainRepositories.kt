package com.example.core.repository

import com.example.core.model.Resource
import com.example.models.Card
import com.example.models.CustomerMapping
import com.example.models.Deposit
import com.example.models.PendingApproval
import com.example.models.Transaction

/**
 * InventoryRepository — إدارة المخزون (الكروت والفئات)
 */
interface InventoryRepository {
    suspend fun insertCard(card: Card): Resource<Unit>
    suspend fun insertCardsBulk(category: Int, codesBlock: String): Resource<Int>
    suspend fun insertCardsList(cards: List<Card>): Resource<Int>
    suspend fun deleteCard(cardId: String): Resource<Unit>
    suspend fun markCardAsUsed(cardId: String): Resource<Unit>
    suspend fun getUnusedCardByCategory(category: Int): Card?
    suspend fun claimUnusedCardByCategory(category: Int): Card?
    suspend fun getUnusedCountByCategory(category: Int): Int
    suspend fun getTotalUnusedCount(): Int
    suspend fun getAllCards(): List<Card>
    suspend fun clearAllCards(): Resource<Unit>
    fun observeAllCards(): kotlinx.coroutines.flow.Flow<List<Card>>
    fun observeCountByCategory(category: Int): kotlinx.coroutines.flow.Flow<Int>
}

/**
 * SalesRepository — المعاملات (التقارير اليومية، الشحنات)
 */
interface SalesRepository {
    suspend fun insertTransaction(phone: String, amount: Int, cardCode: String, walletType: String): Resource<Unit>
    suspend fun getAllTransactions(): List<Transaction>
    suspend fun clearAllTransactions(): Resource<Unit>
    suspend fun insertDeposit(phone: String, amount: Int, walletType: String, isShared: Boolean, cardDetails: String): Resource<Unit>
    suspend fun getAllDeposits(): List<Deposit>
    suspend fun clearAllDeposits(): Resource<Unit>
    suspend fun updateDepositSharing(depositId: String, isShared: Boolean, cardDetails: String): Resource<Unit>
    fun observeTransactions(): kotlinx.coroutines.flow.Flow<List<Transaction>>
    fun observeDeposits(): kotlinx.coroutines.flow.Flow<List<Deposit>>
}

/**
 * WalletRepository — إعدادات وتفعيل المحافظ الإلكترونية
 */
interface WalletRepository {
    suspend fun isJeebEnabled(): Boolean
    suspend fun setJeebEnabled(enabled: Boolean)
    suspend fun isJawaliEnabled(): Boolean
    suspend fun setJawaliEnabled(enabled: Boolean)
    suspend fun isKuraimiEnabled(): Boolean
    suspend fun setKuraimiEnabled(enabled: Boolean)
    suspend fun isHasebEnabled(): Boolean
    suspend fun setHasebEnabled(enabled: Boolean)
    suspend fun isOneCashEnabled(): Boolean
    suspend fun setOneCashEnabled(enabled: Boolean)
    suspend fun isMFloosEnabled(): Boolean
    suspend fun setMFloosEnabled(enabled: Boolean)
    suspend fun getApprovedSmsTemplates(): List<String>
    suspend fun addApprovedSmsTemplate(template: String)
    suspend fun removeApprovedSmsTemplate(template: String)
    suspend fun getGeneralSmsTemplate(): String
    suspend fun setGeneralSmsTemplate(template: String)
    suspend fun getNetworkName(): String
    suspend fun setNetworkName(name: String)
}

/**
 * ReportsRepository — تقارير الإيرادات والمبيعات
 */
interface ReportsRepository {
    suspend fun getTransactionsByDateRange(from: Long, to: Long): List<Transaction>
    suspend fun getTotalRevenue(): Double
    suspend fun getRevenueByWallet(walletType: String): Double
    suspend fun getDepositsByDateRange(from: Long, to: Long): List<Deposit>
    suspend fun getMappings(): List<CustomerMapping>
    suspend fun insertMapping(uniqueId: String, phone: String, name: String, walletType: String): Resource<Unit>
    suspend fun deleteMapping(id: String): Resource<Unit>
}

/**
 * DashboardRepository — بيانات ملخص اللوحة الرئيسية
 */
interface DashboardRepository {
    suspend fun getPendingApprovalsCount(): Int
    suspend fun getLowStockCategories(threshold: Int = 5): List<Int>
    suspend fun getTodayTransactionsCount(): Int
    suspend fun getTodayRevenue(): Double
    fun observePendingApprovals(): kotlinx.coroutines.flow.Flow<List<PendingApproval>>
}

/**
 * ApprovalsRepository — الموافقات المعلقة
 */
interface ApprovalsRepository {
    suspend fun getAllPendingApprovals(): List<PendingApproval>
    suspend fun getPendingApproval(id: String): PendingApproval?
    suspend fun insertPendingApproval(phone: String, amount: Int, walletType: String, isAccountCode: Boolean, depositId: String): Resource<Unit>
    suspend fun deletePendingApproval(id: String): Resource<Unit>
    suspend fun updatePendingPhone(id: String, newPhone: String): Resource<Unit>
    fun observePendingApprovals(): kotlinx.coroutines.flow.Flow<List<PendingApproval>>
}

/**
 * NetworkRepository — إعدادات الشبكة المحلية (Mikrotik/NAS)
 */
interface NetworkRepository {
    suspend fun getAllGeneratedCards(): List<com.example.models.GeneratedMikrotikCard>
    suspend fun insertGeneratedCard(card: com.example.models.GeneratedMikrotikCard): Resource<Unit>
    suspend fun insertGeneratedCards(cards: List<com.example.models.GeneratedMikrotikCard>): Resource<Unit>
    suspend fun markCardAsPrinted(id: String, printed: Boolean): Resource<Unit>
    suspend fun transferCardToAutoSales(id: String, category: Int, pin: String, username: String, password: String): Resource<Unit>
    suspend fun deleteGeneratedCard(id: String): Resource<Unit>
    suspend fun clearAllGeneratedCards(): Resource<Unit>
    fun observeGeneratedCards(): kotlinx.coroutines.flow.Flow<List<com.example.models.GeneratedMikrotikCard>>
}

/**
 * DistributorRepository — نظام الموزع (عملاء، مبيعات، مصروفات، رأس مال)
 */
interface DistributorRepository {
    suspend fun getCustomers(): List<com.example.models.DistributorCustomer>
    suspend fun getCustomerById(id: String): com.example.models.DistributorCustomer?
    suspend fun insertCustomer(customer: com.example.models.DistributorCustomer): Resource<Unit>
    suspend fun deleteCustomer(id: String): Resource<Unit>

    suspend fun getTransactions(): List<com.example.models.DistributorTransaction>
    suspend fun insertTransaction(tx: com.example.models.DistributorTransaction): Resource<Unit>
    suspend fun deleteTransaction(id: String, customerId: String): Resource<Unit>

    suspend fun getExpenses(): List<com.example.models.DistributorExpense>
    suspend fun insertExpense(expense: com.example.models.DistributorExpense): Resource<Unit>
    suspend fun deleteExpense(id: String): Resource<Unit>

    suspend fun getCapitals(): List<com.example.models.DistributorCapital>
    suspend fun insertCapital(capital: com.example.models.DistributorCapital): Resource<Unit>
    suspend fun deleteCapital(id: String): Resource<Unit>

    suspend fun clearAllData(): Resource<Unit>

    fun observeCustomers(): kotlinx.coroutines.flow.Flow<List<com.example.models.DistributorCustomer>>
    fun observeTransactions(): kotlinx.coroutines.flow.Flow<List<com.example.models.DistributorTransaction>>
    fun observeExpenses(): kotlinx.coroutines.flow.Flow<List<com.example.models.DistributorExpense>>
    fun observeCapitals(): kotlinx.coroutines.flow.Flow<List<com.example.models.DistributorCapital>>
}
