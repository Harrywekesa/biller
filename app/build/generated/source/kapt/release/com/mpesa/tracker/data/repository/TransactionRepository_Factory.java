package com.mpesa.tracker.data.repository;

import com.mpesa.tracker.data.local.dao.BudgetDao;
import com.mpesa.tracker.data.local.dao.CategoryDao;
import com.mpesa.tracker.data.local.dao.CustomRuleDao;
import com.mpesa.tracker.data.local.dao.TransactionDao;
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
public final class TransactionRepository_Factory implements Factory<TransactionRepository> {
  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<CustomRuleDao> customRuleDaoProvider;

  private final Provider<BudgetDao> budgetDaoProvider;

  public TransactionRepository_Factory(Provider<TransactionDao> transactionDaoProvider,
      Provider<CategoryDao> categoryDaoProvider, Provider<CustomRuleDao> customRuleDaoProvider,
      Provider<BudgetDao> budgetDaoProvider) {
    this.transactionDaoProvider = transactionDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
    this.customRuleDaoProvider = customRuleDaoProvider;
    this.budgetDaoProvider = budgetDaoProvider;
  }

  @Override
  public TransactionRepository get() {
    return newInstance(transactionDaoProvider.get(), categoryDaoProvider.get(), customRuleDaoProvider.get(), budgetDaoProvider.get());
  }

  public static TransactionRepository_Factory create(
      Provider<TransactionDao> transactionDaoProvider, Provider<CategoryDao> categoryDaoProvider,
      Provider<CustomRuleDao> customRuleDaoProvider, Provider<BudgetDao> budgetDaoProvider) {
    return new TransactionRepository_Factory(transactionDaoProvider, categoryDaoProvider, customRuleDaoProvider, budgetDaoProvider);
  }

  public static TransactionRepository newInstance(TransactionDao transactionDao,
      CategoryDao categoryDao, CustomRuleDao customRuleDao, BudgetDao budgetDao) {
    return new TransactionRepository(transactionDao, categoryDao, customRuleDao, budgetDao);
  }
}
