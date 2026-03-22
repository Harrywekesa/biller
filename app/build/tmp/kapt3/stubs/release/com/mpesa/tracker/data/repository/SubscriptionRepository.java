package com.mpesa.tracker.data.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0012\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006J\u0018\u0010\t\u001a\u0004\u0018\u00010\b2\u0006\u0010\n\u001a\u00020\u000bH\u0086@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\u0010R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/mpesa/tracker/data/repository/SubscriptionRepository;", "", "subscriptionDao", "Lcom/mpesa/tracker/data/local/dao/SubscriptionDao;", "(Lcom/mpesa/tracker/data/local/dao/SubscriptionDao;)V", "getActiveSubscriptions", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/mpesa/tracker/data/local/entities/SubscriptionEntity;", "getSubscriptionByMatcher", "matcher", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertOrUpdateSubscription", "", "subscription", "(Lcom/mpesa/tracker/data/local/entities/SubscriptionEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_release"})
public final class SubscriptionRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.mpesa.tracker.data.local.dao.SubscriptionDao subscriptionDao = null;
    
    @javax.inject.Inject()
    public SubscriptionRepository(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.dao.SubscriptionDao subscriptionDao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.mpesa.tracker.data.local.entities.SubscriptionEntity>> getActiveSubscriptions() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertOrUpdateSubscription(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.entities.SubscriptionEntity subscription, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getSubscriptionByMatcher(@org.jetbrains.annotations.NotNull()
    java.lang.String matcher, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.mpesa.tracker.data.local.entities.SubscriptionEntity> $completion) {
        return null;
    }
}