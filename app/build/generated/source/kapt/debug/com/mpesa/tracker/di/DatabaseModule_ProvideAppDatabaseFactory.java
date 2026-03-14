package com.mpesa.tracker.di;

import android.content.Context;
import com.mpesa.tracker.data.local.AppDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideAppDatabaseFactory implements Factory<AppDatabase> {
  private final Provider<Context> contextProvider;

  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideAppDatabaseFactory(Provider<Context> contextProvider,
      Provider<AppDatabase> databaseProvider) {
    this.contextProvider = contextProvider;
    this.databaseProvider = databaseProvider;
  }

  @Override
  public AppDatabase get() {
    return provideAppDatabase(contextProvider.get(), databaseProvider);
  }

  public static DatabaseModule_ProvideAppDatabaseFactory create(Provider<Context> contextProvider,
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideAppDatabaseFactory(contextProvider, databaseProvider);
  }

  public static AppDatabase provideAppDatabase(Context context,
      Provider<AppDatabase> databaseProvider) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideAppDatabase(context, databaseProvider));
  }
}
