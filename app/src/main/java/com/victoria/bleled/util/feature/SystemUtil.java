package com.victoria.bleled.util.feature;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.Toast;


import androidx.core.app.NotificationCompat;

import com.victoria.lib.util.MStringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by YongTrim on 16. 5. 7. for rbooker_ad
 */
public class SystemUtil {

    /**
     * 특정 파일을 미디어 스캐닝
     */
    public static class MediaScannerNotifier implements MediaScannerConnection.MediaScannerConnectionClient {
        @SuppressWarnings("unused")
        private Context mContext;

        private MediaScannerConnection mConnection;

        private String mPath;

        private String mMimeType;

        public MediaScannerNotifier(Context context, String path, String mimeType) {
            mContext = context;
            mPath = path;
            mMimeType = mimeType;
            mConnection = new MediaScannerConnection(context, this);
            mConnection.connect();
        }

        @Override
        public void onMediaScannerConnected() {
            mConnection.scanFile(mPath, mMimeType);
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            // OPTIONAL: scan is complete, this will cause the viewer to render
            // it
            try {
                /*
                 * if (uri != null) { Intent intent = new Intent(Intent.ACTION_VIEW); intent.setData(uri); mContext.startActivity(intent); }
                 */
            } finally {
                mConnection.disconnect();
                mContext = null;
            }
        }
    }

    /**
     * 현재 네트워크 사용 가능 여부
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null)
            return false;
        else {
            final NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        }
    }

    public static boolean isWifi(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();
        return  isWifiConn;
    }

    /**
     * 检查是否有可用网络
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    /**
     * 외장 메모리 쓰기 가능 여부
     *
     * @return
     */
//    public static boolean isExternalWritable() {
//        File sdDir = new File(Config.SDCARD_DIRECTORY);
//        return (sdDir.exists() && sdDir.canWrite());
//    }

    /**
     * 런처 아이콘에 배지 카운트 브로드캐시트
     *
     * @param activity
     *            패키지명 추출
     * @param badgeCnt
     *            배지 카운트
     */
//    public static void launcherBroadcast(Class<?> clazz, int badgeCnt) {
//        String packageName = null;
//        String className = null;
//
//        if (clazz != null) {
//            packageName = clazz.getPackage().getName();
//            className = packageName + ".ui.MainActivity";
//            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
//            intent.putExtra("badge_count", badgeCnt); // 실제 업데이트 하려는 카운트 개수
//            // 메인메뉴에 나타나는 어플의패키지명
//            intent.putExtra("badge_count_package_name", packageName);
//            // 메인메뉴에 나타나는 어플의클래스명
//            intent.putExtra("badge_count_class_name", className);
//            DpApp.getContext().sendBroadcast(intent);
//        }
//    }

    /**
     * 캐쉬 경로를 반환한다.
     *
     * @param mContext
     * @return
     */
//    public static File getCacheDir(Context mContext) {
//        File cacheDir;
//
//        if (isExternalWritable()) {
//            cacheDir = Config.CACHE_DIR;
//            if (!cacheDir.exists())
//                cacheDir.mkdirs();
//        } else {
//            cacheDir = mContext.getCacheDir();
//        }
//
//        return cacheDir;
//    }

    /**
     * Clipboard에 선택한 텍스트 복사
     *
     * @param mContext
     * @param s
     */
    public static void copyToClipboard(Context mContext, String s) {
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("DP", s);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(mContext, "클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Clipboard에 저장된 텍스트 가져오기
     *
     * @param mContext
     * @return
     */
    public static String pasteFromClipboard(Context mContext) {
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboard.hasPrimaryClip()) {
            return clipboard.getPrimaryClip().getDescription().toString();
        } else {
            return "";
        }
    }

    /**
     * 현재 실행중인 프로세스 패키지 목록
     *
     * @param mContext
     * @return
     */
    public static List<String> getRunningActivity(Context mContext) {
        List<String> activePackage = new ArrayList<String>();

        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> proceses = am.getRunningAppProcesses();

        // 프로세서 전체를 반복
        for (ActivityManager.RunningAppProcessInfo process : proceses) {
            if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                activePackage.add(process.processName); // package이름과 동일함.
            }
        }
        return activePackage;
    }

    /**
     * 현재 화면에 보여지는 최상위 액티비티 클래스명
     *
     * @param mContext
     * @return
     */
    public static String getRunningTopActivity(Context mContext) {

        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task = am.getRunningTasks(1);
        ComponentName topActivity = task.get(0).topActivity;

        return topActivity.getClassName();
    }

    /**
     * .zip 압축 해제 (소스상 현재는 폰트 전용으로 구현되어 있음)
     *
     * @param unZipFile
     * @throws IOException
     */
    public static void unZip(File unZipFile) throws IOException {
        ZipInputStream in = new ZipInputStream(new FileInputStream(unZipFile));
        ZipEntry ze;

        try {
            if (unZipFile.exists()) {
                while ((ze = in.getNextEntry()) != null) {
                    String path = unZipFile.getAbsolutePath();
                    path = MStringUtil.replace(path, ".zip", ".ttf");

                    if (ze.getName().indexOf("/") != -1) {
                        File parent = new File(path).getParentFile();
                        if (!parent.exists()) {
                            if (!parent.mkdirs())
                                throw new IOException("Unable to create folder" + parent);
                        }
                    }

                    FileOutputStream out = new FileOutputStream(path);

                    byte[] buf = new byte[1024];

                    for (int nReadSize = in.read(buf); nReadSize != -1; nReadSize = in.read(buf)) {
                        out.write(buf, 0, nReadSize);
                    }
                    out.close();
                }
            }
        } catch (Exception e) {

        } finally {
            in.close();
        }

        // delete zip file
        unZipFile.delete();
    }

