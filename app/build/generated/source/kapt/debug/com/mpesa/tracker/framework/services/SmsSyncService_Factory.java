package com.mpesa.tracker.framework.services;

import android.content.Context;
import com.mpesa.tracker.data.repository.TransactionRepository;
import com.mpesa.tracker.domain.classifier.CategorizationEngine;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class SmsSyncService_Factory implements Factory<SmsSyncService> {
  private final Provider<Context> contextProvider;

  private final Provider<TransactionRepository> transactionRepositoryProvider;

  private final Provider<CategorizationEngine> categorizationEngineProvider;

  public SmsSyncService_Factory(Provider<Context> contextProvider,
      Provider<TransactionRepository> transactionRepositoryProvider,
      Provider<CategorizationEngine> categorizationEngineProvider) {
    this.contextProvider = contextProvider;
    this.transactionRepositoryProvider = transactionRepositoryProvider;
    this.categorizationEngineProvider = categorizationEngineProvider;
  }

  @Override
  public SmsSyncService get() {
    return newInstance(contextProvider.get(), transactionRepositoryProvider.get(), categorizationEngineProvider.get());
  }

  public static SmsSyncService_Factory create(Provider<Context> contextProvider,
      Provider<TransactionRepository> transactionRepositoryProvider,
      Provider<CategorizationEngine> categorizationEngineProvider) {
    return new SmsSyncService_Factory(contextProvider, transactionRepositoryProvider, categorizationEngineProvider);
  }

  public static SmsSyncService newInstance(Context context,
      TransactionRepository transactionRepository, CategorizationEngine categorizationEngine) {
    return new SmsSyncService(context, transactionRepository, categorizationEngine);
  }
}
