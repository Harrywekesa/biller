package com.mpesa.tracker.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.mpesa.tracker.data.local.entities.SubscriptionEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
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
public final class SubscriptionDao_Impl implements SubscriptionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SubscriptionEntity> __insertionAdapterOfSubscriptionEntity;

  private final EntityDeletionOrUpdateAdapter<SubscriptionEntity> __updateAdapterOfSubscriptionEntity;

  public SubscriptionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSubscriptionEntity = new EntityInsertionAdapter<SubscriptionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `subscriptions` (`id`,`name`,`merchantNameMatcher`,`amount`,`isActive`,`billingCycleDays`,`categoryId`,`expectedNextPaymentDate`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SubscriptionEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        if (entity.getMerchantNameMatcher() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getMerchantNameMatcher());
        }
        statement.bindDouble(4, entity.getAmount());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getBillingCycleDays());
        if (entity.getCategoryId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getCategoryId());
        }
        if (entity.getExpectedNextPaymentDate() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getExpectedNextPaymentDate());
        }
      }
    };
    this.__updateAdapterOfSubscriptionEntity = new EntityDeletionOrUpdateAdapter<SubscriptionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `subscriptions` SET `id` = ?,`name` = ?,`merchantNameMatcher` = ?,`amount` = ?,`isActive` = ?,`billingCycleDays` = ?,`categoryId` = ?,`expectedNextPaymentDate` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SubscriptionEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        if (entity.getMerchantNameMatcher() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getMerchantNameMatcher());
        }
        statement.bindDouble(4, entity.getAmount());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getBillingCycleDays());
        if (entity.getCategoryId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getCategoryId());
        }
        if (entity.getExpectedNextPaymentDate() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getExpectedNextPaymentDate());
        }
        statement.bindLong(9, entity.getId());
      }
    };
  }

  @Override
  public Object insertSubscription(final SubscriptionEntity subscription,
      final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSubscriptionEntity.insert(subscription);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, arg1);
  }

  @Override
  public Object updateSubscription(final SubscriptionEntity subscription,
      final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSubscriptionEntity.handle(subscription);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, arg1);
  }

  @Override
  public Flow<List<SubscriptionEntity>> getActiveSubscriptions() {
    final String _sql = "SELECT * FROM subscriptions WHERE isActive = 1 ORDER BY expectedNextPaymentDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"subscriptions"}, new Callable<List<SubscriptionEntity>>() {
      @Override
      @NonNull
      public List<SubscriptionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfMerchantNameMatcher = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantNameMatcher");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfBillingCycleDays = CursorUtil.getColumnIndexOrThrow(_cursor, "billingCycleDays");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfExpectedNextPaymentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedNextPaymentDate");
          final List<SubscriptionEntity> _result = new ArrayList<SubscriptionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SubscriptionEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpMerchantNameMatcher;
            if (_cursor.isNull(_cursorIndexOfMerchantNameMatcher)) {
              _tmpMerchantNameMatcher = null;
            } else {
              _tmpMerchantNameMatcher = _cursor.getString(_cursorIndexOfMerchantNameMatcher);
            }
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final int _tmpBillingCycleDays;
            _tmpBillingCycleDays = _cursor.getInt(_cursorIndexOfBillingCycleDays);
            final Integer _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getInt(_cursorIndexOfCategoryId);
            }
            final Long _tmpExpectedNextPaymentDate;
            if (_cursor.isNull(_cursorIndexOfExpectedNextPaymentDate)) {
              _tmpExpectedNextPaymentDate = null;
            } else {
              _tmpExpectedNextPaymentDate = _cursor.getLong(_cursorIndexOfExpectedNextPaymentDate);
            }
            _item = new SubscriptionEntity(_tmpId,_tmpName,_tmpMerchantNameMatcher,_tmpAmount,_tmpIsActive,_tmpBillingCycleDays,_tmpCategoryId,_tmpExpectedNextPaymentDate);
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

  @Override
  public Object getSubscriptionByMatcher(final String matcher,
      final Continuation<? super SubscriptionEntity> arg1) {
    final String _sql = "SELECT * FROM subscriptions WHERE merchantNameMatcher = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (matcher == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, matcher);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SubscriptionEntity>() {
      @Override
      @Nullable
      public SubscriptionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfMerchantNameMatcher = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantNameMatcher");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfBillingCycleDays = CursorUtil.getColumnIndexOrThrow(_cursor, "billingCycleDays");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfExpectedNextPaymentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedNextPaymentDate");
          final SubscriptionEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpMerchantNameMatcher;
            if (_cursor.isNull(_cursorIndexOfMerchantNameMatcher)) {
              _tmpMerchantNameMatcher = null;
            } else {
              _tmpMerchantNameMatcher = _cursor.getString(_cursorIndexOfMerchantNameMatcher);
            }
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final int _tmpBillingCycleDays;
            _tmpBillingCycleDays = _cursor.getInt(_cursorIndexOfBillingCycleDays);
            final Integer _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getInt(_cursorIndexOfCategoryId);
            }
            final Long _tmpExpectedNextPaymentDate;
            if (_cursor.isNull(_cursorIndexOfExpectedNextPaymentDate)) {
              _tmpExpectedNextPaymentDate = null;
            } else {
              _tmpExpectedNextPaymentDate = _cursor.getLong(_cursorIndexOfExpectedNextPaymentDate);
            }
            _result = new SubscriptionEntity(_tmpId,_tmpName,_tmpMerchantNameMatcher,_tmpAmount,_tmpIsActive,_tmpBillingCycleDays,_tmpCategoryId,_tmpExpectedNextPaymentDate);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
