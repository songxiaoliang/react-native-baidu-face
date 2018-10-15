/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform.ui;

import com.baidu.idl.face.platform.FaceEnvironment;
import com.baidu.idl.face.platform.FaceStatusEnum;

/**
 * sdk使用Res资源设置功能
 */
public class FaceSDKResSettings {

    public static void initializeResId() {

        // Sound Res Id
        FaceEnvironment.setSoundId(FaceStatusEnum.Detect_NoFace, R.raw.detect_face_in);
        FaceEnvironment.setSoundId(FaceStatusEnum.Detect_FacePointOut, R.raw.detect_face_in);
        FaceEnvironment.setSoundId(FaceStatusEnum.Liveness_Eye, R.raw.liveness_eye);
        FaceEnvironment.setSoundId(FaceStatusEnum.Liveness_Mouth, R.raw.liveness_mouth);
        FaceEnvironment.setSoundId(FaceStatusEnum.Liveness_HeadUp, R.raw.liveness_head_up);
        FaceEnvironment.setSoundId(FaceStatusEnum.Liveness_HeadDown, R.raw.liveness_head_down);
        FaceEnvironment.setSoundId(FaceStatusEnum.Liveness_HeadLeft, R.raw.liveness_head_left);
        FaceEnvironment.setSoundId(FaceStatusEnum.Liveness_HeadRight, R.raw.liveness_head_right);
        FaceEnvironment.setSoundId(FaceStatusEnum.Liveness_HeadLeftRight, R.raw.liveness_head_left_right);
        FaceEnvironment.setSoundId(FaceStatusEnum.Liveness_OK, R.raw.face_good);
        FaceEnvironment.setSoundId(FaceStatusEnum.OK, R.raw.face_good);

        // Tips Res Id
        FaceEnvironment.setTipsId(FaceStatusEnum.Detect_NoFace, R.string.detect_no_face);
        FaceEnvironment.setTipsId(FaceStatusEnum.Detect_FacePointOut, R.string.detect_face_in);
        FaceEnvironment.setTipsId(FaceStatusEnum.Detect_PoorIllumintion, R.string.detect_low_light);
        FaceEnvironment.setTipsId(FaceStatusEnum.Detect_ImageBlured, R.string.detect_keep);
        FaceEnvironment.setTipsId(FaceStatusEnum.Detect_OccLeftEye, R.string.detect_occ_face);
        FaceEnvironment.setTipsId(FaceStatusEnum.Detect_OccRightEye, R.string.detect_occ_face);
        FaceEnvironment.setTipsId(FaceStatusEnum.Detect_OccNose, R.string.detect_occ_face);
        FaceEnvironment.setTipsId(FaceStatusEnum.Detect_OccMouth, R.string.detect_occ_face);
        FaceEnvironment.setTipsId(FaceStatusEnum.Detect_OccLeftContour, R.string.detect_occ_face);
        FaceEnvironment.setTipsId(FaceStatusEnum.Detect_OccRightContour, R.string.detect_occ_face);
        FaceEnvironment.setTipsId(FaceStatusEnum.Detect_OccChin, R.string.detect_occ_face);
        FaceEnvironment.setTipsId(FaceStatusEnum.Detect_PitchOutOfUpMaxRange, R.string.detect_head_down);
        FaceEnvironment.setTipsId(FaceStatusEnum.Detect_PitchOutOfDownMaxRange, R.string.detect_head_up);
        FaceEnvironment.setTipsId(FaceStatusEnum.Detect_PitchOutOfLeftMaxRange, R.string.detect_head_right);
        FaceEnvironment.setTipsId(FaceStatusEnum.Detect_PitchOutOfRightMaxRange, R.string.detect_head_left);
        FaceEnvironment.setTipsId(FaceStatusEnum.Detect_FaceZoomIn, R.string.detect_zoom_in);
        FaceEnvironment.setTipsId(FaceStatusEnum.Detect_FaceZoomOut, R.string.detect_zoom_out);

        FaceEnvironment.setTipsId(FaceStatusEnum.Liveness_Eye, R.string.liveness_eye);
        FaceEnvironment.setTipsId(FaceStatusEnum.Liveness_Mouth, R.string.liveness_mouth);
        FaceEnvironment.setTipsId(FaceStatusEnum.Liveness_HeadUp, R.string.liveness_head_up);
        FaceEnvironment.setTipsId(FaceStatusEnum.Liveness_HeadDown, R.string.liveness_head_down);
        FaceEnvironment.setTipsId(FaceStatusEnum.Liveness_HeadLeft, R.string.liveness_head_left);
        FaceEnvironment.setTipsId(FaceStatusEnum.Liveness_HeadRight, R.string.liveness_head_right);
        FaceEnvironment.setTipsId(FaceStatusEnum.Liveness_HeadLeftRight, R.string.liveness_head_left_right);
        FaceEnvironment.setTipsId(FaceStatusEnum.Liveness_OK, R.string.liveness_good);
        FaceEnvironment.setTipsId(FaceStatusEnum.OK, R.string.liveness_good);

        FaceEnvironment.setTipsId(FaceStatusEnum.Error_Timeout, R.string.detect_timeout);
        FaceEnvironment.setTipsId(FaceStatusEnum.Error_DetectTimeout, R.string.detect_timeout);
        FaceEnvironment.setTipsId(FaceStatusEnum.Error_LivenessTimeout, R.string.detect_timeout);
    }
}
