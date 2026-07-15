package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow
import com.example.models.Card
import com.example.models.Transaction
import com.example.models.PendingApproval
import com.example.models.Deposit
import com.example.models.CustomerMapping
import com.example.models.GeneratedMikrotikCard
import com.example.models.DistributorCustomer
import com.example.models.DistributorTransaction
import com.example.models.DistributorExpense
import com.example.models.DistributorCapital

@Dao
interface CustomerMappingDao {
    @Query("SELECT * FROM customer_mappings ORDER BY id DESC")
    fun getAllMappings(): Flow<List<CustomerMapping>>

    @Query("SELECT * FROM customer_mappings WHERE customerUniqueId = :uniqueId LIMIT 1")
    suspend fun getMappingByUniqueId(uniqueId: String): CustomerMapping?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMapping(mapping: CustomerMapping): Long

    @Query("DELETE FROM customer_mappings WHERE id = :id")
    suspend fun deleteMapping(id: Int)

    @Query("DELETE FROM customer_mappings")
    suspend fun deleteAllMappings()
}

@Dao
interface CardDao {
    @Query("SELECT * FROM cards WHERE category = :category AND used = 0 LIMIT 1")
    suspend fun getUnusedCardByCategory(category: Int): Card?

    @Query("UPDATE cards SET used = 1 WHERE id = :id")
    suspend fun markCardAsUsed(id: Int)

    // ⚠️ عملية ذرّية (Room @Transaction): تسحب كرتاً غير مستخدم وتعلّمه "مستخدم"
    // ضمن معاملة قاعدة بيانات واحدة غير قابلة للمقاطعة. تمنع هذا سيناريو
    // سحب نفس الكرت لعميلين مختلفين لو وصل إيداعان لنفس الفئة في نفس اللحظة
    // (كان هذا ممكناً سابقاً عبر استدعاء getUnusedCardByCategory ثم
    // markCardAsUsed كخطوتين منفصلتين قابلتين للتداخل).
    @androidx.room.Transaction
    suspend fun claimUnusedCardByCategory(category: Int): Card? {
        val card = getUnusedCardByCategory(category) ?: return null
        markCardAsUsed(card.id)
        return card
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: Card)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<Card>)

    @Query("SELECT COUNT(*) FROM cards WHERE category = :category AND used = 0")
    fun getUnusedCountByCategory(category: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM cards WHERE used = 0")
    fun getUnusedCardsCount(): Flow<Int>

    @Query("SELECT * FROM cards ORDER BY id DESC")
    fun getAllCards(): Flow<List<Card>>

    @Query("DELETE FROM cards WHERE id = :id")
    suspend fun deleteCard(id: Int)

    @Query("DELETE FROM cards WHERE category = :category")
    suspend fun deleteCardsByCategory(category: Int)

    @Query("DELETE FROM cards")
    suspend fun deleteAllCards()
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
}

@Dao
interface PendingApprovalDao {
    @Query("SELECT * FROM pending_approvals ORDER BY createdAt DESC")
    fun getAllPendingApprovals(): Flow<List<PendingApproval>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingApproval(pending: PendingApproval): Long

    @Query("SELECT * FROM pending_approvals WHERE id = :id")
    suspend fun getPendingApprovalById(id: Int): PendingApproval?

    @Query("UPDATE pending_approvals SET phone = :phone WHERE id = :id")
    suspend fun updatePendingApprovalPhone(id: Int, phone: String)

    @Query("DELETE FROM pending_approvals WHERE id = :id")
    suspend fun deletePendingApproval(id: Int)

    @Query("DELETE FROM pending_approvals")
    suspend fun deleteAllPendingApprovals()
}

@Dao
interface DepositDao {
    @Query("SELECT * FROM deposits ORDER BY createdAt DESC")
    fun getAllDeposits(): Flow<List<Deposit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeposit(deposit: Deposit): Long

    @Query("UPDATE deposits SET isShared = :isShared, cardDetails = :cardDetails WHERE id = :id")
    suspend fun updateDepositSharing(id: Int, isShared: Boolean, cardDetails: String)

    @Query("DELETE FROM deposits")
    suspend fun deleteAllDeposits()
}

