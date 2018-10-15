/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;
import android.util.SparseIntArray;

/**
 * 音效播放工具
 */
public final class SoundPlayer {

    private static final boolean DEBUG = false;

    private static final String TAG = SoundPlayer.class.getSimpleName();

    /**
     * SoundPool实例：播放较短的音乐效果时，应该使用SoundPool而不使用MediaPlayer
     */
    private SoundPool mSoundPool;
    /**
     * 已加载过的音效文件，缓存住其ID，避免重复加载
     */
    private SparseIntArray mSoundPoolCache;
    /**
     * SoundPool最大同时播放音乐效果的个数
     */
    public static final int MAX_STREAMS = 5;
    /**
     * Single instance
     */
    private static SoundPlayer sSoundPlayer;
    /**
     * 预估加载sound音频消费的时间
     */
    private static final long LOAD_SOUND_MILLIS = 100L;

    public static long playTime = 0l;

    /**
     * 工具类隐藏其构造方法
     */
    private SoundPlayer() {
        mSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        mSoundPoolCache = new SparseIntArray();
        playTime = 0l;
    }

    /**
     * 播放音效
     *
     * @param context Context
     * @param resId   放在raw文件下的资源的id
     */
    @SuppressLint("NewApi")
    public static void play(Context context, final int resId) {
        if (null == sSoundPlayer) {
            sSoundPlayer = new SoundPlayer();
        }

        int id = sSoundPlayer.mSoundPoolCache.get(resId);
        if (0 == id) {
            final int soundId = sSoundPlayer.mSoundPool.load(context, resId, 1);
            if (DEBUG) {
                Log.i(TAG, String.format("SoundPool.load(resId=%d): soundId=%d", resId, soundId));
            }
            sSoundPlayer.mSoundPoolCache.put(resId, soundId);
            if (APIUtils.hasFroyo()) {
                sSoundPlayer.mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                        if (DEBUG) {
                            Log.i(TAG, String.format("SoundPool.onLoadComplete(soundId=%d):sampleId=%d",
                                    soundId, sampleId));
                        }
                        if (0 == status && soundId == sampleId) {
                            try {
                                playTime = System.currentTimeMillis();
                                sSoundPlayer.mSoundPool.play(soundId, 1.0f, 1.0f, MAX_STREAMS, 0, 1.0f);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } else {
                try {
                    Thread.currentThread().join(LOAD_SOUND_MILLIS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                playTime = System.currentTimeMillis();
                sSoundPlayer.mSoundPool.play(soundId, 1.0f, 1.0f, MAX_STREAMS, 0, 1.0f);
            }
        } else {
            try {
                sSoundPlayer.mSoundPool.play(id, 1.0f, 1.0f, MAX_STREAMS, 0, 1.0f);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Release all loaded sound.
     */
    public static void release() {
        if (null != sSoundPlayer) {
            for (int i = 0, n = sSoundPlayer.mSoundPoolCache.size(); i < n; i++) {
                sSoundPlayer.mSoundPool.unload(sSoundPlayer.mSoundPoolCache.valueAt(i));
            }
            sSoundPlayer.mSoundPool.release();
            sSoundPlayer.mSoundPool = null;
            sSoundPlayer.mSoundPoolCache.clear();
            sSoundPlayer.mSoundPoolCache = null;

            sSoundPlayer = null;
        }
    }
}
