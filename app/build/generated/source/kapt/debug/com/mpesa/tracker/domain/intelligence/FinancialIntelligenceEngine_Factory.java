package com.mpesa.tracker.domain.intelligence;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class FinancialIntelligenceEngine_Factory implements Factory<FinancialIntelligenceEngine> {
  @Override
  public FinancialIntelligenceEngine get() {
    return newInstance();
  }

  public static FinancialIntelligenceEngine_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FinancialIntelligenceEngine newInstance() {
    return new FinancialIntelligenceEngine();
  }

  private static final class InstanceHolder {
    private static final FinancialIntelligenceEngine_Factory INSTANCE = new FinancialIntelligenceEngine_Factory();
  }
}
