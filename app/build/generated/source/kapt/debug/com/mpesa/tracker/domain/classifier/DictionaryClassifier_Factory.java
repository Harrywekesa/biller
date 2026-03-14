package com.mpesa.tracker.domain.classifier;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class DictionaryClassifier_Factory implements Factory<DictionaryClassifier> {
  @Override
  public DictionaryClassifier get() {
    return newInstance();
  }

  public static DictionaryClassifier_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DictionaryClassifier newInstance() {
    return new DictionaryClassifier();
  }

  private static final class InstanceHolder {
    private static final DictionaryClassifier_Factory INSTANCE = new DictionaryClassifier_Factory();
  }
}
