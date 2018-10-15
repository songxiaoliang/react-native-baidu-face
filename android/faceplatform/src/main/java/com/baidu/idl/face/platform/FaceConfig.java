/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 人脸检测参数配置类
 */
public class FaceConfig implements Serializable {

    private static final String TAG = FaceConfig.class.getSimpleName();

    // 人脸检测参数
    /**
     * 图像光照阀值
     */
    public float brightnessValue = FaceEnvironment.VALUE_BRIGHTNESS;
    /**
     * 图像模糊阀值
     */
    public float blurnessValue = FaceEnvironment.VALUE_BLURNESS;
    /**
     * 图像中人脸遮挡阀值
     */
    public float occlusionValue = FaceEnvironment.VALUE_OCCLUSION;
    /**
     * 图像中人脸抬头低头角度阀值
     */
    public int headPitchValue = FaceEnvironment.VALUE_HEAD_PITCH;
    /**
     * 图像中人脸左右角度阀值
     */
    public int headYawValue = FaceEnvironment.VALUE_HEAD_YAW;
    /**
     * 图像中人脸偏头阀值
     */
    public int headRollValue = FaceEnvironment.VALUE_HEAD_ROLL;
    /**
     * 裁剪图像中人脸时的大小
     */
    public int cropFaceValue = FaceEnvironment.VALUE_CROP_FACE_SIZE;
    /**
     * 图像能被检测出人脸的最小人脸值
     */
    public int minFaceSize = FaceEnvironment.VALUE_MIN_FACE_SIZE;
    /**
     * 图像能被检测出人脸阀值
     */
    public float notFaceValue = FaceEnvironment.VALUE_NOT_FACE_THRESHOLD;
    /**
     * 人脸采集图片数量阀值
     */
    public int maxCropImageNum = FaceEnvironment.VALUE_MAX_CROP_IMAGE_NUM;
    /**
     * 是否进行人脸图片质量检测
     */
    public boolean isCheckFaceQuality = FaceEnvironment.VALUE_IS_CHECK_QUALITY;
    /**
     * 是否开启提示音
     */
    public boolean isSound = true;
    /**
     * 是否进行检测
     */
    public boolean isVerifyLive = true;
    /**
     * 人脸检测时开启的进程数，建议为CPU核数
     */
    public int faceDecodeNumberOfThreads = 0;
    /**
     * 是否随机活体检测动作
     */
    public boolean isLivenessRandom = false;
    /**
     * 随机活体检测动作数
     */
    public int livenessRandomCount = FaceEnvironment.VALUE_LIVENESS_DEFAULT_RANDOM_COUNT;
    /**
     * 活体检测的动作类型列表
     */
    public List<LivenessTypeEnum> livenessTypeList = FaceEnvironment.livenessTypeDefaultList;

    public FaceConfig() {
    }

    public float getBrightnessValue() {
        return brightnessValue;
    }

    public void setBrightnessValue(float brightnessValue) {
        this.brightnessValue = brightnessValue;
    }

    public float getBlurnessValue() {
        return blurnessValue;
    }

    public void setBlurnessValue(float blurnessValue) {
        this.blurnessValue = blurnessValue;
    }

    public float getOcclusionValue() {
        return occlusionValue;
    }

    public void setOcclusionValue(float occlusionValue) {
        this.occlusionValue = occlusionValue;
    }

    public int getHeadPitchValue() {
        return headPitchValue;
    }

    public void setHeadPitchValue(int headPitchValue) {
        this.headPitchValue = headPitchValue;
    }

    public int getHeadYawValue() {
        return headYawValue;
    }

    public void setHeadYawValue(int headYawValue) {
        this.headYawValue = headYawValue;
    }

    public int getHeadRollValue() {
        return headRollValue;
    }

    public void setHeadRollValue(int headRollValue) {
        this.headRollValue = headRollValue;
    }

    public int getCropFaceValue() {
        return cropFaceValue;
    }

    public void setCropFaceValue(int cropFaceValue) {
        this.cropFaceValue = cropFaceValue;
    }

    public int getMinFaceSize() {
        return minFaceSize;
    }

    public void setMinFaceSize(int minFaceSize) {
        this.minFaceSize = minFaceSize;
    }

    public float getNotFaceValue() {
        return notFaceValue;
    }

    public void setNotFaceValue(float notFaceValue) {
        this.notFaceValue = notFaceValue;
    }

    public int getMaxCropImageNum() {
        return maxCropImageNum;
    }

    public void setMaxCropImageNum(int maxCropImageNum) {
        this.maxCropImageNum = maxCropImageNum;
    }

    public boolean isCheckFaceQuality() {
        return isCheckFaceQuality;
    }

    public void setCheckFaceQuality(boolean checkFaceQuality) {
        isCheckFaceQuality = checkFaceQuality;
    }

    public boolean isSound() {
        return isSound;
    }

    public void setSound(boolean sound) {
        isSound = sound;
    }

    public boolean isLivenessRandom() {
        return isLivenessRandom;
    }

    public void setLivenessRandom(boolean livenessRandom) {
        isLivenessRandom = livenessRandom;
    }

    public int getLivenessRandomCount() {
        return livenessRandomCount;
    }

    public void setLivenessRandomCount(int livenessRandomCount) {
        int count = FaceEnvironment.livenessTypeDefaultList.size();
        this.livenessRandomCount = livenessRandomCount <= count ? livenessRandomCount : count;
    }

    public boolean isVerifyLive() {
        return isVerifyLive;
    }

    public void setVerifyLive(boolean verifyLive) {
        isVerifyLive = verifyLive;
    }

    public int getFaceDecodeNumberOfThreads() {
        return faceDecodeNumberOfThreads;
    }

    public void setFaceDecodeNumberOfThreads(int faceDecodeNumberOfThreads) {
        this.faceDecodeNumberOfThreads = faceDecodeNumberOfThreads;
    }

    public List<LivenessTypeEnum> getLivenessTypeList() {
        // 列表为空
        if (livenessTypeList == null || livenessTypeList.size() == 0) {
            livenessTypeList = new ArrayList<LivenessTypeEnum>();
            livenessTypeList.addAll(FaceEnvironment.livenessTypeDefaultList);
            // 随机列表
            Collections.shuffle(livenessTypeList);
            int count = FaceEnvironment.VALUE_LIVENESS_DEFAULT_RANDOM_COUNT;
            livenessTypeList = livenessTypeList.subList(0, count);
        } else {
            // 列表非空
            if (isLivenessRandom) {
                Collections.shuffle(livenessTypeList);
            }
        }
        return livenessTypeList;
    }

    public void setLivenessTypeList(List<LivenessTypeEnum> list) {
        livenessTypeList = list;
    }
}
