package com.example.database;

import androidx.annotation.NonNull;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.coroutines.FlowUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.example.models.GeneratedMikrotikCard;
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
public final class GeneratedMikrotikCardDao_Impl implements GeneratedMikrotikCardDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<GeneratedMikrotikCard> __insertAdapterOfGeneratedMikrotikCard;

  public GeneratedMikrotikCardDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfGeneratedMikrotikCard = new EntityInsertAdapter<GeneratedMikrotikCard>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `generated_mikrotik_cards` (`id`,`category`,`pin`,`username`,`password`,`printed`,`transferred`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final GeneratedMikrotikCard entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCategory());
        statement.bindText(3, entity.getPin());
        statement.bindText(4, entity.getUsername());
        statement.bindText(5, entity.getPassword());
        final int _tmp = entity.getPrinted() ? 1 : 0;
        statement.bindLong(6, _tmp);
        final int _tmp_1 = entity.getTransferred() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        statement.bindLong(8, entity.getCreatedAt());
      }
    };
  }

  @Override
  public Object insertGeneratedCard(final GeneratedMikrotikCard card,
      final Continuation<? super Long> $completion) {
    if (card == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfGeneratedMikrotikCard.insertAndReturnId(_connection, card);
    }, $completion);
  }

  @Override
  public Object insertGeneratedCards(final List<GeneratedMikrotikCard> cards,
      final Continuation<? super Unit> $completion) {
    if (cards == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfGeneratedMikrotikCard.insert(_connection, cards);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Flow<List<GeneratedMikrotikCard>> getAllGeneratedCards() {
    final String _sql = "SELECT * FROM generated_mikrotik_cards ORDER BY createdAt DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"generated_mikrotik_cards"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCategory = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "category");
        final int _columnIndexOfPin = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "pin");
        final int _columnIndexOfUsername = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "username");
        final int _columnIndexOfPassword = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "password");
        final int _columnIndexOfPrinted = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "printed");
        final int _columnIndexOfTransferred = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "transferred");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final List<GeneratedMikrotikCard> _result = new ArrayList<GeneratedMikrotikCard>();
        while (_stmt.step()) {
          final GeneratedMikrotikCard _item;
          final int _tmpId;
          _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          final int _tmpCategory;
          _tmpCategory = (int) (_stmt.getLong(_columnIndexOfCategory));
          final String _tmpPin;
          _tmpPin = _stmt.getText(_columnIndexOfPin);
          final String _tmpUsername;
          _tmpUsername = _stmt.getText(_columnIndexOfUsername);
          final String _tmpPassword;
          _tmpPassword = _stmt.getText(_columnIndexOfPassword);
          final boolean _tmpPrinted;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfPrinted));
          _tmpPrinted = _tmp != 0;
          final boolean _tmpTransferred;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfTransferred));
          _tmpTransferred = _tmp_1 != 0;
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          _item = new GeneratedMikrotikCard(_tmpId,_tmpCategory,_tmpPin,_tmpUsername,_tmpPassword,_tmpPrinted,_tmpTransferred,_tmpCreatedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object markAsPrinted(final int id, final boolean printed,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE generated_mikrotik_cards SET printed = ? WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        final int _tmp = printed ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
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
  public Object markAsTransferred(final int id, final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE generated_mikrotik_cards SET transferred = 1 WHERE id = ?";
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
  public Object deleteGeneratedCard(final int id, final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM generated_mikrotik_cards WHERE id = ?";
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
  public Object deleteAllGeneratedCards(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM generated_mikrotik_cards";
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
