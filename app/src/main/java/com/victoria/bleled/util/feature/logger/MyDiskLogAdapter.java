package com.victoria.bleled.util.feature.logger;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogAdapter;
import com.victoria.bleled.app.MyApplication;
import com.victoria.bleled.util.CommonUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MyDiskLogAdapter implements LogAdapter {
    public final static String ROOT_DIR = Environment.DIRECTORY_DOCUMENTS;
    public final static String FOLDER_NAME = "mofit";
    public final static String LOG_FILE_PREFIX = "logs_";

    private Context context = null;
    private FormatStrategy formatStrategy;
    private File folder = null;
    private String fileName = "";

    public MyDiskLogAdapter(Context context) {
        this.context = context;

        folder = createFolderInDocument(FOLDER_NAME);
        fileName = createFile();

        HandlerThread ht = new HandlerThread("AndroidFileLogger." + folder);
        ht.start();
        Handler handler = new MyDiskLogStrategy.WriteHandler(ht.getLooper(), folder.getAbsolutePath()+"/" + fileName);
        MyDiskLogStrategy logStrategy = new MyDiskLogStrategy(handler);

        formatStrategy = CsvFormatStrategy.newBuilder()
                .logStrategy(logStrategy)
                .build();
    }

    private File createFolderInDocument(String dir) {
        File retFolder = null;
        File folder = Environment.getExternalStoragePublicDirectory(ROOT_DIR);
        folder = new File(folder, dir);
        boolean isCreated = folder.exists();

        if(isCreated == true) {
            return folder;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // android 10이상일 경우
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, ROOT_DIR + "/" + dir);
            Uri uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues);
            if(uri != null) {
                String path = String.valueOf(uri);
                folder = new File(path);
                isCreated = folder.exists();
                if (!isCreated) {
                    folder.mkdirs();
                }
                retFolder = new File(Environment.getExternalStoragePublicDirectory(ROOT_DIR), dir);
            }
        } else {
            if (!isCreated) {
                folder.mkdirs();
            }
            retFolder = folder;
        }

        return retFolder;
    }

    private String createFile() {
        String fileNamePrefix = LOG_FILE_PREFIX + CommonUtil.getCurrentDate() + "_";
        int fileNo = 1;

        File[] list = folder.listFiles();
        if(list != null) {
            for (int i = 0; i < list.length; i++) {
                String filepath = list[i].getAbsoluteFile().getAbsolutePath();
                if (list[i].getAbsoluteFile().isFile() && filepath.endsWith(".txt") && filepath.contains(fileNamePrefix)) {
                    fileNo++;
                }
            }
        }

        String fileName = fileNamePrefix + fileNo + ".txt";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // android 10이상일 경우
            try {
                OutputStream outputStream = createNewFileInDocument(FOLDER_NAME, fileName);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            File newFile = new File(folder, fileName);
            try {
                FileOutputStream outputStream = new FileOutputStream(newFile, true);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return fileName;
    }

    private Uri getUriFromPath(String displayName) {
        long photoId;
        Uri photoUri = MediaStore.Files.getContentUri("external");
        try {
            String[] projection = {MediaStore.Images.ImageColumns._ID};
            final ContentResolver resolver = context.getContentResolver();
            Cursor cursor = resolver.query(photoUri, projection, MediaStore.Images.ImageColumns.DISPLAY_NAME + " LIKE ?", new String[]{displayName}, null);
            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(projection[0]);
            photoId = cursor.getLong(columnIndex);

            cursor.close();
        } catch (Exception e) {
            return null;
        }
        return Uri.parse(photoUri.toString() + "/" + photoId);
    }

    private OutputStream createNewFileInDocument(String folder, @NonNull String fileName) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/txt");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, ROOT_DIR + "/" + folder);

        try {
            final ContentResolver resolver = context.getContentResolver();
            Uri uri = getUriFromPath(fileName);
            if (uri == null) {
                uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues);
            }
            OutputStream outputStream = resolver.openOutputStream(uri, "wa");
            return outputStream;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clearData() {
        File[] list = folder.listFiles();
        for (int i = 0; i < list.length; i++) {
            String filepath = list[i].getAbsoluteFile().getAbsolutePath();
            if (list[i].getAbsoluteFile().isFile() && filepath.contains(".txt") && filepath.contains(LOG_FILE_PREFIX)) {
                if (list[i].delete()) {
                    Log.d("Deleted", "" + filepath);
                }
            }
        }
    }

    @Override
    public boolean isLoggable(int priority, @Nullable String tag) {
        return true;
    }

    @Override
    public void log(int priority, @Nullable String tag, @NonNull String message) {
        formatStrategy.log(priority, tag, message);
    }
}
