package com.example.database;

import androidx.annotation.NonNull;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.coroutines.FlowUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.example.models.Deposit;
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
public final class DepositDao_Impl implements DepositDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Deposit> __insertAdapterOfDeposit;

  public DepositDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfDeposit = new EntityInsertAdapter<Deposit>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `deposits` (`id`,`phone`,`amount`,`walletType`,`isShared`,`cardDetails`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, @NonNull final Deposit entity) {
        statement.bindLong(1, entity.getId());
        statement.bindText(2, entity.getPhone());
        statement.bindLong(3, entity.getAmount());
        statement.bindText(4, entity.getWalletType());
        final int _tmp = entity.isShared() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindText(6, entity.getCardDetails());
        statement.bindLong(7, entity.getCreatedAt());
      }
    };
  }

  @Override
  public Object insertDeposit(final Deposit deposit, final Continuation<? super Long> $completion) {
    if (deposit == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfDeposit.insertAndReturnId(_connection, deposit);
    }, $completion);
  }

  @Override
  public Flow<List<Deposit>> getAllDeposits() {
    final String _sql = "SELECT * FROM deposits ORDER BY createdAt DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"deposits"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfPhone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "phone");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfWalletType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "walletType");
        final int _columnIndexOfIsShared = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isShared");
        final int _columnIndexOfCardDetails = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cardDetails");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final List<Deposit> _result = new ArrayList<Deposit>();
        while (_stmt.step()) {
          final Deposit _item;
          final int _tmpId;
          _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          final String _tmpPhone;
          _tmpPhone = _stmt.getText(_columnIndexOfPhone);
          final int _tmpAmount;
          _tmpAmount = (int) (_stmt.getLong(_columnIndexOfAmount));
          final String _tmpWalletType;
          _tmpWalletType = _stmt.getText(_columnIndexOfWalletType);
          final boolean _tmpIsShared;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsShared));
          _tmpIsShared = _tmp != 0;
          final String _tmpCardDetails;
          _tmpCardDetails = _stmt.getText(_columnIndexOfCardDetails);
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          _item = new Deposit(_tmpId,_tmpPhone,_tmpAmount,_tmpWalletType,_tmpIsShared,_tmpCardDetails,_tmpCreatedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object updateDepositSharing(final int id, final boolean isShared, final String cardDetails,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE deposits SET isShared = ?, cardDetails = ? WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        final int _tmp = isShared ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindText(_argIndex, cardDetails);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, id);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteAllDeposits(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM deposits";
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
