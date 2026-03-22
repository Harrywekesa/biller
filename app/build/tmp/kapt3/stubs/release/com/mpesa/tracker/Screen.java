package com.mpesa.tracker;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0005\u0003\u0004\u0005\u0006\u0007B\u0007\b\u0004\u00a2\u0006\u0002\u0010\u0002\u0082\u0001\u0005\b\t\n\u000b\f\u00a8\u0006\r"}, d2 = {"Lcom/mpesa/tracker/Screen;", "", "()V", "Analytics", "Dashboard", "Landing", "Subscriptions", "Transactions", "Lcom/mpesa/tracker/Screen$Analytics;", "Lcom/mpesa/tracker/Screen$Dashboard;", "Lcom/mpesa/tracker/Screen$Landing;", "Lcom/mpesa/tracker/Screen$Subscriptions;", "Lcom/mpesa/tracker/Screen$Transactions;", "app_release"})
public abstract class Screen {
    
    private Screen() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/mpesa/tracker/Screen$Analytics;", "Lcom/mpesa/tracker/Screen;", "()V", "app_release"})
    public static final class Analytics extends com.mpesa.tracker.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.mpesa.tracker.Screen.Analytics INSTANCE = null;
        
        private Analytics() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/mpesa/tracker/Screen$Dashboard;", "Lcom/mpesa/tracker/Screen;", "()V", "app_release"})
    public static final class Dashboard extends com.mpesa.tracker.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.mpesa.tracker.Screen.Dashboard INSTANCE = null;
        
        private Dashboard() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/mpesa/tracker/Screen$Landing;", "Lcom/mpesa/tracker/Screen;", "()V", "app_release"})
    public static final class Landing extends com.mpesa.tracker.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.mpesa.tracker.Screen.Landing INSTANCE = null;
        
        private Landing() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/mpesa/tracker/Screen$Subscriptions;", "Lcom/mpesa/tracker/Screen;", "()V", "app_release"})
    public static final class Subscriptions extends com.mpesa.tracker.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.mpesa.tracker.Screen.Subscriptions INSTANCE = null;
        
        private Subscriptions() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\f\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u001b\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\f\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\bJ\t\u0010\r\u001a\u00020\u0005H\u00c6\u0003J$\u0010\u000e\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001\u00a2\u0006\u0002\u0010\u000fJ\u0013\u0010\u0010\u001a\u00020\u00052\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u00d6\u0003J\t\u0010\u0013\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0015H\u00d6\u0001R\u0015\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\t\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u0016"}, d2 = {"Lcom/mpesa/tracker/Screen$Transactions;", "Lcom/mpesa/tracker/Screen;", "categoryId", "", "incomeOnly", "", "(Ljava/lang/Integer;Z)V", "getCategoryId", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getIncomeOnly", "()Z", "component1", "component2", "copy", "(Ljava/lang/Integer;Z)Lcom/mpesa/tracker/Screen$Transactions;", "equals", "other", "", "hashCode", "toString", "", "app_release"})
    public static final class Transactions extends com.mpesa.tracker.Screen {
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Integer categoryId = null;
        private final boolean incomeOnly = false;
        
        public Transactions(@org.jetbrains.annotations.Nullable()
        java.lang.Integer categoryId, boolean incomeOnly) {
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Integer getCategoryId() {
            return null;
        }
        
        public final boolean getIncomeOnly() {
            return false;
        }
        
        public Transactions() {
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Integer component1() {
            return null;
        }
        
        public final boolean component2() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.mpesa.tracker.Screen.Transactions copy(@org.jetbrains.annotations.Nullable()
        java.lang.Integer categoryId, boolean incomeOnly) {
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