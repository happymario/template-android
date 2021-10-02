package com.victoria.bleled.data.file

import android.content.Context
import java.io.*

class FileMgr(private val context: Context) {
    private val rootFolder: File = context.filesDir

    private fun getRootFolder(): String {
        return rootFolder.absolutePath
    }

    fun createFolder(name: String) {
        val folderPath = getRootFolder() + "/" + name
        val folder = File(folderPath)
        if (!folder.exists()) {
            folder.mkdir()
        }
    }

    fun removeFolder(name: String) {
        val folderPath = getRootFolder() + "/" + name
        val folder = File(folderPath)
        if (folder.isDirectory) {
            val children = folder.list()
            for (i in children.indices) {
                File(folder, children[i]).delete()
            }
        }
        folder.delete()
    }

    fun renameFolder(name: String, to: String) {
        val folderPath = getRootFolder() + "/" + name
        val folder = File(folderPath)
        val folderPath1 = getRootFolder() + "/" + to
        folder.renameTo(File(folderPath1))
    }

    fun getFileList(name: String): Array<File> {
        val folderPath = getRootFolder() + "/" + name
        val folder = File(folderPath)
        return folder.listFiles()
    }

    fun readFile(relativePath: String): String {
        val myExternalFile = File(getRootFolder() + "/" + relativePath)
        var categroyData = ""
        try {
            val fin = FileInputStream(myExternalFile)
            categroyData = convertStreamToString(fin)
        } catch (e: Exception) {
        }
        return categroyData
    }

    fun readRawFile(resId: Int): String {
        val inputStream = context.resources.openRawResource(resId)
        val reader = BufferedReader(InputStreamReader(inputStream))
        var result = ""
        try {
            var line = reader.readLine()
            while (line != null) {
                result += line
                line = reader.readLine()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun readAbsoluteFile(path: String?): String {
        val myExternalFile = File(path)
        var categroyData = ""
        try {
            val fin = FileInputStream(myExternalFile)
            categroyData = convertStreamToString(fin)
        } catch (e: Exception) {
        }
        return categroyData
    }

    fun isExist(relativePath: String): Boolean {
        val folderPath = getRootFolder() + "/" + relativePath
        val folder = File(folderPath)
        return folder.exists()
    }

    fun writeFile(relativePath: String, content: String?): Boolean {
        return try {
            val folderPath = getRootFolder() + "/" + relativePath
            val file = File(folderPath)
            val out = FileWriter(file)
            out.write(content)
            out.close()
            true
        } catch (e: IOException) {
            false
        }
    }

    fun copyFile(dest: String, src: File?): Boolean {
        val folderPath = getRootFolder() + "/" + dest
        try {
            val inputStream: InputStream = FileInputStream(src)
            val outputStream: OutputStream = FileOutputStream(folderPath)
            var byteRead: Int
            while (inputStream.read().also { byteRead = it } != -1) {
                outputStream.write(byteRead)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            return false
        }
        return true
    }

    fun deleteFile(relativePath: String): Boolean {
        val folderPath = getRootFolder() + "/" + relativePath
        val folder = File(folderPath)
        return folder.delete()
    }

    companion object {
        @Throws(Exception::class)
        fun convertStreamToString(`is`: InputStream?): String {
            val reader = BufferedReader(InputStreamReader(`is`))
            val sb = StringBuilder()
            var line: String? = null
            while (reader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            reader.close()
            return sb.toString()
        }
    }

}