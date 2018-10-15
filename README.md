# react-native-baidu-face
基于百度人脸识别封装的RN模块，支持Android、iOS平台设备

#### 配置

##### Android配置流程

1. 下载SDK。
2. 下载 license，拷贝到工程的assets目录。
3. 修改Config类，配置对应ID，key，name 等。
4. 添加sdk到项目工程：

```xml
（1）将开发包中的faceplatform-release库Copy 到工程根目录。
（2）将开发包中的faceplatform-ui库Copy 到工程根目录。
（3）在build.gradle使用compile project引人faceplatform-ui库工程。
（4）Setting.gradle中include faceplatfrom-ui和facepaltfrom-release
```

5. 在AndroidManifest.xml文件的<manifest>标签下配置权限，Feature 声明：
  
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>

<!-- 权限级别: dangerous -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<!-- 权限级别: normal -->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
<uses-permission android:name="android.permission.WRITE_SETTINGS" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.hardware.camera.autofocus" />
<uses-permission android:name="android.permission.WAKE_LOCK" />

<!-- 需要使用Feature -->
<uses-feature
    android:name="android.hardware.camera"
    android:required="false" />
<uses-feature
    android:name="android.hardware.camera.front"
    android:required="false" />
<uses-feature
    android:name="android.hardware.camera.autofocus"
    android:required="false" />
<uses-feature
    android:name="android.hardware.camera.flash"
    android:required="false" />

<Application>标签下配置采界面：
<!-- 活体图像采集界面 -->
<activity
    android:name="com.facetest.baiduface.faceactivity.FaceLivenessExpActivity"
    android:hardwareAccelerated="true"
    android:launchMode="singleTop"
    android:screenOrientation="portrait"
    android:theme="@style/Theme_NoTitle" />
<!-- 人脸跟踪采集界面 -->
<activity
    android:name="com.facetest.baiduface.faceactivity.FaceDetectExpActivity"
    android:hardwareAccelerated="true"
    android:launchMode="singleTop"
    android:screenOrientation="portrait"
    android:theme="@style/Theme_NoTitle" />
```
6. 倒入人脸识别封装模块：
    将baiduface文件夹拖到项目包名目录下（例如：com.xxx）

7. 配置Application

```java
package com.facetest;

import android.app.Application;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import com.facetest.baiduface.module.BaiduFacePackage;

import java.util.Arrays;
import java.util.List;

public class MainApplication extends Application implements ReactApplication {

  private static MainApplication mainApplication;
  private static final BaiduFacePackage baiduFacePackage = new BaiduFacePackage(); // 创建package实例

  private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
    @Override
    public boolean getUseDeveloperSupport() {
      return BuildConfig.DEBUG;
    }

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
          new MainReactPackage(),
          baiduFacePackage //注册 package
      );
    }

    @Override
    protected String getJSMainModuleName() {
      return "index";
    }
  };

  @Override
  public ReactNativeHost getReactNativeHost() {
    return mReactNativeHost;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    mainApplication = this;
    SoLoader.init(this, /* native exopackage */ false);
  }

  public static Application getApplication() {
    return mainApplication;
  }
	
  /**
   * 获取人脸识别package实例
   */
  public static BaiduFacePackage getBaiduFacePackage() {
    return baiduFacePackage;
  }

}
```

8. 配置签名文件

1.生成签名Keystore文件，并将keystore签名文件放到android/app根目录下

```xml
keytool -genkey -v -keystore my-release-key.keystore -alias my-key-alias -keyalg RSA -keysize 2048 -validity 10000
```

2.在gradle.properties文件下增加常量标识

```xml
MYAPP_RELEASE_STORE_FILE=my-release-key.keystore
MYAPP_RELEASE_KEY_ALIAS=my-key-alias // 改为对应名称
MYAPP_RELEASE_STORE_PASSWORD=123456 // 改为对应密码
MYAPP_RELEASE_KEY_PASSWORD=123456 // 改为对应密码
```

3.在app的build.gradle下的增加如下配置

```xml
	1.	android {
	2.	…………省略其他配置
	3.	    signingConfigs {
	4.	        debug {
	5.	            storeFile file(MYAPP_RELEASE_STORE_FILE)
	6.	            storePassword MYAPP_RELEASE_STORE_PASSWORD
	7.	            keyAlias MYAPP_RELEASE_KEY_ALIAS
	8.	            keyPassword MYAPP_RELEASE_KEY_PASSWORD
	9.	        }
	10.	        release {
	11.	            storeFile file(MYAPP_RELEASE_STORE_FILE)
	12.	            storePassword MYAPP_RELEASE_STORE_PASSWORD
	13.	            keyAlias MYAPP_RELEASE_KEY_ALIAS
	14.	            keyPassword MYAPP_RELEASE_KEY_PASSWORD
	15.	        }
	16.	    }
	17.	    buildTypes {
	18.	        release {
	19.	            minifyEnabled enableProguardInReleaseBuilds
	20.	            proguardFiles getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"
	21.	            signingConfig signingConfigs.release
	22.	            debuggable false
	23.	            jniDebuggable false
	24.	        }
	25.	    }
	26.	}
