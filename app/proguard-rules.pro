# Smart Finance Pro ProGuard Rules

# Keep Room entities
-keep class com.smartfinance.pro.data.model.** { *; }

# Keep ViewModels
-keep class com.smartfinance.pro.viewmodel.** { *; }

# MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }

# Android Jetpack
-keep class androidx.room.** { *; }
-keep class androidx.lifecycle.** { *; }

# Generic Android keep rules
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
