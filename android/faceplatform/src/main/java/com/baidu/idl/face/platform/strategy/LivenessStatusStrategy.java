/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform.strategy;

import android.util.Log;

import com.baidu.idl.face.platform.FaceStatusEnum;
import com.baidu.idl.face.platform.LivenessTypeEnum;
import com.baidu.idl.face.platform.model.FaceExtInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.baidu.idl.face.platform.FaceEnvironment.TIME_LIVENESS_MODULE;

/**
 * 活体检测条件控制类
 */
class LivenessStatusStrategy {

    private static final String TAG = LivenessStatusStrategy.class.getSimpleName();

    private List<LivenessTypeEnum> mLivenessList;
    private long mLivenessDuration = 0L;
    private volatile int mLivenessIndex = 0;
    private boolean mLivenessTimeoutFlag = false;
    private volatile LivenessTypeEnum mCurrentLivenessTypeEnum = null;
    private long mFaceID = -1;
    private HashMap<LivenessTypeEnum, Boolean> mLivenessStatusMap =
            new HashMap<LivenessTypeEnum, Boolean>();

    public LivenessStatusStrategy() {
        mLivenessIndex = 0;
        mLivenessDuration = System.currentTimeMillis();
    }

    public void setLivenessList(List<LivenessTypeEnum> list) {
        if (list != null && list.size() > 0) {
            mLivenessList = list;
            mCurrentLivenessTypeEnum = mLivenessList.get(0);
            clearLivenessStatus();

            StringBuilder sd = new StringBuilder();
            for (LivenessTypeEnum type : mLivenessList) {
                sd.append(type.name());
                sd.append("-");
            }
//            Log.e(TAG, "setLivenessList =" + sd.toString());
        }
    }

    public LivenessTypeEnum getCurrentLivenessType() {
        return mCurrentLivenessTypeEnum;
    }

    public FaceStatusEnum getCurrentLivenessStatus() {
        FaceStatusEnum status = null;
        if (mCurrentLivenessTypeEnum != null) {
            switch (mCurrentLivenessTypeEnum) {
                case Eye:
                    status = FaceStatusEnum.Liveness_Eye;
                    break;
                case Mouth:
                    status = FaceStatusEnum.Liveness_Mouth;
                    break;
                case HeadUp:
                    status = FaceStatusEnum.Liveness_HeadUp;
                    break;
                case HeadDown:
                    status = FaceStatusEnum.Liveness_HeadDown;
                    break;
                case HeadLeft:
                    status = FaceStatusEnum.Liveness_HeadLeft;
                    break;
                case HeadRight:
                    status = FaceStatusEnum.Liveness_HeadRight;
                    break;
                case HeadLeftOrRight:
                    status = FaceStatusEnum.Liveness_HeadLeftRight;
                    break;
                default:
            }
        }
        return status;
    }

    public boolean isLivenessSuccess() {
        boolean flag = true;
        String name = "";
        for (Map.Entry<LivenessTypeEnum, Boolean> entry : mLivenessStatusMap.entrySet()) {
            if (!entry.getValue()) {
                flag = false;
                name = entry.getKey().name();
                break;
            }
        }
//        Log.e(TAG, "活体全部通过" + flag + "-" + name);
        return flag;
    }

    public boolean isCurrentLivenessSuccess() {
        boolean flag = mLivenessStatusMap.containsKey(mCurrentLivenessTypeEnum)
                ? mLivenessStatusMap.get(mCurrentLivenessTypeEnum) : false;
//        Log.e(TAG, "当前活体检测类型 =" + mCurrentLivenessTypeEnum.name() + "-" + flag);
        return flag;
    }

    public boolean isTimeout() {
        return mLivenessTimeoutFlag;
    }

    public boolean nextLiveness() {
        if ((mLivenessIndex + 1) < mLivenessList.size()) {
            ++mLivenessIndex;
            mCurrentLivenessTypeEnum = mLivenessList.get(mLivenessIndex);
            mLivenessDuration = System.currentTimeMillis();
//            Log.e(TAG, "ext 开始下个活体验证 =" + mCurrentLivenessTypeEnum.name());
            return true;
        }
        return false;
    }

