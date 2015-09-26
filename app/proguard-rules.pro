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

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.content.FragmentActivity
-keep public class * extends android.app.Fragment

-dontwarn com.androidquery.**
-keep class com.androidquery.** { *; }

-dontwarn okio.**
-dontwarn retrofit.appengine.UrlFetchClient
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn retrofit.client.ApacheClient$GenericEntityHttpRequest
-dontwarn retrofit.client.ApacheClient$GenericHttpRequest
-dontwarn retrofit.client.ApacheClient$TypedOutputEntity
-dontwarn rx.**
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.** *;
}

-keep class sun.misc.Unsafe { *; }

-dontwarn com.google.gson.**
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

-dontwarn com.nispok.snackbar.**
-keep class com.nispok.snackbar.** { *; }
-keep interface com.nispok.snackbar.** { *; }

-dontwarn de.greenrobot.dao.**
-keep class de.greenrobot.dao.** { *; }
-keep interface de.greenrobot.dao.** { *; }

-dontwarn com.nineoldandroids.**
-keep class com.nineoldandroids.** { *; }
-keep interface com.nineoldandroids.** { *; }

-dontwarn com.balysv.**
-keep class com.balysv.** { *; }
-keep interface com.balysv.** { *; }

-keep class de.hdodenhof.** { *; }
-keep interface de.hdodenhof.** { *; }

-dontwarn com.appyvet.**
-keep class com.appyvet.** { *; }
-keep interface com.appyvet.** { *; }

-dontwarn pl.droidsonroids.gif.GifIOException
-keep public class pl.droidsonroids.gif.GifIOException{<init>(int);}
-keep class pl.droidsonroids.gif.GifInfoHandle{<init>(long,int,int,int);}



