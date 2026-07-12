package com.example.database;

import androidx.annotation.NonNull;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.coroutines.FlowUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.example.models.Card;
import java.lang.Class;
import java.lang.Integer;
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
public final class CardDao_Impl implements CardDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Card> __insertAdapterOfCard;

  public CardDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfCard = new EntityInsertAdapter<Card>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `cards` (`id`,`category`,`code`,`username`,`password`,`used`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, @NonNull final Card entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCategory());
        statement.bindText(3, entity.getCode());
        statement.bindText(4, entity.getUsername());
        statement.bindText(5, entity.getPassword());
        final int _tmp = entity.getUsed() ? 1 : 0;
        statement.bindLong(6, _tmp);
      }
    };
  }

  @Override
  public Object insertCard(final Card card, final Continuation<? super Unit> $completion) {
    if (card == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfCard.insert(_connection, card);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object insertCards(final List<Card> cards, final Continuation<? super Unit> $completion) {
    if (cards == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfCard.insert(_connection, cards);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object getUnusedCardByCategory(final int category,
      final Continuation<? super Card> $completion) {
    final String _sql = "SELECT * FROM cards WHERE category = ? AND used = 0 LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, category);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCategory = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "category");
        final int _columnIndexOfCode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "code");
        final int _columnIndexOfUsername = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "username");
        final int _columnIndexOfPassword = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "password");
        final int _columnIndexOfUsed = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "used");
        final Card _result;
        if (_stmt.step()) {
          final int _tmpId;
          _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          final int _tmpCategory;
          _tmpCategory = (int) (_stmt.getLong(_columnIndexOfCategory));
          final String _tmpCode;
          _tmpCode = _stmt.getText(_columnIndexOfCode);
          final String _tmpUsername;
          _tmpUsername = _stmt.getText(_columnIndexOfUsername);
          final String _tmpPassword;
          _tmpPassword = _stmt.getText(_columnIndexOfPassword);
          final boolean _tmpUsed;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfUsed));
          _tmpUsed = _tmp != 0;
          _result = new Card(_tmpId,_tmpCategory,_tmpCode,_tmpUsername,_tmpPassword,_tmpUsed);
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
  public Flow<Integer> getUnusedCountByCategory(final int category) {
    final String _sql = "SELECT COUNT(*) FROM cards WHERE category = ? AND used = 0";
    return FlowUtil.createFlow(__db, false, new String[] {"cards"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, category);
        final Integer _result;
        if (_stmt.step()) {
          final int _tmp;
          _tmp = (int) (_stmt.getLong(0));
          _result = _tmp;
        } else {
          _result = 0;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<Integer> getUnusedCardsCount() {
    final String _sql = "SELECT COUNT(*) FROM cards WHERE used = 0";
    return FlowUtil.createFlow(__db, false, new String[] {"cards"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final Integer _result;
        if (_stmt.step()) {
          final int _tmp;
          _tmp = (int) (_stmt.getLong(0));
          _result = _tmp;
        } else {
          _result = 0;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<Card>> getAllCards() {
    final String _sql = "SELECT * FROM cards ORDER BY id DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"cards"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCategory = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "category");
        final int _columnIndexOfCode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "code");
        final int _columnIndexOfUsername = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "username");
        final int _columnIndexOfPassword = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "password");
        final int _columnIndexOfUsed = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "used");
        final List<Card> _result = new ArrayList<Card>();
        while (_stmt.step()) {
          final Card _item;
          final int _tmpId;
          _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          final int _tmpCategory;
          _tmpCategory = (int) (_stmt.getLong(_columnIndexOfCategory));
          final String _tmpCode;
          _tmpCode = _stmt.getText(_columnIndexOfCode);
          final String _tmpUsername;
          _tmpUsername = _stmt.getText(_columnIndexOfUsername);
          final String _tmpPassword;
          _tmpPassword = _stmt.getText(_columnIndexOfPassword);
          final boolean _tmpUsed;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfUsed));
          _tmpUsed = _tmp != 0;
          _item = new Card(_tmpId,_tmpCategory,_tmpCode,_tmpUsername,_tmpPassword,_tmpUsed);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object markCardAsUsed(final int id, final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE cards SET used = 1 WHERE id = ?";
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
  public Object deleteCard(final int id, final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM cards WHERE id = ?";
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
  public Object deleteCardsByCategory(final int category,
      final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM cards WHERE category = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, category);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteAllCards(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM cards";
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
