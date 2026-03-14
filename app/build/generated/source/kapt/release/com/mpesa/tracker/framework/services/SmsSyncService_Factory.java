package com.mpesa.tracker.framework.services;

import android.content.Context;
import com.mpesa.tracker.data.repository.TransactionRepository;
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

  public SmsSyncService_Factory(Provider<Context> contextProvider,
      Provider<TransactionRepository> transactionRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.transactionRepositoryProvider = transactionRepositoryProvider;
  }

  @Override
  public SmsSyncService get() {
    return newInstance(contextProvider.get(), transactionRepositoryProvider.get());
  }

  public static SmsSyncService_Factory create(Provider<Context> contextProvider,
      Provider<TransactionRepository> transactionRepositoryProvider) {
    return new SmsSyncService_Factory(contextProvider, transactionRepositoryProvider);
  }

  public static SmsSyncService newInstance(Context context,
      TransactionRepository transactionRepository) {
    return new SmsSyncService(context, transactionRepository);
  }
}
