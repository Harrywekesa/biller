package com.mpesa.tracker.di;

import com.mpesa.tracker.data.local.AppDatabase;
import com.mpesa.tracker.data.local.dao.CustomRuleDao;
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
public final class DatabaseModule_ProvideCustomRuleDaoFactory implements Factory<CustomRuleDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideCustomRuleDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public CustomRuleDao get() {
    return provideCustomRuleDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideCustomRuleDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideCustomRuleDaoFactory(databaseProvider);
  }

  public static CustomRuleDao provideCustomRuleDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideCustomRuleDao(database));
  }
}
