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
import com.baidu.idl.face.platform.ILivenessStrategy;
import com.baidu.idl.face.platform.ILivenessStrategyCallback;
import com.baidu.idl.face.platform.LivenessTypeEnum;
import com.baidu.idl.face.platform.common.ConstantHelper;
import com.baidu.idl.face.platform.common.LogHelper;
import com.baidu.idl.face.platform.common.SoundPoolHelper;
import com.baidu.idl.face.platform.model.FaceExtInfo;
import com.baidu.idl.face.platform.model.FaceModel;
import com.baidu.idl.face.platform.utils.BitmapUtils;
import com.baidu.idl.facesdk.FaceTracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.baidu.idl.face.platform.FaceEnvironment.TIME_DETECT_MODULE;
import static com.baidu.idl.face.platform.FaceEnvironment.TIME_DETECT_NO_FACE_CONTINUOUS;
import static com.baidu.idl.face.platform.FaceEnvironment.TIME_MODULE;

/**
 * 活体检测策略控制类
 */
public final class FaceLivenessStrategyExtModule extends FaceStrategyModule implements ILivenessStrategy {

    private static final String TAG = FaceLivenessStrategyExtModule.class.getSimpleName();
    private Context mContext;
    private Rect mPreviewRect;
    private Rect mDetectRect;
    private DetectStrategy mDetectStrategy;
    private LivenessStatusStrategy mLivenessStrategy;
    private SoundPoolHelper mSoundPlayHelper = null;
    private volatile boolean mIsEnableSound = true;
    private boolean mIsFirstTipsed = false;
    private boolean mIsFirstLivenessSuccessTipsed = false;
    protected HashMap<String, String> mBase64ImageMap = new HashMap<String, String>();
    protected HashMap<FaceStatusEnum, String> mTipsMap = new HashMap<FaceStatusEnum, String>();
    private long mLivenessTipsTime = 0;
    private long mLivenessTipsDurationTime = 0;
    private volatile LivenessStatus mLivenessStatus = LivenessStatus.LivenessReady;
    private ILivenessStrategyCallback mILivenessStrategyCallback;

    // 活体状态【提示，成功，成功提示】
    private enum LivenessStatus {
        LivenessReady, LivenessTips, LivenessOK, LivenessOKTips
    }

