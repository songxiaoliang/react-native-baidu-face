/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform.strategy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceEnvironment;
import com.baidu.idl.face.platform.FaceStatusEnum;
import com.baidu.idl.face.platform.IDetectStrategy;
import com.baidu.idl.face.platform.IDetectStrategyCallback;
import com.baidu.idl.face.platform.ILivenessStrategyCallback;
import com.baidu.idl.face.platform.common.ConstantHelper;
import com.baidu.idl.face.platform.common.LogHelper;
import com.baidu.idl.face.platform.common.SoundPoolHelper;
import com.baidu.idl.face.platform.model.FaceExtInfo;
import com.baidu.idl.face.platform.model.FaceModel;
import com.baidu.idl.face.platform.utils.BitmapUtils;
import com.baidu.idl.facesdk.FaceTracker;

import java.util.ArrayList;
import java.util.HashMap;

import static com.baidu.idl.face.platform.FaceEnvironment.TIME_DETECT_MODULE;
import static com.baidu.idl.face.platform.FaceEnvironment.TIME_MODULE;

/**
 * 人脸跟踪策略控制类
 */
public final class FaceDetectStrategyExtModule extends FaceStrategyModule implements IDetectStrategy {

    private static final String TAG = FaceDetectStrategyExtModule.class.getSimpleName();
    private Context mContext;
    private Rect mPreviewRect;
    private Rect mDetectRect;
    private DetectStrategy mDetectStrategy;
    private SoundPoolHelper mSoundPlayHelper = null;
    private boolean mIsFirstTipsed = false;
    private volatile boolean mIsEnableSound = true;
    private volatile boolean mIsPlayCompletion = false;
    protected HashMap<String, String> mBase64ImageMap = new HashMap<String, String>();
    protected HashMap<FaceStatusEnum, String> mTipsMap = new HashMap<FaceStatusEnum, String>();
    private IDetectStrategyCallback mIDetectStrategyCallback;

    public FaceDetectStrategyExtModule(Context context, FaceTracker tracker) {
        super(tracker);

        LogHelper.addLog(ConstantHelper.LOG_APPID, context.getPackageName());

        mContext = context;
        mDetectStrategy = new DetectStrategy();
        mSoundPlayHelper = new SoundPoolHelper(context);

        mLaunchTime = System.currentTimeMillis();
    }

    public void setConfigValue(FaceConfig config) {
        if (config != null && mDetectStrategy != null) {
            mDetectStrategy.setHeadAngle(
                    config.getHeadPitchValue(),
                    config.getHeadYawValue(),
                    config.getHeadRollValue());
        }
    }

    @Override
    public void setDetectStrategyConfig(Rect previewRect, Rect detectRect, IDetectStrategyCallback callback) {
        mPreviewRect = previewRect;
        mDetectRect = detectRect;
        mIDetectStrategyCallback = callback;
    }

    @Override
    public void setDetectStrategySoundEnable(boolean flag) {
        mIsEnableSound = flag;
    }

    @Override
    public void setPreviewDegree(int degree) {
        if (mFaceModule != null) {
            mFaceModule.setPreviewDegree(degree);
        }
    }

