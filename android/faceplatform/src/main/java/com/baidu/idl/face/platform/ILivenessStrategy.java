/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform;

import android.graphics.Rect;

import java.util.List;

/**
 * 活体检测功能接口
 */
public interface ILivenessStrategy {

    // 活体检测策略功能接口方法
    void setLivenessStrategyConfig(List<LivenessTypeEnum> livenessList,
                                   Rect previewRect, Rect detectRect,
                                   ILivenessStrategyCallback callback);

    void setLivenessStrategySoundEnable(boolean flag);

    void livenessStrategy(byte[] imageData);

    void setPreviewDegree(int degree);

    String getBestFaceImage();

    void reset();

}
