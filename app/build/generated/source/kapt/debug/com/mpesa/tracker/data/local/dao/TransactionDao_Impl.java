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
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.mpesa.tracker.data.local.entities.CategoryExpense;
import com.mpesa.tracker.data.local.entities.DailySpend;
import com.mpesa.tracker.data.local.entities.TransactionEntity;
import com.mpesa.tracker.data.local.entities.TransactionType;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
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
public final class TransactionDao_Impl implements TransactionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TransactionEntity> __insertionAdapterOfTransactionEntity;

  private final EntityDeletionOrUpdateAdapter<TransactionEntity> __updateAdapterOfTransactionEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateCategoryForMerchant;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllTransactions;

  public TransactionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTransactionEntity = new EntityInsertionAdapter<TransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `transactions` (`receiptNumber`,`type`,`amount`,`transactionCost`,`dateTimestamp`,`recipientName`,`recipientNumber`,`balance`,`categoryId`,`isRecurring`,`isIncome`,`fulizaAmount`,`fulizaFee`,`rawSmsBody`,`simSubscriptionId`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionEntity entity) {
        if (entity.getReceiptNumber() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getReceiptNumber());
        }
        statement.bindString(2, __TransactionType_enumToString(entity.getType()));
        statement.bindDouble(3, entity.getAmount());
        statement.bindDouble(4, entity.getTransactionCost());
        statement.bindLong(5, entity.getDateTimestamp());
        if (entity.getRecipientName() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getRecipientName());
        }
        if (entity.getRecipientNumber() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getRecipientNumber());
        }
        if (entity.getBalance() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getBalance());
        }
        if (entity.getCategoryId() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getCategoryId());
        }
        final int _tmp = entity.isRecurring() ? 1 : 0;
        statement.bindLong(10, _tmp);
        final int _tmp_1 = entity.isIncome() ? 1 : 0;
        statement.bindLong(11, _tmp_1);
        if (entity.getFulizaAmount() == null) {
          statement.bindNull(12);
        } else {
          statement.bindDouble(12, entity.getFulizaAmount());
        }
        if (entity.getFulizaFee() == null) {
          statement.bindNull(13);
        } else {
          statement.bindDouble(13, entity.getFulizaFee());
        }
        if (entity.getRawSmsBody() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getRawSmsBody());
        }
        if (entity.getSimSubscriptionId() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getSimSubscriptionId());
        }
      }
    };
    this.__updateAdapterOfTransactionEntity = new EntityDeletionOrUpdateAdapter<TransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `transactions` SET `receiptNumber` = ?,`type` = ?,`amount` = ?,`transactionCost` = ?,`dateTimestamp` = ?,`recipientName` = ?,`recipientNumber` = ?,`balance` = ?,`categoryId` = ?,`isRecurring` = ?,`isIncome` = ?,`fulizaAmount` = ?,`fulizaFee` = ?,`rawSmsBody` = ?,`simSubscriptionId` = ? WHERE `receiptNumber` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionEntity entity) {
        if (entity.getReceiptNumber() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getReceiptNumber());
        }
        statement.bindString(2, __TransactionType_enumToString(entity.getType()));
        statement.bindDouble(3, entity.getAmount());
        statement.bindDouble(4, entity.getTransactionCost());
        statement.bindLong(5, entity.getDateTimestamp());
        if (entity.getRecipientName() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getRecipientName());
        }
        if (entity.getRecipientNumber() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getRecipientNumber());
        }
        if (entity.getBalance() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getBalance());
        }
        if (entity.getCategoryId() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getCategoryId());
        }
        final int _tmp = entity.isRecurring() ? 1 : 0;
        statement.bindLong(10, _tmp);
        final int _tmp_1 = entity.isIncome() ? 1 : 0;
        statement.bindLong(11, _tmp_1);
        if (entity.getFulizaAmount() == null) {
          statement.bindNull(12);
        } else {
          statement.bindDouble(12, entity.getFulizaAmount());
        }
        if (entity.getFulizaFee() == null) {
          statement.bindNull(13);
        } else {
          statement.bindDouble(13, entity.getFulizaFee());
        }
        if (entity.getRawSmsBody() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getRawSmsBody());
        }
        if (entity.getSimSubscriptionId() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getSimSubscriptionId());
        }
        if (entity.getReceiptNumber() == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.getReceiptNumber());
        }
      }
    };
    this.__preparedStmtOfUpdateCategoryForMerchant = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE transactions SET categoryId = ? WHERE recipientName = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllTransactions = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM transactions";
        return _query;
      }
    };
  }

  @Override
  public Object insertTransaction(final TransactionEntity transaction,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTransactionEntity.insert(transaction);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTransaction(final TransactionEntity transaction,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTransactionEntity.handle(transaction);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateCategoryForMerchant(final String merchantName, final int categoryId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateCategoryForMerchant.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, categoryId);
        _argIndex = 2;
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
          __preparedStmtOfUpdateCategoryForMerchant.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllTransactions(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllTransactions.acquire();
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
          __preparedStmtOfDeleteAllTransactions.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Integer>> getActiveSimIds() {
    final String _sql = "SELECT DISTINCT simSubscriptionId FROM transactions WHERE simSubscriptionId IS NOT NULL ORDER BY simSubscriptionId ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<Integer>>() {
      @Override
      @NonNull
      public List<Integer> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<Integer> _result = new ArrayList<Integer>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Integer _item;
            if (_cursor.isNull(0)) {
              _item = null;
            } else {
              _item = _cursor.getInt(0);
            }
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
  public Flow<List<TransactionEntity>> getAllTransactions(final Integer simId) {
    final String _sql = "SELECT * FROM transactions WHERE (? IS NULL OR simSubscriptionId = ?) ORDER BY dateTimestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (simId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, simId);
    }
    _argIndex = 2;
    if (simId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, simId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfReceiptNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptNumber");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTransactionCost = CursorUtil.getColumnIndexOrThrow(_cursor, "transactionCost");
          final int _cursorIndexOfDateTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTimestamp");
          final int _cursorIndexOfRecipientName = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientName");
          final int _cursorIndexOfRecipientNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientNumber");
          final int _cursorIndexOfBalance = CursorUtil.getColumnIndexOrThrow(_cursor, "balance");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfIsRecurring = CursorUtil.getColumnIndexOrThrow(_cursor, "isRecurring");
          final int _cursorIndexOfIsIncome = CursorUtil.getColumnIndexOrThrow(_cursor, "isIncome");
          final int _cursorIndexOfFulizaAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "fulizaAmount");
          final int _cursorIndexOfFulizaFee = CursorUtil.getColumnIndexOrThrow(_cursor, "fulizaFee");
          final int _cursorIndexOfRawSmsBody = CursorUtil.getColumnIndexOrThrow(_cursor, "rawSmsBody");
          final int _cursorIndexOfSimSubscriptionId = CursorUtil.getColumnIndexOrThrow(_cursor, "simSubscriptionId");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final String _tmpReceiptNumber;
            if (_cursor.isNull(_cursorIndexOfReceiptNumber)) {
              _tmpReceiptNumber = null;
            } else {
              _tmpReceiptNumber = _cursor.getString(_cursorIndexOfReceiptNumber);
            }
            final TransactionType _tmpType;
            _tmpType = __TransactionType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final double _tmpTransactionCost;
            _tmpTransactionCost = _cursor.getDouble(_cursorIndexOfTransactionCost);
            final long _tmpDateTimestamp;
            _tmpDateTimestamp = _cursor.getLong(_cursorIndexOfDateTimestamp);
            final String _tmpRecipientName;
            if (_cursor.isNull(_cursorIndexOfRecipientName)) {
              _tmpRecipientName = null;
            } else {
              _tmpRecipientName = _cursor.getString(_cursorIndexOfRecipientName);
            }
            final String _tmpRecipientNumber;
            if (_cursor.isNull(_cursorIndexOfRecipientNumber)) {
              _tmpRecipientNumber = null;
            } else {
              _tmpRecipientNumber = _cursor.getString(_cursorIndexOfRecipientNumber);
            }
            final Double _tmpBalance;
            if (_cursor.isNull(_cursorIndexOfBalance)) {
              _tmpBalance = null;
            } else {
              _tmpBalance = _cursor.getDouble(_cursorIndexOfBalance);
            }
            final Integer _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getInt(_cursorIndexOfCategoryId);
            }
            final boolean _tmpIsRecurring;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRecurring);
            _tmpIsRecurring = _tmp != 0;
            final boolean _tmpIsIncome;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsIncome);
            _tmpIsIncome = _tmp_1 != 0;
            final Double _tmpFulizaAmount;
            if (_cursor.isNull(_cursorIndexOfFulizaAmount)) {
              _tmpFulizaAmount = null;
            } else {
              _tmpFulizaAmount = _cursor.getDouble(_cursorIndexOfFulizaAmount);
            }
            final Double _tmpFulizaFee;
            if (_cursor.isNull(_cursorIndexOfFulizaFee)) {
              _tmpFulizaFee = null;
            } else {
              _tmpFulizaFee = _cursor.getDouble(_cursorIndexOfFulizaFee);
            }
            final String _tmpRawSmsBody;
            if (_cursor.isNull(_cursorIndexOfRawSmsBody)) {
              _tmpRawSmsBody = null;
            } else {
              _tmpRawSmsBody = _cursor.getString(_cursorIndexOfRawSmsBody);
            }
            final Integer _tmpSimSubscriptionId;
            if (_cursor.isNull(_cursorIndexOfSimSubscriptionId)) {
              _tmpSimSubscriptionId = null;
            } else {
              _tmpSimSubscriptionId = _cursor.getInt(_cursorIndexOfSimSubscriptionId);
            }
            _item = new TransactionEntity(_tmpReceiptNumber,_tmpType,_tmpAmount,_tmpTransactionCost,_tmpDateTimestamp,_tmpRecipientName,_tmpRecipientNumber,_tmpBalance,_tmpCategoryId,_tmpIsRecurring,_tmpIsIncome,_tmpFulizaAmount,_tmpFulizaFee,_tmpRawSmsBody,_tmpSimSubscriptionId);
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
  public Flow<List<TransactionEntity>> getTransactionsBetween(final long startDate,
      final long endDate, final Integer simId) {
    final String _sql = "SELECT * FROM transactions WHERE dateTimestamp BETWEEN ? AND ? AND (? IS NULL OR simSubscriptionId = ?) ORDER BY dateTimestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDate);
    _argIndex = 3;
    if (simId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, simId);
    }
    _argIndex = 4;
    if (simId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, simId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfReceiptNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptNumber");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTransactionCost = CursorUtil.getColumnIndexOrThrow(_cursor, "transactionCost");
          final int _cursorIndexOfDateTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTimestamp");
          final int _cursorIndexOfRecipientName = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientName");
          final int _cursorIndexOfRecipientNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientNumber");
          final int _cursorIndexOfBalance = CursorUtil.getColumnIndexOrThrow(_cursor, "balance");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfIsRecurring = CursorUtil.getColumnIndexOrThrow(_cursor, "isRecurring");
          final int _cursorIndexOfIsIncome = CursorUtil.getColumnIndexOrThrow(_cursor, "isIncome");
          final int _cursorIndexOfFulizaAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "fulizaAmount");
          final int _cursorIndexOfFulizaFee = CursorUtil.getColumnIndexOrThrow(_cursor, "fulizaFee");
          final int _cursorIndexOfRawSmsBody = CursorUtil.getColumnIndexOrThrow(_cursor, "rawSmsBody");
          final int _cursorIndexOfSimSubscriptionId = CursorUtil.getColumnIndexOrThrow(_cursor, "simSubscriptionId");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final String _tmpReceiptNumber;
            if (_cursor.isNull(_cursorIndexOfReceiptNumber)) {
              _tmpReceiptNumber = null;
            } else {
              _tmpReceiptNumber = _cursor.getString(_cursorIndexOfReceiptNumber);
            }
            final TransactionType _tmpType;
            _tmpType = __TransactionType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final double _tmpTransactionCost;
            _tmpTransactionCost = _cursor.getDouble(_cursorIndexOfTransactionCost);
            final long _tmpDateTimestamp;
            _tmpDateTimestamp = _cursor.getLong(_cursorIndexOfDateTimestamp);
            final String _tmpRecipientName;
            if (_cursor.isNull(_cursorIndexOfRecipientName)) {
              _tmpRecipientName = null;
            } else {
              _tmpRecipientName = _cursor.getString(_cursorIndexOfRecipientName);
            }
            final String _tmpRecipientNumber;
            if (_cursor.isNull(_cursorIndexOfRecipientNumber)) {
              _tmpRecipientNumber = null;
            } else {
              _tmpRecipientNumber = _cursor.getString(_cursorIndexOfRecipientNumber);
            }
            final Double _tmpBalance;
            if (_cursor.isNull(_cursorIndexOfBalance)) {
              _tmpBalance = null;
            } else {
              _tmpBalance = _cursor.getDouble(_cursorIndexOfBalance);
            }
            final Integer _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getInt(_cursorIndexOfCategoryId);
            }
            final boolean _tmpIsRecurring;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRecurring);
            _tmpIsRecurring = _tmp != 0;
            final boolean _tmpIsIncome;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsIncome);
            _tmpIsIncome = _tmp_1 != 0;
            final Double _tmpFulizaAmount;
            if (_cursor.isNull(_cursorIndexOfFulizaAmount)) {
              _tmpFulizaAmount = null;
            } else {
              _tmpFulizaAmount = _cursor.getDouble(_cursorIndexOfFulizaAmount);
            }
            final Double _tmpFulizaFee;
            if (_cursor.isNull(_cursorIndexOfFulizaFee)) {
              _tmpFulizaFee = null;
            } else {
              _tmpFulizaFee = _cursor.getDouble(_cursorIndexOfFulizaFee);
            }
            final String _tmpRawSmsBody;
            if (_cursor.isNull(_cursorIndexOfRawSmsBody)) {
              _tmpRawSmsBody = null;
            } else {
              _tmpRawSmsBody = _cursor.getString(_cursorIndexOfRawSmsBody);
            }
            final Integer _tmpSimSubscriptionId;
            if (_cursor.isNull(_cursorIndexOfSimSubscriptionId)) {
              _tmpSimSubscriptionId = null;
            } else {
              _tmpSimSubscriptionId = _cursor.getInt(_cursorIndexOfSimSubscriptionId);
            }
            _item = new TransactionEntity(_tmpReceiptNumber,_tmpType,_tmpAmount,_tmpTransactionCost,_tmpDateTimestamp,_tmpRecipientName,_tmpRecipientNumber,_tmpBalance,_tmpCategoryId,_tmpIsRecurring,_tmpIsIncome,_tmpFulizaAmount,_tmpFulizaFee,_tmpRawSmsBody,_tmpSimSubscriptionId);
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
  public Flow<Double> getTotalSpentBetween(final long startDate, final long endDate,
      final List<String> types, final Integer simId) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT SUM(amount) FROM transactions WHERE type IN (");
    final int _inputSize = types.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(") AND dateTimestamp BETWEEN ");
    _stringBuilder.append("?");
    _stringBuilder.append(" AND ");
    _stringBuilder.append("?");
    _stringBuilder.append(" AND (");
    _stringBuilder.append("?");
    _stringBuilder.append(" IS NULL OR simSubscriptionId = ");
    _stringBuilder.append("?");
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 4 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : types) {
      if (_item == null) {
        _statement.bindNull(_argIndex);
      } else {
        _statement.bindString(_argIndex, _item);
      }
      _argIndex++;
    }
    _argIndex = 1 + _inputSize;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 2 + _inputSize;
    _statement.bindLong(_argIndex, endDate);
    _argIndex = 3 + _inputSize;
    if (simId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, simId);
    }
    _argIndex = 4 + _inputSize;
    if (simId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, simId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
          } else {
            _result = null;
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
  public Flow<Double> getTotalFeesBetween(final long startDate, final long endDate,
      final Integer simId) {
    final String _sql = "SELECT SUM(transactionCost) FROM transactions WHERE dateTimestamp BETWEEN ? AND ? AND (? IS NULL OR simSubscriptionId = ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDate);
    _argIndex = 3;
    if (simId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, simId);
    }
    _argIndex = 4;
    if (simId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, simId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
          } else {
            _result = null;
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
  public Flow<Double> getTotalIncomeBetween(final long startDate, final long endDate,
      final Integer simId) {
    final String _sql = "SELECT SUM(amount) FROM transactions WHERE isIncome = 1 AND dateTimestamp BETWEEN ? AND ? AND (? IS NULL OR simSubscriptionId = ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDate);
    _argIndex = 3;
    if (simId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, simId);
    }
    _argIndex = 4;
    if (simId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, simId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
          } else {
            _result = null;
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
  public Flow<Double> getSpentForPersonBetween(final String personName, final long startDate,
      final long endDate, final Integer simId) {
    final String _sql = "SELECT SUM(amount) FROM transactions WHERE isIncome = 0 AND (recipientName LIKE '%' || ? || '%' OR recipientNumber LIKE '%' || ? || '%') AND dateTimestamp BETWEEN ? AND ? AND (? IS NULL OR simSubscriptionId = ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 6);
    int _argIndex = 1;
    if (personName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, personName);
    }
    _argIndex = 2;
    if (personName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, personName);
    }
    _argIndex = 3;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 4;
    _statement.bindLong(_argIndex, endDate);
    _argIndex = 5;
    if (simId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, simId);
    }
    _argIndex = 6;
    if (simId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, simId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
          } else {
            _result = null;
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
  public Flow<Double> getIncomeFromPersonBetween(final String personName, final long startDate,
      final long endDate, final Integer simId) {
    final String _sql = "SELECT SUM(amount) FROM transactions WHERE isIncome = 1 AND (recipientName LIKE '%' || ? || '%' OR recipientNumber LIKE '%' || ? || '%') AND dateTimestamp BETWEEN ? AND ? AND (? IS NULL OR simSubscriptionId = ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 6);
    int _argIndex = 1;
    if (personName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, personName);
    }
    _argIndex = 2;
    if (personName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, personName);
    }
    _argIndex = 3;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 4;
    _statement.bindLong(_argIndex, endDate);
    _argIndex = 5;
    if (simId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, simId);
    }
    _argIndex = 6;
    if (simId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, simId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
          } else {
            _result = null;
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
  public Flow<Double> getFeesForPersonBetween(final String personName, final long startDate,
      final long endDate, final Integer simId) {
    final String _sql = "SELECT SUM(transactionCost) FROM transactions WHERE (recipientName LIKE '%' || ? || '%' OR recipientNumber LIKE '%' || ? || '%') AND dateTimestamp BETWEEN ? AND ? AND (? IS NULL OR simSubscriptionId = ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 6);
    int _argIndex = 1;
    if (personName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, personName);
    }
    _argIndex = 2;
    if (personName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, personName);
    }
    _argIndex = 3;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 4;
    _statement.bindLong(_argIndex, endDate);
    _argIndex = 5;
    if (simId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, simId);
    }
    _argIndex = 6;
    if (simId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, simId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
          } else {
            _result = null;
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
  public Object getTransactionByReceipt(final String receiptNumber,
      final Continuation<? super TransactionEntity> $completion) {
    final String _sql = "SELECT * FROM transactions WHERE receiptNumber = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (receiptNumber == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, receiptNumber);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TransactionEntity>() {
      @Override
      @Nullable
      public TransactionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfReceiptNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptNumber");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTransactionCost = CursorUtil.getColumnIndexOrThrow(_cursor, "transactionCost");
          final int _cursorIndexOfDateTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTimestamp");
          final int _cursorIndexOfRecipientName = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientName");
          final int _cursorIndexOfRecipientNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientNumber");
          final int _cursorIndexOfBalance = CursorUtil.getColumnIndexOrThrow(_cursor, "balance");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfIsRecurring = CursorUtil.getColumnIndexOrThrow(_cursor, "isRecurring");
          final int _cursorIndexOfIsIncome = CursorUtil.getColumnIndexOrThrow(_cursor, "isIncome");
          final int _cursorIndexOfFulizaAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "fulizaAmount");
          final int _cursorIndexOfFulizaFee = CursorUtil.getColumnIndexOrThrow(_cursor, "fulizaFee");
          final int _cursorIndexOfRawSmsBody = CursorUtil.getColumnIndexOrThrow(_cursor, "rawSmsBody");
          final int _cursorIndexOfSimSubscriptionId = CursorUtil.getColumnIndexOrThrow(_cursor, "simSubscriptionId");
          final TransactionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpReceiptNumber;
            if (_cursor.isNull(_cursorIndexOfReceiptNumber)) {
              _tmpReceiptNumber = null;
            } else {
              _tmpReceiptNumber = _cursor.getString(_cursorIndexOfReceiptNumber);
            }
            final TransactionType _tmpType;
            _tmpType = __TransactionType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final double _tmpTransactionCost;
            _tmpTransactionCost = _cursor.getDouble(_cursorIndexOfTransactionCost);
            final long _tmpDateTimestamp;
            _tmpDateTimestamp = _cursor.getLong(_cursorIndexOfDateTimestamp);
            final String _tmpRecipientName;
            if (_cursor.isNull(_cursorIndexOfRecipientName)) {
              _tmpRecipientName = null;
            } else {
              _tmpRecipientName = _cursor.getString(_cursorIndexOfRecipientName);
            }
            final String _tmpRecipientNumber;
            if (_cursor.isNull(_cursorIndexOfRecipientNumber)) {
              _tmpRecipientNumber = null;
            } else {
              _tmpRecipientNumber = _cursor.getString(_cursorIndexOfRecipientNumber);
            }
            final Double _tmpBalance;
            if (_cursor.isNull(_cursorIndexOfBalance)) {
              _tmpBalance = null;
            } else {
              _tmpBalance = _cursor.getDouble(_cursorIndexOfBalance);
            }
            final Integer _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getInt(_cursorIndexOfCategoryId);
            }
            final boolean _tmpIsRecurring;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRecurring);
            _tmpIsRecurring = _tmp != 0;
            final boolean _tmpIsIncome;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsIncome);
            _tmpIsIncome = _tmp_1 != 0;
            final Double _tmpFulizaAmount;
            if (_cursor.isNull(_cursorIndexOfFulizaAmount)) {
              _tmpFulizaAmount = null;
            } else {
              _tmpFulizaAmount = _cursor.getDouble(_cursorIndexOfFulizaAmount);
            }
            final Double _tmpFulizaFee;
            if (_cursor.isNull(_cursorIndexOfFulizaFee)) {
              _tmpFulizaFee = null;
            } else {
              _tmpFulizaFee = _cursor.getDouble(_cursorIndexOfFulizaFee);
            }
            final String _tmpRawSmsBody;
            if (_cursor.isNull(_cursorIndexOfRawSmsBody)) {
              _tmpRawSmsBody = null;
            } else {
              _tmpRawSmsBody = _cursor.getString(_cursorIndexOfRawSmsBody);
            }
            final Integer _tmpSimSubscriptionId;
            if (_cursor.isNull(_cursorIndexOfSimSubscriptionId)) {
              _tmpSimSubscriptionId = null;
            } else {
              _tmpSimSubscriptionId = _cursor.getInt(_cursorIndexOfSimSubscriptionId);
            }
            _result = new TransactionEntity(_tmpReceiptNumber,_tmpType,_tmpAmount,_tmpTransactionCost,_tmpDateTimestamp,_tmpRecipientName,_tmpRecipientNumber,_tmpBalance,_tmpCategoryId,_tmpIsRecurring,_tmpIsIncome,_tmpFulizaAmount,_tmpFulizaFee,_tmpRawSmsBody,_tmpSimSubscriptionId);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CategoryExpense>> getExpensesByCategory(final long startDate, final long endDate,
      final Integer simId) {
    final String _sql = "\n"
            + "        SELECT \n"
            + "            c.id as categoryId,\n"
            + "            c.name as categoryName, \n"
            + "            c.colorCode as colorCode, \n"
            + "            SUM(t.amount) as totalAmount,\n"
            + "            b.monthlyLimit as budgetLimit\n"
            + "        FROM transactions t\n"
            + "        LEFT JOIN categories c ON t.categoryId = c.id\n"
            + "        LEFT JOIN budgets b ON c.id = b.categoryId\n"
            + "        WHERE t.dateTimestamp BETWEEN ? AND ?\n"
            + "        AND t.isIncome = 0\n"
            + "        AND (? IS NULL OR t.simSubscriptionId = ?)\n"
            + "        GROUP BY c.id\n"
            + "        ORDER BY totalAmount DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDate);
    _argIndex = 3;
    if (simId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, simId);
    }
    _argIndex = 4;
    if (simId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, simId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions", "categories",
        "budgets"}, new Callable<List<CategoryExpense>>() {
      @Override
      @NonNull
      public List<CategoryExpense> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCategoryId = 0;
          final int _cursorIndexOfCategoryName = 1;
          final int _cursorIndexOfColorCode = 2;
          final int _cursorIndexOfTotalAmount = 3;
          final int _cursorIndexOfBudgetLimit = 4;
          final List<CategoryExpense> _result = new ArrayList<CategoryExpense>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CategoryExpense _item;
            final Integer _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getInt(_cursorIndexOfCategoryId);
            }
            final String _tmpCategoryName;
            if (_cursor.isNull(_cursorIndexOfCategoryName)) {
              _tmpCategoryName = null;
            } else {
              _tmpCategoryName = _cursor.getString(_cursorIndexOfCategoryName);
            }
            final String _tmpColorCode;
            if (_cursor.isNull(_cursorIndexOfColorCode)) {
              _tmpColorCode = null;
            } else {
              _tmpColorCode = _cursor.getString(_cursorIndexOfColorCode);
            }
            final double _tmpTotalAmount;
            _tmpTotalAmount = _cursor.getDouble(_cursorIndexOfTotalAmount);
            final Double _tmpBudgetLimit;
            if (_cursor.isNull(_cursorIndexOfBudgetLimit)) {
              _tmpBudgetLimit = null;
            } else {
              _tmpBudgetLimit = _cursor.getDouble(_cursorIndexOfBudgetLimit);
            }
            _item = new CategoryExpense(_tmpCategoryId,_tmpCategoryName,_tmpColorCode,_tmpTotalAmount,_tmpBudgetLimit);
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
  public Flow<List<DailySpend>> getDailySpendingTrend(final long startDate, final long endDate,
      final Integer simId) {
    final String _sql = "\n"
            + "        SELECT \n"
            + "            strftime('%Y-%m-%d', dateTimestamp / 1000, 'unixepoch', 'localtime') as dateString,\n"
            + "            SUM(amount) as totalAmount\n"
            + "        FROM transactions\n"
            + "        WHERE dateTimestamp BETWEEN ? AND ?\n"
            + "        AND isIncome = 0\n"
            + "        AND (? IS NULL OR simSubscriptionId = ?)\n"
            + "        GROUP BY dateString\n"
            + "        ORDER BY dateString ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDate);
    _argIndex = 3;
    if (simId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, simId);
    }
    _argIndex = 4;
    if (simId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, simId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<DailySpend>>() {
      @Override
      @NonNull
      public List<DailySpend> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDateString = 0;
          final int _cursorIndexOfTotalAmount = 1;
          final List<DailySpend> _result = new ArrayList<DailySpend>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailySpend _item;
            final String _tmpDateString;
            if (_cursor.isNull(_cursorIndexOfDateString)) {
              _tmpDateString = null;
            } else {
              _tmpDateString = _cursor.getString(_cursorIndexOfDateString);
            }
            final double _tmpTotalAmount;
            _tmpTotalAmount = _cursor.getDouble(_cursorIndexOfTotalAmount);
            _item = new DailySpend(_tmpDateString,_tmpTotalAmount);
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

  private String __TransactionType_enumToString(@NonNull final TransactionType _value) {
    switch (_value) {
      case PAYBILL: return "PAYBILL";
      case BUY_GOODS: return "BUY_GOODS";
      case SEND_MONEY: return "SEND_MONEY";
      case RECEIVED_MONEY: return "RECEIVED_MONEY";
      case WITHDRAW_CASH: return "WITHDRAW_CASH";
      case BUY_AIRTIME: return "BUY_AIRTIME";
      case UNKNOWN: return "UNKNOWN";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private TransactionType __TransactionType_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "PAYBILL": return TransactionType.PAYBILL;
      case "BUY_GOODS": return TransactionType.BUY_GOODS;
      case "SEND_MONEY": return TransactionType.SEND_MONEY;
      case "RECEIVED_MONEY": return TransactionType.RECEIVED_MONEY;
      case "WITHDRAW_CASH": return TransactionType.WITHDRAW_CASH;
      case "BUY_AIRTIME": return TransactionType.BUY_AIRTIME;
      case "UNKNOWN": return TransactionType.UNKNOWN;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
