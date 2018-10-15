package com.baidu.idl.face.platform.model;

import android.graphics.Point;
import android.graphics.Rect;

import com.baidu.idl.facesdk.FaceInfo;

import java.util.HashMap;

/**
 * 人脸数据对象
 */
public class FaceExtInfo {

    private int mWidth;
    private int mAngle;
    private int mCenter_y;
    private int mCenter_x;
    private float mConf;
    public int[] landmarks;
    private int face_id;
    private float[] headPose;
    private int[] is_live;

    public FaceExtInfo() {
    }

    public FaceExtInfo(FaceInfo info) {
        this.mWidth = info.mWidth;
        this.mAngle = info.mAngle;
        this.mCenter_y = info.mCenter_y;
        this.mCenter_x = info.mCenter_x;
        this.mConf = info.mConf;
        this.landmarks = info.landmarks;
        this.face_id = info.face_id;
        this.headPose = info.headPose;
        this.is_live = info.is_live;
    }

    public void addFaceInfo(FaceInfo info) {
        this.mWidth = info.mWidth;
        this.mAngle = info.mAngle;
        this.mCenter_y = info.mCenter_y;
        this.mCenter_x = info.mCenter_x;
        this.mConf = info.mConf;
        this.landmarks = info.landmarks;
        this.face_id = info.face_id;
        this.headPose = info.headPose;
        this.is_live = info.is_live;
    }

    public int getFaceId() {
        return face_id;
    }

    public void getRectPoints(int[] pts) {
        double degree_rad = (double) this.mAngle * 3.14159D / 180.0D;
        double cos_degree = Math.cos(degree_rad);
        double sin_degree = Math.sin(degree_rad);
        int center_x = (int) ((double) this.mCenter_x + cos_degree * (double) this.mWidth / 2.0D - sin_degree * (double) this.mWidth / 2.0D);
        int center_y = (int) ((double) this.mCenter_y + sin_degree * (double) this.mWidth / 2.0D + cos_degree * (double) this.mWidth / 2.0D);
        double _angle = (double) this.mAngle * 3.14159D / 180.0D;
        double b = Math.cos(_angle) * 0.5D;
        double a = Math.sin(_angle) * 0.5D;
        if (pts == null || pts.length == 0) {
            pts = new int[8];
        }

        pts[0] = (int) ((double) center_x - a * (double) this.mWidth - b * (double) this.mWidth);
        pts[1] = (int) ((double) center_y + b * (double) this.mWidth - a * (double) this.mWidth);
        pts[2] = (int) ((double) center_x + a * (double) this.mWidth - b * (double) this.mWidth);
        pts[3] = (int) ((double) center_y - b * (double) this.mWidth - a * (double) this.mWidth);
        pts[4] = 2 * center_x - pts[0];
        pts[5] = 2 * center_y - pts[1];
        pts[6] = 2 * center_x - pts[2];
        pts[7] = 2 * center_y - pts[3];
    }

    public boolean isLiveEye() {
        return this.is_live != null && this.is_live.length == 11 ? 1 == this.is_live[0] : false;
    }

    public boolean isLiveMouth() {
        return this.is_live != null && this.is_live.length == 11 ? 1 == this.is_live[3] : false;
    }

    public boolean isLiveHeadTurnLeft() {
        return this.is_live != null && this.is_live.length == 11 ? 1 == this.is_live[5] : false;
    }

    public boolean isLiveHeadTurnRight() {
        return this.is_live != null && this.is_live.length == 11 ? 1 == this.is_live[6] : false;
    }

    public boolean isLiveHeadTurnLeftOrRight() {
        return this.is_live != null && this.is_live.length == 11
                ? (1 == this.is_live[5] || 1 == this.is_live[6]) : false;
    }

    public boolean isLiveHeadUp() {
        return this.is_live != null && this.is_live.length == 11 ? 1 == this.is_live[8] : false;
    }

