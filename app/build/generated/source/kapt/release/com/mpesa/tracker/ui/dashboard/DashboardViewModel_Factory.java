package com.mpesa.tracker.ui.dashboard;

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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<TransactionRepository> repositoryProvider;

  private final Provider<SmsSyncService> smsSyncServiceProvider;

  public DashboardViewModel_Factory(Provider<TransactionRepository> repositoryProvider,
      Provider<SmsSyncService> smsSyncServiceProvider) {
    this.repositoryProvider = repositoryProvider;
    this.smsSyncServiceProvider = smsSyncServiceProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(repositoryProvider.get(), smsSyncServiceProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<TransactionRepository> repositoryProvider,
      Provider<SmsSyncService> smsSyncServiceProvider) {
    return new DashboardViewModel_Factory(repositoryProvider, smsSyncServiceProvider);
  }

  public static DashboardViewModel newInstance(TransactionRepository repository,
      SmsSyncService smsSyncService) {
    return new DashboardViewModel(repository, smsSyncService);
  }
}
