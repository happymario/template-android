package com.victoria.bleled.common;

/*****************************************************************************
 *  Camera와 Gallery에서 얻은 이미지를 External Temp File을 만들어 조작을 진행한다.
 *  결과값은 이 tempFile을  내보낸다
 ***************************************************************************/

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
import android.os.CancellationSignal;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.Size;
import android.widget.ArrayAdapter;

import androidx.core.content.FileProvider;

import com.theartofdev.edmodo.cropper.CropImage;
import com.victoria.bleled.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static java.lang.StrictMath.max;

public class MediaManager {
    public interface MediaCallback {
        void onImage(Uri uri, Bitmap bitmap);

        void onVideo(Uri video, Uri thumb, Bitmap thumbBitmap);

        void onFailed(int code, String err);

        void onDelete();
    }

    // content External Uri는 이렇게 내부File로 변환하여 업로드할때 리용한다.
    public static File createTempInInternal(Context context, Uri externalUri) {
        String filename = getFileName(context, externalUri);
        File destinationFilename = new File(context.getFilesDir().getPath() + File.separatorChar + filename);
        try (InputStream ins = context.getContentResolver().openInputStream(externalUri)) {
            createFileFromStream(ins, destinationFilename);
        } catch (Exception ex) {
            Log.e("Save File", ex.getMessage());
            ex.printStackTrace();
        }
        return destinationFilename;
    }


    private static void createFileFromStream(InputStream ins, File destination) {
        try (OutputStream os = new FileOutputStream(destination)) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = ins.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
        } catch (Exception ex) {
            Log.e("Save File", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static String getFileName(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Cursor returnCursor =
                    context.getContentResolver().query(uri, null, null, null, null);
            assert returnCursor != null;
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String name = returnCursor.getString(nameIndex);
            returnCursor.close();
            return name;
        } else {
            return new File(uri.getPath()).getName();
        }
    }

    /************************************************************
     *  Static
     ************************************************************/
    public final static int FAILED_BY_LIB = 40000;
    public final static int FAILED_BY_SIZE_LIMIT = 40001;
    public final static int FAILED_BY_PERMISSION = 40002;
    public final static int FAILED_BY_NO_IMAGE = 40003;
    public final static int FAILED_BY_APPS = 40004;

    public final static int REQ_SET_GALLERY = 1;
    public final static int REQ_SET_CAMERA = 2;
    public final static int REQ_SET_CAMERA_VIDEO = 3;
    public static int REQ_CROP_IMAGE = 4;

    private final static int MAX_RESOLUTION = 400;
    private final static int MAX_IMAGE_SIZE = 5 * 1024;

    private final static String TAG = "MediaManager";

    /************************************************************
     *  Variables
     ************************************************************/
    private Activity mActivity = null;

    private boolean mCropFreeRatio = true;
    private boolean mCropByOS = true;
    private boolean mCropEnable = true;

    private Uri mCameraUri = null;
    private Uri mGalleryUri = null;

    private Uri mCropInputUri = null;
    private Uri mCropOutputUri = null;

    private Uri mLastUri = null;

    private MediaCallback mCallback = null;

    /************************************************************
     *  Public
     ************************************************************/
    public MediaManager(Activity activity) {
        mActivity = activity;
        mCropByOS = true;
    }

    public MediaManager(Activity activity, boolean useOtherCrop) {
        mActivity = activity;
        mCropByOS = !useOtherCrop;

        if (useOtherCrop == true) {
            REQ_CROP_IMAGE = CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;
        }
    }

    public void setMediaCallback(MediaCallback cb) {
        mCallback = cb;
    }


    public void setCropFreeRatio(boolean cropFreeRatio) {
        this.mCropFreeRatio = cropFreeRatio;
    }


    public void setCropEnable(boolean cropEnable) {
        this.mCropEnable = cropEnable;
    }

    public File getFileFromUri(Uri uri) {
        return getWorkFile(uri);
    }

    public void openGallery() {
        openGallery(false);
    }

    public void openGallery(boolean video) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE); // | MediaStore.Video.Media.CONTENT_TYPE
        if (video) {
            intent.setType("image/* video/*");
        } else {
            intent.setType("image/*");
        }

