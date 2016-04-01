# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Eclipse\adt-bundle-windows-x86_64-20140702\sdk/tools/proguard/proguard-android.txt
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
-ignorewarnings
-keepattributes Signature
-keep class cn.bmob.v3.**{*;}
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReveiver
-keep public class * extends android.content.ContentProvider
-keep public class com.will.ontheroad.bean.**{*;}
-keepclasseswithmembers public class * extends android.view.View{*;}
-dontwarn com.squareup.okhttp.**