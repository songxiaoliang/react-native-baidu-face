/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform.network;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * 数据统计接口
 */
public class LogRequest extends BaseRequest {

    public static final String URL_GET_LOG = "http://face.baidu.com/openapi/v2/stat/sdkdata";

    public static void sendLogMessage(final String message) {
        if (message != null && message.length() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    httpUrlConnectionPost(message);
                }
            }).start();
        }
    }

    private static void httpUrlConnectionPost(String message) {
        StringBuffer result = new StringBuffer();
        HttpURLConnection urlConnection = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        try {
            JSONObject json = new JSONObject(message);
//            Log.e("log", json.toString());

            URL url = new URL(URL_GET_LOG);
            urlConnection = (HttpURLConnection) url.openConnection();
            System.setProperty("sun.net.client.defaultConnectTimeout", "8000");
            System.setProperty("sun.net.client.defaultReadTimeout", "8000");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setUseCaches(false);
            urlConnection.setInstanceFollowRedirects(true);
//            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("contentType", "application/json");
            urlConnection.connect();

            outputStream = urlConnection.getOutputStream();

//            Log.e("log", message);
            outputStream.write(json.toString().getBytes("utf-8"));
            outputStream.flush();
            outputStream.close();

            int responseCode = urlConnection.getResponseCode();
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
        }
    }
}
