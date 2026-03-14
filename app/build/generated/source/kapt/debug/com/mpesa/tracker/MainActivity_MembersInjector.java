package com.mpesa.tracker;

import com.mpesa.tracker.data.local.dao.CategoryDao;
import com.mpesa.tracker.data.local.dao.SubscriptionDao;
import com.mpesa.tracker.framework.services.SmsSyncService;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<SmsSyncService> smsSyncServiceProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<SubscriptionDao> subscriptionDaoProvider;

  public MainActivity_MembersInjector(Provider<SmsSyncService> smsSyncServiceProvider,
      Provider<CategoryDao> categoryDaoProvider,
      Provider<SubscriptionDao> subscriptionDaoProvider) {
    this.smsSyncServiceProvider = smsSyncServiceProvider;
    this.categoryDaoProvider = categoryDaoProvider;
    this.subscriptionDaoProvider = subscriptionDaoProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<SmsSyncService> smsSyncServiceProvider, Provider<CategoryDao> categoryDaoProvider,
      Provider<SubscriptionDao> subscriptionDaoProvider) {
    return new MainActivity_MembersInjector(smsSyncServiceProvider, categoryDaoProvider, subscriptionDaoProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectSmsSyncService(instance, smsSyncServiceProvider.get());
    injectCategoryDao(instance, categoryDaoProvider.get());
    injectSubscriptionDao(instance, subscriptionDaoProvider.get());
  }

  @InjectedFieldSignature("com.mpesa.tracker.MainActivity.smsSyncService")
  public static void injectSmsSyncService(MainActivity instance, SmsSyncService smsSyncService) {
    instance.smsSyncService = smsSyncService;
  }

  @InjectedFieldSignature("com.mpesa.tracker.MainActivity.categoryDao")
  public static void injectCategoryDao(MainActivity instance, CategoryDao categoryDao) {
    instance.categoryDao = categoryDao;
  }

  @InjectedFieldSignature("com.mpesa.tracker.MainActivity.subscriptionDao")
  public static void injectSubscriptionDao(MainActivity instance, SubscriptionDao subscriptionDao) {
    instance.subscriptionDao = subscriptionDao;
  }
}
