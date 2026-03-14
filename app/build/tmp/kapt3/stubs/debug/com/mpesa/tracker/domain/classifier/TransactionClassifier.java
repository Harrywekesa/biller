package com.mpesa.tracker.domain.classifier;

/**
 * Interface defining the categorization logic for an M-Pesa transaction.
 * Different strategies (e.g., Dictionary Match, Machine Learning) will implement this.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/mpesa/tracker/domain/classifier/TransactionClassifier;", "", "classify", "", "transaction", "Lcom/mpesa/tracker/data/local/entities/TransactionEntity;", "(Lcom/mpesa/tracker/data/local/entities/TransactionEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface TransactionClassifier {
    
    /**
     * Attempts to classify a transaction into a category.
     * @param transaction The parsed M-Pesa transaction entity.
     * @return The Category ID if a match is confidently found, or null otherwise.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object classify(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.entities.TransactionEntity transaction, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
}