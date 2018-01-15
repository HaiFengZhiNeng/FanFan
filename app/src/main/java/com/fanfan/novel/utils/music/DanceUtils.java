package com.fanfan.novel.utils.music;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.fanfan.novel.utils.MediaFile;

import java.io.File;
import java.io.IOException;

/**
 * Created by android on 2018/1/11.
 */

public class DanceUtils {

    private static DanceUtils mDanceManager;

    private MediaPlayer mediaPlayer;

    private AudioManager mAudioManager;

    public static synchronized DanceUtils getInstance() {
        if (mDanceManager == null) {
            synchronized (DanceUtils.class) {
                if (mDanceManager == null) {
                    mDanceManager = new DanceUtils();
                }
            }
        }
        return mDanceManager;
    }


    private boolean initMedia(Context context, String path) {
        //把音乐音量强制设置为最大音量
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前音乐音量
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);// 获取最大声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0); // 设置为最大声音，可通过SeekBar更改音量大小
        if (path != null) {
            return loadMusicFile(path);
        }
        return loadMusic(context);
    }

    private boolean loadMusicFile(String path) {
        File file = new File(path);
        if (file.exists() && MediaFile.isAudioFileType(path)) {
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(path);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private boolean loadMusic(Context context) {
        try {
            AssetFileDescriptor fileDescriptor = context.getAssets().openFd("dance.mp3");

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void initMediaplayer() {
        try {
            mediaPlayer = new MediaPlayer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startDance(Context context, String path) {
        if (mediaPlayer == null) {
            initMediaplayer();
        } else {
            mediaPlayer.reset();
        }
        if (initMedia(context, path)) {

            if (!mediaPlayer.isPlaying()) {
                try {
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }
        }
    }

    public void stopDance() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    }

}
