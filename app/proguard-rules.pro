# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\rahul\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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

-libraryjars /libs/android-query-full.0.26.8.jar
-libraryjars /libs/retrofit-1.9.0.jar
-libraryjars /libs/okhttp-urlconnection-2.0.0.jar

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.content.FragmentActivity
-keep public class * extends android.app.Fragment
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.** *;
}
-keepclassmembers class * {
    @retrofit.** *;
}

-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }

-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }

-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }

-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

-keep class com.nispok.snackbar.** { *; }
-keep interface com.nispok.snackbar.** { *; }

-keep class de.greenrobot.dao.** { *; }
-keep interface de.greenrobot.dao.** { *; }

-keep class com.nineoldandroids.** { *; }
-keep interface com.nineoldandroids.** { *; }

-keep class com.balysv.** { *; }
-keep interface com.balysv.** { *; }

-keep class de.hdodenhof.** { *; }
-keep interface de.hdodenhof.** { *; }

-keep class com.appyvet.** { *; }
-keep interface com.appyvet.** { *; }

-keep public class pl.droidsonroids.gif.GifIOException{<init>(int);}
-keep class pl.droidsonroids.gif.GifInfoHandle{<init>(long,int,int,int);}



