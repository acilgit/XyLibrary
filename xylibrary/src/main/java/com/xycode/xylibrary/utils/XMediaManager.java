package com.xycode.xylibrary.utils;


import android.media.AudioManager;
import android.media.MediaPlayer;

/**
 * Created by XY on 2015/7/17.
 */
public class XMediaManager {
    private static MediaPlayer mediaPlayer;
    private static boolean isPause = true;

    public static MediaPlayer getInstance() {
        return mediaPlayer;
    }

    /**
     * @param filePath
     * @param onCompletionListener
     */
    public static void playSound(String filePath, MediaPlayer.OnCompletionListener onCompletionListener) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();

            mediaPlayer.setOnErrorListener((arg0, arg1, arg2) -> {
                mediaPlayer.reset();
                return false;
            });
        } else {
            mediaPlayer.reset();
        }

        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(onCompletionListener);
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            isPause = false;
            mediaPlayer.start();
        } catch (Exception e) {

        }
    }

    /**
     * pause
     */
    public static void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPause = true;
        }
    }

    /**
     * resume
     */
    public static void resume() {
        if (mediaPlayer != null && isPause) {
            mediaPlayer.start();
            isPause = false;
        }
    }

    /**
     * release
     */
    public static void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static boolean isPause() {
        return isPause;
    }

    public static void stopAndPlayAudio(String audioFilePath, MediaPlayer.OnCompletionListener completionListener) {
        XMediaManager.pause();
        XMediaManager.playSound(audioFilePath, completionListener);
    }
}