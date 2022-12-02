package com.victoria.bleled.util.feature;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import com.victoria.bleled.util.CommonUtil;

public class SoundPlayer {
    private Context context;
    private MediaPlayer bgPlayer = null;
    private MediaPlayer forgroundPlayer = null;
    private SoundPool soundPool = null;
    private int soundPoolID = 0;

    public SoundPlayer(Context context) {
        this.context = context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void playBGSound(String name) {
        if (name == null) {
            return;
        }

        String[] subs = name.split("\\.");
        String resName = name;
        if (subs.length > 0) {
            resName = subs[0];
        }

        int resId = CommonUtil.getResourceId(context, resName, "raw");
        if (resId <= 0) {
            return;
        }

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(false);
        stopBGSound();
        bgPlayer = MediaPlayer.create(context, resId);
        bgPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        bgPlayer.setVolume(0.3f, 0.3f);
        bgPlayer.setLooping(true);

        bgPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //stopUrl();
            }
        });

        try {
            bgPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        bgPlayer.start();
    }

    public void stopBGSound() {
        if (bgPlayer == null) {
            return;
        }

        bgPlayer.stop();
        bgPlayer.release();
        bgPlayer = null;
    }

    public void pauseBGSound() {
        if (bgPlayer == null) {
            return;
        }
        bgPlayer.pause();
    }

    public void resumeBGSound() {
        if (bgPlayer == null) {
            return;
        }

        bgPlayer.start();
    }

    public boolean hasBGSound() {
        if (bgPlayer == null) {
            return false;
        }

        return true;
    }

    public boolean isPlayingBGSound() {
        if (bgPlayer == null) {
            return false;
        }
        return bgPlayer.isPlaying();
    }

    public void playFGSound(String name) {
        playFGSound(name, null);
    }

    public void playFGSound(String name, MediaPlayer.OnCompletionListener completionListener) {
        if (name == null) {
            return;
        }

        String[] subs = name.split("\\.");
        String resName = name;
        if (subs.length > 0) {
            resName = subs[0];
        }
        resName += "a";
        int resId = CommonUtil.getResourceId(context, resName, "raw");
        if (resId <= 0) {
            return;
        }

        //Initialize MediaPlayer
        stopFGSound();
        try {
            final AssetFileDescriptor afd = context.getResources().openRawResourceFd(resId);
            forgroundPlayer = new MediaPlayer();
            forgroundPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            if (completionListener != null) {
                forgroundPlayer.setOnCompletionListener(completionListener);
            }
            forgroundPlayer.prepare();
            forgroundPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopFGSound() {
        if (forgroundPlayer == null) {
            return;
        }

        forgroundPlayer.stop();
        forgroundPlayer.release();
        forgroundPlayer = null;
    }

    public void pauseFGSound() {
        if (forgroundPlayer == null) {
            return;
        }
        forgroundPlayer.pause();
    }

    public void resumeFGSound() {
        if (forgroundPlayer == null) {
            return;
        }

        forgroundPlayer.start();
    }

    public MediaPlayer getForegroundPlayer() {
        return forgroundPlayer;
    }

    public void playSoundEffect(String name) {
        if (name == null) {
            return;
        }

        String[] subs = name.split("\\.");
        String resName = name;
        if (subs.length > 0) {
            resName = subs[0];
        }

        int resId = CommonUtil.getResourceId(context, resName, "raw");
        if (resId <= 0) {
            return;
        }

        if (soundPoolID != 0) {
            soundPool.play(soundPoolID, 1f, 1f, 0, 0, 1f);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                soundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(8).build();
            } else {
                soundPool = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
            }
            soundPool.load(context, resId, 1);
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    soundPoolID = sampleId;
                    soundPool.play(sampleId, 1f, 1f, 0, 0, 1f);
                }
            });
        }
    }

    public boolean isPlayingFGSound() {
        if (forgroundPlayer == null) {
            return false;
        }
        return forgroundPlayer.isPlaying();
    }
}
