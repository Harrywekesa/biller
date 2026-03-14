package com.mpesa.tracker;

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

  public MainActivity_MembersInjector(Provider<SmsSyncService> smsSyncServiceProvider) {
    this.smsSyncServiceProvider = smsSyncServiceProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<SmsSyncService> smsSyncServiceProvider) {
    return new MainActivity_MembersInjector(smsSyncServiceProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectSmsSyncService(instance, smsSyncServiceProvider.get());
  }

  @InjectedFieldSignature("com.mpesa.tracker.MainActivity.smsSyncService")
  public static void injectSmsSyncService(MainActivity instance, SmsSyncService smsSyncService) {
    instance.smsSyncService = smsSyncService;
  }
}
