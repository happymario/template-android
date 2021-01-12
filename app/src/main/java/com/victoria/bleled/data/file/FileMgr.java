package com.kyad.moneyplex.data.file;


import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class FileMgr {
    public final static String ROOT_FOLDER = Environment.getExternalStorageDirectory() + "/AlwaysWithYou";
    private Context mContext;

    public FileMgr(Context context) {
        mContext = context;

        File folder = new File(ROOT_FOLDER);

        if (folder.exists() == false) {
            folder.mkdir();
        }
    }

    public void createFolder(String name) {
        String folderPath = ROOT_FOLDER + "/" + name;

        File folder = new File(folderPath);

        if (folder.exists() == false) {
            folder.mkdir();
        }
    }

    public void removeFolder(String name) {
        String folderPath = ROOT_FOLDER + "/" + name;

        File folder = new File(folderPath);

        if (folder.isDirectory()) {
            String[] children = folder.list();
            for (int i = 0; i < children.length; i++) {
                new File(folder, children[i]).delete();
            }
        }

        folder.delete();
    }

    public void renameFolder(String name, String to) {
        String folderPath = ROOT_FOLDER + "/" + name;

        File folder = new File(folderPath);
        String folderPath1 = ROOT_FOLDER + "/" + to;

        folder.renameTo(new File(folderPath1));
    }

    public File[] getFileList(String name) {
        String folderPath = ROOT_FOLDER + "/" + name;

        File folder = new File(folderPath);

        return folder.listFiles();
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }


    public String readFile(String relativePath) {
        File myExternalFile = new File(ROOT_FOLDER + "/" + relativePath);
        String categroyData = "";
        try {
            FileInputStream fin = new FileInputStream(myExternalFile);
            categroyData = convertStreamToString(fin);
        } catch (Exception e) {
        }

        return categroyData;
    }

    public String readRawFile(int resId) {
        InputStream inputStream = mContext.getResources().openRawResource(resId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String result = "";
        try {
            String line = reader.readLine();
            while (line != null) {
                result += line;
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String readAbsoluteFile(String path) {
        File myExternalFile = new File(path);
        String categroyData = "";
        try {
            FileInputStream fin = new FileInputStream(myExternalFile);
            categroyData = convertStreamToString(fin);
        } catch (Exception e) {
        }

        return categroyData;
    }

    public boolean isExist(String relativePath) {
        String folderPath = ROOT_FOLDER + "/" + relativePath;

        File folder = new File(folderPath);

        return folder.exists();
    }

    public boolean writeFile(String relativePath, String content) {
        try {
            String folderPath = ROOT_FOLDER + "/" + relativePath;

            File file = new File(folderPath);

            FileWriter out = new FileWriter(file);
            out.write(content);
            out.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean copyFile(String dest, File src) {
        String folderPath = ROOT_FOLDER + "/" + dest;
        try {
            InputStream inputStream = new FileInputStream(src);
            OutputStream outputStream = new FileOutputStream(folderPath);
            int byteRead;

            while ((byteRead = inputStream.read()) != -1) {
                outputStream.write(byteRead);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteFile(String relativePath) {
        String folderPath = ROOT_FOLDER + "/" + relativePath;

        File folder = new File(folderPath);

        return folder.delete();
    }
}
