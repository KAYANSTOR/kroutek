package com.example.database;

import androidx.annotation.NonNull;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenDelegate;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.SQLite;
import androidx.sqlite.SQLiteConnection;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile CardDao _cardDao;

  private volatile TransactionDao _transactionDao;

  private volatile PendingApprovalDao _pendingApprovalDao;

  private volatile DepositDao _depositDao;

  private volatile CustomerMappingDao _customerMappingDao;

  private volatile GeneratedMikrotikCardDao _generatedMikrotikCardDao;

  private volatile DistributorDao _distributorDao;

  @Override
  @NonNull
  protected RoomOpenDelegate createOpenDelegate() {
    final RoomOpenDelegate _openDelegate = new RoomOpenDelegate(7, "2ee5f2fed15f21906cbaada3b939046f", "2053ba9e398fcd211f0453598f01d69d") {
      @Override
      public void createAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `cards` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `category` INTEGER NOT NULL, `code` TEXT NOT NULL, `username` TEXT NOT NULL, `password` TEXT NOT NULL, `used` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_cards_category_used` ON `cards` (`category`, `used`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `transactions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `phone` TEXT NOT NULL, `amount` INTEGER NOT NULL, `cardCode` TEXT NOT NULL, `walletType` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_transactions_createdAt` ON `transactions` (`createdAt`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `pending_approvals` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `phone` TEXT NOT NULL, `amount` INTEGER NOT NULL, `walletType` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `isAccountCode` INTEGER NOT NULL, `depositId` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `deposits` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `phone` TEXT NOT NULL, `amount` INTEGER NOT NULL, `walletType` TEXT NOT NULL, `isShared` INTEGER NOT NULL, `cardDetails` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `customer_mappings` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `customerUniqueId` TEXT NOT NULL, `basicPhone` TEXT NOT NULL, `customerName` TEXT NOT NULL, `walletType` TEXT NOT NULL)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `generated_mikrotik_cards` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `category` INTEGER NOT NULL, `pin` TEXT NOT NULL, `username` TEXT NOT NULL, `password` TEXT NOT NULL, `printed` INTEGER NOT NULL, `transferred` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `distributor_customers` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `totalSales` REAL NOT NULL, `totalPayments` REAL NOT NULL, `currentBalance` REAL NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `distributor_transactions` (`id` TEXT NOT NULL, `customerId` TEXT NOT NULL, `date` INTEGER NOT NULL, `type` TEXT NOT NULL, `amount` REAL NOT NULL, `notes` TEXT NOT NULL, PRIMARY KEY(`id`))");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `distributor_expenses` (`id` TEXT NOT NULL, `category` TEXT NOT NULL, `amount` REAL NOT NULL, `description` TEXT NOT NULL, `date` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `distributor_capitals` (`id` TEXT NOT NULL, `type` TEXT NOT NULL, `amount` REAL NOT NULL, `description` TEXT NOT NULL, `date` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        SQLite.execSQL(connection, "INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '2ee5f2fed15f21906cbaada3b939046f')");
      }

      @Override
      public void dropAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `cards`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `transactions`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `pending_approvals`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `deposits`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `customer_mappings`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `generated_mikrotik_cards`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `distributor_customers`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `distributor_transactions`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `distributor_expenses`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `distributor_capitals`");
      }

      @Override
      public void onCreate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      public void onOpen(@NonNull final SQLiteConnection connection) {
        internalInitInvalidationTracker(connection);
      }

      @Override
      public void onPreMigrate(@NonNull final SQLiteConnection connection) {
        DBUtil.dropFtsSyncTriggers(connection);
      }

      @Override
      public void onPostMigrate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      @NonNull
      public RoomOpenDelegate.ValidationResult onValidateSchema(
          @NonNull final SQLiteConnection connection) {
        final Map<String, TableInfo.Column> _columnsCards = new HashMap<String, TableInfo.Column>(6);
        _columnsCards.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCards.put("category", new TableInfo.Column("category", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCards.put("code", new TableInfo.Column("code", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCards.put("username", new TableInfo.Column("username", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCards.put("password", new TableInfo.Column("password", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCards.put("used", new TableInfo.Column("used", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysCards = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesCards = new HashSet<TableInfo.Index>(1);
        _indicesCards.add(new TableInfo.Index("index_cards_category_used", false, Arrays.asList("category", "used"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoCards = new TableInfo("cards", _columnsCards, _foreignKeysCards, _indicesCards);
        final TableInfo _existingCards = TableInfo.read(connection, "cards");
        if (!_infoCards.equals(_existingCards)) {
          return new RoomOpenDelegate.ValidationResult(false, "cards(com.example.models.Card).\n"
                  + " Expected:\n" + _infoCards + "\n"
                  + " Found:\n" + _existingCards);
        }
        final Map<String, TableInfo.Column> _columnsTransactions = new HashMap<String, TableInfo.Column>(6);
        _columnsTransactions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("phone", new TableInfo.Column("phone", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("amount", new TableInfo.Column("amount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("cardCode", new TableInfo.Column("cardCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("walletType", new TableInfo.Column("walletType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysTransactions = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesTransactions = new HashSet<TableInfo.Index>(1);
        _indicesTransactions.add(new TableInfo.Index("index_transactions_createdAt", false, Arrays.asList("createdAt"), Arrays.asList("ASC")));
        final TableInfo _infoTransactions = new TableInfo("transactions", _columnsTransactions, _foreignKeysTransactions, _indicesTransactions);
        final TableInfo _existingTransactions = TableInfo.read(connection, "transactions");
        if (!_infoTransactions.equals(_existingTransactions)) {
          return new RoomOpenDelegate.ValidationResult(false, "transactions(com.example.models.Transaction).\n"
                  + " Expected:\n" + _infoTransactions + "\n"
                  + " Found:\n" + _existingTransactions);
        }
        final Map<String, TableInfo.Column> _columnsPendingApprovals = new HashMap<String, TableInfo.Column>(7);
        _columnsPendingApprovals.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingApprovals.put("phone", new TableInfo.Column("phone", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingApprovals.put("amount", new TableInfo.Column("amount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingApprovals.put("walletType", new TableInfo.Column("walletType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingApprovals.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingApprovals.put("isAccountCode", new TableInfo.Column("isAccountCode", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingApprovals.put("depositId", new TableInfo.Column("depositId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysPendingApprovals = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesPendingApprovals = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPendingApprovals = new TableInfo("pending_approvals", _columnsPendingApprovals, _foreignKeysPendingApprovals, _indicesPendingApprovals);
        final TableInfo _existingPendingApprovals = TableInfo.read(connection, "pending_approvals");
        if (!_infoPendingApprovals.equals(_existingPendingApprovals)) {
          return new RoomOpenDelegate.ValidationResult(false, "pending_approvals(com.example.models.PendingApproval).\n"
                  + " Expected:\n" + _infoPendingApprovals + "\n"
                  + " Found:\n" + _existingPendingApprovals);
        }
        final Map<String, TableInfo.Column> _columnsDeposits = new HashMap<String, TableInfo.Column>(7);
        _columnsDeposits.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDeposits.put("phone", new TableInfo.Column("phone", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDeposits.put("amount", new TableInfo.Column("amount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDeposits.put("walletType", new TableInfo.Column("walletType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDeposits.put("isShared", new TableInfo.Column("isShared", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDeposits.put("cardDetails", new TableInfo.Column("cardDetails", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDeposits.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysDeposits = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesDeposits = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDeposits = new TableInfo("deposits", _columnsDeposits, _foreignKeysDeposits, _indicesDeposits);
        final TableInfo _existingDeposits = TableInfo.read(connection, "deposits");
        if (!_infoDeposits.equals(_existingDeposits)) {
          return new RoomOpenDelegate.ValidationResult(false, "deposits(com.example.models.Deposit).\n"
                  + " Expected:\n" + _infoDeposits + "\n"
                  + " Found:\n" + _existingDeposits);
        }
        final Map<String, TableInfo.Column> _columnsCustomerMappings = new HashMap<String, TableInfo.Column>(5);
        _columnsCustomerMappings.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomerMappings.put("customerUniqueId", new TableInfo.Column("customerUniqueId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomerMappings.put("basicPhone", new TableInfo.Column("basicPhone", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomerMappings.put("customerName", new TableInfo.Column("customerName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomerMappings.put("walletType", new TableInfo.Column("walletType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysCustomerMappings = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesCustomerMappings = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCustomerMappings = new TableInfo("customer_mappings", _columnsCustomerMappings, _foreignKeysCustomerMappings, _indicesCustomerMappings);
        final TableInfo _existingCustomerMappings = TableInfo.read(connection, "customer_mappings");
        if (!_infoCustomerMappings.equals(_existingCustomerMappings)) {
          return new RoomOpenDelegate.ValidationResult(false, "customer_mappings(com.example.models.CustomerMapping).\n"
                  + " Expected:\n" + _infoCustomerMappings + "\n"
                  + " Found:\n" + _existingCustomerMappings);
        }
        final Map<String, TableInfo.Column> _columnsGeneratedMikrotikCards = new HashMap<String, TableInfo.Column>(8);
        _columnsGeneratedMikrotikCards.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeneratedMikrotikCards.put("category", new TableInfo.Column("category", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeneratedMikrotikCards.put("pin", new TableInfo.Column("pin", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeneratedMikrotikCards.put("username", new TableInfo.Column("username", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeneratedMikrotikCards.put("password", new TableInfo.Column("password", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeneratedMikrotikCards.put("printed", new TableInfo.Column("printed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeneratedMikrotikCards.put("transferred", new TableInfo.Column("transferred", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeneratedMikrotikCards.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysGeneratedMikrotikCards = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesGeneratedMikrotikCards = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoGeneratedMikrotikCards = new TableInfo("generated_mikrotik_cards", _columnsGeneratedMikrotikCards, _foreignKeysGeneratedMikrotikCards, _indicesGeneratedMikrotikCards);
        final TableInfo _existingGeneratedMikrotikCards = TableInfo.read(connection, "generated_mikrotik_cards");
        if (!_infoGeneratedMikrotikCards.equals(_existingGeneratedMikrotikCards)) {
          return new RoomOpenDelegate.ValidationResult(false, "generated_mikrotik_cards(com.example.models.GeneratedMikrotikCard).\n"
                  + " Expected:\n" + _infoGeneratedMikrotikCards + "\n"
                  + " Found:\n" + _existingGeneratedMikrotikCards);
        }
        final Map<String, TableInfo.Column> _columnsDistributorCustomers = new HashMap<String, TableInfo.Column>(6);
        _columnsDistributorCustomers.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributorCustomers.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributorCustomers.put("totalSales", new TableInfo.Column("totalSales", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributorCustomers.put("totalPayments", new TableInfo.Column("totalPayments", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributorCustomers.put("currentBalance", new TableInfo.Column("currentBalance", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributorCustomers.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysDistributorCustomers = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesDistributorCustomers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDistributorCustomers = new TableInfo("distributor_customers", _columnsDistributorCustomers, _foreignKeysDistributorCustomers, _indicesDistributorCustomers);
        final TableInfo _existingDistributorCustomers = TableInfo.read(connection, "distributor_customers");
        if (!_infoDistributorCustomers.equals(_existingDistributorCustomers)) {
          return new RoomOpenDelegate.ValidationResult(false, "distributor_customers(com.example.models.DistributorCustomer).\n"
                  + " Expected:\n" + _infoDistributorCustomers + "\n"
                  + " Found:\n" + _existingDistributorCustomers);
        }
        final Map<String, TableInfo.Column> _columnsDistributorTransactions = new HashMap<String, TableInfo.Column>(6);
        _columnsDistributorTransactions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributorTransactions.put("customerId", new TableInfo.Column("customerId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributorTransactions.put("date", new TableInfo.Column("date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributorTransactions.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributorTransactions.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributorTransactions.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysDistributorTransactions = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesDistributorTransactions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDistributorTransactions = new TableInfo("distributor_transactions", _columnsDistributorTransactions, _foreignKeysDistributorTransactions, _indicesDistributorTransactions);
        final TableInfo _existingDistributorTransactions = TableInfo.read(connection, "distributor_transactions");
        if (!_infoDistributorTransactions.equals(_existingDistributorTransactions)) {
          return new RoomOpenDelegate.ValidationResult(false, "distributor_transactions(com.example.models.DistributorTransaction).\n"
                  + " Expected:\n" + _infoDistributorTransactions + "\n"
                  + " Found:\n" + _existingDistributorTransactions);
        }
        final Map<String, TableInfo.Column> _columnsDistributorExpenses = new HashMap<String, TableInfo.Column>(5);
        _columnsDistributorExpenses.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributorExpenses.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributorExpenses.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributorExpenses.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributorExpenses.put("date", new TableInfo.Column("date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysDistributorExpenses = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesDistributorExpenses = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDistributorExpenses = new TableInfo("distributor_expenses", _columnsDistributorExpenses, _foreignKeysDistributorExpenses, _indicesDistributorExpenses);
        final TableInfo _existingDistributorExpenses = TableInfo.read(connection, "distributor_expenses");
        if (!_infoDistributorExpenses.equals(_existingDistributorExpenses)) {
          return new RoomOpenDelegate.ValidationResult(false, "distributor_expenses(com.example.models.DistributorExpense).\n"
                  + " Expected:\n" + _infoDistributorExpenses + "\n"
                  + " Found:\n" + _existingDistributorExpenses);
        }
        final Map<String, TableInfo.Column> _columnsDistributorCapitals = new HashMap<String, TableInfo.Column>(5);
        _columnsDistributorCapitals.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributorCapitals.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributorCapitals.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributorCapitals.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributorCapitals.put("date", new TableInfo.Column("date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysDistributorCapitals = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesDistributorCapitals = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDistributorCapitals = new TableInfo("distributor_capitals", _columnsDistributorCapitals, _foreignKeysDistributorCapitals, _indicesDistributorCapitals);
        final TableInfo _existingDistributorCapitals = TableInfo.read(connection, "distributor_capitals");
        if (!_infoDistributorCapitals.equals(_existingDistributorCapitals)) {
          return new RoomOpenDelegate.ValidationResult(false, "distributor_capitals(com.example.models.DistributorCapital).\n"
                  + " Expected:\n" + _infoDistributorCapitals + "\n"
                  + " Found:\n" + _existingDistributorCapitals);
        }
        return new RoomOpenDelegate.ValidationResult(true, null);
      }
    };
    return _openDelegate;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final Map<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final Map<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "cards", "transactions", "pending_approvals", "deposits", "customer_mappings", "generated_mikrotik_cards", "distributor_customers", "distributor_transactions", "distributor_expenses", "distributor_capitals");
  }

  @Override
  public void clearAllTables() {
    super.performClear(false, "cards", "transactions", "pending_approvals", "deposits", "customer_mappings", "generated_mikrotik_cards", "distributor_customers", "distributor_transactions", "distributor_expenses", "distributor_capitals");
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final Map<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(CardDao.class, CardDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TransactionDao.class, TransactionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PendingApprovalDao.class, PendingApprovalDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DepositDao.class, DepositDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CustomerMappingDao.class, CustomerMappingDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(GeneratedMikrotikCardDao.class, GeneratedMikrotikCardDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DistributorDao.class, DistributorDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final Set<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public CardDao cardDao() {
    if (_cardDao != null) {
      return _cardDao;
    } else {
      synchronized(this) {
        if(_cardDao == null) {
          _cardDao = new CardDao_Impl(this);
        }
        return _cardDao;
      }
    }
  }

  @Override
  public TransactionDao transactionDao() {
    if (_transactionDao != null) {
      return _transactionDao;
    } else {
      synchronized(this) {
        if(_transactionDao == null) {
          _transactionDao = new TransactionDao_Impl(this);
        }
        return _transactionDao;
      }
    }
  }

  @Override
  public PendingApprovalDao pendingApprovalDao() {
    if (_pendingApprovalDao != null) {
      return _pendingApprovalDao;
    } else {
      synchronized(this) {
        if(_pendingApprovalDao == null) {
          _pendingApprovalDao = new PendingApprovalDao_Impl(this);
        }
        return _pendingApprovalDao;
      }
    }
  }

  @Override
  public DepositDao depositDao() {
    if (_depositDao != null) {
      return _depositDao;
    } else {
      synchronized(this) {
        if(_depositDao == null) {
          _depositDao = new DepositDao_Impl(this);
        }
        return _depositDao;
      }
    }
  }

  @Override
  public CustomerMappingDao customerMappingDao() {
    if (_customerMappingDao != null) {
      return _customerMappingDao;
    } else {
      synchronized(this) {
        if(_customerMappingDao == null) {
          _customerMappingDao = new CustomerMappingDao_Impl(this);
        }
        return _customerMappingDao;
      }
    }
  }

  @Override
  public GeneratedMikrotikCardDao generatedMikrotikCardDao() {
    if (_generatedMikrotikCardDao != null) {
      return _generatedMikrotikCardDao;
    } else {
      synchronized(this) {
        if(_generatedMikrotikCardDao == null) {
          _generatedMikrotikCardDao = new GeneratedMikrotikCardDao_Impl(this);
        }
        return _generatedMikrotikCardDao;
      }
    }
  }

  @Override
  public DistributorDao distributorDao() {
    if (_distributorDao != null) {
      return _distributorDao;
    } else {
      synchronized(this) {
        if(_distributorDao == null) {
          _distributorDao = new DistributorDao_Impl(this);
        }
        return _distributorDao;
      }
    }
  }
}
