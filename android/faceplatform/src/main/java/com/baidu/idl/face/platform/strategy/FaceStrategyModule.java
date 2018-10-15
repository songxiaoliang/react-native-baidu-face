/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform.strategy;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.baidu.idl.face.platform.FaceEnvironment;
import com.baidu.idl.face.platform.common.ConstantHelper;
import com.baidu.idl.face.platform.common.LogHelper;
import com.baidu.idl.face.platform.decode.FaceModule;
import com.baidu.idl.facesdk.FaceTracker;

/**
 * 人脸跟踪,活体检测策略控制类
 */
abstract class FaceStrategyModule {

    private static final String TAG = FaceStrategyModule.class.getSimpleName();
    protected FaceModule mFaceModule;
    protected byte[] mImageData;
    protected long mLaunchTime = 0l;
    protected long mNoFaceTime = 0l;
    //    protected int mNoFaceCount = 0;
    protected Handler mUIHandler;
    protected volatile boolean mIsProcessing = true;
    protected volatile boolean mIsCompletion = false;
    private static volatile int mProcessCount = 0;

    public FaceStrategyModule(FaceTracker tracker) {
        mUIHandler = new Handler(Looper.getMainLooper());
        mFaceModule = new FaceModule(tracker);
        LogHelper.clear();

        LogHelper.addLog(ConstantHelper.LOG_CATE, FaceEnvironment.TAG + FaceEnvironment.SDK_VERSION);
        LogHelper.addLog(ConstantHelper.LOG_OS, Build.VERSION.SDK_INT);
        LogHelper.addLog(ConstantHelper.LOG_VS, FaceEnvironment.SDK_VERSION);
        LogHelper.addLog(ConstantHelper.LOG_DE, Build.MODEL + " " + Build.MANUFACTURER);
        LogHelper.addLog(ConstantHelper.LOG_STM, System.currentTimeMillis());
    }

    protected void process(byte[] imageData) {

        if (mProcessCount > 0)
            return;

        mImageData = imageData;
        new FaceProcessRunnable().run();
        ++mProcessCount;
    }

    abstract protected void processStrategy(byte[] imageData);

    protected void processUIStrategy(Runnable runnable) {
        if (mUIHandler != null) {
            mUIHandler.post(runnable);
        }
    }

    protected void processUIStrategyDelay(Runnable runnable, long delay) {
        if (mUIHandler != null) {
            mUIHandler.postDelayed(runnable, delay);
        }
    }

    private class FaceProcessRunnable implements Runnable {

        @Override
        public void run() {
            processStrategy(mImageData);
            --mProcessCount;
        }
    }

    public void reset() {
//        mUIHandler = null;
        mProcessCount = 0;
        if (mFaceModule != null) {
            mFaceModule.reset();
        }
    }
}
