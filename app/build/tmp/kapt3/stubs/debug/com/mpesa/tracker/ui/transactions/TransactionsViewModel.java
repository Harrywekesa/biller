package com.mpesa.tracker.ui.transactions;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u000f\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\"\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020\u000f2\b\b\u0002\u0010%\u001a\u00020\u000f2\b\b\u0002\u0010&\u001a\u00020\u000fJ\u0006\u0010\'\u001a\u00020#J\u000e\u0010(\u001a\u00020#2\u0006\u0010)\u001a\u00020\u000fJ\u0015\u0010*\u001a\u00020#2\b\u0010+\u001a\u0004\u0018\u00010\t\u00a2\u0006\u0002\u0010,J\u0006\u0010-\u001a\u00020#J\u0006\u0010.\u001a\u00020#J\u001e\u0010/\u001a\u00020#2\u0006\u00100\u001a\u00020\u000f2\u0006\u0010+\u001a\u00020\t2\u0006\u00101\u001a\u00020\u000fR\u0016\u0010\u0007\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000b0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\u00120\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0019\u0010\u0016\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0015R\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0015R\u0017\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0015R\u0017\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0015R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0015R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u001f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u00120\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0015\u00a8\u00062"}, d2 = {"Lcom/mpesa/tracker/ui/transactions/TransactionsViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/mpesa/tracker/data/repository/TransactionRepository;", "smsSyncService", "Lcom/mpesa/tracker/framework/services/SmsSyncService;", "(Lcom/mpesa/tracker/data/repository/TransactionRepository;Lcom/mpesa/tracker/framework/services/SmsSyncService;)V", "_filterByCategoryId", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_filterIncomeOnly", "", "_filterUncategorizedOnly", "_isSyncing", "_searchQuery", "", "categories", "Lkotlinx/coroutines/flow/StateFlow;", "", "Lcom/mpesa/tracker/data/local/entities/CategoryEntity;", "getCategories", "()Lkotlinx/coroutines/flow/StateFlow;", "filterByCategoryId", "getFilterByCategoryId", "filterIncomeOnly", "getFilterIncomeOnly", "filterUncategorizedOnly", "getFilterUncategorizedOnly", "isSyncing", "searchQuery", "getSearchQuery", "transactions", "Lcom/mpesa/tracker/data/local/entities/TransactionEntity;", "getTransactions", "addCategory", "", "name", "colorCode", "iconName", "clearAndResyncSms", "onSearchQueryChange", "query", "setCategoryFilter", "categoryId", "(Ljava/lang/Integer;)V", "toggleIncomeFilter", "toggleUncategorizedFilter", "updateTransactionCategory", "receiptNumber", "merchantName", "app_debug"})
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
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.mpesa.tracker.data.local.entities.TransactionEntity>> getTransactions() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.mpesa.tracker.data.local.entities.CategoryEntity>> getCategories() {
        return null;
    }
    
    public final void onSearchQueryChange(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
    }
    
    public final void toggleIncomeFilter() {
    }
    
    public final void toggleUncategorizedFilter() {
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
}