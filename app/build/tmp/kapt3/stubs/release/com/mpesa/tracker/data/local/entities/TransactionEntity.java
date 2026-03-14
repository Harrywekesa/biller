package com.mpesa.tracker.data.local.entities;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b)\b\u0087\b\u0018\u00002\u00020\u0001Bu\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\u0003\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u000f\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0011\u0012\u0006\u0010\u0012\u001a\u00020\u0011\u0012\u0006\u0010\u0013\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0014J\t\u0010(\u001a\u00020\u0003H\u00c6\u0003J\t\u0010)\u001a\u00020\u0011H\u00c6\u0003J\t\u0010*\u001a\u00020\u0011H\u00c6\u0003J\t\u0010+\u001a\u00020\u0003H\u00c6\u0003J\t\u0010,\u001a\u00020\u0005H\u00c6\u0003J\t\u0010-\u001a\u00020\u0007H\u00c6\u0003J\t\u0010.\u001a\u00020\u0007H\u00c6\u0003J\t\u0010/\u001a\u00020\nH\u00c6\u0003J\t\u00100\u001a\u00020\u0003H\u00c6\u0003J\u000b\u00101\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0010\u00102\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0018J\u0010\u00103\u001a\u0004\u0018\u00010\u000fH\u00c6\u0003\u00a2\u0006\u0002\u0010\u001bJ\u008c\u0001\u00104\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\u00032\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u00112\b\b\u0002\u0010\u0013\u001a\u00020\u0003H\u00c6\u0001\u00a2\u0006\u0002\u00105J\u0013\u00106\u001a\u00020\u00112\b\u00107\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00108\u001a\u00020\u000fH\u00d6\u0001J\t\u00109\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0015\u0010\r\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\n\n\u0002\u0010\u0019\u001a\u0004\b\u0017\u0010\u0018R\u0015\u0010\u000e\u001a\u0004\u0018\u00010\u000f\u00a2\u0006\n\n\u0002\u0010\u001c\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\u0012\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u001fR\u0011\u0010\u0010\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u001fR\u0011\u0010\u0013\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010!R\u0011\u0010\u000b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010!R\u0013\u0010\f\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010!R\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u0016R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\'\u00a8\u0006:"}, d2 = {"Lcom/mpesa/tracker/data/local/entities/TransactionEntity;", "", "receiptNumber", "", "type", "Lcom/mpesa/tracker/data/local/entities/TransactionType;", "amount", "", "transactionCost", "dateTimestamp", "", "recipientName", "recipientNumber", "balance", "categoryId", "", "isRecurring", "", "isIncome", "rawSmsBody", "(Ljava/lang/String;Lcom/mpesa/tracker/data/local/entities/TransactionType;DDJLjava/lang/String;Ljava/lang/String;Ljava/lang/Double;Ljava/lang/Integer;ZZLjava/lang/String;)V", "getAmount", "()D", "getBalance", "()Ljava/lang/Double;", "Ljava/lang/Double;", "getCategoryId", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getDateTimestamp", "()J", "()Z", "getRawSmsBody", "()Ljava/lang/String;", "getReceiptNumber", "getRecipientName", "getRecipientNumber", "getTransactionCost", "getType", "()Lcom/mpesa/tracker/data/local/entities/TransactionType;", "component1", "component10", "component11", "component12", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;Lcom/mpesa/tracker/data/local/entities/TransactionType;DDJLjava/lang/String;Ljava/lang/String;Ljava/lang/Double;Ljava/lang/Integer;ZZLjava/lang/String;)Lcom/mpesa/tracker/data/local/entities/TransactionEntity;", "equals", "other", "hashCode", "toString", "app_release"})
@androidx.room.Entity(tableName = "transactions")
public final class TransactionEntity {
    @androidx.room.PrimaryKey()
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String receiptNumber = null;
    @org.jetbrains.annotations.NotNull()
    private final com.mpesa.tracker.data.local.entities.TransactionType type = null;
    private final double amount = 0.0;
    private final double transactionCost = 0.0;
    private final long dateTimestamp = 0L;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String recipientName = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String recipientNumber = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double balance = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer categoryId = null;
    private final boolean isRecurring = false;
    private final boolean isIncome = false;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String rawSmsBody = null;
    
    public TransactionEntity(@org.jetbrains.annotations.NotNull()
    java.lang.String receiptNumber, @org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.entities.TransactionType type, double amount, double transactionCost, long dateTimestamp, @org.jetbrains.annotations.NotNull()
    java.lang.String recipientName, @org.jetbrains.annotations.Nullable()
    java.lang.String recipientNumber, @org.jetbrains.annotations.Nullable()
    java.lang.Double balance, @org.jetbrains.annotations.Nullable()
    java.lang.Integer categoryId, boolean isRecurring, boolean isIncome, @org.jetbrains.annotations.NotNull()
    java.lang.String rawSmsBody) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getReceiptNumber() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.mpesa.tracker.data.local.entities.TransactionType getType() {
        return null;
    }
    
    public final double getAmount() {
        return 0.0;
    }
    
    public final double getTransactionCost() {
        return 0.0;
    }
    
    public final long getDateTimestamp() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRecipientName() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getRecipientNumber() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getBalance() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getCategoryId() {
        return null;
    }
    
    public final boolean isRecurring() {
        return false;
    }
    
    public final boolean isIncome() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRawSmsBody() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    public final boolean component10() {
        return false;
    }
    
    public final boolean component11() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component12() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.mpesa.tracker.data.local.entities.TransactionType component2() {
        return null;
    }
    
    public final double component3() {
        return 0.0;
    }
    
    public final double component4() {
        return 0.0;
    }
    
    public final long component5() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component8() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.mpesa.tracker.data.local.entities.TransactionEntity copy(@org.jetbrains.annotations.NotNull()
    java.lang.String receiptNumber, @org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.entities.TransactionType type, double amount, double transactionCost, long dateTimestamp, @org.jetbrains.annotations.NotNull()
    java.lang.String recipientName, @org.jetbrains.annotations.Nullable()
    java.lang.String recipientNumber, @org.jetbrains.annotations.Nullable()
    java.lang.Double balance, @org.jetbrains.annotations.Nullable()
    java.lang.Integer categoryId, boolean isRecurring, boolean isIncome, @org.jetbrains.annotations.NotNull()
    java.lang.String rawSmsBody) {
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