```

5.进入android目录，终端执行如下命令：

```xml
./gradlew assembleRelease
```

以上执行完后，apk文件会生成在android/app/build/outputs/apk/目录下。（每次打包之前，将之前的apk文件删除）



##### iOS配置流程

1. 下载SDK，以及 license
￼
下载SDK分为自动配置授权信息（创建license后就可以选择为该应用，下载后SDK自动帮您配置授权，不用下载license拷贝到工程中，初始化参数licenseID,包名也帮您配置好了）和未配置授权信息两种方式：
￼

1.2 运行示例工程
1.2.1 自动配置授权信息集成
如果您是通过自动配置授权信息下载的示例工程，只需配置好证书即可。查看下项目中的FaceParameterConfig.h文件，已经自动配置
￼
配置好证书，即可运行。注意已经设置好的bundle id不要随意改动。
—————————————————————————————————————————————————————————————————————
下面截图为下载官网SDK后解压的文件
￼

打开红色截图的文件(IDLFaceSDKDemoOC.xcodeproj) 

打开之后，配置一下项目的证书

￼



到此如果运行成功了，则代表以上准备的东西没问题了，接下来就可以把SDK集成到我们自己的项目。

先去官网下载SDK，注意现在下载的SDK是 未配置授权信息 的版本！！！！

然后再看以下教程
—————————————————————————————————————————————————————————————————————
1.2.2 未使用自动配置授权信息的集成
打开或者新建一个项目。
右键点击项目，会出现一个添加菜单，在菜单中选择『Add Files to“此处是你的项目名字”…… 』,如下图所示：
￼
1.3 添加SDK到工程
	1.	打开或者新建一个项目。
	2.	右键点击项目，会出现一个添加菜单，在菜单中选择『Add Files to“此处是你的项目名字”…… 』,如下图所示：
	3.	在添加文件弹出框里面选择申请到的license和SDK添加进来。如下图：
注意：license为百度官方提供的。
SDK包含下面三个文件:
	•	IDLFaceSDK.framework
	•	com.baidu.idl.face.faceSDK.bundle
	•	com.baidu.idl.face.model.bundle
￼
	4.	确认下Bundle Identifier 是否是申请license时填报的那一个，注意：license和Bundle Identifier是一一对应关系，填错了会导致SDK不能用。
	￼
	6.	填写正确的FACE_LICENSE_ID。（即后台展示的LicenseID）
在FaceParameterConfig.h文件里面填写拼接好的FACE_LICENSE_ID。
￼
	6.	选择链接C++标准库。
￼
	7.	如果没有使用pod管理第三方库的话，请在Build Setting >Linking > Other Linker Flags 上面加入 –ObjC 选项。如果用了pod请忽略，因为pod会自动添加上。
￼
1.5 权限声明
需要使用相机权限：编辑Info.plist文件，添加 Privacy- Camera Usage Description 的Key值，Value为使用相机时候的提示语，可以填写：『使用相机』。
￼
—————————————————————————————————————————————————————————————————————
关于SDK的位置，可以直接复制SDK中的整个文件夹
￼
SDK放在项目的位置可以发在iOS项目的根目录先：
￼
—————————————————————————————————————————————————————————————————————

2. 集成RN桥接文件

复制demo中的四个文件和一个文件夹
￼

在文件夹中选择这几个文件


然后拉进项目中，弹窗的选项如下：


点击 finish即可。

最后，再项目的 AppDelegate.m 文件中添加SDK的初始化代码
￼
#import "IDLFaceSDK/IDLFaceSDK.h"
#import "FaceParameterConfig.h"


 NSString* licensePath = [[NSBundle mainBundle] pathForResource:FACE_LICENSE_NAME ofType:FACE_LICENSE_SUFFIX];
  NSAssert([[NSFileManager defaultManager] fileExistsAtPath:licensePath], @"license文件路径不对，请仔细查看文档");
  [[FaceSDKManager sharedInstance] setLicenseID:FACE_LICENSE_ID andLocalLicenceFile:licensePath];
  NSLog(@"canWork = %d",[[FaceSDKManager sharedInstance] canWork]);
  
至此，iOS原生的代码集成完毕。更多详情可参考官方文档：https://ai.baidu.com/docs#/Face-iOS-SDK/top


#### React Native 使用

1.引入平台module

```javascript
const FaceCheckHelper = Platform.select({
    android: ()=> NativeModules.PushFaceViewControllerModule,
    ios: ()=> NativeModules.RNIOSExportJsToReact
})();
const NativeModule = new NativeEventEmitter(FaceCheckHelper);
```

2. 启动采集界面

```javascript
 let obj = {
            //质量校验设置
            'quality':{
                'minFaceSize' : 200,// 设置最小检测人脸阈值 默认是200
                'cropFaceSizeWidth' : 400,// 设置截取人脸图片大小 默认是 400
                'occluThreshold' : 0.5,// 设置人脸遮挡阀值 默认是 0.5
                'illumThreshold' : 40,// 设置亮度阀值 默认是 40
                'blurThreshold' : 0.7,// 设置图像模糊阀值 默认是 0.7
                'EulurAngleThrPitch' : 10,// 设置头部姿态角度 默认是 10
                'EulurAngleThrYaw' : 10,// 设置头部姿态角度 默认是 10
                'EulurAngleThrRoll' : 10,// 设置头部姿态角度 默认是 10
                'isCheckQuality' : true,// 设置是否进行人脸图片质量检测 默认是 true
                'conditionTimeout' : 10,// 设置超时时间 默认是 10
                'notFaceThreshold' : 0.6,// 设置人脸检测精度阀值 默认是0.6
                'maxCropImageNum' : 1,// 设置照片采集张数 默认是 1
            },
            'liveActionArray' :[
                0, //眨眨眼
                1, //张张嘴
                2, //向右摇头
                3, //向左摇头
                4, //抬头
                5, //低头
                6, //摇头
            ], //活体动作列表
            'order': true,//是否按顺序进行活体动作
            'sound': true, // 提示音，默认不开启
        };
        // FaceCheckHelper.openPushFaceViewController( obj );
        // 如果都不设置，需要传 {} 空对象， 建议设置 liveActionArray
        FaceCheckHelper.openPushFaceViewController( {} );
```

3. 注册监听，接收采集结果(收集结果为base64图片格式)

```javascript
componentDidMount() {
   NativeModule.addListener('FaceCheckHelper', (data) => this.faceCheckCallback(data));
}
/**
* 人脸检测结果
*/
faceCheckCallback(data) {
	this.setState({
		text: Object.keys(data)
	});
	if (data.remindCode == 0){
	    let imagesArray = [];
	    let imagesName = Object.keys(data.images); // bestImage liveEye liveYaw liveMouth yawRight yawLeft pitchUp pitchDown
	     imagesName.map((info,index) =>{
		imagesArray.push(
		    <View key={index} style={{margin:50}}>
			<Image
			    style={{width:180, height: 320, backgroundColor:'red'}}
			    source={{uri:'data:image/jpg;base64,'+ data.images[info]}}/>
			<Text>{info}</Text>
		    </View>
		)
	     })
	    this.setState({imagesArray})
	} else if (data.remindCode == 36) {
	    alert('采集超时');
	}
}
```


人脸识别模块基于Native层进行封装，即要从Native层进行配置。关于配置的详细流程可以参考百度人脸识别SDK官方文档。
