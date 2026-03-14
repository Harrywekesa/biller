package com.mpesa.tracker.data.repository;

import com.mpesa.tracker.data.local.dao.SubscriptionDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class SubscriptionRepository_Factory implements Factory<SubscriptionRepository> {
  private final Provider<SubscriptionDao> subscriptionDaoProvider;

  public SubscriptionRepository_Factory(Provider<SubscriptionDao> subscriptionDaoProvider) {
    this.subscriptionDaoProvider = subscriptionDaoProvider;
  }

  @Override
  public SubscriptionRepository get() {
    return newInstance(subscriptionDaoProvider.get());
  }

  public static SubscriptionRepository_Factory create(
      Provider<SubscriptionDao> subscriptionDaoProvider) {
    return new SubscriptionRepository_Factory(subscriptionDaoProvider);
  }

  public static SubscriptionRepository newInstance(SubscriptionDao subscriptionDao) {
    return new SubscriptionRepository(subscriptionDao);
  }
}