    /**
     * 단말의 폰번호 알아오기
     *
     * @param mContext
     * @return
     */
    public static String getPhoneNumber(Context mContext) {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        String phoneNumber = MStringUtil.defaultIfBlank(tm.getLine1Number());
        phoneNumber = MStringUtil.removeString(phoneNumber);
        if (MStringUtil.startsWith(phoneNumber, "821")) { // 대한민국 국가번호 포함시
            phoneNumber = "01" + MStringUtil.substringAfter(phoneNumber, "821");
        }

        return phoneNumber;
    }

    /**
     * 단말의 폰번호 알아오기
     *
     * @param mContext
     * @return
     */
    public static String getOperator(Context mContext) {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = tm.getNetworkOperator();

        return operator;
    }

    /**
     * 앱의 버전명 갖고 오기
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
     * 앱의 버전코드 갖고 오기
     *
     * @param context
     * @return
     */
    public static int getVerionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * 단말의 고유 ID
     *
     * @return
     */
    public static String getDeviceId() {
        // Returns the unique device ID, for example,
        // the IMEI for GSM and the MEID for CDMA phones.
        // Return null if device ID is not available.
        // Requires Permission: READ_PHONE_STATE

        // String id = TelephonyManager.getDeviceId();
        return null;
    }

    public static String getMacAddress(Context mContext) {
        // Requires Permission: ACCESS_WIFI_STATE
        WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wm.getConnectionInfo();
        if(wifiInfo != null) {
            return wifiInfo.getMacAddress();
        }

        return null;
    }


    public static String getDataFromAsset(Context context, String filename) {

        StringBuilder buf = new StringBuilder();
        try {
            InputStream json = context.getAssets().open(filename);
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            while ((str = in.readLine()) != null) {
                buf.append(str);
            }

            in.close();


        } catch (Exception e) {
            return null;

        }

        return buf.toString();
    }


    public static void showNotification(Context context, Intent intent, String title, String message, int alarm) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mCompatBuilder = new NotificationCompat.Builder(context);
        //mCompatBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mCompatBuilder.setWhen(System.currentTimeMillis());
        mCompatBuilder.setContentTitle(title);
        mCompatBuilder.setTicker(message);
        mCompatBuilder.setContentText(message);
        mCompatBuilder.setDefaults(alarm);
        mCompatBuilder.setContentIntent(pendingIntent);
        mCompatBuilder.setAutoCancel(true);

        long time = new Date().getTime();
        String tmpStr = String.valueOf(time);
        String last4Str = tmpStr.substring(tmpStr.length() - 5);
        int notificationId = Integer.valueOf(last4Str);

        nm.notify(notificationId, mCompatBuilder.build());
    }


    public static void setBadge(Context context, int count) {
        int badgeCount = count < 0 ? 0 : count;
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");

        intent.putExtra("badge_count", badgeCount);
        // 메인 메뉴에 나타나는 어플의  패키지 명
        intent.putExtra("badge_count_package_name", context.getPackageName());
        // 메인메뉴에 나타나는 어플의 클래스 명
        intent.putExtra("badge_count_class_name", getLauncherClassName(context));
        context.sendBroadcast(intent);
    }


    private static String getLauncherClassName(Context context) {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(context.getPackageName());

        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);
        if (resolveInfoList != null && resolveInfoList.size() > 0) {
            return resolveInfoList.get(0).activityInfo.name;
        }
        return "";
    }

    /**
     * Compares two version strings.
     * <p>
     * Use this instead of String.compareTo() for a non-lexicographical
     * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
     *
     * @param str1 a string of ordinal numbers separated by decimal points.
     * @param str2 a string of ordinal numbers separated by decimal points.
     * @return The result is a negative integer if str1 is _numerically_ less than str2.
     * The result is a positive integer if str1 is _numerically_ greater than str2.
     * The result is zero if the strings are _numerically_ equal.
     * @note It does not work if "1.10" is supposed to be equal to "1.10.0".
     */
    public static int versionCompare(String str1, String str2) {
        String[] vals1 = str1.split("\\.");
        String[] vals2 = str2.split("\\.");
        int i = 0;
        // set index to first non-equal ordinal or length of shortest version string
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
            i++;
        }
        // compare first non-equal ordinal number
        if (i < vals1.length && i < vals2.length) {
            int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
            return Integer.signum(diff);
        }
        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        return Integer.signum(vals1.length - vals2.length);
    }

    public static void jumpToGoogleStoreForUpdate(Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public static UUID getDeviceUuid(Context context) {
        UUID uuid = null;

        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            if (!"9774d56d682e549c".equals(androidId)) {
                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
            } else {
                final String deviceId = ((TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE )).getDeviceId();
                uuid = deviceId!=null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return uuid;
    }
}
