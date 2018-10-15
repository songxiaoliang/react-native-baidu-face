/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5Utils
 */
public class MD5Utils {

    public static String encryption(byte[] message) {
        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(message);
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                int i = (b & 0xFF);
                if (i < 0x10) {
                    hex.append('0');
                }
                hex.append(Integer.toHexString(i));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
