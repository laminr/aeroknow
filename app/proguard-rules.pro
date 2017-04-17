# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/laminr/Library/Android/sdk/tools/proguard/proguard-android.txt
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

#RETROFIT RULES
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

-dontwarn okio.**

# for kotlin breakpoint
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-keep public class * extends android.support.v4.** {*;}
-keep public class * extends android.app.Fragment


# Warning
-dontwarn java8.util.**
-dontwarn java.lang.invoke.*

# FirebaseCrash
-keep class com.google.android.gms.crash.** {*;}

# MOSHI
-keepclassmembers class  biz.eventually.atpl.network.network.** {*;}

# Recycler view
-keep public class * extends android.support.v7.widget.RecyclerView$ViewHolder {
    public <init>(...);
}

# Tests
#-keepclassmembers class fr.planetvo.pvo2mobility.data.app.model.** {*;}
-keep interface javax.inject.Inject

# Google analytics
-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**