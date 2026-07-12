package com.example.database;

import androidx.annotation.NonNull;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.coroutines.FlowUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.example.models.Transaction;
import java.lang.Class;
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
public final class TransactionDao_Impl implements TransactionDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Transaction> __insertAdapterOfTransaction;

  public TransactionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfTransaction = new EntityInsertAdapter<Transaction>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `transactions` (`id`,`phone`,`amount`,`cardCode`,`walletType`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final Transaction entity) {
        statement.bindLong(1, entity.getId());
        statement.bindText(2, entity.getPhone());
        statement.bindLong(3, entity.getAmount());
        statement.bindText(4, entity.getCardCode());
        statement.bindText(5, entity.getWalletType());
        statement.bindLong(6, entity.getCreatedAt());
      }
    };
  }

  @Override
  public Object insertTransaction(final Transaction transaction,
      final Continuation<? super Unit> $completion) {
    if (transaction == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfTransaction.insert(_connection, transaction);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Flow<List<Transaction>> getAllTransactions() {
    final String _sql = "SELECT * FROM transactions ORDER BY createdAt DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"transactions"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfPhone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "phone");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfCardCode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cardCode");
        final int _columnIndexOfWalletType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "walletType");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final List<Transaction> _result = new ArrayList<Transaction>();
        while (_stmt.step()) {
          final Transaction _item;
          final int _tmpId;
          _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          final String _tmpPhone;
          _tmpPhone = _stmt.getText(_columnIndexOfPhone);
          final int _tmpAmount;
          _tmpAmount = (int) (_stmt.getLong(_columnIndexOfAmount));
          final String _tmpCardCode;
          _tmpCardCode = _stmt.getText(_columnIndexOfCardCode);
          final String _tmpWalletType;
          _tmpWalletType = _stmt.getText(_columnIndexOfWalletType);
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          _item = new Transaction(_tmpId,_tmpPhone,_tmpAmount,_tmpCardCode,_tmpWalletType,_tmpCreatedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object deleteAllTransactions(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM transactions";
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
