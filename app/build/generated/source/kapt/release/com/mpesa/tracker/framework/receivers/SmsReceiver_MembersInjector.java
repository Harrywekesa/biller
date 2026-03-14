package com.mpesa.tracker.framework.receivers;

import com.mpesa.tracker.data.local.dao.TransactionDao;
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
public final class SmsReceiver_MembersInjector implements MembersInjector<SmsReceiver> {
  private final Provider<TransactionDao> transactionDaoProvider;

  public SmsReceiver_MembersInjector(Provider<TransactionDao> transactionDaoProvider) {
    this.transactionDaoProvider = transactionDaoProvider;
  }

  public static MembersInjector<SmsReceiver> create(
      Provider<TransactionDao> transactionDaoProvider) {
    return new SmsReceiver_MembersInjector(transactionDaoProvider);
  }

  @Override
  public void injectMembers(SmsReceiver instance) {
    injectTransactionDao(instance, transactionDaoProvider.get());
  }

  @InjectedFieldSignature("com.mpesa.tracker.framework.receivers.SmsReceiver.transactionDao")
  public static void injectTransactionDao(SmsReceiver instance, TransactionDao transactionDao) {
    instance.transactionDao = transactionDao;
  }
}
