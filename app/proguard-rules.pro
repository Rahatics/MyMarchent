# Add project specific ProGuard rules here.

# Keep all data model classes
-keep class com.mymarchent.mymarchent.data.model.** { *; }

# Keep OkHttp, Retrofit, and GSON classes needed for networking
-keep class retrofit2.** { *; }
-keep class com.google.gson.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# Keep Coroutines classes
-keepnames class kotlinx.coroutines.internal.** { *; }
