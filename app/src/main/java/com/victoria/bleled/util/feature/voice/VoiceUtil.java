package com.victoria.bleled.util.feature.voice;

import android.app.Activity;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by Crazy on 5/11/2018.
 */

public class VoiceUtil {
    private static VoiceUtil singleton;

    private MediaPlayer m_mediaPlayer;

    private Boolean m_nowPlaying = false;

    public static VoiceUtil getInstance() {
        if (singleton == null) {
            singleton = new VoiceUtil();
        }

        return singleton;
    }

    public void startPlaying(String url, OnLoadSuccess onLoadSuccess, OnLoadFailed onLoadFailed) throws IllegalArgumentException, SecurityException, IllegalStateException {
        if (m_nowPlaying)
            stopPlaying();

//        if (m_mediaPlayer == null) {
        m_mediaPlayer = new MediaPlayer();
//        }

        if (url.isEmpty())
            if (onLoadFailed != null) {
                onLoadFailed.onLoadFailed();
                return;
            }

        m_nowPlaying = true;

        try {
            m_mediaPlayer.setDataSource(url);
            m_mediaPlayer.prepare();
            m_mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    mp.reset();
                }
            });
            m_mediaPlayer.start();

            if (onLoadSuccess != null)
                onLoadSuccess.onLoadSuccess();

        } catch (IOException e) {
            e.printStackTrace();

            if (onLoadFailed != null) {
                onLoadFailed.onLoadFailed();
            }
        }
    }

    public Boolean isNowPlaying() {
        return m_nowPlaying;
    }

    public void startPlaying(String url, OnLoadFailed onLoadFailed) {
        startPlaying(url, null, onLoadFailed);
    }

    public void startPlaying(Activity activity, String url) {
        startPlaying(url, null, null);
    }

    public int getDuration(String url) {
        if (url == null || url.isEmpty())
            return 0;

        m_mediaPlayer = new MediaPlayer();
        try {
            m_mediaPlayer.setDataSource(url);
            m_mediaPlayer.prepare();

            return m_mediaPlayer.getDuration() / 1000;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void stopPlaying() {
        if (m_mediaPlayer != null && m_nowPlaying) {
            m_nowPlaying = false;
            m_mediaPlayer.stop();
            m_mediaPlayer.release();
        }
    }

    public interface OnLoadSuccess {
        void onLoadSuccess();
    }

    public interface OnLoadFailed {
        void onLoadFailed();
    }

}
