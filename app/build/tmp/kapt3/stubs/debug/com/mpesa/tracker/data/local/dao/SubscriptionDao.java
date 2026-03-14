package com.mpesa.tracker.data.local.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\bg\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0018\u0010\u0006\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0007\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0016\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0016\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\r\u00a8\u0006\u000f"}, d2 = {"Lcom/mpesa/tracker/data/local/dao/SubscriptionDao;", "", "getActiveSubscriptions", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/mpesa/tracker/data/local/entities/SubscriptionEntity;", "getSubscriptionByMatcher", "matcher", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertSubscription", "", "subscription", "(Lcom/mpesa/tracker/data/local/entities/SubscriptionEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateSubscription", "app_debug"})
@androidx.room.Dao()
public abstract interface SubscriptionDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertSubscription(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.entities.SubscriptionEntity subscription, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateSubscription(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.entities.SubscriptionEntity subscription, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM subscriptions WHERE isActive = 1 ORDER BY expectedNextPaymentDate ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.mpesa.tracker.data.local.entities.SubscriptionEntity>> getActiveSubscriptions();
    
    @androidx.room.Query(value = "SELECT * FROM subscriptions WHERE merchantNameMatcher = :matcher LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getSubscriptionByMatcher(@org.jetbrains.annotations.NotNull()
    java.lang.String matcher, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.mpesa.tracker.data.local.entities.SubscriptionEntity> $completion);
}