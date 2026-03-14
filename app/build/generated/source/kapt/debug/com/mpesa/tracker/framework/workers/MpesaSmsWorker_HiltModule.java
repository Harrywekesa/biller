package com.mpesa.tracker.framework.workers;

import androidx.hilt.work.WorkerAssistedFactory;
import androidx.work.ListenableWorker;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import javax.annotation.processing.Generated;

@Generated("androidx.hilt.AndroidXHiltProcessor")
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = MpesaSmsWorker.class
)
public interface MpesaSmsWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.mpesa.tracker.framework.workers.MpesaSmsWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(MpesaSmsWorker_AssistedFactory factory);
}
