package com.mpesa.tracker.ui.transactions;

import com.mpesa.tracker.data.repository.TransactionRepository;
import com.mpesa.tracker.framework.services.SmsSyncService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class TransactionsViewModel_Factory implements Factory<TransactionsViewModel> {
  private final Provider<TransactionRepository> repositoryProvider;

  private final Provider<SmsSyncService> smsSyncServiceProvider;

  public TransactionsViewModel_Factory(Provider<TransactionRepository> repositoryProvider,
      Provider<SmsSyncService> smsSyncServiceProvider) {
    this.repositoryProvider = repositoryProvider;
    this.smsSyncServiceProvider = smsSyncServiceProvider;
  }

  @Override
  public TransactionsViewModel get() {
    return newInstance(repositoryProvider.get(), smsSyncServiceProvider.get());
  }

  public static TransactionsViewModel_Factory create(
      Provider<TransactionRepository> repositoryProvider,
      Provider<SmsSyncService> smsSyncServiceProvider) {
    return new TransactionsViewModel_Factory(repositoryProvider, smsSyncServiceProvider);
  }

  public static TransactionsViewModel newInstance(TransactionRepository repository,
      SmsSyncService smsSyncService) {
    return new TransactionsViewModel(repository, smsSyncService);
  }
}