    public boolean isLiveHeadDown() {
        return this.is_live != null && this.is_live.length == 11 ? 1 == this.is_live[9] : false;
    }

    public int getLeftEyeState() {
        return this.is_live != null && this.is_live.length == 11 ? this.is_live[1] : 0;
    }

    public int getRightEyeState() {
        return this.is_live != null && this.is_live.length == 11 ? this.is_live[2] : 0;
    }

    public int getMouthState() {
        return this.is_live != null && this.is_live.length == 11 ? this.is_live[4] : 0;
    }

    // 人脸区域
    public Rect getFaceRect() {
        Rect rect = new Rect(
                mCenter_x - mWidth / 2,
                mCenter_y - mWidth / 2,
                mWidth,
                mWidth);
        return rect;
    }

    private HashMap<String, Point[]> facePointMap;

    // 人脸宽度
    public int getFaceWidth() {
        return mWidth;
    }

    // 头部姿态
    // pitch 低头仰头角度
    // yaw 侧脸
    // roll平面内旋转

    private float mPitch;
    private float mYaw;
    private float mRoll;

    public float getPitch() {
        mPitch = headPose[0];
        if (headPose != null && headPose.length > 0) {
            mPitch = headPose[0];
        }
        return mPitch;
    }

    public float getYaw() {
        if (headPose != null && headPose.length > 1) {
            mYaw = headPose[1];
        }
        return mYaw;
    }

    public float getRoll() {
        if (headPose != null && headPose.length > 2) {
            mRoll = headPose[2];
        }
        return mRoll;
    }

    // 置信度
    public float getConfidence() {
        return mConf;
    }

    // 取得活体状态
    public int[] getLiveInfo() {
        return is_live;
    }

    // 取得人脸在跟踪框外的关键点数量
    private static int nComponents = 9;
    private static int comp1[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    private static int comp2[] = {13, 14, 15, 16, 17, 18, 19, 20, 13, 21};
    private static int comp3[] = {22, 23, 24, 25, 26, 27, 28, 29, 22};
    private static int comp4[] = {30, 31, 32, 33, 34, 35, 36, 37, 30, 38};
    private static int comp5[] = {39, 40, 41, 42, 43, 44, 45, 46, 39};
    private static int comp6[] = {47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 47};
    private static int comp7[] = {51, 57, 52};
    private static int comp8[] = {58, 59, 60, 61, 62, 63, 64, 65, 58};
    private static int comp9[] = {58, 66, 67, 68, 62, 69, 70, 71, 58};
    private static int nPoints[] = {13, 10, 9, 10, 9, 11, 3, 9, 9};

    public int getLandmarksOutOfDetectCount(Rect detectRect) {
        float ratioX = 1;
        float ratioY = 1;
        int outCount = 0;
        if (landmarks.length == 144) {
            int idx[][] = {comp1, comp2, comp3, comp4, comp5, comp6, comp7, comp8, comp9};
            float[] positionArr = new float[4];
            for (int i = 0; i < nComponents; ++i) {
                for (int j = 0; j < nPoints[i] - 1; ++j) {
                    positionArr[0] = landmarks[idx[i][j] << 1];
                    positionArr[1] = landmarks[1 + (idx[i][j] << 1)];
                    positionArr[2] = landmarks[idx[i][j + 1] << 1];
                    positionArr[3] = landmarks[1 + (idx[i][j + 1] << 1)];

                    if (!detectRect.contains((int) (positionArr[0] * ratioX), (int) (positionArr[1] * ratioY))) {
                        outCount++;
                    }
                    if (!detectRect.contains((int) (positionArr[2] * ratioX), (int) (positionArr[3] * ratioY))) {
                        outCount++;
                    }
                }
            }
        }
        return outCount;
    }

    public boolean isOutofDetectRect(Rect detectRect) {
        Rect rect = getFaceRect();
        return detectRect.contains(rect);
    }
}
