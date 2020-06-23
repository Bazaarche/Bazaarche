# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/marat/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# We just enabled code and resource shrinking but don't need obfuscation
-dontobfuscate

##---------------Begin: proguard configuration for Gson  ----------
# Gson specific classes
-dontwarn sun.misc.**

##---------------Begin: proguard configuration for Material  ----------
-dontwarn com.google.android.material.snackbar.*

##---------------Begin: proguard configuration for Retrofit  ----------
# TODO: Remove this part when R8 enabled(maybe with updating gradle plugin)

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*
##---------------Begin: proguard configuration for okhttp  ----------
# TODO: Remove this part when R8 enabled(maybe with updating gradle plugin)
# This is because this version of proguard bug. Can be removed when R8 Enabled
-dontwarn okhttp3.**

##---------------Begin: proguard configuration for dagger.android  ----------
# TODO: Remove this part when R8 enabled(maybe with updating gradle plugin)
-dontwarn com.google.errorprone.annotations.**

##---------------Begin: proguard configuration for Jackson  ----------
-keep @com.fasterxml.jackson.annotation.* class *
-keepclassmembers class * {
    @com.fasterxml.jackson.annotation.* *;
}
##---------------Begin: proguard configuration for Sentry  ----------
-dontwarn javax.**