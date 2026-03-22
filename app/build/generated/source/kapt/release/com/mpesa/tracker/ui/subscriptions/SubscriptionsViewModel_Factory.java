package com.mpesa.tracker.ui.subscriptions;

import com.mpesa.tracker.data.repository.TransactionRepository;
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
public final class SubscriptionsViewModel_Factory implements Factory<SubscriptionsViewModel> {
  private final Provider<TransactionRepository> repositoryProvider;

  public SubscriptionsViewModel_Factory(Provider<TransactionRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SubscriptionsViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static SubscriptionsViewModel_Factory create(
      Provider<TransactionRepository> repositoryProvider) {
    return new SubscriptionsViewModel_Factory(repositoryProvider);
  }

  public static SubscriptionsViewModel newInstance(TransactionRepository repository) {
    return new SubscriptionsViewModel(repository);
  }
}
