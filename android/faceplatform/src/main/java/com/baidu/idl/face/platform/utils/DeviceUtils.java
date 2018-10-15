/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform.utils;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.util.UUID;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * 设备信息工具类
 */
public class DeviceUtils {

    private static final String TAG = DeviceUtils.class.getSimpleName();

    public static String getDeviceCode(Context context) {
        String code = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            code = tm.getDeviceId();
            code = MD5Utils.encryption(code.getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return code;
    }

    public static String getAndroidID(Context context) {
        String androidId = Settings.Secure
                .getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            androidId = MD5Utils.encryption(androidId.getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return androidId;
    }

    public static String getSerialNumber(Context context) {
        return android.os.Build.SERIAL;
    }

    public static String getUUID() {
        String uniqueID = UUID.randomUUID().toString();
        return uniqueID;
    }
}
