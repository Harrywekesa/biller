package com.mpesa.tracker.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.mpesa.tracker.data.local.entities.CustomRuleEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CustomRuleDao_Impl implements CustomRuleDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CustomRuleEntity> __insertionAdapterOfCustomRuleEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteRule;

  public CustomRuleDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCustomRuleEntity = new EntityInsertionAdapter<CustomRuleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `custom_rules` (`merchantName`,`categoryId`,`createdAt`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CustomRuleEntity entity) {
        if (entity.getMerchantName() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getMerchantName());
        }
        statement.bindLong(2, entity.getCategoryId());
        statement.bindLong(3, entity.getCreatedAt());
      }
    };
    this.__preparedStmtOfDeleteRule = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM custom_rules WHERE merchantName = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrUpdateRule(final CustomRuleEntity rule,
      final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCustomRuleEntity.insert(rule);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, arg1);
  }

  @Override
  public Object deleteRule(final String merchantName, final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteRule.acquire();
        int _argIndex = 1;
        if (merchantName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, merchantName);
        }
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteRule.release(_stmt);
        }
      }
    }, arg1);
  }

  @Override
  public Object getCategoryIdForMerchant(final String merchantName,
      final Continuation<? super Integer> arg1) {
    final String _sql = "SELECT categoryId FROM custom_rules WHERE merchantName = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (merchantName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, merchantName);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null;
            } else {
              _result = _cursor.getInt(0);
            }
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, arg1);
  }

  @Override
  public Flow<List<CustomRuleEntity>> getAllRules() {
    final String _sql = "SELECT * FROM custom_rules ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"custom_rules"}, new Callable<List<CustomRuleEntity>>() {
      @Override
      @NonNull
      public List<CustomRuleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMerchantName = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantName");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<CustomRuleEntity> _result = new ArrayList<CustomRuleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CustomRuleEntity _item;
            final String _tmpMerchantName;
            if (_cursor.isNull(_cursorIndexOfMerchantName)) {
              _tmpMerchantName = null;
            } else {
              _tmpMerchantName = _cursor.getString(_cursorIndexOfMerchantName);
            }
            final int _tmpCategoryId;
            _tmpCategoryId = _cursor.getInt(_cursorIndexOfCategoryId);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new CustomRuleEntity(_tmpMerchantName,_tmpCategoryId,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
