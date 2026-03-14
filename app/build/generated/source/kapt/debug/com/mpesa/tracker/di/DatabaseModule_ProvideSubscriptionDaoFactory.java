package com.mpesa.tracker.di;

import com.mpesa.tracker.data.local.AppDatabase;
import com.mpesa.tracker.data.local.dao.SubscriptionDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideSubscriptionDaoFactory implements Factory<SubscriptionDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideSubscriptionDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public SubscriptionDao get() {
    return provideSubscriptionDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideSubscriptionDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideSubscriptionDaoFactory(databaseProvider);
  }

  public static SubscriptionDao provideSubscriptionDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideSubscriptionDao(database));
  }
}
