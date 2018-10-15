/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform;

import com.baidu.idl.face.platform.model.FaceModel;

/**
 * 人脸跟踪功能接口
 */
public interface IDetect {
    FaceModel detect(byte[] imageData, int imageWidth, int imageHeight);

    int[] getBestFaceImage();

    void reset();
}
