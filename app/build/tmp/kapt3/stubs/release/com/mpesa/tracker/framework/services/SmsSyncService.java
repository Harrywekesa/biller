package com.mpesa.tracker.framework.services;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B!\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u000e\u0010\t\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000bR\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/mpesa/tracker/framework/services/SmsSyncService;", "", "context", "Landroid/content/Context;", "transactionRepository", "Lcom/mpesa/tracker/data/repository/TransactionRepository;", "categorizationEngine", "Lcom/mpesa/tracker/domain/classifier/CategorizationEngine;", "(Landroid/content/Context;Lcom/mpesa/tracker/data/repository/TransactionRepository;Lcom/mpesa/tracker/domain/classifier/CategorizationEngine;)V", "syncHistoricMessages", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_release"})
public final class SmsSyncService {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.mpesa.tracker.data.repository.TransactionRepository transactionRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.mpesa.tracker.domain.classifier.CategorizationEngine categorizationEngine = null;
    
    @javax.inject.Inject()
    public SmsSyncService(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.repository.TransactionRepository transactionRepository, @org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.domain.classifier.CategorizationEngine categorizationEngine) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object syncHistoricMessages(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
}