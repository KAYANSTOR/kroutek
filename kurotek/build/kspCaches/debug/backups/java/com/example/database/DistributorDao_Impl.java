package com.example.database;

import androidx.annotation.NonNull;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.coroutines.FlowUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.example.models.DistributorCapital;
import com.example.models.DistributorCustomer;
import com.example.models.DistributorExpense;
import com.example.models.DistributorTransaction;
import java.lang.Class;
import java.lang.Long;
import java.lang.NullPointerException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class DistributorDao_Impl implements DistributorDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<DistributorCustomer> __insertAdapterOfDistributorCustomer;

  private final EntityInsertAdapter<DistributorTransaction> __insertAdapterOfDistributorTransaction;

  private final EntityInsertAdapter<DistributorExpense> __insertAdapterOfDistributorExpense;

  private final EntityInsertAdapter<DistributorCapital> __insertAdapterOfDistributorCapital;

  public DistributorDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfDistributorCustomer = new EntityInsertAdapter<DistributorCustomer>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `distributor_customers` (`id`,`name`,`totalSales`,`totalPayments`,`currentBalance`,`createdAt`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final DistributorCustomer entity) {
        statement.bindText(1, entity.getId());
        statement.bindText(2, entity.getName());
        statement.bindDouble(3, entity.getTotalSales());
        statement.bindDouble(4, entity.getTotalPayments());
        statement.bindDouble(5, entity.getCurrentBalance());
        statement.bindLong(6, entity.getCreatedAt());
      }
    };
    this.__insertAdapterOfDistributorTransaction = new EntityInsertAdapter<DistributorTransaction>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `distributor_transactions` (`id`,`customerId`,`date`,`type`,`amount`,`notes`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final DistributorTransaction entity) {
        statement.bindText(1, entity.getId());
        statement.bindText(2, entity.getCustomerId());
        statement.bindLong(3, entity.getDate());
        statement.bindText(4, entity.getType());
        statement.bindDouble(5, entity.getAmount());
        statement.bindText(6, entity.getNotes());
      }
    };
    this.__insertAdapterOfDistributorExpense = new EntityInsertAdapter<DistributorExpense>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `distributor_expenses` (`id`,`category`,`amount`,`description`,`date`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final DistributorExpense entity) {
        statement.bindText(1, entity.getId());
        statement.bindText(2, entity.getCategory());
        statement.bindDouble(3, entity.getAmount());
        statement.bindText(4, entity.getDescription());
        statement.bindLong(5, entity.getDate());
      }
    };
    this.__insertAdapterOfDistributorCapital = new EntityInsertAdapter<DistributorCapital>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `distributor_capitals` (`id`,`type`,`amount`,`description`,`date`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final DistributorCapital entity) {
        statement.bindText(1, entity.getId());
        statement.bindText(2, entity.getType());
        statement.bindDouble(3, entity.getAmount());
        statement.bindText(4, entity.getDescription());
        statement.bindLong(5, entity.getDate());
      }
    };
  }

  @Override
  public Object insertCustomer(final DistributorCustomer customer,
      final Continuation<? super Long> $completion) {
    if (customer == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfDistributorCustomer.insertAndReturnId(_connection, customer);
    }, $completion);
  }

  @Override
  public Object insertTransaction(final DistributorTransaction transaction,
      final Continuation<? super Long> $completion) {
    if (transaction == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfDistributorTransaction.insertAndReturnId(_connection, transaction);
    }, $completion);
  }

  @Override
  public Object insertExpense(final DistributorExpense expense,
      final Continuation<? super Long> $completion) {
    if (expense == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfDistributorExpense.insertAndReturnId(_connection, expense);
    }, $completion);
  }

  @Override
  public Object insertCapital(final DistributorCapital capital,
      final Continuation<? super Long> $completion) {
    if (capital == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfDistributorCapital.insertAndReturnId(_connection, capital);
    }, $completion);
  }

  @Override
  public Flow<List<DistributorCustomer>> getAllCustomers() {
    final String _sql = "SELECT * FROM distributor_customers ORDER BY name ASC";
    return FlowUtil.createFlow(__db, false, new String[] {"distributor_customers"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfTotalSales = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "totalSales");
        final int _columnIndexOfTotalPayments = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "totalPayments");
        final int _columnIndexOfCurrentBalance = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "currentBalance");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final List<DistributorCustomer> _result = new ArrayList<DistributorCustomer>();
        while (_stmt.step()) {
          final DistributorCustomer _item;
          final String _tmpId;
          _tmpId = _stmt.getText(_columnIndexOfId);
          final String _tmpName;
          _tmpName = _stmt.getText(_columnIndexOfName);
          final double _tmpTotalSales;
          _tmpTotalSales = _stmt.getDouble(_columnIndexOfTotalSales);
          final double _tmpTotalPayments;
          _tmpTotalPayments = _stmt.getDouble(_columnIndexOfTotalPayments);
          final double _tmpCurrentBalance;
          _tmpCurrentBalance = _stmt.getDouble(_columnIndexOfCurrentBalance);
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          _item = new DistributorCustomer(_tmpId,_tmpName,_tmpTotalSales,_tmpTotalPayments,_tmpCurrentBalance,_tmpCreatedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object getCustomerById(final String id,
      final Continuation<? super DistributorCustomer> $completion) {
    final String _sql = "SELECT * FROM distributor_customers WHERE id = ? LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindText(_argIndex, id);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfTotalSales = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "totalSales");
        final int _columnIndexOfTotalPayments = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "totalPayments");
        final int _columnIndexOfCurrentBalance = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "currentBalance");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final DistributorCustomer _result;
        if (_stmt.step()) {
          final String _tmpId;
          _tmpId = _stmt.getText(_columnIndexOfId);
          final String _tmpName;
          _tmpName = _stmt.getText(_columnIndexOfName);
          final double _tmpTotalSales;
          _tmpTotalSales = _stmt.getDouble(_columnIndexOfTotalSales);
          final double _tmpTotalPayments;
          _tmpTotalPayments = _stmt.getDouble(_columnIndexOfTotalPayments);
          final double _tmpCurrentBalance;
          _tmpCurrentBalance = _stmt.getDouble(_columnIndexOfCurrentBalance);
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          _result = new DistributorCustomer(_tmpId,_tmpName,_tmpTotalSales,_tmpTotalPayments,_tmpCurrentBalance,_tmpCreatedAt);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Flow<List<DistributorTransaction>> getAllTransactions() {
    final String _sql = "SELECT * FROM distributor_transactions ORDER BY date DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"distributor_transactions"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCustomerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "customerId");
        final int _columnIndexOfDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "date");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfNotes = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notes");
        final List<DistributorTransaction> _result = new ArrayList<DistributorTransaction>();
        while (_stmt.step()) {
          final DistributorTransaction _item;
          final String _tmpId;
          _tmpId = _stmt.getText(_columnIndexOfId);
          final String _tmpCustomerId;
          _tmpCustomerId = _stmt.getText(_columnIndexOfCustomerId);
          final long _tmpDate;
          _tmpDate = _stmt.getLong(_columnIndexOfDate);
          final String _tmpType;
          _tmpType = _stmt.getText(_columnIndexOfType);
          final double _tmpAmount;
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount);
          final String _tmpNotes;
          _tmpNotes = _stmt.getText(_columnIndexOfNotes);
          _item = new DistributorTransaction(_tmpId,_tmpCustomerId,_tmpDate,_tmpType,_tmpAmount,_tmpNotes);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<DistributorTransaction>> getTransactionsByCustomer(final String customerId) {
    final String _sql = "SELECT * FROM distributor_transactions WHERE customerId = ? ORDER BY date DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"distributor_transactions"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindText(_argIndex, customerId);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCustomerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "customerId");
        final int _columnIndexOfDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "date");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfNotes = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notes");
        final List<DistributorTransaction> _result = new ArrayList<DistributorTransaction>();
        while (_stmt.step()) {
          final DistributorTransaction _item;
          final String _tmpId;
          _tmpId = _stmt.getText(_columnIndexOfId);
          final String _tmpCustomerId;
          _tmpCustomerId = _stmt.getText(_columnIndexOfCustomerId);
          final long _tmpDate;
          _tmpDate = _stmt.getLong(_columnIndexOfDate);
          final String _tmpType;
          _tmpType = _stmt.getText(_columnIndexOfType);
          final double _tmpAmount;
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount);
          final String _tmpNotes;
          _tmpNotes = _stmt.getText(_columnIndexOfNotes);
          _item = new DistributorTransaction(_tmpId,_tmpCustomerId,_tmpDate,_tmpType,_tmpAmount,_tmpNotes);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object getTransactionsByCustomerSync(final String customerId,
      final Continuation<? super List<DistributorTransaction>> $completion) {
    final String _sql = "SELECT * FROM distributor_transactions WHERE customerId = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindText(_argIndex, customerId);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCustomerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "customerId");
        final int _columnIndexOfDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "date");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfNotes = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notes");
        final List<DistributorTransaction> _result = new ArrayList<DistributorTransaction>();
        while (_stmt.step()) {
          final DistributorTransaction _item;
          final String _tmpId;
          _tmpId = _stmt.getText(_columnIndexOfId);
          final String _tmpCustomerId;
          _tmpCustomerId = _stmt.getText(_columnIndexOfCustomerId);
          final long _tmpDate;
          _tmpDate = _stmt.getLong(_columnIndexOfDate);
          final String _tmpType;
          _tmpType = _stmt.getText(_columnIndexOfType);
          final double _tmpAmount;
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount);
          final String _tmpNotes;
          _tmpNotes = _stmt.getText(_columnIndexOfNotes);
          _item = new DistributorTransaction(_tmpId,_tmpCustomerId,_tmpDate,_tmpType,_tmpAmount,_tmpNotes);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Flow<List<DistributorExpense>> getAllExpenses() {
    final String _sql = "SELECT * FROM distributor_expenses ORDER BY date DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"distributor_expenses"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCategory = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "category");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "date");
        final List<DistributorExpense> _result = new ArrayList<DistributorExpense>();
        while (_stmt.step()) {
          final DistributorExpense _item;
          final String _tmpId;
          _tmpId = _stmt.getText(_columnIndexOfId);
          final String _tmpCategory;
          _tmpCategory = _stmt.getText(_columnIndexOfCategory);
          final double _tmpAmount;
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount);
          final String _tmpDescription;
          _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          final long _tmpDate;
          _tmpDate = _stmt.getLong(_columnIndexOfDate);
          _item = new DistributorExpense(_tmpId,_tmpCategory,_tmpAmount,_tmpDescription,_tmpDate);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<DistributorCapital>> getAllCapitals() {
    final String _sql = "SELECT * FROM distributor_capitals ORDER BY date DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"distributor_capitals"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "date");
        final List<DistributorCapital> _result = new ArrayList<DistributorCapital>();
        while (_stmt.step()) {
          final DistributorCapital _item;
          final String _tmpId;
          _tmpId = _stmt.getText(_columnIndexOfId);
          final String _tmpType;
          _tmpType = _stmt.getText(_columnIndexOfType);
          final double _tmpAmount;
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount);
          final String _tmpDescription;
          _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          final long _tmpDate;
          _tmpDate = _stmt.getLong(_columnIndexOfDate);
          _item = new DistributorCapital(_tmpId,_tmpType,_tmpAmount,_tmpDescription,_tmpDate);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object deleteCustomer(final String id, final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM distributor_customers WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindText(_argIndex, id);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object updateCustomerBalance(final String id, final double sales, final double payments,
      final double balance, final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE distributor_customers SET totalSales = ?, totalPayments = ?, currentBalance = ? WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindDouble(_argIndex, sales);
        _argIndex = 2;
        _stmt.bindDouble(_argIndex, payments);
        _argIndex = 3;
        _stmt.bindDouble(_argIndex, balance);
        _argIndex = 4;
        _stmt.bindText(_argIndex, id);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteTransaction(final String id, final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM distributor_transactions WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindText(_argIndex, id);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteTransactionsByCustomer(final String customerId,
      final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM distributor_transactions WHERE customerId = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindText(_argIndex, customerId);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteExpense(final String id, final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM distributor_expenses WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindText(_argIndex, id);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteCapital(final String id, final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM distributor_capitals WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindText(_argIndex, id);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object clearAllCustomers(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM distributor_customers";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object clearAllTransactions(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM distributor_transactions";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object clearAllExpenses(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM distributor_expenses";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object clearAllCapitals(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM distributor_capitals";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