        mActivity.startActivityForResult(intent, REQ_SET_GALLERY);
    }

    public void openCamera() {
        openCamera(false);
    }

    public void openCamera(boolean video) {
        File file = createNewFile(video);
        mCameraUri = getUriFromFile(mActivity, file);

        try {
            if (video) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraUri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mActivity.startActivityForResult(intent, REQ_SET_CAMERA_VIDEO);
            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraUri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mActivity.startActivityForResult(intent, REQ_SET_CAMERA);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showSelectPopup(boolean forDelete) {
        try {
            String[] items;
            ArrayAdapter<String> adapter;
            AlertDialog.Builder builder;
            AlertDialog dialog;

            String title = mActivity.getResources().getString(R.string.photo);

            String option1 = mActivity.getResources().getString(R.string.gallery);
            String option2 = mActivity.getResources().getString(R.string.camera);
            String option3 = mActivity.getResources().getString(R.string.delete);

            if (forDelete) {
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
                        openGallery();
                    } else if (item == 1) { // camera
                        openCamera();
                    } else { // delete
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!(requestCode >= REQ_SET_GALLERY && requestCode <= REQ_SET_CAMERA_VIDEO) && requestCode != REQ_CROP_IMAGE) {
            return;
        }

        if (resultCode != RESULT_OK) {
            mCallback.onFailed(FAILED_BY_APPS, String.valueOf(requestCode));
            return;
        }

        if (requestCode == REQ_SET_GALLERY) {
            mGalleryUri = data.getData();

            String fileType = getUriType(mActivity, mGalleryUri);
            if (fileType.startsWith("image")) {
                Uri uri = createNewResizeAndRotate(mGalleryUri);
                if (uri == null) {
                    if (mCallback != null) {
                        mCallback.onFailed(FAILED_BY_NO_IMAGE, String.valueOf(REQ_SET_GALLERY));
                    }
                    return;
                }

                if (mCropEnable) {
                    cropImage(uri);
                } else {
                    setImageLastResult(uri);
                }
            } else if (fileType.startsWith("video")) {
                Bitmap thumb = getThumbBitmap(mGalleryUri);
                Uri thumbUri = createFileUri(thumb);

                if (mCallback != null) {
                    mCallback.onVideo(mGalleryUri, thumbUri, thumb);
                }
            } else {
                if (mCallback != null) {
                    mCallback.onFailed(FAILED_BY_NO_IMAGE, String.valueOf(REQ_SET_GALLERY));
                }
            }
        } else if (requestCode == REQ_SET_CAMERA) { // 카메라로 사진을 캡쳐한 경우.
            Uri uri = createNewResizeAndRotate(mCameraUri);
            if (uri == null) {
                if (mCallback != null) {
                    mCallback.onFailed(FAILED_BY_NO_IMAGE, String.valueOf(REQ_SET_CAMERA));
                }
                return;
            }

            if (mCropEnable) {
                cropImage(uri);
            } else {
                setImageLastResult(uri);
            }
        } else if (requestCode == REQ_SET_CAMERA_VIDEO) { // 카메라로 동영상을 캡쳐한 경우.
            Bitmap thumb = getThumbBitmap(mCameraUri);
            Uri thumbUri = createFileUri(thumb);

            if (mCallback != null) {
                mCallback.onVideo(mCameraUri, thumbUri, thumb);
            }
        } else if (requestCode == REQ_CROP_IMAGE) {
            Uri cropOutputUri = null;

            if (!mCropByOS) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (result == null || result.getUri() == null) {
                    //Exception error = result.getError();
                    mCallback.onFailed(FAILED_BY_APPS, String.valueOf(REQ_CROP_IMAGE));
                    return;
                }
                cropOutputUri = result.getUri();
            } else {
                cropOutputUri = mCropOutputUri;
            }

            Uri resultUri = createNewResizeAndRotate(cropOutputUri);
            setImageLastResult(resultUri);
        }
    }

    /************************************************************
     *  Helper
     ************************************************************/
    private String getUriType(Context context, Uri uri) {
        String[] columns = {MediaStore.Images.Media._ID, MediaStore.Images.Media.MIME_TYPE};

        Cursor cursor = context.getContentResolver().query(uri, columns, null, null, null);
        cursor.moveToFirst();
        int mimeTypeColumnIndex = cursor.getColumnIndex(columns[1]);
        String mimeType = cursor.getString(mimeTypeColumnIndex);
        cursor.close();

        return mimeType;
    }

    //
    // ExternalFolder is "Pictures"
    //
    private File getWorkFolder() {
        return mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    private File getWorkFile(Uri uri) {
        File folder = getWorkFolder();

        File tempFile = new File(uri.getPath());
        File file = new File(folder.getAbsolutePath() + File.separatorChar + tempFile.getName());
        return file;
    }

    private Uri createFileUri(Bitmap bitmap) {
        File file = createNewFile(false);
        saveBitmap(bitmap, file.getAbsolutePath());
        Uri uri = getUriFromFile(mActivity, file);
        return uri;
    }

    private File createNewFile(boolean isVideo) {
        File folder = getWorkFolder();

        if (!folder.exists())
            folder.mkdirs();

        Long tsLong = System.currentTimeMillis() / 1000;
        String ext = isVideo ? ".mp4" : ".png";
        String filename = "temp_" + tsLong.toString() + ext;
        File newFile = new File(folder.toString(), filename);
        try {
            newFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newFile;
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

    //
    // Camera와 Gallery에서 얻은 사진을 축소 및 회전하여 새 파일을 돌려준다.
    //
    private Uri createNewResizeAndRotate(Uri externalUri) {
        BitmapFactory.Options options = getBitmapFactory(externalUri);
        if (options.outWidth == -1 || options.outHeight == -1) {
            return null;
        }

        // resize and rotate
        double ratio = getRatio(options);
        Bitmap bitmap = resizeBitmap(mActivity, externalUri, ratio);
        bitmap = rotateWithCheck(externalUri, bitmap);

        // 새 ExternalFile에 Bitmap쓰기
        Uri retUri = createFileUri(bitmap);
        return retUri;
    }


    private BitmapFactory.Options getBitmapFactory(Uri uri) {
        InputStream input = null;
        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            input = mActivity.getContentResolver().openInputStream(uri);

            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional

            BitmapFactory.decodeStream(input, null, options);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return options;
    }

    private double getRatio(BitmapFactory.Options options) {
        int size = max(options.outWidth, options.outHeight);
        return max(size / MAX_RESOLUTION, 1);
    }

    private Bitmap rotateWithCheck(Uri fileUri, Bitmap bitmap) {
        int orientation = -1;
        ExifInterface ei = null;
        try {
            if (isOSNougat()) {
                InputStream inputStream = mActivity.getContentResolver().openInputStream(fileUri);
                ei = new ExifInterface(inputStream);
            } else {
                File f = new File(fileUri.getPath());
                ei = new ExifInterface(f.getAbsolutePath());
            }
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

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0)
            return 1;
        else
            return k;
    }

    private static Bitmap resizeBitmap(Context context, Uri uri, double ratio) {
        InputStream input = null;

        try {
            input = context.getContentResolver().openInputStream(uri);

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional

        Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);

        try {
            input.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
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


    private Bitmap getThumbBitmap(Uri videoUri) {
        Bitmap bmp = null;
        try {
            File f = createTempInInternal(mActivity, videoUri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                bmp = ThumbnailUtils.createVideoThumbnail(f, new Size(MAX_RESOLUTION, MAX_RESOLUTION), new CancellationSignal());
            } else {
                bmp = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
            }
            return bmp;
        } catch (Exception e) {

        }
        return null;
    }

    private void cropImage(Uri externalUri) {
        mCropInputUri = externalUri;

        if (!mCropByOS) {
            if (mCropFreeRatio) {
                CropImage.activity(externalUri).start(mActivity);
            } else {
                CropImage.activity(externalUri).setAspectRatio(1, 1).start(mActivity);
            }
        } else {
            // android 7.0 gallaxy s7 crop activity is not supported of "not contained file type in name"
            File file;
            if (isOSNougat()) {
                file = getWorkFile(externalUri);

                // rename file and change uri
                if (file.getAbsolutePath().indexOf(".png") == -1) {
                    File newFile = new File(file.getAbsolutePath() + ".png");
                    file.renameTo(newFile);
                    externalUri = getUriFromFile(mActivity, newFile);
                    mCropInputUri = externalUri;
                }
            }

            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(externalUri, "image/*");

            List<ResolveInfo> list = mActivity.getPackageManager().queryIntentActivities(intent, 0);
            if (isOSNougat()) {
                mActivity.grantUriPermission(list.get(0).activityInfo.packageName, externalUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            int size = list.size();
            if (size == 0) {
                mCallback.onFailed(FAILED_BY_PERMISSION, String.valueOf(REQ_CROP_IMAGE));
                return;
            } else {
                if (isOSNougat()) {
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                if (!mCropFreeRatio) {
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("scale", true);
                }

                File outputFile = createNewFile(false);
                mCropOutputUri = getUriFromFile(mActivity, outputFile);

                intent.putExtra("return-data", false);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropOutputUri);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());

                Intent newIntent = new Intent(intent);
                ResolveInfo res = list.get(0);

                if (isOSNougat()) {
                    newIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    newIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    mActivity.grantUriPermission(res.activityInfo.packageName, mCropOutputUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                newIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                mActivity.startActivityForResult(newIntent, REQ_CROP_IMAGE);
            }
        }
    }

    private void setImageLastResult(Uri uri) {
        try {
            File file = getWorkFile(uri);
            int size = Integer.parseInt(String.valueOf(file.length() / 1024));
            if (size > MAX_IMAGE_SIZE) {
                deleteTempFiles();
                mCallback.onFailed(FAILED_BY_SIZE_LIMIT, mActivity.getResources().getString(R.string.photo_max_size));
                return;
            }

            Bitmap bitmap = resizeBitmap(mActivity, uri, 1.0);
            exportToGallery(file.getAbsolutePath());
            mLastUri = uri;
            if (mCallback != null) {
                deleteTempFiles();
                mCallback.onImage(uri, bitmap);
            }

        } catch (Exception e) {
            e.printStackTrace();

            if (mCallback != null) {
                deleteTempFiles();
                mCallback.onFailed(FAILED_BY_LIB, e.toString());
            }
        }
    }

    private void exportToGallery(String filePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(filePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mActivity.sendBroadcast(mediaScanIntent);
    }

    private void deleteTempFiles() {
        if (mCameraUri != null) {
            File file = getWorkFile(mCameraUri);
            file.delete();
        }

        if (mCropInputUri != null) {
            File file = getWorkFile(mCropInputUri);
            file.delete();
        }

        if (mCropOutputUri != null) {
            File file = getWorkFile(mCropOutputUri);
            file.delete();
        }
    }
}