    public FaceLivenessStrategyExtModule(Context context, FaceTracker tracker) {
        super(tracker);

        LogHelper.addLog(ConstantHelper.LOG_APPID, context.getPackageName());

        mContext = context;
        mDetectStrategy = new DetectStrategy();
        mLivenessStrategy = new LivenessStatusStrategy();
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
    public void setLivenessStrategyConfig(List<LivenessTypeEnum> livenessList,
                                          Rect previewRect, Rect detectRect,
                                          ILivenessStrategyCallback callback) {
        mLivenessStrategy.setLivenessList(livenessList);
        mPreviewRect = previewRect;
        mDetectRect = detectRect;
        mILivenessStrategyCallback = callback;
    }

    @Override
    public void setLivenessStrategySoundEnable(boolean flag) {
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
            }
        }
        return encodeImage;
    }

    @Override
    public void reset() {
        super.reset();
        if (mLivenessStrategy != null && !mIsCompletion) {
            mLivenessStrategy.reset();
        }
        if (mBase64ImageMap != null && !mIsCompletion) {
            mBase64ImageMap.clear();
        }
        if (mSoundPlayHelper != null) {
            mSoundPlayHelper.release();
        }
    }

    @Override
    public void livenessStrategy(byte[] imageData) {
        if (!mIsFirstTipsed) {
            mIsFirstTipsed = true;
            processUITips(FaceStatusEnum.Detect_FacePointOut);
            return;
        }
        if (mIsProcessing) {
            process(imageData);
        }
    }

    @Override
    protected void processStrategy(byte[] imageData) {
//        if (FaceEnvironment.isDebugable()) {
//            Log.e(TAG, "face liveness process");
//        }
        FaceModel model = mFaceModule.detect(imageData, mPreviewRect.height(), mPreviewRect.width());
        processUIStrategy(new UILivenessResultRunnable(model));
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

        FaceExtInfo faceInfo = null;
        FaceStatusEnum decodeStatus = FaceStatusEnum.Detect_NoFace;
        LivenessTypeEnum livenessType = mLivenessStrategy.getCurrentLivenessType();
        if (model != null
                && model.getFaceInfos() != null
                && model.getFaceInfos().length > 0) {

            decodeStatus = model.getFaceModuleState();
            faceInfo = model.getFaceInfos()[0];
            LogHelper.addLogWithKey(ConstantHelper.LOG_FTM, System.currentTimeMillis());

//            if (faceInfo != null) {
//                mLivenessStrategy.processLiveness(faceInfo);
//                if (mLivenessStrategy.isCurrentLivenessSuccess()) {
//                    saveLivenessImage(mLivenessStrategy.getCurrentLivenessType(),
//                            model.getArgbImage(), mPreviewRect);
//                }
//            }

        } else {
            if (mDetectStrategy != null) {
                mDetectStrategy.reset();
            }
        }

        if (faceInfo != null) {
            decodeStatus = mDetectStrategy.checkDetect(
                    mPreviewRect, mDetectRect,
                    faceInfo.getPitch(), faceInfo.getYaw(),
                    faceInfo.getLandmarksOutOfDetectCount(mDetectRect),
                    faceInfo.getFaceWidth(),
                    decodeStatus);
        }

        if (decodeStatus != FaceStatusEnum.OK) {

            if (mDetectStrategy.isTimeout()) {
                mIsProcessing = false;
                processUICallback(FaceStatusEnum.Error_DetectTimeout);
                return;
            }

            switch (decodeStatus) {
                case Detect_NoFace:
                case Detect_FacePointOut:
                    if (mNoFaceTime == 0) {
                        mNoFaceTime = System.currentTimeMillis();
                    }

                    if (System.currentTimeMillis() - mNoFaceTime > TIME_DETECT_MODULE) {
                        mIsProcessing = false;
                        processUICallback(FaceStatusEnum.Error_DetectTimeout);
                        return;
                    }

                    if (FaceStatusEnum.Detect_NoFace == decodeStatus) {

                        if (mIsFirstLivenessSuccessTipsed
                                && mNoFaceTime != 0
                                && System.currentTimeMillis() - mNoFaceTime < TIME_DETECT_NO_FACE_CONTINUOUS) {
                            return;
                        }

                        mIsFirstLivenessSuccessTipsed = false;

                        mDetectStrategy.reset();
                        mLivenessStatus = LivenessStatus.LivenessReady;
                        mLivenessStrategy.reset();

                        if (mBase64ImageMap != null) {
                            mBase64ImageMap.clear();
                        }

                    } else {

                        mDetectStrategy.reset();
                        mLivenessStatus = LivenessStatus.LivenessReady;
                        mLivenessStrategy.resetState();

                    }
                    processUITips(decodeStatus);
                    break;
                default:
                    processUITips(decodeStatus);
                    mDetectStrategy.reset();
                    mLivenessStatus = LivenessStatus.LivenessReady;
                    mLivenessStrategy.resetState();
            }

        } else if (faceInfo != null && decodeStatus == FaceStatusEnum.OK) {

//            if (mLivenessStrategy.getCurrentLivenessStatus() == FaceStatusEnum.Liveness_HeadLeftRight) {
//                if ((mLivenessStatus == LivenessStatus.LivenessTips)) {
//                        // && (mLivenessTipsTime != 0 && model.getFrameTime() - mLivenessTipsTime > 200)) {
//                    Log.e(TAG, "ext 可以检测摇摇头了");
//                    mLivenessStrategy.processLiveness(faceInfo);
//                }
//            } else {
//            }

            if (mLivenessStrategy.getCurrentLivenessStatus() == FaceStatusEnum.Liveness_HeadLeftRight
                    || mLivenessStrategy.getCurrentLivenessStatus() == FaceStatusEnum.Liveness_HeadLeft
                    || mLivenessStrategy.getCurrentLivenessStatus() == FaceStatusEnum.Liveness_HeadRight) {

                if (mLivenessStatus == LivenessStatus.LivenessTips) {

                    long du = (System.currentTimeMillis() - mLivenessTipsTime);

                    if (du > mLivenessTipsDurationTime) {
                        mLivenessStrategy.processLiveness(faceInfo);
                    }
                }

            } else {
                mLivenessStrategy.processLiveness(faceInfo);
            }

            if (mLivenessStrategy.isCurrentLivenessSuccess()) {
                saveLivenessImage(mLivenessStrategy.getCurrentLivenessType(),
                        model.getArgbImage(), mPreviewRect);
            }

            mNoFaceTime = 0;
            mDetectStrategy.setLiveness(livenessType);

            LogHelper.addLogWithKey(ConstantHelper.LOG_BTM, System.currentTimeMillis());

            if (mLivenessStrategy.isTimeout()) {
                mIsProcessing = false;
                processUICallback(FaceStatusEnum.Error_LivenessTimeout);
                return;
            }

            Log.e(TAG, "switch =========================");
            switch (mLivenessStatus) {
                case LivenessReady:
                    Log.e(TAG, "switch " + mLivenessStatus.name() + "-" + mLivenessStrategy.getCurrentLivenessStatus());
                    if (processUITips(mLivenessStrategy.getCurrentLivenessStatus())) {
                        if (mLivenessTipsDurationTime == 0) {
                            mLivenessTipsDurationTime = mSoundPlayHelper.getPlayDuration();
                        }
                        mLivenessStatus = LivenessStatus.LivenessTips;
                        mLivenessTipsTime = System.currentTimeMillis();
                    }
                    break;
                case LivenessTips:
                    Log.e(TAG, "switch " + mLivenessStatus.name() + "-" + mLivenessStrategy.getCurrentLivenessStatus());
                    if (mLivenessStrategy.isCurrentLivenessSuccess()) {
                        mLivenessStatus = LivenessStatus.LivenessOK;
                        mLivenessTipsTime = 0;
                        mLivenessTipsDurationTime = 0;
                    } else {
                        processUITips(mLivenessStrategy.getCurrentLivenessStatus());
                    }
                    break;
                case LivenessOK:
                    Log.e(TAG, "switch " + mLivenessStatus.name() + "-" + mLivenessStrategy.getCurrentLivenessStatus());
                    if (processUITips(FaceStatusEnum.Liveness_OK)) {
                        if (!mIsFirstLivenessSuccessTipsed) {
                            mIsFirstLivenessSuccessTipsed = true;
                        }
//                        mLivenessStrategy.nextLiveness();
//                        mLivenessStatus = LivenessStatus.LivenessOKTips;
                        if (mLivenessStrategy.nextLiveness()) {
                            mLivenessStatus = LivenessStatus.LivenessReady;
                            mLivenessTipsTime = 0;
                            mLivenessTipsDurationTime = 0;
                        } else {
                            if (mLivenessStrategy.isLivenessSuccess()) {
                                processUICallback(FaceStatusEnum.OK);
                            }
                        }
                    }
                    break;
//                case LivenessOKTips:
//                    Log.e(TAG, "switch " + mLivenessStatus.name() + "-" + mLivenessStrategy.getCurrentLivenessStatus());
//                    if (mLivenessStrategy.isLivenessSuccess()) {
//                        processUICallback(FaceStatusEnum.OK);
//                    } else {
//                        mLivenessStatus = LivenessStatus.LivenessReady;
//                    }
//                    break;
                default:
            }
        }
    }

    private void saveLivenessImage(final LivenessTypeEnum type, final int[] argbByte,
                                   final Rect roundRect) {
        if (!mBase64ImageMap.containsKey(type.name())) {
            Bitmap image = BitmapUtils.createLivenessBitmap(mContext, argbByte, roundRect);
            String imageEncode = BitmapUtils.bitmapToJpegBase64(image, 80);
            if (imageEncode != null && imageEncode.length() > 0) {
                imageEncode = imageEncode.replace("\\/", "/");
                mBase64ImageMap.put(type.name(), imageEncode);
            }
            if (image != null && !image.isRecycled()) {
                image.recycle();
                image = null;
            }
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
        if (status == FaceStatusEnum.OK
                || status == FaceStatusEnum.Liveness_Completion) {
            Log.e(TAG, "processUICompletion");
            mIsProcessing = false;
            mIsCompletion = true;

            LogHelper.addLogWithKey(ConstantHelper.LOG_ETM, System.currentTimeMillis());
            LogHelper.addLogWithKey(ConstantHelper.LOG_FINISH, 1);
            LogHelper.sendLog();

            if (mILivenessStrategyCallback != null) {
                ArrayList<String> imageList = mFaceModule.getDetectBestImageList();
                for (int i = 0; i < imageList.size(); i++) {
                    mBase64ImageMap.put(ILivenessStrategyCallback.IMAGE_KEY_BEST_IMAGE + i,
                            imageList.get(i));
                }
                mILivenessStrategyCallback.onLivenessCompletion(status, getStatusTextResId(status), mBase64ImageMap);
            }
        } else {
//            Log.e(TAG, "processUICallback " + status.name());
            if (mILivenessStrategyCallback != null) {
                mILivenessStrategyCallback.onLivenessCompletion(status, getStatusTextResId(status), null);
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

    private class UILivenessResultRunnable implements Runnable {
        private final FaceModel mModel;

        public UILivenessResultRunnable(FaceModel model) {
            mModel = model;
        }

        @Override
        public void run() {
            processUIResult(mModel);
        }
    }
}
