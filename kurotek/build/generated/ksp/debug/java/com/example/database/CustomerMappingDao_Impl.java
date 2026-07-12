package com.example.database;

import androidx.annotation.NonNull;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.coroutines.FlowUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.example.models.CustomerMapping;
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
public final class CustomerMappingDao_Impl implements CustomerMappingDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<CustomerMapping> __insertAdapterOfCustomerMapping;

  public CustomerMappingDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfCustomerMapping = new EntityInsertAdapter<CustomerMapping>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `customer_mappings` (`id`,`customerUniqueId`,`basicPhone`,`customerName`,`walletType`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final CustomerMapping entity) {
        statement.bindLong(1, entity.getId());
        statement.bindText(2, entity.getCustomerUniqueId());
        statement.bindText(3, entity.getBasicPhone());
        statement.bindText(4, entity.getCustomerName());
        statement.bindText(5, entity.getWalletType());
      }
    };
  }

  @Override
  public Object insertMapping(final CustomerMapping mapping,
      final Continuation<? super Long> $completion) {
    if (mapping == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfCustomerMapping.insertAndReturnId(_connection, mapping);
    }, $completion);
  }

  @Override
  public Flow<List<CustomerMapping>> getAllMappings() {
    final String _sql = "SELECT * FROM customer_mappings ORDER BY id DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"customer_mappings"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCustomerUniqueId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "customerUniqueId");
        final int _columnIndexOfBasicPhone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "basicPhone");
        final int _columnIndexOfCustomerName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "customerName");
        final int _columnIndexOfWalletType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "walletType");
        final List<CustomerMapping> _result = new ArrayList<CustomerMapping>();
        while (_stmt.step()) {
          final CustomerMapping _item;
          final int _tmpId;
          _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          final String _tmpCustomerUniqueId;
          _tmpCustomerUniqueId = _stmt.getText(_columnIndexOfCustomerUniqueId);
          final String _tmpBasicPhone;
          _tmpBasicPhone = _stmt.getText(_columnIndexOfBasicPhone);
          final String _tmpCustomerName;
          _tmpCustomerName = _stmt.getText(_columnIndexOfCustomerName);
          final String _tmpWalletType;
          _tmpWalletType = _stmt.getText(_columnIndexOfWalletType);
          _item = new CustomerMapping(_tmpId,_tmpCustomerUniqueId,_tmpBasicPhone,_tmpCustomerName,_tmpWalletType);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object getMappingByUniqueId(final String uniqueId,
      final Continuation<? super CustomerMapping> $completion) {
    final String _sql = "SELECT * FROM customer_mappings WHERE customerUniqueId = ? LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindText(_argIndex, uniqueId);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCustomerUniqueId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "customerUniqueId");
        final int _columnIndexOfBasicPhone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "basicPhone");
        final int _columnIndexOfCustomerName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "customerName");
        final int _columnIndexOfWalletType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "walletType");
        final CustomerMapping _result;
        if (_stmt.step()) {
          final int _tmpId;
          _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          final String _tmpCustomerUniqueId;
          _tmpCustomerUniqueId = _stmt.getText(_columnIndexOfCustomerUniqueId);
          final String _tmpBasicPhone;
          _tmpBasicPhone = _stmt.getText(_columnIndexOfBasicPhone);
          final String _tmpCustomerName;
          _tmpCustomerName = _stmt.getText(_columnIndexOfCustomerName);
          final String _tmpWalletType;
          _tmpWalletType = _stmt.getText(_columnIndexOfWalletType);
          _result = new CustomerMapping(_tmpId,_tmpCustomerUniqueId,_tmpBasicPhone,_tmpCustomerName,_tmpWalletType);
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
  public Object deleteMapping(final int id, final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM customer_mappings WHERE id = ?";
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
  public Object deleteAllMappings(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM customer_mappings";
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
