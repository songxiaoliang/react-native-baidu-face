/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform.strategy;

import android.graphics.Rect;

import com.baidu.idl.face.platform.FaceEnvironment;
import com.baidu.idl.face.platform.FaceStatusEnum;
import com.baidu.idl.face.platform.LivenessTypeEnum;

import static com.baidu.idl.face.platform.FaceEnvironment.TIME_DETECT_MODULE;
import static com.baidu.idl.face.platform.FaceStatusEnum.Detect_FacePointOut;
import static com.baidu.idl.face.platform.FaceStatusEnum.Detect_FaceZoomIn;
import static com.baidu.idl.face.platform.FaceStatusEnum.Detect_FaceZoomOut;
import static com.baidu.idl.face.platform.FaceStatusEnum.OK;

/**
 * 人脸跟踪检测控制类
 */
class DetectStrategy {

    private static final String TAG = DetectStrategy.class.getSimpleName();

    private LivenessTypeEnum mLivenessTypeEnum;
    private FaceStatusEnum mCurrentFaceStatus;
    private int mHeadPitchValue = FaceEnvironment.VALUE_HEAD_PITCH;
    private int mHeadYawValue = FaceEnvironment.VALUE_HEAD_YAW;
    private int mHeadRollValue = FaceEnvironment.VALUE_HEAD_ROLL;
    private long mDuration = 0l;
    private boolean mTimeoutFlag = false;
    private boolean mIsDetectSuccess = false;

    public DetectStrategy() {
    }

    public void setHeadAngle(int p, int y, int r) {
        mHeadPitchValue = p;
        mHeadYawValue = y;
        mHeadRollValue = r;
    }

    public void setLiveness(LivenessTypeEnum type) {
        mLivenessTypeEnum = type;
    }

    public boolean isTimeout() {
        return mTimeoutFlag;
    }

    public boolean isDetectCheckSuccess() {
        return mIsDetectSuccess;
    }

    public FaceStatusEnum checkDetect(Rect previewRect,
                                      Rect detectRect,
                                      float headh, float headv,
                                      int faceOutCount,
                                      int faceWidth,
                                      FaceStatusEnum status) {
        boolean flag = isDefaultDetectStatus(status);
        if (flag) {
            checkTimeout(status);
            return status;
        }

        if (faceWidth > (detectRect.width() * 1)) {
            status = Detect_FaceZoomOut;
            checkTimeout(status);
            return status;
        } else if (faceWidth < (detectRect.width() * 0.4f)) {
            status = Detect_FaceZoomIn;
            checkTimeout(status);
            return status;
        } else {
            FaceStatusEnum st = getHeadPose(headh, headv);
            if (st != null) {
                status = st;
            }
        }

        if (faceOutCount > 10) {
//            Log.e(TAG, "DetectStrategy =" + faceOutCount);
            status = Detect_FacePointOut;
            checkTimeout(status);
            return status;
        }

        checkTimeout(status);

        if (status == OK) {
            mIsDetectSuccess = true;
        }
        return status;
    }

    private FaceStatusEnum getHeadPose(float headh, float headv) {
        FaceStatusEnum status = null;
        float left = mHeadYawValue;
        float right = mHeadYawValue;
        float up = mHeadPitchValue;
        float down = mHeadPitchValue;
        if (headh > up &&
                !(mLivenessTypeEnum == LivenessTypeEnum.HeadDown)) {
            status = FaceStatusEnum.Detect_PitchOutOfDownMaxRange;
        } else if (headh < down * -1 &&
                !(mLivenessTypeEnum == LivenessTypeEnum.HeadUp)) {
            status = FaceStatusEnum.Detect_PitchOutOfUpMaxRange;
        } else if (headv > left &&
                !(mLivenessTypeEnum == LivenessTypeEnum.HeadLeft
                        || mLivenessTypeEnum == LivenessTypeEnum.HeadLeftOrRight)) {
            status = FaceStatusEnum.Detect_PitchOutOfLeftMaxRange;
        } else if (headv < right * -1 &&
                !(mLivenessTypeEnum == LivenessTypeEnum.HeadRight
                        || mLivenessTypeEnum == LivenessTypeEnum.HeadLeftOrRight)) {
            status = FaceStatusEnum.Detect_PitchOutOfRightMaxRange;
        }
        return status;
    }

    private void checkTimeout(FaceStatusEnum status) {
        if (mCurrentFaceStatus == null || mCurrentFaceStatus != status) {
            mCurrentFaceStatus = status;
            mDuration = System.currentTimeMillis();
            mTimeoutFlag = false;
        }

        long t = System.currentTimeMillis();
        if (mCurrentFaceStatus == status &&
                t - mDuration > TIME_DETECT_MODULE) {
            mTimeoutFlag = true;
        }
    }

    private boolean isDefaultDetectStatus(FaceStatusEnum status) {
        boolean flag = false;
        switch (status) {
            case Detect_PoorIllumintion:
            case Detect_ImageBlured:
            case Detect_OccLeftEye:
            case Detect_OccRightEye:
            case Detect_OccNose:
            case Detect_OccMouth:
            case Detect_OccLeftContour:
            case Detect_OccRightContour:
            case Detect_OccChin:
            case Detect_FaceZoomIn:
            case Detect_FaceZoomOut:
            case Detect_FacePointOut:
            case Detect_PitchOutOfUpMaxRange:
            case Detect_PitchOutOfDownMaxRange:
            case Detect_PitchOutOfLeftMaxRange:
            case Detect_PitchOutOfRightMaxRange:
                flag = true;
                break;
            default:
        }
        return flag;
    }

    public void reset() {
        mDuration = 0l;
        mTimeoutFlag = false;
        mIsDetectSuccess = false;
        mCurrentFaceStatus = null;
    }
}
