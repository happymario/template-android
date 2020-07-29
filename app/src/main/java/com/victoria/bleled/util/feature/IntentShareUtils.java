package com.victoria.bleled.util.feature;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.view.View;

import androidx.core.content.FileProvider;

import com.victoria.bleled.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class IntentShareUtils {
    public static boolean shareInstagram(Context context, String str) {
        Parcelable c;
        if (Build.VERSION.SDK_INT >= 23) {
            c = getUri(context, str);
        } else {
            c = getUri(str);
        }
        if (context.getPackageManager().getLaunchIntentForPackage("com.instagram.android") != null) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            intent.setPackage("com.instagram.android");
            intent.putExtra("android.intent.extra.STREAM", c);
            intent.setType("image/*");
            context.startActivity(intent);
            return true;
        }

        return false;
    }

    public static void shareText(Context context, String content) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        context.startActivity(shareIntent);
    }

    public static void shareEtc(Context context, String file_path) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("image/*");

        Parcelable c;
        if (Build.VERSION.SDK_INT >= 23) {
            c = getUri(context, file_path);
        } else {
            c = getUri(file_path);
        }

        intent.putExtra("android.intent.extra.STREAM", c);
        context.startActivity(Intent.createChooser(intent, "Share to"));
    }

    private static Uri getUri(String str) {
        Uri fromFile = Uri.fromFile(new File(str));
        return fromFile;
    }

    private static Uri getUri(Context context, String str) {
        Uri uriForFile = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(str));
        return uriForFile;
    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    public static String saveToSharedImage(Context context, Bitmap bitmap) {
        if (bitmap == null) {
            return "";
        }
        File externalFilesDir = context.getExternalFilesDir("share_image");
        if (!externalFilesDir.exists()) {
            externalFilesDir.mkdir();
        }
        File file = new File(externalFilesDir, "share_image.jpg");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            OutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        String absolutePath = file.getAbsolutePath();

        return absolutePath;
    }
}
