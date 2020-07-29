package com.victoria.bleled.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.core.content.FileProvider;

import com.theartofdev.edmodo.cropper.CropImage;
import com.victoria.bleled.BuildConfig;
import com.victoria.bleled.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static java.lang.StrictMath.max;

public class MediaManager {
    /************************************************************
     *  Variables
     ************************************************************/

    private final String TAG = "MediaManager";

    public final static int FAILED_BY_CRASH = 3000;
    public final static int FAILED_BY_SIZE_LIMIT = 3001;
    public final static int FAILED_BY_PERMISSON = 3002;
    public final static int FAILED_BY_NOIMAGE = 3003;

    private final int MAX_RESOLUTION = 400;
    private final int MAX_IMAGE_SIZE = 5 * 1024;

    private Activity mActivity = null;
    private MediaCallback mCallback = null;

    private static Uri mOriginUri = null;
    private static Uri mLastUri = null;

    public final static int SET_GALLERY = 1;
    public final static int SET_CAMERA = 2;
    public final static int SET_CAMERA_VIDEO = 3;
    public static int CROP_IMAGE = 4;

    private boolean mSquareCropRatio = true;
    private boolean mUseOtherCrop = false;

    /************************************************************
     *  Public
     ************************************************************/

    public interface MediaCallback {
        void onSelected(Boolean isVideo, Uri uri, Bitmap bitmap, String videoPath, String thumbPath);

        void onFailed(int code, String err);

        void onDelete();
    }

    public MediaManager(Activity activity) {
        mActivity = activity;
        mUseOtherCrop = false;
    }

    public MediaManager(Activity activity, boolean useOtherCrop) {
        mActivity = activity;
        mUseOtherCrop = useOtherCrop;
        CROP_IMAGE = CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;
    }

    public void setMediaCallback(MediaCallback cb) {
        mCallback = cb;
    }

    public void setCropRatio(boolean is_square) {
        mSquareCropRatio = is_square;
    }

