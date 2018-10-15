/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform.common;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.baidu.idl.face.platform.FaceEnvironment;
import com.baidu.idl.face.platform.FaceStatusEnum;
import com.baidu.idl.face.platform.utils.SoundPlayer;

import java.util.HashMap;

/**
 * 音频播放工具类
 */
public class SoundPoolHelper {

    private static final String TAG = SoundPoolHelper.class.getSimpleName();

    private Context mContext;
    private FaceStatusEnum mPlaySoundStatusEnum;
    private volatile long mPlayDuration = 0L;
    private volatile long mPlayTime = 0L;
    private volatile boolean mIsPlaying = false;
    private volatile boolean mIsEnableSound = true;
    private HashMap<Integer, Long> mPlayDurationMap = new HashMap<Integer, Long>();

    public SoundPoolHelper(Context context) {
        mContext = context;
    }

    public void setEnableSound(boolean flag) {
        mIsEnableSound = flag;
    }

    public boolean getEnableSound() {
        return mIsEnableSound;
    }

    public long getPlayDuration() {
        return mPlayDuration;
    }

    public boolean playSound(FaceStatusEnum status) {

        mIsPlaying = System.currentTimeMillis() - SoundPlayer.playTime < mPlayDuration;
        if (mIsPlaying
                || (mPlaySoundStatusEnum == status
                && System.currentTimeMillis() - mPlayTime < FaceEnvironment.TIME_TIPS_REPEAT)) {
//            Log.e(TAG, "ext no playSound " + status.name() + "-" + mIsPlaying + "-" + (System.currentTimeMillis() - mPlayTime));
            return false;
        }

        mIsPlaying = true;
        mPlaySoundStatusEnum = status;
        mPlayDuration = 0;
        mPlayTime = System.currentTimeMillis();

        int resId = FaceEnvironment.getSoundId(status);
        if (resId > 0) {
            mPlayDuration = getSoundDuration(resId);
            SoundPlayer.playTime = System.currentTimeMillis();
            if (mIsEnableSound) {
                SoundPlayer.play(mContext, resId);
            }
        }
        return mIsPlaying;
    }

    private long getSoundDuration(int rawId) {
        long duration = 600L;
        long durationStep = 0;
        if (mPlayDurationMap.containsKey(rawId)) {
            duration = mPlayDurationMap.get(rawId);
        } else {
            long time = System.currentTimeMillis();
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            try {
                Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + rawId);
                mmr.setDataSource(mContext, uri);
                String d = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                duration = Long.valueOf(d) + durationStep;
                mPlayDurationMap.put(rawId, duration);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            Log.e(TAG, "Retriever raw load time " + (System.currentTimeMillis() - time) + "-" + duration);
        }
        return duration;
    }

    public void release() {
        SoundPlayer.release();
        mContext = null;
    }
}
