package com.mpesa.tracker.domain.classifier;

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
public final class CategorizationEngine_Factory implements Factory<CategorizationEngine> {
  private final Provider<DictionaryClassifier> dictionaryClassifierProvider;

  private final Provider<TensorFlowLiteClassifier> mlClassifierProvider;

  public CategorizationEngine_Factory(Provider<DictionaryClassifier> dictionaryClassifierProvider,
      Provider<TensorFlowLiteClassifier> mlClassifierProvider) {
    this.dictionaryClassifierProvider = dictionaryClassifierProvider;
    this.mlClassifierProvider = mlClassifierProvider;
  }

  @Override
  public CategorizationEngine get() {
    return newInstance(dictionaryClassifierProvider.get(), mlClassifierProvider.get());
  }

  public static CategorizationEngine_Factory create(
      Provider<DictionaryClassifier> dictionaryClassifierProvider,
      Provider<TensorFlowLiteClassifier> mlClassifierProvider) {
    return new CategorizationEngine_Factory(dictionaryClassifierProvider, mlClassifierProvider);
  }

  public static CategorizationEngine newInstance(DictionaryClassifier dictionaryClassifier,
      TensorFlowLiteClassifier mlClassifier) {
    return new CategorizationEngine(dictionaryClassifier, mlClassifier);
  }
}
