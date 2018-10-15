/*
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform.network;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 无动作活体接口
 */
public class NoMotionRequest extends BaseRequest {

    private static final String TAG = NoMotionRequest.class.getSimpleName();
    // 无动作活体检测地址
    public static final String URL_POST_NOMOTION_LIVENESS = "http://face.baidu.com/gate/api/userverifydemo";

    public static void sendMessage(final String image, final Handler uiHandler) {
        if (image != null && image.length() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    httpUrlConnectionPost(image, uiHandler);
                }
            }).start();
        }
    }

    private static void httpUrlConnectionPost(String message, Handler uiHandler) {
        StringBuilder result = new StringBuilder("");
        HttpURLConnection urlConnection = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        int responseCode = 0;
        try {
            String requestMessage = "pic_file=" + URLEncoder.encode(message, "UTF-8");
            URL url = new URL(URL_POST_NOMOTION_LIVENESS);
            urlConnection = (HttpURLConnection) url.openConnection();
            System.setProperty("sun.net.client.defaultConnectTimeout", "8000");
            System.setProperty("sun.net.client.defaultReadTimeout", "8000");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setInstanceFollowRedirects(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.connect();

            outputStream = urlConnection.getOutputStream();
            outputStream.write(requestMessage.getBytes());
            outputStream.flush();
            outputStream.close();

            responseCode = urlConnection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == responseCode) {
                inputStream = urlConnection.getInputStream();
                byte[] buffer = new byte[1024];
                baos = new ByteArrayOutputStream();
                int len = -1;
                while ((len = inputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                byte[] b = baos.toByteArray();
                result.append(new String(b, "utf-8"));
                baos.flush();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (baos != null) {
                    baos.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (uiHandler != null) {
                Message msg = uiHandler.obtainMessage(0);
                msg.arg1 = responseCode;
                msg.obj = result.toString();
                uiHandler.sendMessage(msg);
            }
        }
    }
}
