package com.mpesa.tracker;

@dagger.hilt.android.AndroidEntryPoint()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0014J\b\u0010 \u001a\u00020\u001dH\u0002R\u001e\u0010\u0003\u001a\u00020\u00048\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\f\u001a\u0010\u0012\f\u0012\n \u000f*\u0004\u0018\u00010\u000e0\u000e0\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0010\u001a\u00020\u00118\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R\u001e\u0010\u0016\u001a\u00020\u00178\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0019\"\u0004\b\u001a\u0010\u001b\u00a8\u0006!"}, d2 = {"Lcom/mpesa/tracker/MainActivity;", "Landroidx/fragment/app/FragmentActivity;", "()V", "categoryDao", "Lcom/mpesa/tracker/data/local/dao/CategoryDao;", "getCategoryDao", "()Lcom/mpesa/tracker/data/local/dao/CategoryDao;", "setCategoryDao", "(Lcom/mpesa/tracker/data/local/dao/CategoryDao;)V", "hasPermissionState", "Landroidx/compose/runtime/MutableState;", "", "requestPermissionLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "", "kotlin.jvm.PlatformType", "smsSyncService", "Lcom/mpesa/tracker/framework/services/SmsSyncService;", "getSmsSyncService", "()Lcom/mpesa/tracker/framework/services/SmsSyncService;", "setSmsSyncService", "(Lcom/mpesa/tracker/framework/services/SmsSyncService;)V", "subscriptionDao", "Lcom/mpesa/tracker/data/local/dao/SubscriptionDao;", "getSubscriptionDao", "()Lcom/mpesa/tracker/data/local/dao/SubscriptionDao;", "setSubscriptionDao", "(Lcom/mpesa/tracker/data/local/dao/SubscriptionDao;)V", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "syncSms", "app_release"})
public final class MainActivity extends androidx.fragment.app.FragmentActivity {
    @javax.inject.Inject()
    public com.mpesa.tracker.framework.services.SmsSyncService smsSyncService;
    @javax.inject.Inject()
    public com.mpesa.tracker.data.local.dao.CategoryDao categoryDao;
    @javax.inject.Inject()
    public com.mpesa.tracker.data.local.dao.SubscriptionDao subscriptionDao;
    @org.jetbrains.annotations.NotNull()
    private androidx.compose.runtime.MutableState<java.lang.Boolean> hasPermissionState;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String> requestPermissionLauncher = null;
    
    public MainActivity() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.mpesa.tracker.framework.services.SmsSyncService getSmsSyncService() {
        return null;
    }
    
    public final void setSmsSyncService(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.framework.services.SmsSyncService p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.mpesa.tracker.data.local.dao.CategoryDao getCategoryDao() {
        return null;
    }
    
    public final void setCategoryDao(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.dao.CategoryDao p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.mpesa.tracker.data.local.dao.SubscriptionDao getSubscriptionDao() {
        return null;
    }
    
    public final void setSubscriptionDao(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.dao.SubscriptionDao p0) {
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void syncSms() {
    }
}