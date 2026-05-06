# ProPDF ProGuard Rules
# Keep application class
-keep class com.propdf.editor.ProPDFApp { *; }

# Keep model classes for serialization
-keep class com.propdf.editor.domain.model.** { *; }

# Keep Room entities
-keep class com.propdf.editor.data.local.entity.** { *; }

# Keep Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponents { *; }

# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel { *; }

# Keep Activities
-keep class * extends android.app.Activity { *; }

# Pdfium
-keep class com.shockwave.pdfium.** { *; }
-dontwarn com.shockwave.pdfium.**

# PDFBox Android
-keep class com.tom_roush.pdfbox.** { *; }
-dontwarn com.tom_roush.pdfbox.**

# Tesseract
-keep class com.googlecode.tesseract.android.** { *; }
-dontwarn com.googlecode.tesseract.android.**

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# General Android
-keepattributes SourceFile,LineNumberTable
-keep public class * extends android.view.View
-keep public class * extends android.app.Application

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}
