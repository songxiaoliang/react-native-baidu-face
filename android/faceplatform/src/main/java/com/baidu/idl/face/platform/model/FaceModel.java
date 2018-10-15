/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform.model;

import com.baidu.idl.face.platform.FaceStatusEnum;

/**
 * 人脸数据对象
 */
public class FaceModel {

    private FaceExtInfo[] faceInfos;
    private FaceStatusEnum faceStatus;
    private int[] argbImage;
    private long frameTime;

    public FaceExtInfo[] getFaceInfos() {
        return faceInfos;
    }

    public void setFaceInfos(FaceExtInfo[] faceInfos) {
        this.faceInfos = faceInfos;
    }

    public FaceStatusEnum getFaceModuleState() {
        return faceStatus;
    }

    public void setFaceModuleState(FaceStatusEnum faceStatus) {
        this.faceStatus = faceStatus;
    }

    public int[] getArgbImage() {
        return argbImage;
    }

    public void setArgbImage(int[] argb) {
        this.argbImage = argb;
    }

    public long getFrameTime() {
        return frameTime;
    }

    public void setFrameTime(long frameTime) {
        this.frameTime = frameTime;
    }
}