    public void processLiveness(FaceExtInfo faceInfo) {
        // 超时检查
        long t = System.currentTimeMillis();
        if (t - mLivenessDuration > TIME_LIVENESS_MODULE) {
            mLivenessTimeoutFlag = true;
            return;
        }

        if (faceInfo != null) {
            if (faceInfo.getFaceId() != mFaceID) {
                mFaceID = faceInfo.getFaceId();
//                mLivenessStatusMap.clear();
//                Log.e(TAG, "faceId changed");
            }

            switch (mCurrentLivenessTypeEnum) {
                case Eye:
                    Log.e(TAG, "ext Eye " + faceInfo.isLiveEye());
                    break;
                case Mouth:
                    Log.e(TAG, "ext Mouth " + faceInfo.isLiveMouth());
                    break;
                case HeadUp:
                    Log.e(TAG, "ext HeadUp " + faceInfo.isLiveHeadUp());
                    break;
                case HeadDown:
                    Log.e(TAG, "ext HeadDown " + faceInfo.isLiveHeadDown());
                    break;
                case HeadLeft:
                    Log.e(TAG, "ext HeadLeft " + faceInfo.isLiveHeadTurnLeft());
                    break;
                case HeadRight:
                    Log.e(TAG, "ext HeadRight " + faceInfo.isLiveHeadTurnRight());
                    break;
                case HeadLeftOrRight:
                    Log.e(TAG, "ext HeadLeftOrRight "
                            + faceInfo.isLiveHeadTurnLeft() + "-"
                            + faceInfo.isLiveHeadTurnRight());
                    break;
                default:
            }

            if (mLivenessList.contains(LivenessTypeEnum.Eye)
                    && !mLivenessStatusMap.containsKey(LivenessTypeEnum.Eye)) {
                mLivenessStatusMap.put(LivenessTypeEnum.Eye, faceInfo.isLiveEye());
            } else {
                if (mCurrentLivenessTypeEnum == LivenessTypeEnum.Eye && faceInfo.isLiveEye()) {
                    mLivenessStatusMap.put(LivenessTypeEnum.Eye, faceInfo.isLiveEye());
//                    Log.e(TAG, "eye done");
                }
            }

            if (mLivenessList.contains(LivenessTypeEnum.Mouth)
                    && !mLivenessStatusMap.containsKey(LivenessTypeEnum.Mouth)) {
                mLivenessStatusMap.put(LivenessTypeEnum.Mouth, faceInfo.isLiveMouth());
            } else {
                if (mCurrentLivenessTypeEnum == LivenessTypeEnum.Mouth && faceInfo.isLiveMouth()) {
                    mLivenessStatusMap.put(LivenessTypeEnum.Mouth, faceInfo.isLiveMouth());
//                    Log.e(TAG, "mouth done");
                }
            }

            if (mLivenessList.contains(LivenessTypeEnum.HeadUp)
                    && !mLivenessStatusMap.containsKey(LivenessTypeEnum.HeadUp)) {
                mLivenessStatusMap.put(LivenessTypeEnum.HeadUp, faceInfo.isLiveHeadUp());
            } else {
                if (mCurrentLivenessTypeEnum == LivenessTypeEnum.HeadUp && faceInfo.isLiveHeadUp()) {
                    mLivenessStatusMap.put(LivenessTypeEnum.HeadUp, faceInfo.isLiveHeadUp());
//                    Log.e(TAG, "head_up done");
                }
            }

            if (mLivenessList.contains(LivenessTypeEnum.HeadDown)
                    && !mLivenessStatusMap.containsKey(LivenessTypeEnum.HeadDown)) {
                mLivenessStatusMap.put(LivenessTypeEnum.HeadDown, faceInfo.isLiveHeadDown());
            } else {
                if (mCurrentLivenessTypeEnum == LivenessTypeEnum.HeadDown && faceInfo.isLiveHeadDown()) {
                    mLivenessStatusMap.put(LivenessTypeEnum.HeadDown, faceInfo.isLiveHeadDown());
//                    Log.e(TAG, "head_down done");
                }
            }

            if (mLivenessList.contains(LivenessTypeEnum.HeadLeft)
                    && !mLivenessStatusMap.containsKey(LivenessTypeEnum.HeadLeft)) {
                mLivenessStatusMap.put(LivenessTypeEnum.HeadLeft, faceInfo.isLiveHeadTurnLeft());
            } else {
                if (mCurrentLivenessTypeEnum == LivenessTypeEnum.HeadLeft && faceInfo.isLiveHeadTurnLeft()) {
                    mLivenessStatusMap.put(LivenessTypeEnum.HeadLeft, faceInfo.isLiveHeadTurnLeft());
//                    Log.e(TAG, "head_left done");
                }
            }

            if (mLivenessList.contains(LivenessTypeEnum.HeadRight)
                    && !mLivenessStatusMap.containsKey(LivenessTypeEnum.HeadRight)) {
                mLivenessStatusMap.put(LivenessTypeEnum.HeadRight, faceInfo.isLiveHeadTurnRight());
            } else {
                if (mCurrentLivenessTypeEnum == LivenessTypeEnum.HeadRight && faceInfo.isLiveHeadTurnRight()) {
                    mLivenessStatusMap.put(LivenessTypeEnum.HeadRight, faceInfo.isLiveHeadTurnRight());
//                    Log.e(TAG, "head_right done");
                }
            }

            if (mLivenessList.contains(LivenessTypeEnum.HeadLeftOrRight)
                    && !mLivenessStatusMap.containsKey(LivenessTypeEnum.HeadLeftOrRight)) {
                mLivenessStatusMap.put(LivenessTypeEnum.HeadLeftOrRight, faceInfo.isLiveHeadTurnLeftOrRight());
            } else {
                if (mCurrentLivenessTypeEnum == LivenessTypeEnum.HeadLeftOrRight
                        && faceInfo.isLiveHeadTurnLeftOrRight()) {
                    mLivenessStatusMap.put(LivenessTypeEnum.HeadLeftOrRight, faceInfo.isLiveHeadTurnLeftOrRight());
//                    Log.e(TAG, "ext leftOrRight done");
                }
            }
        }
    }

    private void clearLivenessStatus() {
        mLivenessStatusMap.clear();
        for (int i = 0; i < mLivenessList.size(); i++) {
            mLivenessStatusMap.put(mLivenessList.get(i), false);
        }
    }

    public void reset() {
        mLivenessIndex = 0;
        clearLivenessStatus();
        // Collections.shuffle(mLivenessList);
        if (mLivenessList != null && mLivenessIndex < mLivenessList.size()) {
            mCurrentLivenessTypeEnum = mLivenessList.get(mLivenessIndex);
        }
        mLivenessDuration = System.currentTimeMillis();
        mLivenessTimeoutFlag = false;
    }

    public void resetState() {
        mLivenessDuration = System.currentTimeMillis();
        mLivenessTimeoutFlag = false;
    }

}
