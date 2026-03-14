package com.mpesa.tracker.ui.analytics;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020$J\u000e\u0010%\u001a\u00020\"2\u0006\u0010&\u001a\u00020\fJ\u0006\u0010\'\u001a\u00020\"J\u0016\u0010(\u001a\u00020\"2\u0006\u0010)\u001a\u00020*2\u0006\u0010+\u001a\u00020,J\u001f\u0010-\u001a\u00020\"2\b\u0010.\u001a\u0004\u0018\u00010\u00072\b\u0010/\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\u0002\u00100R\u0016\u0010\u0005\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\r\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0019\u0010\u0011\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010R#\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00150\u00140\u000e\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\u0016\u0010\u0017\u001a\u0004\b\u0018\u0010\u0010R#\u0010\u0019\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001a0\u00140\u000e\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\u001b\u0010\u0017\u001a\u0004\b\u001c\u0010\u0010R\u0017\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\n0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0010R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\f0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u0010\u00a8\u00061"}, d2 = {"Lcom/mpesa/tracker/ui/analytics/AnalyticsViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/mpesa/tracker/data/repository/TransactionRepository;", "(Lcom/mpesa/tracker/data/repository/TransactionRepository;)V", "_customEndDate", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_customStartDate", "_exportState", "Lcom/mpesa/tracker/ui/analytics/ExportState;", "_selectedPeriod", "Lcom/mpesa/tracker/domain/models/ReportPeriod;", "customEndDate", "Lkotlinx/coroutines/flow/StateFlow;", "getCustomEndDate", "()Lkotlinx/coroutines/flow/StateFlow;", "customStartDate", "getCustomStartDate", "dailySpendTrend", "", "Lcom/mpesa/tracker/data/local/entities/DailySpend;", "getDailySpendTrend$annotations", "()V", "getDailySpendTrend", "expensesByCategory", "Lcom/mpesa/tracker/data/local/entities/CategoryExpense;", "getExpensesByCategory$annotations", "getExpensesByCategory", "exportState", "getExportState", "selectedPeriod", "getSelectedPeriod", "exportData", "", "context", "Landroid/content/Context;", "onPeriodSelected", "period", "resetExportState", "setBudget", "categoryId", "", "limit", "", "setCustomDateRange", "start", "end", "(Ljava/lang/Long;Ljava/lang/Long;)V", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class AnalyticsViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.mpesa.tracker.data.repository.TransactionRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.mpesa.tracker.domain.models.ReportPeriod> _selectedPeriod = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.mpesa.tracker.domain.models.ReportPeriod> selectedPeriod = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Long> _customStartDate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Long> customStartDate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Long> _customEndDate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Long> customEndDate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.mpesa.tracker.ui.analytics.ExportState> _exportState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.mpesa.tracker.ui.analytics.ExportState> exportState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.mpesa.tracker.data.local.entities.CategoryExpense>> expensesByCategory = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.mpesa.tracker.data.local.entities.DailySpend>> dailySpendTrend = null;
    
    @javax.inject.Inject()
    public AnalyticsViewModel(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.repository.TransactionRepository repository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.mpesa.tracker.domain.models.ReportPeriod> getSelectedPeriod() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Long> getCustomStartDate() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Long> getCustomEndDate() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.mpesa.tracker.ui.analytics.ExportState> getExportState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.mpesa.tracker.data.local.entities.CategoryExpense>> getExpensesByCategory() {
        return null;
    }
    
    @kotlin.OptIn(markerClass = {kotlinx.coroutines.ExperimentalCoroutinesApi.class})
    @java.lang.Deprecated()
    public static void getExpensesByCategory$annotations() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.mpesa.tracker.data.local.entities.DailySpend>> getDailySpendTrend() {
        return null;
    }
    
    @kotlin.OptIn(markerClass = {kotlinx.coroutines.ExperimentalCoroutinesApi.class})
    @java.lang.Deprecated()
    public static void getDailySpendTrend$annotations() {
    }
    
    public final void onPeriodSelected(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.domain.models.ReportPeriod period) {
    }
    
    public final void setCustomDateRange(@org.jetbrains.annotations.Nullable()
    java.lang.Long start, @org.jetbrains.annotations.Nullable()
    java.lang.Long end) {
    }
    
    public final void exportData(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    public final void resetExportState() {
    }
    
    public final void setBudget(int categoryId, double limit) {
    }
}