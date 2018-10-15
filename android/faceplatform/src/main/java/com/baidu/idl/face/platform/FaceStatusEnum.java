/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform;

/**
 * SDK功能状态
 */
public enum FaceStatusEnum {
    OK,
    Detect_NoFace,
    Detect_PoorIllumintion,
    Detect_ImageBlured,
    Detect_OccLeftEye,
    Detect_OccRightEye,
    Detect_OccNose,
    Detect_OccMouth,
    Detect_OccLeftContour,
    Detect_OccRightContour,
    Detect_OccChin,
    Detect_PitchOutOfUpMaxRange,
    Detect_PitchOutOfDownMaxRange,
    Detect_PitchOutOfLeftMaxRange,
    Detect_PitchOutOfRightMaxRange,
    Detect_FaceDetected,
    Detect_FaceZoomIn,
    Detect_FaceZoomOut,
    Detect_FacePointOut,
    Detect_DataNotReady,
    Detect_DataHitOne,
    Detect_DataHitLast,
    Detect_FaceNotComplete,
    Liveness_Eye,
    Liveness_Mouth,
    Liveness_HeadUp,
    Liveness_HeadDown,
    Liveness_HeadLeft,
    Liveness_HeadRight,
    Liveness_HeadLeftRight,
    Liveness_OK,
    Liveness_Completion,
    Error_Timeout,
    Error_DetectTimeout,
    Error_LivenessTimeout,
    Error_License,
    Error_Param,
    Error_Image,
    Error_Detect,
    Error_Liveness
}