    public void showMediaManager(final String imageName, final Bitmap bitmap, final boolean forDelete) {
        try {
            String[] items;
            ArrayAdapter<String> adapter;
            AlertDialog.Builder builder;
            AlertDialog dialog;

            String title = mActivity.getResources().getString(R.string.photo);

            String option1 = mActivity.getResources().getString(R.string.gallery);
            String option2 = mActivity.getResources().getString(R.string.camera);
            String option3 = mActivity.getResources().getString(R.string.delete);

            if ((imageName.length() > 0 || bitmap != null) && forDelete) {
                items = new String[]{option1, option2, option3};
            } else {
                items = new String[]{option1, option2};
            }
            adapter = new ArrayAdapter<>(mActivity, android.R.layout.select_dialog_item, items);
            builder = new AlertDialog.Builder(mActivity);

            builder.setTitle(title);
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0) { // gallery
                        getMediaFromGallery();
                    } else if (item == 1) { // camera
                        getImageFromCamera();
                    } else { // delete
                        // TODO: delete bitmap.
                        mCallback.onDelete();
                    }
                    dialog.cancel();
                }
            });

            dialog = builder.create();
            dialog.show();

        } catch (final Exception ex) {
            Log.d(TAG, ex.toString());
        }
    }

    public void getMediaFromGallery() {
        getMediaFromGallery(false);
    }

    public void getMediaFromGallery(boolean video) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE); // | MediaStore.Video.Media.CONTENT_TYPE
        if (video) {
            intent.setType("image/* video/*");
        } else {
            intent.setType("image/*");
        }
        mActivity.startActivityForResult(intent, SET_GALLERY);
    }

    public void getImageFromCamera() {
        File file = createFile(false);
        mOriginUri = getUri(mActivity, file);

        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mOriginUri);
            mActivity.startActivityForResult(intent, SET_CAMERA);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getVideoFromCamera() {
        File file = createFile(true);
        mOriginUri = getUri(mActivity, file);

        try {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mOriginUri);
            mActivity.startActivityForResult(intent, SET_CAMERA_VIDEO);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == SET_GALLERY) {
            mOriginUri = data.getData();
            String fileType = getFileTypeFromGallary(mOriginUri);
            if (fileType.startsWith("image") == true) {
                Uri uri = resizeAndRotate(mOriginUri);

                if (uri == null) {
                    mCallback.onFailed(FAILED_BY_NOIMAGE, mOriginUri.getPath());
                    return;
                }

                CropImage(uri);
            } else if (fileType.startsWith("video") == true) {
                String path = getFile(mOriginUri).getPath();
                String thumb = getThumbnail(path);

                if (mCallback != null) {
                    mCallback.onSelected(true, null, null, path, thumb);
                }
            }
        } else if (requestCode == SET_CAMERA) { // 카메라로 사진을 캡쳐한 경우.
            Uri uri = resizeAndRotate(mOriginUri);

            if (uri == null) {
                mCallback.onFailed(FAILED_BY_NOIMAGE, mOriginUri.getPath());
                return;
            }

            CropImage(uri);
        } else if (requestCode == SET_CAMERA_VIDEO) { // 카메라로 동영상을 캡쳐한 경우.
            String path = getFile(mOriginUri).getPath();
            String thumb = getThumbnail(path);

            if (mCallback != null) {
                mCallback.onSelected(true, null, null, path, thumb);
            }
        } else if (requestCode == CROP_IMAGE) {

            if (mUseOtherCrop == true) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    mLastUri = result.getUri();
                } else {
                    Exception error = result.getError();
                    mCallback.onFailed(FAILED_BY_CRASH, error.getMessage());
                    return;
                }
            }


            // 카메라로 저장한 이미지는 캐시된이미지이므로 파일로 확실하게 저장해야 한다.
            Bitmap bitmap = resizeBitmap(mLastUri, 1.0);
            File newFile = null;
            try {
                if (checkHighSDK()) {
                    newFile = createFile(false);
                } else {
                    newFile = new File(mLastUri.getPath());
                    newFile.createNewFile();
                }

                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(newFile));
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                int size = Integer.parseInt(String.valueOf(newFile.length() / 1024));
                if (size > MAX_IMAGE_SIZE) {
                    mCallback.onFailed(FAILED_BY_SIZE_LIMIT, mActivity.getResources().getString(R.string.photo_max_size));
                    if (newFile != null && newFile.exists()) {
                        newFile.delete();
                    }
                }

                stream.flush();
                stream.close();

                Uri fileUri = Uri.fromFile(newFile);

                // 갤러리에 남겨보자
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = fileUri;
                mediaScanIntent.setData(contentUri);
                mActivity.sendBroadcast(mediaScanIntent);

                if (mCallback != null)
                    mCallback.onSelected(false, fileUri, bitmap, "", "");
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Sorry, Camera Crashed in createNewFile");
                mCallback.onFailed(FAILED_BY_CRASH, e.toString());
                if (newFile != null && newFile.exists()) {
                    newFile.delete();
                }
            }
        }
    }

    /************************************************************
     *  Helper
     ************************************************************/
    private Uri resizeAndRotate(Uri uri) {
        // 갤러리인 경우에도 이미지를 줄이고 rotate를 해주어야 crop intent가 잘 돌아가므로
        BitmapFactory.Options options = getBitmapFactory(uri);
        if (options.outWidth == -1 || options.outHeight == -1) {
            return null;
        }

        double ratio = getRatio(options);
        Bitmap bitmap = resizeBitmap(uri, ratio);
        String path = getMediaPathFromURI(mActivity, uri);
        File file = getFile(uri);
        if (path == null || path.isEmpty() == true) {
            bitmap = checkRotate(bitmap, file.getAbsolutePath());
        } else {
            bitmap = checkRotate(bitmap, path);
        }
        saveBitmap(bitmap, file.getAbsolutePath());

        if (checkHighSDK()) {
            uri = getUriFromFile(mActivity, file);
        }

        return uri;
    }

    private void CropImage(Uri uri) {
        if (mUseOtherCrop == true) {
            if (mSquareCropRatio == true) {
                CropImage.activity(uri).setAspectRatio(1, 1).start(mActivity);
            } else {
                CropImage.activity(uri).start(mActivity);
            }
        } else {
            // android 7.0 gallaxy s7 crop activity is not supported of "not contained file type in name"
            File file;
            if (checkHighSDK()) {
                file = getFile(uri);

                if (file.getAbsolutePath().indexOf(".png") == -1) {
                    File newFile = new File(file.getAbsolutePath() + ".png");
                    file.renameTo(newFile);
                    uri = getUriFromFile(mActivity, newFile);
                }
            }

            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");

            List<ResolveInfo> list = mActivity.getPackageManager().queryIntentActivities(intent, 0);
            if (checkHighSDK()) {
                mActivity.grantUriPermission(list.get(0).activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            int size = list.size();
            if (size == 0) {
                mCallback.onFailed(FAILED_BY_PERMISSON, "permission error");
                return;
            } else {
                if (checkHighSDK()) {
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                if (mSquareCropRatio == true) {
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("scale", true);
                }

                file = createFile(false);
                mLastUri = getUri(mActivity, file);

                intent.putExtra("return-data", false);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mLastUri);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());

                Intent newIntent = new Intent(intent);
                ResolveInfo res = list.get(0);

                if (checkHighSDK()) {
                    newIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    newIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    mActivity.grantUriPermission(res.activityInfo.packageName, mLastUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                newIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                mActivity.startActivityForResult(newIntent, CROP_IMAGE);
            }
        }
    }


    private File createFile(boolean isVideo) {
        File folder = new File(getFolderPath());
        if (!folder.exists())
            folder.mkdirs();

        Long tsLong = System.currentTimeMillis() / 1000;
        String ext = isVideo ? ".mp4" : ".png";
        String filename = tsLong.toString() + ext;
        return new File(folder.toString(), filename);
    }

    private File createFileType(String type) {
        File folder = new File(getFolderPath());
        if (!folder.exists())
            folder.mkdirs();

        Long tsLong = System.currentTimeMillis() / 1000;
        String ext = "." + type;
        String filename = tsLong.toString() + ext;
        return new File(folder.toString(), filename);
    }

    private String getFolderPath() {
        File file = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return file.getAbsolutePath();
    }

    private Uri getUri(Context context, File file) {
        if (checkHighSDK()) {
            return getUriFromFile(context, file);
        } else {
            return Uri.fromFile(file);
        }
    }

    private Uri getUriFromFile(Context context, File file) {
        Log.d("asd", "file:" + file);
        Log.d("asd", "BuildConfig.APPLICATION_ID:" + BuildConfig.APPLICATION_ID);
        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
    }

    private boolean checkHighSDK() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    private String getThumbnail(String videoFile) {
        File thumbFile = createFile(false);

        Bitmap bmp = ThumbnailUtils.createVideoThumbnail(videoFile, MediaStore.Images.Thumbnails.MINI_KIND);
        saveBitmap(bmp, thumbFile.getPath());

        return thumbFile.getPath();
    }

    private String getFileTypeFromGallary(Uri uri) {
        String[] columns = {MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.MIME_TYPE};

        Cursor cursor = mActivity.getContentResolver().query(uri, columns, null, null, null);
        cursor.moveToFirst();
        int mimeTypeColumnIndex = cursor.getColumnIndex(columns[1]);
        String mimeType = cursor.getString(mimeTypeColumnIndex);
        cursor.close();

        return mimeType;
    }

    private BitmapFactory.Options getBitmapFactory(Uri uri) {
        InputStream input = null;

        try {
            input = mActivity.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inDither = true; // optional
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional

        BitmapFactory.decodeStream(input, null, options);

        try {
            input.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return options;
    }

    private double getRatio(BitmapFactory.Options options) {
        int size = max(options.outWidth, options.outHeight);
        return max(size / MAX_RESOLUTION, 1);
    }

    private Bitmap resizeBitmap(Uri uri, double ratio) {
        InputStream input = null;

        try {
            input = mActivity.getContentResolver().openInputStream(uri);

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        options.inDither = true;// optional
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional

        Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);

        try {
            input.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0)
            return 1;
        else
            return k;
    }

    private File getFile(Uri url) {
        File folder = new File(getFolderPath());
        return new File(folder, new File(url.getPath()).getName());
    }

    private Bitmap checkRotate(Bitmap bitmap, String filename) {
        int orientation = -1;

        try {
            ExifInterface ei = new ExifInterface(filename);
            orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        } catch (IOException e) {
            e.printStackTrace();
        }

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                bitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                bitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                bitmap = rotateImage(bitmap, 270);
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                bitmap = flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                bitmap = flip(bitmap, false, true);
        }
        return bitmap;
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
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

    public String saveBitmapToGallery(String ext, Bitmap bitmap) {
        File thumbFile = createFileType(ext);

        saveBitmap(bitmap, thumbFile.getPath());

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(thumbFile);
        mediaScanIntent.setData(contentUri);
        mActivity.sendBroadcast(mediaScanIntent);

        return thumbFile.getPath();
    }

    private void copyStream(InputStream in, OutputStream out) throws Exception {
        try {
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    public String saveFileToGallery(String ext, File file) {
        File thumbFile = createFileType(ext);

        try {
            InputStream in = new FileInputStream(file);
            OutputStream out = new FileOutputStream(thumbFile);
            copyStream(in, out);

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(thumbFile);
            mediaScanIntent.setData(contentUri);
            mActivity.sendBroadcast(mediaScanIntent);
        } catch (Exception e) {
        }

        return thumbFile.getPath();
    }

    public String saveStreamToGallery(String ext, InputStream inputStream) {
        File file = createFileType(ext);

        InputStream in = inputStream;
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            copyStream(in, out);

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            mActivity.sendBroadcast(mediaScanIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }

    private String getMediaPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Log.e(TAG, "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    /************************************************************
     *  Static Functions
     ************************************************************/
}

