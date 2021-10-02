package com.victoria.bleled.util.feature.logger;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.orhanobut.logger.LogStrategy;

import java.io.FileOutputStream;
import java.io.IOException;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/*
 public void initLogger() {
        myDiskLogAdapter = new MyDiskLogAdapter(this);
        Logger.addLogAdapter(myDiskLogAdapter);
        if (true || Constants.IS_TEST) {
            Logger.d(CommonUtil.getCurrentDate());
        }
    }

    public void clearLogger() {
        Logger.clearLogAdapters();
    }
 */

public class MyDiskLogStrategy implements LogStrategy {

    @NonNull
    private final Handler handler;

    public MyDiskLogStrategy(@NonNull Handler handler) {
        this.handler = checkNotNull(handler);
    }

    @Override
    public void log(int level, @Nullable String tag, @NonNull String message) {
        checkNotNull(message);

        // do nothing on the calling thread, simply pass the tag/msg to the background thread
        handler.sendMessage(handler.obtainMessage(level, message));
    }

    static class WriteHandler extends Handler {

        @NonNull
        private final String filePath;

        WriteHandler(@NonNull Looper looper, @NonNull String file) {
            super(checkNotNull(looper));
            this.filePath = checkNotNull(file);
        }

        @SuppressWarnings("checkstyle:emptyblock")
        @Override
        public void handleMessage(@NonNull Message msg) {
            String content = (String) msg.obj;

            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(filePath, true);
                if (outputStream == null) {
                    return;
                }
                content += "\n";
                outputStream.write(content.getBytes());

                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                if (outputStream != null) {
                    try {
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e1) { /* fail silently */ }
                }
            }
        }
    }
}
