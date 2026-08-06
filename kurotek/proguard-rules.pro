# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep Retrofit interfaces
-keep interface com.example.security.SecurityApi { *; }

# Keep Moshi generated adapters
-keep class com.squareup.moshi.** { *; }
-keep @com.squareup.moshi.JsonQualifier @interface * {}

# Keep Room entities
-keep class com.example.models.** { *; }

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class com.example.**_HiltComponents.* { *; }
-keep class com.example.**_Factory { *; }

# Keep ViewModel factories
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.ViewModelProvider.Factory { *; }

# Keep WorkManager workers
-keep class com.example.core.work.** { *; }

# Keep SecurityEngine object
-keep class com.example.core.security.SecurityEngine { *; }
-keep class com.example.core.engine.security.SecurityEngine { *; }

# Keep Application class
-keep class com.example.KurotekApplication { *; }

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
