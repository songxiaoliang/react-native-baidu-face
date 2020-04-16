package com.facetest.baiduface.module;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceEnvironment;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.LivenessTypeEnum;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.core.PermissionListener;
import com.facetest.baiduface.Config;
import com.facetest.baiduface.faceactivity.FaceDetectExpActivity;
import com.facetest.baiduface.faceactivity.FaceLivenessExpActivity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 基于Baidu-ai sdk 实现 RN Bridge
 */
public class BaiduFaceModule extends ReactContextBaseJavaModule implements PermissionListener{

    private boolean isOrder = false; //是否随机
    private Activity mActivity;
    private ReactApplicationContext mContext;
    private static final String GET_RESULT_EVENT = "FaceCheckHelper";
     private static final String GET_DETECT_RESULT_EVENT = "DetectFaceCheckHelper";
    private static final String BAIDU_FACE_MODULE_NAME = "PushFaceViewControllerModule";

    public BaiduFaceModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.mContext = reactContext;
    }

    @Override
    public String getName() {
        return BAIDU_FACE_MODULE_NAME;
    }

    /**
     * 人脸活体检测（采集）
     */
    @ReactMethod
    public void openPushFaceViewController(ReadableMap faceConfigParams) {
        requestPermissions(99, Manifest.permission.CAMERA);
        setFaceConfig(faceConfigParams);
        startFaceActy(FaceLivenessExpActivity.class);
    }


    /**
     * 人脸图像识别（采集）
     */
    @ReactMethod
    public void faceDetectExp(ReadableMap faceConfigParams) {
        requestPermissions(99, Manifest.permission.CAMERA);
        setFaceConfig(faceConfigParams);
        startFaceActy(FaceDetectExpActivity.class);
    }

    /**
     * 获取检测图片结果（Base64），传递到react native
     */
    public void sendFaceCheckBase64Img(WritableMap faceCheckResult) {
        mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(GET_RESULT_EVENT, faceCheckResult);
    }
    
    /**
     * 获取人脸固定采集检测图片结果（Base64），传递到react native
     */
    public void sendFaceDetectExpBase64Img(WritableMap faceCheckResult) {
        mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(GET_DETECT_RESULT_EVENT, faceCheckResult);
    }

    /**
     * 初始化sdk库
     * 为了android和ios区分授权, appId=appname_face_android, 其中appname为申请sdk时的应用名
     * (1)应用上下文
     * (2)申请License取得的APPID
     * (3)assets目录下License文件名
     */
    private void initLib() {
        FaceSDKManager.getInstance().initialize(mContext, Config.licenseID, Config.licenseFileName);
    }

    /**
     * 活体识别动作参数配置
     */
    private List<LivenessTypeEnum> getLivingAction(ReadableArray liveActionList) {
        List<LivenessTypeEnum> liveActionConfigList = new ArrayList<>(7);
        LivenessTypeEnum type = null;
        for (int i = 0; i < liveActionList.size(); i++) {
            /**
             * 0, 眨眨眼
             * 1, 张张嘴
             * 2, 向右摇头
             * 3, 向左摇头
             * 4, 抬头
             * 5, 低头
             * 6, 摇头
             */
            int tag = liveActionList.getInt(i);
            switch (tag) {
                case 0:
                    type = LivenessTypeEnum.Eye;
                    break;
                case 1:
                    type = LivenessTypeEnum.Mouth;
                    break;
                case 2:
                    type = LivenessTypeEnum.HeadRight;
                    break;
                case 3:
                    type = LivenessTypeEnum.HeadLeft;
                    break;
                case 4:
                    type = LivenessTypeEnum.HeadUp;
                    break;
                case 5:
                    type = LivenessTypeEnum.HeadDown;
                    break;
                case 6:
                    type = LivenessTypeEnum.HeadLeftOrRight;
                    break;
                default:
                   break;
            }
            if(type != null) {
                liveActionConfigList.add(type);
            }
        }

        return liveActionConfigList;
    }

    /**
     * 人脸识别检测参数配置
     */
    private void setFaceConfig(ReadableMap faceConfigParams) {

        initLib();
        FaceConfig config = FaceSDKManager.getInstance().getFaceConfig();

        // 检测动作配置
        if(faceConfigParams.hasKey("liveActionArray")) {
            ReadableArray liveActionList = faceConfigParams.getArray("liveActionArray");
            config.setLivenessTypeList(getLivingAction(liveActionList));
        } else {
            config.setLivenessTypeList(getDefaultLivingAction());
        }

        // 是否顺序进行检测（true: 随机，false: 顺序）
        if(faceConfigParams.hasKey("order")){
            isOrder = faceConfigParams.getBoolean("order");
        }
        config.setLivenessRandom(isOrder);
        // SDK初始化已经设置完默认参数（推荐参数），您也根据实际需求进行数值调整
        if(faceConfigParams.hasKey("quality")) {
            ReadableMap qualityMap = faceConfigParams.getMap("quality");
             if(qualityMap.hasKey("minFaceSize")) {
                 config.setMinFaceSize(qualityMap.getInt("minFaceSize"));
             } else {
                 config.setMinFaceSize(FaceEnvironment.VALUE_MIN_FACE_SIZE);
             }
             if(qualityMap.hasKey("cropFaceSizeWidth")) {
                 config.setCropFaceValue(qualityMap.getInt("cropFaceSizeWidth"));
             } else {
                 config.setCropFaceValue(FaceEnvironment.VALUE_CROP_FACE_SIZE);
             }
             if(qualityMap.hasKey("occluThreshold")) {
                 config.setOcclusionValue((float)qualityMap.getDouble("occluThreshold"));
             } else {
                 config.setOcclusionValue(FaceEnvironment.VALUE_OCCLUSION);
             }
            if(qualityMap.hasKey("illumThreshold")) {
                config.setBrightnessValue(qualityMap.getInt("illumThreshold"));
            } else {
                config.setBrightnessValue(FaceEnvironment.VALUE_BRIGHTNESS);
            }

            if(qualityMap.hasKey("blurThreshold")) {
                config.setBlurnessValue((float)qualityMap.getDouble("blurThreshold"));
             } else {
                config.setBlurnessValue(FaceEnvironment.VALUE_BLURNESS);
            }
            if(qualityMap.hasKey("EulurAngleThrPitch")) {
                config.setHeadPitchValue(qualityMap.getInt("EulurAngleThrPitch"));
             } else {
                config.setHeadPitchValue(FaceEnvironment.VALUE_HEAD_PITCH);
            }
            if(qualityMap.hasKey("EulurAngleThrYaw")) {
                config.setHeadYawValue(qualityMap.getInt("EulurAngleThrYaw"));
            } else {
                config.setHeadYawValue(FaceEnvironment.VALUE_HEAD_YAW);
            }
            if(qualityMap.hasKey("eulurAngleThrRoll")) {
                config.setHeadRollValue(qualityMap.getInt("eulurAngleThrRoll"));
             } else {
                config.setHeadRollValue(FaceEnvironment.VALUE_HEAD_ROLL);
            }
            if(qualityMap.hasKey("isCheckQuality")) {
                config.setCheckFaceQuality(qualityMap.getBoolean("isCheckQuality"));
             } else {
                config.setCheckFaceQuality(true);
            }
            if(qualityMap.hasKey("notFaceThreshold")) {
                config.setNotFaceValue((float)qualityMap.getDouble("notFaceThreshold"));
             } else {
                config.setNotFaceValue(FaceEnvironment.VALUE_NOT_FACE_THRESHOLD);
            }
            if(qualityMap.hasKey("maxCropImageNum")) {
                config.setMaxCropImageNum(qualityMap.getInt("maxCropImageNum"));
            } else {
                config.setMaxCropImageNum(FaceEnvironment.VALUE_MAX_CROP_IMAGE_NUM);
            }
        } else {
            config.setBlurnessValue(FaceEnvironment.VALUE_BLURNESS);
            config.setBrightnessValue(FaceEnvironment.VALUE_BRIGHTNESS);
            config.setCropFaceValue(FaceEnvironment.VALUE_CROP_FACE_SIZE);
            config.setHeadPitchValue(FaceEnvironment.VALUE_HEAD_PITCH);
            config.setHeadRollValue(FaceEnvironment.VALUE_HEAD_ROLL);
            config.setHeadYawValue(FaceEnvironment.VALUE_HEAD_YAW);
            config.setMinFaceSize(FaceEnvironment.VALUE_MIN_FACE_SIZE);
            config.setNotFaceValue(FaceEnvironment.VALUE_NOT_FACE_THRESHOLD);
            config.setOcclusionValue(FaceEnvironment.VALUE_OCCLUSION);
            config.setCheckFaceQuality(true);
        }

        if(faceConfigParams.hasKey("sound")) {
            boolean playSound = faceConfigParams.getBoolean("sound");
            config.setSound(playSound); // 是否开启声音
        } else {
            config.setSound(false); // 默认关闭声音
        }
        config.setFaceDecodeNumberOfThreads(2);
        FaceSDKManager.getInstance().setFaceConfig(config);
    }

    /**
     * 获取默认动作设置项
     * @return
     */
    public List<LivenessTypeEnum> getDefaultLivingAction() {
        List<LivenessTypeEnum> defaultLivingAction = new ArrayList<>();
        defaultLivingAction.add(LivenessTypeEnum.Eye);
        defaultLivingAction.add(LivenessTypeEnum.Mouth);
        defaultLivingAction.add(LivenessTypeEnum.HeadRight);
        defaultLivingAction.add(LivenessTypeEnum.HeadLeft);
        defaultLivingAction.add(LivenessTypeEnum.HeadUp);
        defaultLivingAction.add(LivenessTypeEnum.HeadDown);
        defaultLivingAction.add(LivenessTypeEnum.HeadLeftOrRight);
        return defaultLivingAction;
    }

    /**
     * 跳转
     */
    private void startFaceActy(Class activityClass) {
        Intent intent = new Intent(mContext, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getCurrentActivity().startActivity(intent);
    }

    /**
     * 授权
     * @param requestCode
     * @param permission
     */
    public void requestPermissions(int requestCode, String permission) {
        if (permission != null && permission.length() > 0) {
            try {
                if (Build.VERSION.SDK_INT >= 23) {
                    // 检查是否有权限
                    int hasPer = getCurrentActivity().checkSelfPermission(permission);
                    if (hasPer != PackageManager.PERMISSION_GRANTED) {
                        // 是否应该显示权限请求
                        boolean isShould = getCurrentActivity().shouldShowRequestPermissionRationale(permission);
                        getCurrentActivity().requestPermissions(new String[]{permission}, requestCode);
                    }
                } else {

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean flag = false;
        for (int i = 0; i < permissions.length; i++) {
            if (PackageManager.PERMISSION_GRANTED == grantResults[i]) {
                flag = true;
            }
        }
        if (!flag) {
            requestPermissions(99, Manifest.permission.CAMERA);
        }
        return flag;
    }

}
