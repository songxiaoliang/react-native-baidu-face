/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform.strategy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.text.TextUtils;

import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceEnvironment;
import com.baidu.idl.face.platform.FaceStatusEnum;
import com.baidu.idl.face.platform.IDetectStrategyCallback;
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

import java.util.HashMap;
import java.util.List;

import static com.baidu.idl.face.platform.FaceEnvironment.TIME_DETECT_MODULE;
import static com.baidu.idl.face.platform.FaceEnvironment.TIME_DETECT_NO_FACE_CONTINUOUS;
import static com.baidu.idl.face.platform.FaceEnvironment.TIME_MODULE;

/**
 * 活体检测策略控制类
 */
@Deprecated
public final class FaceLivenessStrategyModule extends FaceStrategyModule implements ILivenessStrategy {

    private static final String TAG = FaceLivenessStrategyModule.class.getSimpleName();
    private Context mContext;
    private Rect mPreviewRect;
    private Rect mDetectRect;
    private DetectStrategy mDetectStrategy;
    private LivenessStrategy mLivenessStrategy;
    private SoundPoolHelper mSoundPlayHelper = null;
    private volatile boolean mIsEnableSound = true;
    private volatile boolean mIsTipsed = false;
    private boolean mIsFirstTipsed = false;
    protected int[] mBestFaceImage;
    protected HashMap<String, String> mBase64ImageMap = new HashMap<String, String>();
    protected HashMap<FaceStatusEnum, String> mTipsMap = new HashMap<FaceStatusEnum, String>();
    private ILivenessStrategyCallback mILivenessStrategyCallback;

