# Keep Application class and Hilt generated base class - CRITICAL FOR STARTUP
-keep class com.example.KurotekApplication { *; }
-keep class com.example.Hilt_KurotekApplication { *; }
-keep class * extends android.app.Application { *; }
-keep class * extends androidx.multidex.MultiDexApplication { *; }

# Keep all classes referenced from AndroidManifest
-keep class com.example.KurotekApplication { *; }
-keep class com.example.MainActivity { *; }
-keep class com.example.receiver.SmsReceiver { *; }
-keep class com.example.receiver.PendingApprovalReceiver { *; }
-keep class com.example.network.SyncService { *; }

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class com.example.**_HiltComponents.* { *; }
-keep class com.example.**_Factory { *; }
-keep class com.example.**_MemberInjector { *; }

# Keep ViewModel factories
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.ViewModelProvider.Factory { *; }

# Keep Room entities
-keep class com.example.models.** { *; }

# Keep Retrofit interfaces
-keep interface com.example.security.SecurityApi { *; }

# Keep Moshi generated adapters
-keep class com.squareup.moshi.** { *; }
-keep @com.squareup.moshi.JsonQualifier @interface * {}

# Keep WorkManager workers
-keep class com.example.core.work.** { *; }

# Keep SecurityEngine object
-keep class com.example.core.security.SecurityEngine { *; }
-keep class com.example.core.engine.security.SecurityEngine { *; }

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
