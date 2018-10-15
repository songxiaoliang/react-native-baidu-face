/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform.common;

import android.text.TextUtils;

import com.baidu.idl.face.platform.FaceStatusEnum;
import com.baidu.idl.face.platform.network.LogRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 统计工具类
 */
public class LogHelper {

    private static final String TAG = LogHelper.class.getSimpleName();

    private static HashMap<String, Object> logMap = new HashMap<String, Object>();
    private static ArrayList<Integer> logLivenessLiveness = new ArrayList<Integer>();
    private static HashMap<String, Integer> logTipsMap = new HashMap<String, Integer>();

    public static void addLogWithKey(String key, Object value) {
        if (logMap != null && !logMap.containsKey(key)) {
            logMap.put(key, value);
        }
    }

    public static void addLog(String key, Object value) {
        if (logMap != null) {
            logMap.put(key, value);
        }
    }

    public static void addLivenessLog(int livenessIndex) {
        if (logLivenessLiveness != null && !logLivenessLiveness.contains(livenessIndex)) {
            logLivenessLiveness.add(livenessIndex);
        }
    }

    public static void addTipsLogWithKey(String status) {
        if (logTipsMap != null && !logTipsMap.containsKey(status)) {
            logTipsMap.put(status, 1);
        } else if (logTipsMap != null && logTipsMap.containsKey(status)) {
            int count = logTipsMap.get(status) + 1;
            logTipsMap.put(status, count);
        }
    }

    private static String getLog() {
        StringBuilder log = new StringBuilder();
        try {
            int index = 0;
            log.append("{");
            for (Map.Entry<String, Object> entry : logMap.entrySet()) {
                if (index == logMap.size() - 1) {
                    if (entry.getValue() instanceof String) {
                        log.append(entry.getKey() + ":'" + entry.getValue() + "'");
                    } else {
                        log.append(entry.getKey() + ":" + entry.getValue());
                    }
                } else {
                    if (entry.getValue() instanceof String) {
                        log.append(entry.getKey() + ":'" + entry.getValue() + "'");
                    } else {
                        log.append(entry.getKey() + ":" + entry.getValue());
                    }
                    log.append(",");
                }
                ++index;
            }

            if (logLivenessLiveness != null && logLivenessLiveness.size() > 0) {
                log.append("," + ConstantHelper.LOG_LV + ":[");
                for (int i = 0; i < logLivenessLiveness.size(); i++) {
                    if (i == logLivenessLiveness.size() - 1) {
                        log.append(logLivenessLiveness.get(i));
                    } else {
                        log.append(logLivenessLiveness.get(i) + ",");
                    }
                }
                log.append("]");
            }

            if (logTipsMap != null && logTipsMap.size() > 0) {
                log.append("," + ConstantHelper.LOG_MSG + ":{");
                log.append(getTipsMessage());
                log.append("}");
            }

            log.append("}");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        logMap = new HashMap<String, Object>();
        logLivenessLiveness = new ArrayList<Integer>();
        logTipsMap = new HashMap<String, Integer>();
        return log.toString();
    }

    private static String getTipsMessage() {
        StringBuilder log = new StringBuilder();
        int index = 0;
        String key = "";
        for (Map.Entry<String, Integer> entry : logTipsMap.entrySet()) {
            key = getTipsKey(entry.getKey());
            if (!TextUtils.isEmpty(key)) {
                log.append(key + ":" + entry.getValue());
                log.append(",");
            }
            ++index;
        }
        if (log.length() > 0) {
            log.deleteCharAt(log.length() - 1);
        }
        return log.toString();
    }

    private static String getTipsKey(String key) {
        String tipsKey = "";
        if (TextUtils.equals(key, FaceStatusEnum.Detect_OccLeftEye.name())) {
            tipsKey = ConstantHelper.LOG_TIPS_LEFTEYE_OCC;
        } else if (TextUtils.equals(key, FaceStatusEnum.Detect_OccRightEye.name())) {
            tipsKey = ConstantHelper.LOG_TIPS_RIGHTEYE_OCC;
        } else if (TextUtils.equals(key, FaceStatusEnum.Detect_OccNose.name())) {
            tipsKey = ConstantHelper.LOG_TIPS_NOSE_OCC;
        } else if (TextUtils.equals(key, FaceStatusEnum.Detect_OccMouth.name())) {
            tipsKey = ConstantHelper.LOG_TIPS_MOUTH_OCC;
        } else if (TextUtils.equals(key, FaceStatusEnum.Detect_OccLeftContour.name())) {
            tipsKey = ConstantHelper.LOG_TIPS_LEFTFACE_OCC;
        } else if (TextUtils.equals(key, FaceStatusEnum.Detect_OccRightContour.name())) {
            tipsKey = ConstantHelper.LOG_TIPS_RIGHTFACE_OCC;
        } else if (TextUtils.equals(key, FaceStatusEnum.Detect_OccChin.name())) {
            tipsKey = ConstantHelper.LOG_TIPS_CHIN_OCC;
        } else if (TextUtils.equals(key, FaceStatusEnum.Detect_PoorIllumintion.name())) {
            tipsKey = ConstantHelper.LOG_TIPS_LIGHTUP;
        } else if (TextUtils.equals(key, FaceStatusEnum.Detect_ImageBlured.name())) {
            tipsKey = ConstantHelper.LOG_TIPS_STAYSTILL;
        } else if (TextUtils.equals(key, FaceStatusEnum.Detect_FaceZoomIn.name())) {
            tipsKey = ConstantHelper.LOG_TIPS_MOVECLOSE;
        } else if (TextUtils.equals(key, FaceStatusEnum.Detect_FaceZoomOut.name())) {
            tipsKey = ConstantHelper.LOG_TIPS_MOVEFURTHER;
        } else if (TextUtils.equals(key, FaceStatusEnum.Detect_PitchOutOfDownMaxRange.name())) {
            tipsKey = ConstantHelper.LOG_TIPS_HEADUP;
        } else if (TextUtils.equals(key, FaceStatusEnum.Detect_PitchOutOfUpMaxRange.name())) {
            tipsKey = ConstantHelper.LOG_TIPS_HEADDOWN;
        } else if (TextUtils.equals(key, FaceStatusEnum.Detect_PitchOutOfRightMaxRange.name())) {
            tipsKey = ConstantHelper.LOG_TIPS_TURNLEFT;
        } else if (TextUtils.equals(key, FaceStatusEnum.Detect_PitchOutOfLeftMaxRange.name())) {
            tipsKey = ConstantHelper.LOG_TIPS_TURNRIGHT;
        } else if (TextUtils.equals(key, FaceStatusEnum.Detect_NoFace.name())
                || TextUtils.equals(key, FaceStatusEnum.Detect_FacePointOut.name())) {
            tipsKey = ConstantHelper.LOG_TIPS_MOVEFACE;
        }
        return tipsKey;
    }

    public static void sendLog() {
        String message = getLog();
        LogRequest.sendLogMessage(message);
    }

    public static void clear() {
        logMap = new HashMap<String, Object>();
        logLivenessLiveness = new ArrayList<Integer>();
        logTipsMap = new HashMap<String, Integer>();
    }

}
