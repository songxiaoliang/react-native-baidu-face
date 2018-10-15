/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform;

import android.graphics.Rect;

/**
 * 人脸跟踪功能接口
 */
public interface IDetectStrategy {

    // 人脸跟踪策略功能接口方法
    void setDetectStrategyConfig(Rect previewRect, Rect detectRect,
                                 IDetectStrategyCallback callback);

    void setDetectStrategySoundEnable(boolean flag);

    void detectStrategy(byte[] imageData);

    void setPreviewDegree(int degree);

    String getBestFaceImage();

    void reset();

}
