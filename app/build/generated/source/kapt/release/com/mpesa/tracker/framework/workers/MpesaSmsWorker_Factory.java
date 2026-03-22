package com.mpesa.tracker.framework.workers;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.mpesa.tracker.data.local.dao.TransactionDao;
import com.mpesa.tracker.domain.classifier.CategorizationEngine;
import dagger.internal.DaggerGenerated;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class MpesaSmsWorker_Factory {
  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<CategorizationEngine> categorizationEngineProvider;

  public MpesaSmsWorker_Factory(Provider<TransactionDao> transactionDaoProvider,
      Provider<CategorizationEngine> categorizationEngineProvider) {
    this.transactionDaoProvider = transactionDaoProvider;
    this.categorizationEngineProvider = categorizationEngineProvider;
  }

  public MpesaSmsWorker get(Context context, WorkerParameters workerParams) {
    return newInstance(context, workerParams, transactionDaoProvider.get(), categorizationEngineProvider.get());
  }

  public static MpesaSmsWorker_Factory create(Provider<TransactionDao> transactionDaoProvider,
      Provider<CategorizationEngine> categorizationEngineProvider) {
    return new MpesaSmsWorker_Factory(transactionDaoProvider, categorizationEngineProvider);
  }

  public static MpesaSmsWorker newInstance(Context context, WorkerParameters workerParams,
      TransactionDao transactionDao, CategorizationEngine categorizationEngine) {
    return new MpesaSmsWorker(context, workerParams, transactionDao, categorizationEngine);
  }
}
