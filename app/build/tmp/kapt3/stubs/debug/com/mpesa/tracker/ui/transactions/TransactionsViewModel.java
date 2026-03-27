package com.mpesa.tracker.ui.transactions;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\"\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0018\b\u0007\u0018\u00002\u00020\u0001:\u0001HB\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\"\u00100\u001a\u0002012\u0006\u00102\u001a\u00020\u00102\b\b\u0002\u00103\u001a\u00020\u00102\b\b\u0002\u00104\u001a\u00020\u0010J\u000e\u00105\u001a\u0002012\u0006\u00106\u001a\u00020\tJ\u0006\u00107\u001a\u000201J\u0006\u00108\u001a\u000201J\u000e\u00109\u001a\u0002012\u0006\u0010:\u001a\u00020\u0010J\u0014\u0010;\u001a\u0002012\f\u0010<\u001a\b\u0012\u0004\u0012\u00020\u00100\u0016J\u0015\u0010=\u001a\u0002012\b\u00106\u001a\u0004\u0018\u00010\t\u00a2\u0006\u0002\u0010>J\u0015\u0010?\u001a\u0002012\b\u0010@\u001a\u0004\u0018\u00010\t\u00a2\u0006\u0002\u0010>J\u0006\u0010A\u001a\u000201J\u0006\u0010B\u001a\u000201J\u000e\u0010C\u001a\u0002012\u0006\u0010D\u001a\u00020\u0010J\u0006\u0010E\u001a\u000201J\u001e\u0010F\u001a\u0002012\u0006\u0010D\u001a\u00020\u00102\u0006\u00106\u001a\u00020\t2\u0006\u0010G\u001a\u00020\u0010R\u0016\u0010\u0007\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000b0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000b0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0011\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0012\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u00130\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u00160\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u001d\u0010\u0019\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001a0\u00160\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0018R\u0019\u0010\u001c\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0018R\u0017\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0018R\u0017\u0010 \u001a\b\u0012\u0004\u0012\u00020\u000b0\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0018R\u0017\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0018R\u0017\u0010$\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u0018R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00100\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\u0018R\u0019\u0010\'\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\u0018R\u001d\u0010)\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u00130\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010\u0018R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R#\u0010+\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020,0\u00160\u0015\u00a2\u0006\u000e\n\u0000\u0012\u0004\b-\u0010.\u001a\u0004\b/\u0010\u0018\u00a8\u0006I"}, d2 = {"Lcom/mpesa/tracker/ui/transactions/TransactionsViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/mpesa/tracker/data/repository/TransactionRepository;", "smsSyncService", "Lcom/mpesa/tracker/framework/services/SmsSyncService;", "(Lcom/mpesa/tracker/data/repository/TransactionRepository;Lcom/mpesa/tracker/framework/services/SmsSyncService;)V", "_filterByCategoryId", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_filterFulizaOnly", "", "_filterIncomeOnly", "_filterUncategorizedOnly", "_isSyncing", "_searchQuery", "", "_selectedSimId", "_selectedTransactionIds", "", "activeSimIds", "Lkotlinx/coroutines/flow/StateFlow;", "", "getActiveSimIds", "()Lkotlinx/coroutines/flow/StateFlow;", "categories", "Lcom/mpesa/tracker/data/local/entities/CategoryEntity;", "getCategories", "filterByCategoryId", "getFilterByCategoryId", "filterFulizaOnly", "getFilterFulizaOnly", "filterIncomeOnly", "getFilterIncomeOnly", "filterUncategorizedOnly", "getFilterUncategorizedOnly", "isSyncing", "searchQuery", "getSearchQuery", "selectedSimId", "getSelectedSimId", "selectedTransactionIds", "getSelectedTransactionIds", "transactions", "Lcom/mpesa/tracker/data/local/entities/TransactionEntity;", "getTransactions$annotations", "()V", "getTransactions", "addCategory", "", "name", "colorCode", "iconName", "categorizeSelected", "categoryId", "clearAndResyncSms", "clearSelection", "onSearchQueryChange", "query", "selectAll", "receiptNumbers", "setCategoryFilter", "(Ljava/lang/Integer;)V", "setSimFilter", "simId", "toggleFulizaFilter", "toggleIncomeFilter", "toggleSelection", "receiptNumber", "toggleUncategorizedFilter", "updateTransactionCategory", "merchantName", "FilterArgs", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class TransactionsViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.mpesa.tracker.data.repository.TransactionRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.mpesa.tracker.framework.services.SmsSyncService smsSyncService = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isSyncing = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isSyncing = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Integer> _selectedSimId = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> selectedSimId = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _searchQuery = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> searchQuery = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _filterIncomeOnly = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> filterIncomeOnly = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _filterUncategorizedOnly = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> filterUncategorizedOnly = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Integer> _filterByCategoryId = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> filterByCategoryId = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _filterFulizaOnly = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> filterFulizaOnly = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.Set<java.lang.String>> _selectedTransactionIds = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.Set<java.lang.String>> selectedTransactionIds = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<java.lang.Integer>> activeSimIds = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.mpesa.tracker.data.local.entities.TransactionEntity>> transactions = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.mpesa.tracker.data.local.entities.CategoryEntity>> categories = null;
    
    @javax.inject.Inject()
    public TransactionsViewModel(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.repository.TransactionRepository repository, @org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.framework.services.SmsSyncService smsSyncService) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isSyncing() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> getSelectedSimId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getSearchQuery() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getFilterIncomeOnly() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getFilterUncategorizedOnly() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> getFilterByCategoryId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getFilterFulizaOnly() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.Set<java.lang.String>> getSelectedTransactionIds() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<java.lang.Integer>> getActiveSimIds() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.mpesa.tracker.data.local.entities.TransactionEntity>> getTransactions() {
        return null;
    }
    
    @kotlin.OptIn(markerClass = {kotlinx.coroutines.ExperimentalCoroutinesApi.class})
    @java.lang.Deprecated()
    public static void getTransactions$annotations() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.mpesa.tracker.data.local.entities.CategoryEntity>> getCategories() {
        return null;
    }
    
    public final void onSearchQueryChange(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
    }
    
    public final void setSimFilter(@org.jetbrains.annotations.Nullable()
    java.lang.Integer simId) {
    }
    
    public final void toggleIncomeFilter() {
    }
    
    public final void toggleUncategorizedFilter() {
    }
    
    public final void toggleFulizaFilter() {
    }
    
    public final void setCategoryFilter(@org.jetbrains.annotations.Nullable()
    java.lang.Integer categoryId) {
    }
    
    public final void clearAndResyncSms() {
    }
    
    public final void updateTransactionCategory(@org.jetbrains.annotations.NotNull()
    java.lang.String receiptNumber, int categoryId, @org.jetbrains.annotations.NotNull()
    java.lang.String merchantName) {
    }
    
    public final void addCategory(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String colorCode, @org.jetbrains.annotations.NotNull()
    java.lang.String iconName) {
    }
    
    public final void toggleSelection(@org.jetbrains.annotations.NotNull()
    java.lang.String receiptNumber) {
    }
    
    public final void clearSelection() {
    }
    
    public final void selectAll(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> receiptNumbers) {
    }
    
    public final void categorizeSelected(int categoryId) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0017\b\u0082\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\u0006\u0010\t\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0005H\u00c6\u0003J\u0010\u0010\u0017\u001a\u0004\u0018\u00010\bH\u00c6\u0003\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0018\u001a\u00020\u0005H\u00c6\u0003JB\u0010\u0019\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\b\u0002\u0010\t\u001a\u00020\u0005H\u00c6\u0001\u00a2\u0006\u0002\u0010\u001aJ\u0013\u0010\u001b\u001a\u00020\u00052\b\u0010\u001c\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001d\u001a\u00020\bH\u00d6\u0001J\t\u0010\u001e\u001a\u00020\u0003H\u00d6\u0001R\u0015\u0010\u0007\u001a\u0004\u0018\u00010\b\u00a2\u0006\n\n\u0002\u0010\r\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\t\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000f\u00a8\u0006\u001f"}, d2 = {"Lcom/mpesa/tracker/ui/transactions/TransactionsViewModel$FilterArgs;", "", "query", "", "incomeOnly", "", "uncategorizedOnly", "categoryIdFilter", "", "fulizaOnly", "(Ljava/lang/String;ZZLjava/lang/Integer;Z)V", "getCategoryIdFilter", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getFulizaOnly", "()Z", "getIncomeOnly", "getQuery", "()Ljava/lang/String;", "getUncategorizedOnly", "component1", "component2", "component3", "component4", "component5", "copy", "(Ljava/lang/String;ZZLjava/lang/Integer;Z)Lcom/mpesa/tracker/ui/transactions/TransactionsViewModel$FilterArgs;", "equals", "other", "hashCode", "toString", "app_debug"})
    static final class FilterArgs {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String query = null;
        private final boolean incomeOnly = false;
        private final boolean uncategorizedOnly = false;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Integer categoryIdFilter = null;
        private final boolean fulizaOnly = false;
        
        public FilterArgs(@org.jetbrains.annotations.NotNull()
        java.lang.String query, boolean incomeOnly, boolean uncategorizedOnly, @org.jetbrains.annotations.Nullable()
        java.lang.Integer categoryIdFilter, boolean fulizaOnly) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getQuery() {
            return null;
        }
        
        public final boolean getIncomeOnly() {
            return false;
        }
        
        public final boolean getUncategorizedOnly() {
            return false;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Integer getCategoryIdFilter() {
            return null;
        }
        
        public final boolean getFulizaOnly() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        public final boolean component2() {
            return false;
        }
        
        public final boolean component3() {
            return false;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Integer component4() {
            return null;
        }
        
        public final boolean component5() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.mpesa.tracker.ui.transactions.TransactionsViewModel.FilterArgs copy(@org.jetbrains.annotations.NotNull()
        java.lang.String query, boolean incomeOnly, boolean uncategorizedOnly, @org.jetbrains.annotations.Nullable()
        java.lang.Integer categoryIdFilter, boolean fulizaOnly) {
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