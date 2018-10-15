/*
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform;

import java.util.HashMap;

/**
 * IDetectStrategyCallback
 * 描述:人脸跟踪回调接口
 */
public interface IDetectStrategyCallback {

    String IMAGE_KEY_BEST_IMAGE = "bestImage";

    void onDetectCompletion(FaceStatusEnum status, String message, HashMap<String, String> base64ImageMap);
}