    public FaceLivenessStrategyModule(Context context, FaceTracker tracker) {
        super(tracker);

        LogHelper.addLog(ConstantHelper.LOG_APPID, context.getPackageName());

        mContext = context;
        mDetectStrategy = new DetectStrategy();
        mLivenessStrategy = new LivenessStrategy();
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
        if (mFaceModule != null &&
                mFaceModule.getBestFaceImage() != null &&
                mFaceModule.getBestFaceImage().length > 0) {
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
        if (mLivenessStrategy != null && !mIsCompletion) {
            mLivenessStrategy.reset();
        }
        if (mBase64ImageMap != null && !mIsCompletion) {
            mBase64ImageMap.clear();
        }
    }

    @Override
    public void livenessStrategy(byte[] imageData) {
        if (!mIsFirstTipsed) {
            mIsFirstTipsed = true;
            processUITips(FaceStatusEnum.Detect_NoFace);
        }
        if (mIsProcessing)
            process(imageData);
    }

    @Override
    protected void processStrategy(byte[] imageData) {
        if (FaceEnvironment.isDebugable()) {
//            Log.e(TAG, "face liveness process");
        }
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

        if (System.currentTimeMillis() - mLaunchTime < 1600) {
            return;
        }

        FaceExtInfo faceInfo;
        if (model != null &&
                model.getFaceInfos() != null &&
                model.getFaceInfos().length > 0) {
            faceInfo = model.getFaceInfos()[0];

            LogHelper.addLogWithKey(ConstantHelper.LOG_FTM, System.currentTimeMillis());

        } else {
            faceInfo = null;
            if (mDetectStrategy != null) {
                mDetectStrategy.reset();
            }
        }
        FaceStatusEnum detectStatus = FaceStatusEnum.Detect_NoFace;
        if (faceInfo != null) {

            LivenessTypeEnum livenessType = mLivenessStrategy.getCurrentLivenessType();
            mDetectStrategy.setLiveness(livenessType);

            detectStatus = mDetectStrategy.checkDetect(
                    mPreviewRect, mDetectRect,
                    faceInfo.getPitch(), faceInfo.getYaw(),
                    faceInfo.getLandmarksOutOfDetectCount(mDetectRect),
                    faceInfo.getFaceWidth(),
                    model.getFaceModuleState());

            if (detectStatus == FaceStatusEnum.OK) {

                LogHelper.addLogWithKey(ConstantHelper.LOG_BTM, System.currentTimeMillis());

                mNoFaceTime = 0;

                // 活体验证超时
                if (mLivenessStrategy.isTimeout()) {
                    mIsProcessing = false;
                    processUICallback(FaceStatusEnum.Error_LivenessTimeout);
                    return;
                }
                if (!mLivenessStrategy.isCurrentLivenessCheckSuccess()) {
                    FaceStatusEnum livenessStatus = mLivenessStrategy.getCurrentLivenessStatus();
                    boolean flag = processUITips(livenessStatus);
                    if (flag) {
                        mIsTipsed = true;
//                        Log.e(TAG, "改变提示状态值 提示过");
                    }
                } else {
                    if (mLivenessStrategy.isLivenessCheckSuccess()) {
                        if (!isPrepareDataSuccess(faceInfo.getFaceId())) {
                            return;
                        }
                        boolean flag = processUITips(FaceStatusEnum.Liveness_OK);
                        if (flag)
                            processUICompletion(faceInfo.getFaceId(), FaceStatusEnum.OK);

                    } else if (mIsTipsed) {
                        boolean flag = processUITips(FaceStatusEnum.Liveness_OK);
                        if (flag) {
                            mLivenessStrategy.nextLiveness();
                            mIsTipsed = false;
//                            Log.e(TAG, "改变提示状态值 未提示 1");
                        }
                    }
                }
                // 活体验证
                if (mIsTipsed) {
                    mLivenessStrategy.checkLiveness(faceInfo.getLiveInfo());
                } else {
//                    Log.e(TAG, "没提示过，不检测 " + System.currentTimeMillis() + "-" + mIsTipsed);
                }

                if (mLivenessStrategy.isCurrentLivenessCheckSuccess()) {
                    // 记录活体采集数据
                    saveLivenessImage(livenessType, model.getArgbImage(), mPreviewRect);

                    LogHelper.addLogWithKey(ConstantHelper.LOG_PTM, System.currentTimeMillis());
                    LogHelper.addLivenessLog(livenessType.ordinal());
                }
            } else {
//                Log.e(TAG, "检测错误 " + detectStatus.name());
                boolean flag = processUITips(detectStatus);
                if (flag) {
                    if (detectStatus == FaceStatusEnum.Detect_NoFace) {
//                        Log.e(TAG, "改变提示状态值 未提示 2");
                        mIsTipsed = false;
                        mDetectStrategy.reset();
                        mLivenessStrategy.reset();
                        if (mBase64ImageMap != null) {
                            mBase64ImageMap.clear();
                        }
                    } else {
//                        Log.e(TAG, "改变提示状态值 未提示 3");
                        mIsTipsed = false;
                        mLivenessStrategy.resetState();
                        mNoFaceTime = 0;
                    }
                }

                if (mDetectStrategy.isTimeout()) {
                    mIsProcessing = false;
                    processUICallback(FaceStatusEnum.Error_DetectTimeout);
                    return;
                }

            }
        } else {

            if (detectStatus == FaceStatusEnum.Detect_NoFace) {
//                Log.e(TAG, "no face");
                if (mNoFaceTime == 0) {
                    mNoFaceTime = System.currentTimeMillis();
                } else if (System.currentTimeMillis() - mNoFaceTime > TIME_DETECT_MODULE) {
                    mIsProcessing = false;
                    processUICallback(FaceStatusEnum.Error_DetectTimeout);
                    return;
                }
                if (mNoFaceTime != 0 && System.currentTimeMillis() - mNoFaceTime > TIME_DETECT_NO_FACE_CONTINUOUS) {

//                    Log.e(TAG, "改变提示状态值 未提示 4");
                    mIsTipsed = false;

                    mDetectStrategy.reset();
                    mLivenessStrategy.reset();
                    if (mBase64ImageMap != null) {
                        mBase64ImageMap.clear();
                    }

                } else {
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
            processUITips(detectStatus);
        }
    }

    private boolean isPrepareDataSuccess(int faceId) {
        String encodeImage = mFaceModule.getDetectBestImage(faceId);
        return !TextUtils.isEmpty(encodeImage);
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

        if (status == FaceStatusEnum.Error_DetectTimeout ||
                status == FaceStatusEnum.Error_LivenessTimeout ||
                status == FaceStatusEnum.Error_Timeout) {

            LogHelper.addLogWithKey(ConstantHelper.LOG_ETM, System.currentTimeMillis());
            LogHelper.sendLog();
        }

        if (mILivenessStrategyCallback != null) {
            mILivenessStrategyCallback.onLivenessCompletion(status, getStatusTextResId(status), null);
        }
    }

    private void processUICompletion(int faceId, FaceStatusEnum status) {
        mIsProcessing = false;
        mIsCompletion = true;

        LogHelper.addLogWithKey(ConstantHelper.LOG_ETM, System.currentTimeMillis());
        LogHelper.addLogWithKey(ConstantHelper.LOG_FINISH, 1);
        LogHelper.sendLog();

        if (mILivenessStrategyCallback != null) {
            String imageEncode = mFaceModule.getDetectBestImage(faceId);
            mBase64ImageMap.put(IDetectStrategyCallback.IMAGE_KEY_BEST_IMAGE, imageEncode);
            processUIStrategyDelay(new Runnable() {
                @Override
                public void run() {
                    processUITips(FaceStatusEnum.Liveness_Completion);
                }
            }, 500);
            mILivenessStrategyCallback.onLivenessCompletion(status, getStatusTextResId(status), mBase64ImageMap);
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