    @Override
    public String getBestFaceImage() {
        String encodeImage = "";
        if (mFaceModule != null
                && mFaceModule.getBestFaceImage() != null
                && mFaceModule.getBestFaceImage().length > 0) {
            try {
                int[] image = mFaceModule.getBestFaceImage();

                int w = mPreviewRect.height();
                int h = mPreviewRect.width();

                Bitmap bmp = Bitmap.createBitmap(h, w, Bitmap.Config.ARGB_8888);
                bmp.setPixels(image, 0, h, 0, 0, h, w);

                encodeImage = BitmapUtils.bitmapToJpegBase64(bmp, 100);
                if (encodeImage != null && encodeImage.length() > 0) {
                    encodeImage = encodeImage.replace("\\/", "/");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e(TAG, "getBestFaceImage Exception " + ex.getMessage());
            }
        }
        return encodeImage;
    }

    @Override
    public void detectStrategy(byte[] imageData) {
        if (!mIsFirstTipsed) {
            mIsFirstTipsed = true;
            processUITips(FaceStatusEnum.Detect_FacePointOut);
        }
        if (mIsProcessing) {
            process(imageData);
        }
    }

    @Override
    protected void processStrategy(byte[] imageData) {
        FaceModel model = mFaceModule.detect(imageData, mPreviewRect.height(), mPreviewRect.width());
        processUIStrategy(new UIDetectResultRunnable(model));
    }

    private void processUIResult(FaceModel model) {

        if (!mIsProcessing) {
            return;
        }

        if (System.currentTimeMillis() - mLaunchTime > TIME_MODULE && TIME_MODULE != 0) {
            mIsProcessing = false;
            processUICallback(FaceStatusEnum.Error_Timeout);
            return;
        }

        FaceExtInfo faceInfo;
        if (model != null
                && model.getFaceInfos() != null
                && model.getFaceInfos().length > 0) {
            faceInfo = model.getFaceInfos()[0];
            LogHelper.addLogWithKey(ConstantHelper.LOG_FTM, System.currentTimeMillis());
        } else {
            faceInfo = null;
            if (mDetectStrategy != null) {
                mDetectStrategy.reset();
            }
        }

        if (faceInfo != null) {

            if (mIsCompletion) {
                mIsPlayCompletion = processUITips(FaceStatusEnum.OK);
                return;
            }

            FaceStatusEnum detectStatus = mDetectStrategy.checkDetect(
                    mPreviewRect, mDetectRect,
                    faceInfo.getPitch(), faceInfo.getYaw(),
                    faceInfo.getLandmarksOutOfDetectCount(mDetectRect),
                    faceInfo.getFaceWidth(),
                    model.getFaceModuleState());

            if (detectStatus == FaceStatusEnum.OK
                    || detectStatus == FaceStatusEnum.Detect_DataNotReady) {
                LogHelper.addLogWithKey(ConstantHelper.LOG_BTM, System.currentTimeMillis());
            }

            if (detectStatus == FaceStatusEnum.OK) {
                boolean flag = processUITips(FaceStatusEnum.OK);
                mIsCompletion = true;
                return;
            }

            if (mDetectStrategy.isTimeout()) {
                mIsProcessing = false;
                processUICallback(FaceStatusEnum.Error_DetectTimeout);
                return;
            }

            processUITips(detectStatus);

//            Log.e(TAG, "processUIResult " + detectStatus.name());
        } else {
            if (model != null && model.getFaceModuleState() == FaceStatusEnum.Detect_NoFace) {
                mDetectStrategy.reset();
                if (mNoFaceTime == 0) {
                    mNoFaceTime = System.currentTimeMillis();
                } else if (System.currentTimeMillis() - mNoFaceTime > TIME_DETECT_MODULE) {
                    mIsProcessing = false;
                    processUICallback(FaceStatusEnum.Error_DetectTimeout);
                    return;
                }
            } else {
                mNoFaceTime = 0;
            }
            if (mDetectStrategy.isTimeout()) {
                mIsProcessing = false;
                processUICallback(FaceStatusEnum.Error_DetectTimeout);
                return;
            }
            processUITips(FaceStatusEnum.Detect_NoFace);
        }
    }

    private boolean processUITips(FaceStatusEnum status) {
        boolean flag = false;
        if (status != null) {
            mSoundPlayHelper.setEnableSound(mIsEnableSound);
            flag = mSoundPlayHelper.playSound(status);
            if (flag) {
                LogHelper.addTipsLogWithKey(status.name());
                processUICallback(status);
            }
        }
        return flag;
    }

    private void processUICallback(FaceStatusEnum status) {
        if (status == FaceStatusEnum.Error_DetectTimeout
                || status == FaceStatusEnum.Error_LivenessTimeout
                || status == FaceStatusEnum.Error_Timeout) {
            LogHelper.addLogWithKey(ConstantHelper.LOG_ETM, System.currentTimeMillis());
            LogHelper.sendLog();
        }
        if (mIDetectStrategyCallback != null) {
            if (FaceStatusEnum.OK == status) {
                mIsProcessing = false;
                mIsCompletion = true;

                LogHelper.addLogWithKey(ConstantHelper.LOG_ETM, System.currentTimeMillis());
                LogHelper.addLogWithKey(ConstantHelper.LOG_FINISH, 1);
                LogHelper.sendLog();

                ArrayList<String> imageList = mFaceModule.getDetectBestImageList();
                for (int i = 0; i < imageList.size(); i++) {
                    mBase64ImageMap.put(ILivenessStrategyCallback.IMAGE_KEY_BEST_IMAGE + i,
                            imageList.get(i));
                }
                mIDetectStrategyCallback.onDetectCompletion(status, getStatusTextResId(status), mBase64ImageMap);
            } else {
                mIDetectStrategyCallback.onDetectCompletion(status, getStatusTextResId(status), null);
            }
        }
    }

    private String getStatusTextResId(FaceStatusEnum status) {
        String tips = "";
        if (mTipsMap.containsKey(status)) {
            tips = mTipsMap.get(status);
        } else {
            int resId = FaceEnvironment.getTipsId(status);
            if (resId > 0) {
                tips = mContext.getResources().getString(resId);
                mTipsMap.put(status, tips);
            }
        }
        return tips;
    }

    private class UIDetectResultRunnable implements Runnable {
        private final FaceModel mModel;

        public UIDetectResultRunnable(FaceModel model) {
            mModel = model;
        }

        @Override
        public void run() {
            processUIResult(mModel);
        }
    }

    public void reset() {
        super.reset();
        if (mSoundPlayHelper != null) {
            mSoundPlayHelper.release();
        }
    }
}
