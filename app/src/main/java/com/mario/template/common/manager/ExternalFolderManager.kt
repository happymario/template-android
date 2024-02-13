package com.mario.template.common.manager

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class ExternalFolderManager(
    /************************************************************
     * Public
     */
    private val context: Context, rootFolder: String
) {
    private val externalFolder: File?

    init {
        externalFolder = createFolderExternal(context, rootFolder)
    }

    fun saveFileToExternal(originFile: File): File? {
        val fileName = originFile.getName()
        var outputFilePath: String? = null
        var outputStream: OutputStream? = null
        outputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // android 10이상일 경우
            try {
                createFileExternal(context, externalFolder!!.getName(), fileName)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        } else {
            val newFile = File(externalFolder, fileName)
            try {
                FileOutputStream(newFile, true)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
        try {
            val ins: InputStream = FileInputStream(originFile)
            val buffer = ByteArray(4096)
            var length: Int
            while (ins.read(buffer).also { length = it } > 0) {
                outputStream!!.write(buffer, 0, length)
            }
            outputStream!!.flush()
            outputStream.close()
            outputFilePath = externalFolder!!.absolutePath + "/" + fileName
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return File(outputFilePath)
    }

    fun createBitmapUri(bitmap: Bitmap): Uri? {
        try {
            val tsLong = System.currentTimeMillis() / 1000
            val ext = ".jpg"
            val newFileName = "temp_$tsLong$ext"
            var newMediaUri: Uri? = null
            newMediaUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveBitmapToMediaStore(context, bitmap, CompressFormat.JPEG, ext, newFileName)
            } else {
                saveBitmapToOldStorage(bitmap, newFileName)
            }
            return newMediaUri
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private val relativePath: String
        /************************************************************
         * Helper
         */
        private get() = ROOT_DIR + "/" + externalFolder!!.getName()

    private fun createFolderExternal(context: Context, dir: String): File? {
        var retFolder: File? = null
        var folder = Environment.getExternalStoragePublicDirectory(ROOT_DIR)
        folder = File(folder, dir)
        var isCreated = folder.exists()
        if (isCreated == true) {
            return folder
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // android 10이상일 경우
            val resolver = context.contentResolver
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, ROOT_DIR + "/" + dir)
            val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
            if (uri != null) {
                val path = uri.toString()
                folder = File(path)
                isCreated = folder.exists()
                if (!isCreated) {
                    folder.mkdirs()
                }
                retFolder = File(Environment.getExternalStoragePublicDirectory(ROOT_DIR), dir)
            }
        } else {
            if (!isCreated) {
                folder.mkdirs()
            }
            retFolder = folder
        }
        return retFolder
    }

    private fun createFileExternal(
        context: Context,
        folder: String,
        fileName: String
    ): OutputStream? {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/txt")
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, ROOT_DIR + "/" + folder)
        try {
            val resolver = context.contentResolver
            var uri = getUriFromName(context, fileName)
            if (uri == null) {
                uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
            }
            return resolver.openOutputStream(uri!!, "wa")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getUriFromName(context: Context, displayName: String): Uri? {
        val photoId: Long
        val photoUri = MediaStore.Files.getContentUri("external")
        try {
            val projection = arrayOf(MediaStore.Images.ImageColumns._ID)
            val resolver = context.contentResolver
            val cursor = resolver.query(
                photoUri,
                projection,
                MediaStore.Images.ImageColumns.DISPLAY_NAME + " LIKE ?",
                arrayOf(displayName),
                null
            )!!
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(projection[0])
            photoId = cursor.getLong(columnIndex)
            cursor.close()
        } catch (e: Exception) {
            return null
        }
        return Uri.parse("$photoUri/$photoId")
    }

    private val isOSNougat: Boolean
        private get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

    private fun getUriForAPI7(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(context, context.packageName + ".provider", file)
    }

    private fun getUriFromFile(context: Context, file: File): Uri {
        return if (isOSNougat) {
            getUriForAPI7(context, file)
        } else {
            Uri.fromFile(file)
        }
    }

    private fun saveBitmap(bitmap: Bitmap, path: String) {
        var stream: OutputStream? = null
        try {
            val file = File(path)
            stream = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        bitmap.compress(CompressFormat.PNG, 100, stream!!)
        try {
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun saveBitmapToOldStorage(
        bitmap: Bitmap,
        fileName: String
    ): Uri {
        val imagesDir = externalFolder
        val image = File(imagesDir, fileName)
        saveBitmap(bitmap, image.absolutePath)
        return getUriFromFile(context, image)
    }

    @Throws(IOException::class)
    private fun saveBitmapToMediaStore(
        context: Context, bitmap: Bitmap,
        format: CompressFormat, mimeType: String,
        displayName: String
    ): Uri? {
        val relativeLocation = relativePath
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
        val resolver = context.contentResolver
        var stream: OutputStream? = null
        var uri: Uri? = null
        try {
            //final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            val contentUri = MediaStore.Files.getContentUri("external")
            uri = resolver.insert(contentUri, contentValues)
            if (uri == null) {
                throw IOException("Failed to create new MediaStore record.")
            }
            stream = resolver.openOutputStream(uri)
            if (stream == null) {
                throw IOException("Failed to get output stream.")
            }
            if (bitmap.compress(format, 95, stream) == false) {
                throw IOException("Failed to save bitmap.")
            }
        } catch (e: IOException) {
            if (uri != null) {
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(uri, null, null)
            }
            throw e
        } finally {
            stream?.close()
        }
        return uri
    }

    companion object {
        /************************************************************
         * Static
         */
        private const val TAG = "ExternalFolderManager"
        val ROOT_DIR = Environment.DIRECTORY_DOWNLOADS // Environment.DIRECTORY_PICTURES
    }
}
