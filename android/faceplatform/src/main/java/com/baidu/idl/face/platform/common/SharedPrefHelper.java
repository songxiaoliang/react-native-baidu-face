/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.baidu.idl.face.platform.LivenessTypeEnum;

import java.util.Arrays;
import java.util.List;

/**
 * 配置存储工具类
 */
public final class SharedPrefHelper {

    private SharedPrefHelper() {
    }

    private static final String SHARED_PREFERENCES_NAME_FACE_VALUE = "face_sdk_value";

    private static SharedPreferences getPref(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME_FACE_VALUE,
                Context.MODE_PRIVATE);
    }
}
