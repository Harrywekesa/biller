package com.mpesa.tracker.ui.analytics;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0006\n\u0002\b\t\b\u0007\u0018\u00002\u00020\u0001:\u00019B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\'\u001a\u00020(2\u0006\u0010)\u001a\u00020*J\u000e\u0010+\u001a\u00020(2\u0006\u0010,\u001a\u00020\fJ\u0006\u0010-\u001a\u00020(J\u0016\u0010.\u001a\u00020(2\u0006\u0010/\u001a\u00020\u000e2\u0006\u00100\u001a\u000201J\u001f\u00102\u001a\u00020(2\b\u00103\u001a\u0004\u0018\u00010\u00072\b\u00104\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\u0002\u00105J\u0015\u00106\u001a\u00020(2\b\u00107\u001a\u0004\u0018\u00010\u000e\u00a2\u0006\u0002\u00108R\u0016\u0010\u0005\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\r\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000e0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u00110\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0019\u0010\u0014\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0013R\u0019\u0010\u0016\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0013R#\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00190\u00110\u0010\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\u001a\u0010\u001b\u001a\u0004\b\u001c\u0010\u0013R#\u0010\u001d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001e0\u00110\u0010\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\u001f\u0010\u001b\u001a\u0004\b \u0010\u0013R\u0017\u0010!\u001a\b\u0012\u0004\u0012\u00020\n0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u0013R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010#\u001a\b\u0012\u0004\u0012\u00020\f0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u0013R\u0019\u0010%\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000e0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\u0013\u00a8\u0006:"}, d2 = {"Lcom/mpesa/tracker/ui/analytics/AnalyticsViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/mpesa/tracker/data/repository/TransactionRepository;", "(Lcom/mpesa/tracker/data/repository/TransactionRepository;)V", "_customEndDate", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_customStartDate", "_exportState", "Lcom/mpesa/tracker/ui/analytics/ExportState;", "_selectedPeriod", "Lcom/mpesa/tracker/domain/models/ReportPeriod;", "_selectedSimId", "", "activeSimIds", "Lkotlinx/coroutines/flow/StateFlow;", "", "getActiveSimIds", "()Lkotlinx/coroutines/flow/StateFlow;", "customEndDate", "getCustomEndDate", "customStartDate", "getCustomStartDate", "dailySpendTrend", "Lcom/mpesa/tracker/data/local/entities/DailySpend;", "getDailySpendTrend$annotations", "()V", "getDailySpendTrend", "expensesByCategory", "Lcom/mpesa/tracker/data/local/entities/CategoryExpense;", "getExpensesByCategory$annotations", "getExpensesByCategory", "exportState", "getExportState", "selectedPeriod", "getSelectedPeriod", "selectedSimId", "getSelectedSimId", "exportData", "", "context", "Landroid/content/Context;", "onPeriodSelected", "period", "resetExportState", "setBudget", "categoryId", "limit", "", "setCustomDateRange", "start", "end", "(Ljava/lang/Long;Ljava/lang/Long;)V", "setSimFilter", "simId", "(Ljava/lang/Integer;)V", "AnalyticsArgs", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class AnalyticsViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.mpesa.tracker.data.repository.TransactionRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.mpesa.tracker.domain.models.ReportPeriod> _selectedPeriod = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.mpesa.tracker.domain.models.ReportPeriod> selectedPeriod = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Integer> _selectedSimId = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> selectedSimId = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<java.lang.Integer>> activeSimIds = null;
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
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> getSelectedSimId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<java.lang.Integer>> getActiveSimIds() {
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
    
    public final void setSimFilter(@org.jetbrains.annotations.Nullable()
    java.lang.Integer simId) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0011\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B+\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\b\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000bJ\u0010\u0010\u0015\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000bJ\u0010\u0010\u0016\u001a\u0004\u0018\u00010\bH\u00c6\u0003\u00a2\u0006\u0002\u0010\u0010J<\u0010\u0017\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\bH\u00c6\u0001\u00a2\u0006\u0002\u0010\u0018J\u0013\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001c\u001a\u00020\bH\u00d6\u0001J\t\u0010\u001d\u001a\u00020\u001eH\u00d6\u0001R\u0015\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\n\n\u0002\u0010\f\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0015\u0010\u0007\u001a\u0004\u0018\u00010\b\u00a2\u0006\n\n\u0002\u0010\u0011\u001a\u0004\b\u000f\u0010\u0010R\u0015\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\n\n\u0002\u0010\f\u001a\u0004\b\u0012\u0010\u000b\u00a8\u0006\u001f"}, d2 = {"Lcom/mpesa/tracker/ui/analytics/AnalyticsViewModel$AnalyticsArgs;", "", "period", "Lcom/mpesa/tracker/domain/models/ReportPeriod;", "start", "", "end", "simId", "", "(Lcom/mpesa/tracker/domain/models/ReportPeriod;Ljava/lang/Long;Ljava/lang/Long;Ljava/lang/Integer;)V", "getEnd", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getPeriod", "()Lcom/mpesa/tracker/domain/models/ReportPeriod;", "getSimId", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getStart", "component1", "component2", "component3", "component4", "copy", "(Lcom/mpesa/tracker/domain/models/ReportPeriod;Ljava/lang/Long;Ljava/lang/Long;Ljava/lang/Integer;)Lcom/mpesa/tracker/ui/analytics/AnalyticsViewModel$AnalyticsArgs;", "equals", "", "other", "hashCode", "toString", "", "app_debug"})
    static final class AnalyticsArgs {
        @org.jetbrains.annotations.NotNull()
        private final com.mpesa.tracker.domain.models.ReportPeriod period = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Long start = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Long end = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Integer simId = null;
        
        public AnalyticsArgs(@org.jetbrains.annotations.NotNull()
        com.mpesa.tracker.domain.models.ReportPeriod period, @org.jetbrains.annotations.Nullable()
        java.lang.Long start, @org.jetbrains.annotations.Nullable()
        java.lang.Long end, @org.jetbrains.annotations.Nullable()
        java.lang.Integer simId) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.mpesa.tracker.domain.models.ReportPeriod getPeriod() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Long getStart() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Long getEnd() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Integer getSimId() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.mpesa.tracker.domain.models.ReportPeriod component1() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Long component2() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Long component3() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Integer component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.mpesa.tracker.ui.analytics.AnalyticsViewModel.AnalyticsArgs copy(@org.jetbrains.annotations.NotNull()
        com.mpesa.tracker.domain.models.ReportPeriod period, @org.jetbrains.annotations.Nullable()
        java.lang.Long start, @org.jetbrains.annotations.Nullable()
        java.lang.Long end, @org.jetbrains.annotations.Nullable()
        java.lang.Integer simId) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
}