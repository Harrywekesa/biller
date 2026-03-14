package com.mpesa.tracker.domain.classifier;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0011\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0018\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\fH\u0096@\u00a2\u0006\u0002\u0010\rR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082D\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/mpesa/tracker/domain/classifier/TensorFlowLiteClassifier;", "Lcom/mpesa/tracker/domain/classifier/TransactionClassifier;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "interpreter", "Lorg/tensorflow/lite/Interpreter;", "modelName", "", "classify", "", "transaction", "Lcom/mpesa/tracker/data/local/entities/TransactionEntity;", "(Lcom/mpesa/tracker/data/local/entities/TransactionEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class TensorFlowLiteClassifier implements com.mpesa.tracker.domain.classifier.TransactionClassifier {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.Nullable()
    private org.tensorflow.lite.Interpreter interpreter;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String modelName = "mpesa_categorization_model.tflite";
    
    @javax.inject.Inject()
    public TensorFlowLiteClassifier(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object classify(@org.jetbrains.annotations.NotNull()
    com.mpesa.tracker.data.local.entities.TransactionEntity transaction, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
}