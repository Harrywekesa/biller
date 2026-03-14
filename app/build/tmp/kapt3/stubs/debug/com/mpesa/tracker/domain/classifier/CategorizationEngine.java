package com.mpesa.tracker.domain.classifier;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\u0007\u001a\u0004\u0018\u00010\b2\u0006\u0010\t\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/mpesa/tracker/domain/classifier/CategorizationEngine;", "", "dictionaryClassifier", "Lcom/mpesa/tracker/domain/classifier/DictionaryClassifier;", "mlClassifier", "Lcom/mpesa/tracker/domain/classifier/TensorFlowLiteClassifier;", "(Lcom/mpesa/tracker/domain/classifier/DictionaryClassifier;Lcom/mpesa/tracker/domain/classifier/TensorFlowLiteClassifier;)V", "categorize", "", "transaction", "Lcom/mpesa/tracker/data/local/entities/TransactionEntity;", "(Lcom/mpesa/tracker/data/local/entities/TransactionEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class CategorizationEngine {
    @org.jetbrains.annotations.NotNull()
    private final com.mpesa.tracker.domain.classifier.DictionaryClassifier dictionaryClassifier = null;
    @org.jetbrains.annotations.NotNull()
    private final com.mpesa.tracker.domain.classifier.TensorFlowLiteClassifier mlClassifier = null;
    
    @javax.inject.Inject()
    public CategorizationEngine(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.domain.classifier.DictionaryClassifier dictionaryClassifier, @org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.domain.classifier.TensorFlowLiteClassifier mlClassifier) {
        super();
    }
    
    /**
     * Determines the optimal category for a transaction.
     * It first attempts a strict heuristic rule match (fastest and 100% precise).
     * If that fails, it falls back to the Machine Learning NLP model.
     * Finally, it defaults to a generic category or null.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object categorize(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.entities.TransactionEntity transaction, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
}