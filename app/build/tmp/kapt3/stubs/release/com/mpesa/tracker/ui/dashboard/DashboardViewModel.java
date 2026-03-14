package com.mpesa.tracker.ui.dashboard;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0013\u001a\u00020\u0014R\u0016\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u001d\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u000e0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\f\u00a8\u0006\u0015"}, d2 = {"Lcom/mpesa/tracker/ui/dashboard/DashboardViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/mpesa/tracker/data/repository/TransactionRepository;", "(Lcom/mpesa/tracker/data/repository/TransactionRepository;)V", "currencyFormatter", "Ljava/text/NumberFormat;", "kotlin.jvm.PlatformType", "monthSpend", "Lkotlinx/coroutines/flow/StateFlow;", "", "getMonthSpend", "()Lkotlinx/coroutines/flow/StateFlow;", "recentTransactions", "", "Lcom/mpesa/tracker/data/local/entities/TransactionEntity;", "getRecentTransactions", "totalBalance", "getTotalBalance", "syncHistoricSms", "", "app_release"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class DashboardViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.mpesa.tracker.data.repository.TransactionRepository repository = null;
    private final java.text.NumberFormat currencyFormatter = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.mpesa.tracker.data.local.entities.TransactionEntity>> recentTransactions = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> monthSpend = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> totalBalance = null;
    
    @javax.inject.Inject()
    public DashboardViewModel(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.repository.TransactionRepository repository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.mpesa.tracker.data.local.entities.TransactionEntity>> getRecentTransactions() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getMonthSpend() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getTotalBalance() {
        return null;
    }
    
    public final void syncHistoricSms() {
    }
}