package com.mpesa.tracker;

import androidx.hilt.work.HiltWorkerFactory;
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
public final class MpesaTrackerApp_MembersInjector implements MembersInjector<MpesaTrackerApp> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public MpesaTrackerApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<MpesaTrackerApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new MpesaTrackerApp_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(MpesaTrackerApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.mpesa.tracker.MpesaTrackerApp.workerFactory")
  public static void injectWorkerFactory(MpesaTrackerApp instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
