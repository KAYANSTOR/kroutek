package com.example.database;

import androidx.annotation.NonNull;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.coroutines.FlowUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.example.models.PendingApproval;
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
public final class PendingApprovalDao_Impl implements PendingApprovalDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<PendingApproval> __insertAdapterOfPendingApproval;

  public PendingApprovalDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfPendingApproval = new EntityInsertAdapter<PendingApproval>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `pending_approvals` (`id`,`phone`,`amount`,`walletType`,`createdAt`,`isAccountCode`,`depositId`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final PendingApproval entity) {
        statement.bindLong(1, entity.getId());
        statement.bindText(2, entity.getPhone());
        statement.bindLong(3, entity.getAmount());
        statement.bindText(4, entity.getWalletType());
        statement.bindLong(5, entity.getCreatedAt());
        final int _tmp = entity.isAccountCode() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getDepositId());
      }
    };
  }

  @Override
  public Object insertPendingApproval(final PendingApproval pending,
      final Continuation<? super Long> $completion) {
    if (pending == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfPendingApproval.insertAndReturnId(_connection, pending);
    }, $completion);
  }

  @Override
  public Flow<List<PendingApproval>> getAllPendingApprovals() {
    final String _sql = "SELECT * FROM pending_approvals ORDER BY createdAt DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"pending_approvals"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfPhone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "phone");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfWalletType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "walletType");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfIsAccountCode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isAccountCode");
        final int _columnIndexOfDepositId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "depositId");
        final List<PendingApproval> _result = new ArrayList<PendingApproval>();
        while (_stmt.step()) {
          final PendingApproval _item;
          final int _tmpId;
          _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          final String _tmpPhone;
          _tmpPhone = _stmt.getText(_columnIndexOfPhone);
          final int _tmpAmount;
          _tmpAmount = (int) (_stmt.getLong(_columnIndexOfAmount));
          final String _tmpWalletType;
          _tmpWalletType = _stmt.getText(_columnIndexOfWalletType);
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final boolean _tmpIsAccountCode;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsAccountCode));
          _tmpIsAccountCode = _tmp != 0;
          final int _tmpDepositId;
          _tmpDepositId = (int) (_stmt.getLong(_columnIndexOfDepositId));
          _item = new PendingApproval(_tmpId,_tmpPhone,_tmpAmount,_tmpWalletType,_tmpCreatedAt,_tmpIsAccountCode,_tmpDepositId);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object getPendingApprovalById(final int id,
      final Continuation<? super PendingApproval> $completion) {
    final String _sql = "SELECT * FROM pending_approvals WHERE id = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfPhone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "phone");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfWalletType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "walletType");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfIsAccountCode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isAccountCode");
        final int _columnIndexOfDepositId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "depositId");
        final PendingApproval _result;
        if (_stmt.step()) {
          final int _tmpId;
          _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          final String _tmpPhone;
          _tmpPhone = _stmt.getText(_columnIndexOfPhone);
          final int _tmpAmount;
          _tmpAmount = (int) (_stmt.getLong(_columnIndexOfAmount));
          final String _tmpWalletType;
          _tmpWalletType = _stmt.getText(_columnIndexOfWalletType);
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final boolean _tmpIsAccountCode;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsAccountCode));
          _tmpIsAccountCode = _tmp != 0;
          final int _tmpDepositId;
          _tmpDepositId = (int) (_stmt.getLong(_columnIndexOfDepositId));
          _result = new PendingApproval(_tmpId,_tmpPhone,_tmpAmount,_tmpWalletType,_tmpCreatedAt,_tmpIsAccountCode,_tmpDepositId);
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
  public Object updatePendingApprovalPhone(final int id, final String phone,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE pending_approvals SET phone = ? WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindText(_argIndex, phone);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deletePendingApproval(final int id, final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM pending_approvals WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteAllPendingApprovals(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM pending_approvals";
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
