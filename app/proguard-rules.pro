# ============================================================
# M-Pesa Tracker ProGuard Rules
# ============================================================

# --- Hilt / Dagger ---
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keepnames @dagger.hilt.android.AndroidEntryPoint class *

# --- Room ---
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-dontwarn androidx.room.**

# --- Kotlin Coroutines ---
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# --- TensorFlow Lite ---
-keep class org.tensorflow.** { *; }
-keep interface org.tensorflow.** { *; }
-dontwarn org.tensorflow.**

# --- Google Generative AI (Gemini) ---
-keep class com.google.ai.client.generativeai.** { *; }
-dontwarn com.google.ai.client.generativeai.**

# --- Kotlin Metadata (needed by reflection-based libs) ---
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**

# --- AndroidX Biometric ---
-keep class androidx.biometric.** { *; }

# --- WorkManager ---
-keep class androidx.work.** { *; }
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.CoroutineWorker { *; }

# --- App-specific: keep enums used in Room columns ---
-keepclassmembers enum com.mpesa.tracker.** { *; }

# --- Keep data classes used as Room entities/return types ---
-keep class com.mpesa.tracker.data.local.entities.** { *; }
-keep class com.mpesa.tracker.domain.parser.ParsedMpesaResult { *; }

# --- Remove Android logging in release ---
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int d(...);
}