@Dao
interface GeneratedMikrotikCardDao {
    @Query("SELECT * FROM generated_mikrotik_cards ORDER BY createdAt DESC")
    fun getAllGeneratedCards(): Flow<List<GeneratedMikrotikCard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneratedCard(card: GeneratedMikrotikCard): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneratedCards(cards: List<GeneratedMikrotikCard>)

    @Query("UPDATE generated_mikrotik_cards SET printed = :printed WHERE id = :id")
    suspend fun markAsPrinted(id: Int, printed: Boolean)

    @Query("UPDATE generated_mikrotik_cards SET transferred = 1 WHERE id = :id")
    suspend fun markAsTransferred(id: Int)

    @Query("DELETE FROM generated_mikrotik_cards WHERE id = :id")
    suspend fun deleteGeneratedCard(id: Int)

    @Query("DELETE FROM generated_mikrotik_cards")
    suspend fun deleteAllGeneratedCards()
}

@Dao
interface DistributorDao {
    // Customers
    @Query("SELECT * FROM distributor_customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<DistributorCustomer>>

    @Query("SELECT * FROM distributor_customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerById(id: String): DistributorCustomer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: DistributorCustomer): Long

    @Query("DELETE FROM distributor_customers WHERE id = :id")
    suspend fun deleteCustomer(id: String)

    @Query("UPDATE distributor_customers SET totalSales = :sales, totalPayments = :payments, currentBalance = :balance WHERE id = :id")
    suspend fun updateCustomerBalance(id: String, sales: Double, payments: Double, balance: Double)

    // Transactions
    @Query("SELECT * FROM distributor_transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<DistributorTransaction>>

    @Query("SELECT * FROM distributor_transactions WHERE customerId = :customerId ORDER BY date DESC")
    fun getTransactionsByCustomer(customerId: String): Flow<List<DistributorTransaction>>

    @Query("SELECT * FROM distributor_transactions WHERE customerId = :customerId")
    suspend fun getTransactionsByCustomerSync(customerId: String): List<DistributorTransaction>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: DistributorTransaction): Long

    @Query("DELETE FROM distributor_transactions WHERE id = :id")
    suspend fun deleteTransaction(id: String)

    @Query("DELETE FROM distributor_transactions WHERE customerId = :customerId")
    suspend fun deleteTransactionsByCustomer(customerId: String)

    // Expenses
    @Query("SELECT * FROM distributor_expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<DistributorExpense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: DistributorExpense): Long

    @Query("DELETE FROM distributor_expenses WHERE id = :id")
    suspend fun deleteExpense(id: String)

    // Capitals
    @Query("SELECT * FROM distributor_capitals ORDER BY date DESC")
    fun getAllCapitals(): Flow<List<DistributorCapital>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCapital(capital: DistributorCapital): Long

    @Query("DELETE FROM distributor_capitals WHERE id = :id")
    suspend fun deleteCapital(id: String)

    // Nuke operations for isolated DB reset
    @Query("DELETE FROM distributor_customers")
    suspend fun clearAllCustomers()

    @Query("DELETE FROM distributor_transactions")
    suspend fun clearAllTransactions()

    @Query("DELETE FROM distributor_expenses")
    suspend fun clearAllExpenses()

    @Query("DELETE FROM distributor_capitals")
    suspend fun clearAllCapitals()
}

@Database(
    entities = [
        Card::class,
        Transaction::class,
        PendingApproval::class,
        Deposit::class,
        CustomerMapping::class,
        GeneratedMikrotikCard::class,
        DistributorCustomer::class,
        DistributorTransaction::class,
        DistributorExpense::class,
        DistributorCapital::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
    abstract fun transactionDao(): TransactionDao
    abstract fun pendingApprovalDao(): PendingApprovalDao
    abstract fun depositDao(): DepositDao
    abstract fun customerMappingDao(): CustomerMappingDao
    abstract fun generatedMikrotikCardDao(): GeneratedMikrotikCardDao
    abstract fun distributorDao(): DistributorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_cards_category_used` ON `cards` (`category`, `used`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_createdAt` ON `transactions` (`createdAt`)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dahsha_database"
                )
                .addMigrations(MIGRATION_6_7)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
