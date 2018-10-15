/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform.utils;

import android.os.Build;

/**
 * 系统版本信息工具类
 */
public final class APIUtils {

    /**
     * 私有构造函数
     */
    private APIUtils() {
    }

    /**
     * If platform is Froyo (level 8) or above.
     *
     * @return If platform SDK is above Froyo
     */
    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    /**
     * If platform is Gingerbread (level 9) or above.
     *
     * @return If platform SDK is above Gingerbread
     */
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * If platform is Honeycomb (level 11) or above.
     *
     * @return If platform SDK is above Honeycomb
     */
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * If platform is Honeycomb MR1 (level 12) or above.
     *
     * @return If platform SDK is above Honeycomb MR1
     */
    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    /**
     * If platform is Ice Cream Sandwich (level 14) or above.
     *
     * @return If platform SDK is above Ice Cream Sandwich
     */
    public static boolean hasICS() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * If platform is JellyBean (level 16) or above.
     *
     * @return If platform SDK is above JellyBean
     */
    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * If platform is JellyBean MR1 (level 17) or above.
     *
     * @return If platform SDK is above JellyBean MR1
     */
    public static boolean hasJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }
}
