/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform;

import java.util.HashMap;

/**
 * 活体检测回调接口
 */
public interface ILivenessStrategyCallback {

    String IMAGE_KEY_BEST_IMAGE = "bestImage";

    void onLivenessCompletion(FaceStatusEnum status, String message,
                              HashMap<String, String> base64ImageMap);
}
