/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform.strategy;

import com.baidu.idl.face.platform.FaceStatusEnum;
import com.baidu.idl.face.platform.LivenessTypeEnum;

import java.util.List;

import static com.baidu.idl.face.platform.FaceEnvironment.TIME_LIVENESS_MODULE;
import static com.baidu.idl.face.platform.LivenessTypeEnum.Eye;
import static com.baidu.idl.face.platform.LivenessTypeEnum.HeadDown;
import static com.baidu.idl.face.platform.LivenessTypeEnum.HeadLeft;
import static com.baidu.idl.face.platform.LivenessTypeEnum.HeadLeftOrRight;
import static com.baidu.idl.face.platform.LivenessTypeEnum.HeadRight;
import static com.baidu.idl.face.platform.LivenessTypeEnum.HeadUp;
import static com.baidu.idl.face.platform.LivenessTypeEnum.Mouth;

/**
 * 活体检测条件控制类
 */
@Deprecated
class LivenessStrategy {

    private static final String TAG = LivenessStrategy.class.getSimpleName();

    // 活体检测类型列表
    private List<LivenessTypeEnum> mLivenessList;
    // 单个活体检测超时时间
    private long mDuration = 0L;
    // 当前验证活体类型在列表中的下标
    private volatile int mIndex = 0;
    // 当前活体类型
    private volatile LivenessTypeEnum mCurrentLivenessTypeEnum = null;
    private volatile boolean mIsCurrentCheckSuccess = false;

    // 超时标志位
    private boolean mTimeoutFlag = false;

    public LivenessStrategy() {
        mIndex = 0;
        mDuration = System.currentTimeMillis();
    }

    public void setLivenessList(List<LivenessTypeEnum> list) {
        mLivenessList = list;
        if (mLivenessList != null && mIndex < mLivenessList.size()) {
            mCurrentLivenessTypeEnum = mLivenessList.get(mIndex);
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

    public boolean isLivenessCheckSuccess() {
        return mIsCurrentCheckSuccess && mIndex >= mLivenessList.size() - 1;
    }

    public boolean isCurrentLivenessCheckSuccess() {
        return mIsCurrentCheckSuccess;
    }

    public boolean isTimeout() {
        return mTimeoutFlag;
    }

    public void nextLiveness() {
        if ((mIndex + 1) < mLivenessList.size()) {
//            Log.e(TAG, "liveness completion === " + mCurrentLivenessTypeEnum.name());
            ++mIndex;
            mIsCurrentCheckSuccess = false;
            mCurrentLivenessTypeEnum = mLivenessList.get(mIndex);
            mDuration = System.currentTimeMillis();
        }
    }

    public void checkLiveness(int[] livenessStatus) {
        if (mIndex < mLivenessList.size() && !mIsCurrentCheckSuccess) {
            long t = System.currentTimeMillis();
            if (t - mDuration > TIME_LIVENESS_MODULE) {
                mTimeoutFlag = true;
                mIsCurrentCheckSuccess = false;
            } else {
                mIsCurrentCheckSuccess = getLivenessStatus(livenessStatus, mCurrentLivenessTypeEnum);
            }
        }
    }

    // 取得活体状态
    public static boolean getLivenessStatus(int[] live, LivenessTypeEnum type) {
        boolean flag = false;

        if (type == Eye) {
            if (live[0] == 1) {
                flag = true;
            } else {
                flag = false;
            }

//            if (flag)
//                Log.e(TAG, "LivenessReady status eye " + live[0]);

        } else if (type == Mouth) {
            flag = live[3] == 1;

//            if (flag)
//                Log.e(TAG, "LivenessReady status mouth " + live[3] );

        } else if (type == HeadLeft) {
//            Log.e(TAG, "LivenessReady status headleft " + live[5]);
            flag = live[5] == 1;
        } else if (type == HeadRight) {
//            Log.e(TAG, "LivenessReady status headright " + live[6]);
            flag = live[6] == 1;
        } else if (type == HeadUp) {
//            Log.e(TAG, "LivenessReady status headup " + live[8]);
            flag = live[8] == 1;
        } else if (type == HeadDown) {
//            Log.e(TAG, "LivenessReady status headdown " + live[9]);
            flag = live[9] == 1;
        } else if (type == HeadLeftOrRight) {
//            Log.e(TAG, "LivenessReady status headleft " + live[5]);
            flag = (live[5] == 1 || live[6] == 1);
        }
        return flag;
    }

    public void reset() {
        mIsCurrentCheckSuccess = false;
        mIndex = 0;
        // Collections.shuffle(mLivenessList);
        if (mLivenessList != null && mIndex < mLivenessList.size()) {
            mCurrentLivenessTypeEnum = mLivenessList.get(mIndex);
        }
        mDuration = System.currentTimeMillis();
        mTimeoutFlag = false;
    }

    public void resetState() {
        mIsCurrentCheckSuccess = false;
        mDuration = System.currentTimeMillis();
        mTimeoutFlag = false;
    }

}
