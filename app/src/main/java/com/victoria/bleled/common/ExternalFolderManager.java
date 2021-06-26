package com.victoria.bleled.common;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ExternalFolderManager {
    /************************************************************
     *  Static
     ************************************************************/
    private final static String TAG = "ExternalFolderManager";
    public final static String ROOT_DIR = Environment.DIRECTORY_DOWNLOADS; // Environment.DIRECTORY_PICTURES

    /************************************************************
     *  Public
     ************************************************************/
    private Context context;
    private File externalFolder;

    public ExternalFolderManager(Context context, String rootFolder) {
        this.context = context;
        this.externalFolder = createFolderExternal(context, rootFolder);
    }

    public File saveFileToExternal(File originFile) {
        String fileName = originFile.getName();
        String outputFilePath = null;
        OutputStream outputStream = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // android 10이상일 경우
            try {
                outputStream = createFileExternal(context, externalFolder.getName(), fileName);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            File newFile = new File(externalFolder, fileName);
            try {
                outputStream = new FileOutputStream(newFile, true);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        try {
            InputStream ins = new FileInputStream(originFile);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = ins.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();

            outputFilePath = externalFolder.getAbsolutePath() + "/" + fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return new File(outputFilePath);
    }


    public Uri createBitmapUri(Bitmap bitmap) {
        try {
            Long tsLong = System.currentTimeMillis() / 1000;
            String ext = ".jpg";
            String newFileName = "temp_" + tsLong.toString() + ext;
            Uri newMediaUri = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                newMediaUri = saveBitmapToMediaStore(context, bitmap, Bitmap.CompressFormat.JPEG, ext, newFileName);
            } else {
                newMediaUri = saveBitmapToOldStorage(bitmap, newFileName);
            }

            return newMediaUri;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /************************************************************
     *  Helper
     ************************************************************/
    private String getRelativePath() {
        return ROOT_DIR + "/" + externalFolder.getName();
    }


    private File createFolderExternal(Context context, String dir) {
        File retFolder = null;
        File folder = Environment.getExternalStoragePublicDirectory(ROOT_DIR);
        folder = new File(folder, dir);
        boolean isCreated = folder.exists();

        if (isCreated == true) {
            return folder;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // android 10이상일 경우
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, ROOT_DIR + "/" + dir);
            Uri uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues);
            if (uri != null) {
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

    private OutputStream createFileExternal(Context context, String folder, String fileName) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/txt");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, ROOT_DIR + "/" + folder);

        try {
            final ContentResolver resolver = context.getContentResolver();
            Uri uri = getUriFromName(context, fileName);
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

    private Uri getUriFromName(Context context, String displayName) {
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

    private boolean isOSNougat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    private Uri getUriForAPI7(Context context, File file) {
        return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
    }

    private Uri getUriFromFile(Context context, File file) {
        if (isOSNougat()) {
            return getUriForAPI7(context, file);
        } else {
            return Uri.fromFile(file);
        }
    }


    private void saveBitmap(Bitmap bitmap, String path) {
        OutputStream stream = null;

        try {
            File file = new File(path);
            stream = new FileOutputStream(file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        try {
            stream.flush();
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Uri saveBitmapToOldStorage(Bitmap bitmap, String fileName) {
        File imagesDir = externalFolder;
        File image = new File(imagesDir, fileName);
        saveBitmap(bitmap, image.getAbsolutePath());
        Uri newMediaUri = getUriFromFile(context, image);

        return newMediaUri;
    }

    @Nullable
    private Uri saveBitmapToMediaStore(@NonNull final Context context, @NonNull final Bitmap bitmap,
                                       @NonNull final Bitmap.CompressFormat format, @NonNull final String mimeType,
                                       @NonNull final String displayName) throws IOException {

        final String relativeLocation = getRelativePath();

        final ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation);

        final ContentResolver resolver = context.getContentResolver();

        OutputStream stream = null;
        Uri uri = null;

        try {
            //final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            final Uri contentUri = MediaStore.Files.getContentUri("external");
            uri = resolver.insert(contentUri, contentValues);

            if (uri == null) {
                throw new IOException("Failed to create new MediaStore record.");
            }

            stream = resolver.openOutputStream(uri);

            if (stream == null) {
                throw new IOException("Failed to get output stream.");
            }

            if (bitmap.compress(format, 95, stream) == false) {
                throw new IOException("Failed to save bitmap.");
            }
        } catch (IOException e) {
            if (uri != null) {
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(uri, null, null);
            }

            throw e;
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return uri;
    }
}
