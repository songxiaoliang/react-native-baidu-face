## ==========================================
## 通用设置
## ==========================================
## 压缩优化算法
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
## 迭代优化次数
#-optimizationpasses 5
## 禁止优化
#-dontoptimize
## 禁止缩减代码
#-dontshrink
## 禁止多样化类名
#-dontusemixedcaseclassnames
## 禁止混淆公共LIB类
#-dontskipnonpubliclibraryclasses
## 禁止预先验证
#-dontpreverify
## 忽略警告信息
#-ignorewarnings
## 输出详细LOG
#-verbose
## 保持@JavascriptInterface annotations 不被混淆掉
#-keepattributes *Annotation*
#
## 禁止混淆类
#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Application
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#-keep public class com.android.vending.licensing.ILicensingService
#-keep public class * extends android.app.Activity
#
## 禁止混淆本地方法
#-keepclasseswithmembernames class * {
#    native <methods>;
#}
#
## 禁止混淆枚举类型
#-keepclassmembers,allowoptimization enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#
## 禁止混淆初始化方法
#-keepclasseswithmembers class * {
#    public <init>(android.content.Context, android.util.AttributeSet);
#}
#-keepclasseswithmembers class * {
#    public <init>(android.content.Context, android.util.AttributeSet, int);
#}
#
## 禁止混淆Parcelable对象
#-keepclassmembers class * implements android.os.Parcelable {
#    static android.os.Parcelable$Creator CREATOR;
#}
#
## 禁止混淆Serializable对象
#-keepnames class * implements java.io.Serializable
#-keepclassmembers class * implements java.io.Serializable {
#    static final long serialVersionUID;
#    private static final java.io.ObjectStreamField[] serialPersistentFields;
#    !static !transient <fields>;
#    private void writeObject(java.io.ObjectOutputStream);
#    private void readObject(java.io.ObjectInputStream);
#    java.lang.Object writeReplace();
#    java.lang.Object readResolve();
#}
#
## 禁止混淆二维码解析SDK
##-libraryjars libs/facesdk.jar
#-dontwarn com.baidu.idl.facesdk.FaceInfo
#-dontwarn com.baidu.idl.facesdk.FaceSDK
#-dontwarn com.baidu.idl.facesdk.FaceTracker
#-dontwarn com.baidu.idl.facesdk.FaceVerifyData
#-keep class com.baidu.idl.facesdk.FaceInfo { *; }
#-keep class com.baidu.idl.facesdk.FaceSDK { *; }
#-keep class com.baidu.idl.facesdk.FaceTracker { *; }
#-keep class com.baidu.idl.facesdk.FaceVerifyData { *; }
#

