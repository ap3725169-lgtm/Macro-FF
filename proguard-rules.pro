# iSwipeMacro Proguard Rules
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}
-keep class com.iswipemacro.app.model.** { *; }
-keepclassmembers class com.iswipemacro.app.model.** { *; }
-dontwarn com.google.gson.**
