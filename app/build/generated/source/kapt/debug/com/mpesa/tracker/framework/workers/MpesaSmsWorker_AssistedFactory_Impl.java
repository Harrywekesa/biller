package com.mpesa.tracker.framework.workers;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MpesaSmsWorker_AssistedFactory_Impl implements MpesaSmsWorker_AssistedFactory {
  private final MpesaSmsWorker_Factory delegateFactory;

  MpesaSmsWorker_AssistedFactory_Impl(MpesaSmsWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public MpesaSmsWorker create(Context arg0, WorkerParameters arg1) {
    return delegateFactory.get(arg0, arg1);
  }

  public static Provider<MpesaSmsWorker_AssistedFactory> create(
      MpesaSmsWorker_Factory delegateFactory) {
    return InstanceFactory.create(new MpesaSmsWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<MpesaSmsWorker_AssistedFactory> createFactoryProvider(
      MpesaSmsWorker_Factory delegateFactory) {
    return InstanceFactory.create(new MpesaSmsWorker_AssistedFactory_Impl(delegateFactory));
  }
}
