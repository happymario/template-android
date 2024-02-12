package com.mario.template.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.provider.Settings
import android.view.View
import androidx.core.content.FileProvider
import com.mario.template.BuildConfig
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

object IntentShareUtil {
    fun shareInstagram(context: Context, str: String): Boolean {
        val c: Parcelable
        c = if (Build.VERSION.SDK_INT >= 23) {
            getUri(context, str)
        } else {
            getUri(str)
        }
        if (context.packageManager.getLaunchIntentForPackage("com.instagram.android") != null) {
            val intent = Intent()
            intent.setAction("android.intent.action.SEND")
            intent.setPackage("com.instagram.android")
            intent.putExtra("android.intent.extra.STREAM", c)
            intent.setType("image/*")
            context.startActivity(intent)
            return true
        }
        return false
    }

    fun shareText(context: Context, content: String?) {
        val sendIntent = Intent()
        sendIntent.setAction(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, content)
        sendIntent.setType("text/plain")
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    fun sharePdf(context: Context, file_path: String) {
        var intent = Intent(Intent.ACTION_VIEW)
        val c: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            c = getUri(context, file_path)
            intent.setData(c)
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        } else {
            c = getUri(file_path)
            intent.setDataAndType(c, "application/pdf")
            intent = Intent.createChooser(intent, "Open File")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    fun shareEtc(context: Context, file_path: String) {
        val intent = Intent("android.intent.action.SEND")
        intent.setType("image/*")
        val c: Parcelable
        c = if (Build.VERSION.SDK_INT >= 23) {
            getUri(context, file_path)
        } else {
            getUri(file_path)
        }
        intent.putExtra("android.intent.extra.STREAM", c)
        context.startActivity(Intent.createChooser(intent, "Share to"))
    }

    private fun getUri(str: String): Uri {
        return Uri.fromFile(File(str))
    }

    private fun getUri(context: Context, str: String): Uri {
        return FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID + ".provider",
            File(str)
        )
    }

    fun captureBitmapFromView(v: View): Bitmap {
        val b = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)
        return b
    }

    fun saveToSharedImage(
        context: Context,
        bitmap: Bitmap?
    ): String {
        if (bitmap == null) {
            return ""
        }
        val externalFilesDir = context.getExternalFilesDir("share_image")
        if (!externalFilesDir!!.exists()) {
            externalFilesDir.mkdir()
        }
        val file = File(externalFilesDir, "share_image.jpg")
        if (file.exists()) {
            file.delete()
        }
        try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            val fileOutputStream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.close()
        } catch (e2: IOException) {
            e2.printStackTrace()
        }
        return file.absolutePath
    }

    fun gotoPhone(context: Context, tel: String) {
        val number = "tel:" + tel.trim { it <= ' ' }
        val callIntent = Intent(Intent.ACTION_CALL, Uri.parse(number))
        context.startActivity(callIntent)
    }

    @JvmStatic
    fun gotoSetting(activity: Activity, request: Int) {
        val intent = Intent()
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", activity.packageName, null)
        intent.setData(uri)
        activity.startActivityForResult(intent, request)
    }
}
