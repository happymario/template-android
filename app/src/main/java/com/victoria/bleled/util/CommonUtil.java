package com.victoria.bleled.util;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.victoria.bleled.BuildConfig;
import com.victoria.bleled.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {
    public static final String TIME_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_FORMAT1 = "yyyy.MM.dd hh:mm:ss";
    public static final String TIME_FORMAT2 = "yyyy.MM.dd";
    public static final String TIME_FORMAT3 = "MM.dd";
    public static final String TIME_FORMAT4 = "yy.MM.dd";
    public static final String TIME_FORMAT5 = "MM.dd hh:mm:ss";
    public static final String TIME_FORMAT6 = "HH:mm";
    public static final String TIME_FORMAT7 = "a hh:mm";

    private static class TIME_MAXIMUM {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }

    public static void showToast(Context p_context, int strId) {
        String msg = p_context.getString(strId);
        showToast(p_context, msg);
    }

    public static void showToast(Context p_context, String msg) {
        Toast.makeText(p_context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showNIToast(Context p_context) {
        Toast.makeText(p_context, p_context.getText(R.string.ready_service), Toast.LENGTH_SHORT).show();
    }

    /**
     * Close keyboard
     */
    public static void hideKeyboard(EditText edit) {
        InputMethodManager imm = (InputMethodManager) edit.getContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }

    public static void showKeyboard(EditText edit) {
        InputMethodManager imm = (InputMethodManager) edit.getContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edit, 0);
    }


    public static void showKeyboardForced(Context context) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void gotoSetting(Activity activity, int request) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, request);
    }

    public static void gotoGPS(Activity activity, int request) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivityForResult(intent, request);
    }

    public static void gotoWifi(Context context) {
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        context.startActivity(intent);
    }

    public static void gotoPhone(Context context, String tel) {
        String number = "tel:" + tel.trim();
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
        context.startActivity(callIntent);
    }

    public static String getVersionName(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static String getOSVersion(Context context) {
        return Build.VERSION.RELEASE;
    }

    public static void goPlaystore(Context activity, String packageName) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }

    public static void goUrl(Context activity, String url) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Log.e("KYAD", e.toString());
            Toast.makeText(activity, "Target URL Error", Toast.LENGTH_LONG).show();
        }

    }

    public static String getGmailAccount(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
                return "";
            }
        }

        // Getting all registered Google Accounts;
        String mail = "";
        Account[] accounts = AccountManager.get(context).getAccounts();
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                mail = account.name;
            }
        }

        //return Base64.encodeToString(mail.getBytes(), Base64.DEFAULT);
        return mail;
    }

    /**
     * Returns the consumer friendly device name
     */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    /**
     * dp -> pixel 변환
     *
     * @param context
     * @param dp      dp값
     *                return int 픽셀로 변환된 값
     ***/
    public static int dpToPx(Context context, int dp) {
        Resources resources = context.getResources();

        DisplayMetrics metrics = resources.getDisplayMetrics();

        float px = dp * (metrics.densityDpi / 160f);

        return (int) px;
    }

    /**
     * pixel -> dp 변환
     *
     * @param context
     * @param px      px 값
     *                return int dp로 변환된 값
     ***/
    public static int pxToDp(Context context, int px) {
        Resources resources = context.getResources();

        DisplayMetrics metrics = resources.getDisplayMetrics();

        float dp = px / (metrics.densityDpi / 160f);

        return (int) dp;

    }

    public static int spToPx(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static String makeMoneyType(int money) {
        return makeMoneyType(String.valueOf(money));
    }

    /**
     * 금액(double)을 금액표시타입(소숫점2자리)으로 변환한다.
     *
     * @param moneyString 금액(double 형)
     * @return 변경된 금액 문자렬
     */
    public static String makeMoneyType(String moneyString) {
        String format = "#,###.##"/* "#.##0.00" */;
        DecimalFormat df = new DecimalFormat(format);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();

        dfs.setGroupingSeparator(',');
        df.setGroupingSize(3);
        df.setDecimalFormatSymbols(dfs);

        try {
            return (df.format(Float.parseFloat(moneyString))).toString();
        } catch (Exception e) {
            return moneyString;
        }
    }

    public static String getCurrentDate() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        return getDateFormat(cal.getTime(), "yyyyMMdd");
    }

    public static String getDateFormat(Date time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(time);
    }

    public static String dateFormatConvert(String dateTime, String inFormat, String outFormat) {
        SimpleDateFormat w_sdf = new SimpleDateFormat(inFormat);
        w_sdf.setTimeZone(TimeZone.getTimeZone("UTC+09:00"));
        try {
            Date w_date = w_sdf.parse(dateTime);
            SimpleDateFormat w_sdf2 = new SimpleDateFormat(outFormat);
            w_sdf2.setTimeZone(TimeZone.getTimeZone("UTC+09:00"));
            String w_strConvertedDate = w_sdf2.format(w_date);
            return w_strConvertedDate;
        } catch (Exception e) {
            e.printStackTrace();
            return dateTime;
        }
    }

    public static String diffOfDate(Context context, Date begin, Date end) {
        if (begin == null || end == null)
            return "";

        long diff = end.getTime() - begin.getTime();
        double diffSeconds = diff / 1000.0f;
        double diffMins = diff / (60 * 1000.0f);
        double diffHours = diff / (60 * 60 * 1000.0f);
        double diffDays = diff / (24 * 60 * 60 * 1000.0f);

        if (diffMins < 1) {
//            return String.format(context.getString(R.string.second_age_format), diffSeconds);
            return context.getString(R.string.just);
        } else if (diffHours < 1) {
            return String.format(context.getString(R.string.minute_age_format), (int) Math.ceil(diffMins));
        } else if (diffDays < 1) {
            return String.format(context.getString(R.string.hour_age_format), (int) Math.ceil(diffHours));
        } else if (diffDays <= 30) {
            return String.format(context.getString(R.string.day_age_format), (int) Math.floor(diffDays));
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
            return simpleDateFormat.format(begin);
        }
    }

    public static String diffOfDate(Context context, int diffSecond) {
        long diffMins = diffSecond / (60);
        long diffHours = diffSecond / (60 * 60);
        long diffDays = diffSecond / (24 * 60 * 60);

        if (diffMins < 1) {
            return String.format(context.getString(R.string.second_age_format), diffSecond);
//            return context.getString(R.string.just);
        } else if (diffHours < 1) {
            return String.format(context.getString(R.string.minute_age_format), diffMins);
        } else if (diffDays < 1) {
            return String.format(context.getString(R.string.hour_age_format), diffHours);
        } else {/*if (diffDays <= 10) {*/
            return String.format(context.getString(R.string.day_age_format), diffHours);
        }/* else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
            return simpleDateFormat.format(begin);
        }*/
    }

    public static String diffOfDate2(Context context, Date begin, Date end) {
        if (begin == null || end == null)
            return "";

        long diff = end.getTime() - begin.getTime();
        long diffSeconds = diff / 1000;
        long diffMins = diffSeconds / (60);
        long diffHours = diffSeconds / (60 * 60);
        long diffDays = diffSeconds / (24 * 60 * 60);
        long diffMonths = diffSeconds / (30 * 24 * 60 * 60);
        long diffYear = diffSeconds / (12 * 30 * 24 * 60 * 60);

        if (diffMins < 1) {
//            return String.format(context.getString(R.string.second_age_format), diffSeconds);
            return context.getString(R.string.just);
        } else if (diffHours < 1) {
            return String.format(context.getString(R.string.minute_age_format), diffMins);
        } else if (diffDays < 1) {
            return String.format(context.getString(R.string.hour_age_format), diffHours);
        } else if (diffMonths < 1) {
            return String.format(context.getString(R.string.day_age_format), diffDays);
        } else if (diffYear < 1) {
            return String.format(context.getString(R.string.month_age_format), diffDays);
        } else {
            return String.format(context.getString(R.string.year_age_format), diffDays);
        }
    }

    public static String getDateString(String format, Date date) {
        if (date == null)
            return "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static String getDateStringPM(String format, Date date) {
        if (date == null)
            return "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        String timeString = simpleDateFormat.format(date);
        if (timeString.contains("am") == true) {
            timeString = timeString.replace("am", "");
            timeString = "오전" + timeString;
        } else if (timeString.contains("pm") == true) {
            timeString = timeString.replace("pm", "");
            timeString = "오후" + timeString;
        }

        return timeString;
    }

    public static Date getDateFromServer(String dateStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_DEFAULT, Locale.getDefault());
        Date date;
        try {
            date = simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            date = new Date();
        }
        return date;
    }

    public static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static void sendEmail(Context context, String mail, String title, String content) {
        // 이메일 발송
        Uri uri = Uri.parse("mailto:" + mail);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra(Intent.EXTRA_SUBJECT, title);
        it.putExtra(Intent.EXTRA_TEXT, content);
        context.startActivity(it);

        /*
        String mailto = "mailto:bob@example.org" +
            "?cc=" + "alice@example.com" +
            "&subject=" + Uri.encode(subject) +
            "&body=" + Uri.encode(bodyText);

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(mailto));
         */
    }

    /**
     * 리소스 아이디 반환
     *
     * @param cont    Context
     * @param resouce 찾을 리소스
     * @param type    찾을 타입(id, layout, drawable등);
     */
    public static int getResourceId(Context cont, String resouce, String type) {
        int iResocue = cont.getResources().getIdentifier(resouce, type, BuildConfig.APPLICATION_ID);
        return iResocue;
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

    public static String getKeyHash(final Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (Exception e) {

        }
        if (packageInfo == null)
            return null;

        for (android.content.pm.Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.w("Util", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }

    //check email
    public static boolean isValidEmail(String email) {
        boolean isValid = false;

        int count = email.length() - email.replaceAll("@", "").length();
        if (count >= 2) {
            return false;
        }

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isEnglishNumberSpecial(String str) {
        boolean chk = Pattern.matches("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?~`]+$", str);

        return chk;
    }

    public static boolean isContainEnglish(String str) {
        Pattern regex = Pattern.compile("[a-zA-Z]");

        if (regex.matcher(str).find()) {
            return true;
        }

        return false;
    }

    public static boolean isContainNumber(String str) {
        Pattern regex = Pattern.compile("[0-9]");

        if (regex.matcher(str).find()) {
            return true;
        }

        return false;
    }

    public static boolean isContainSpecial(String str) {
        Pattern regex = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?~`]");

        if (regex.matcher(str).find()) {
            return true;
        }

        return false;
    }

    public static boolean isAlphaNumberHypen(String str) {
        boolean chk = Pattern.matches("^[a-zA-Z0-9_]+$", str);

        return chk;
    }

    //Pull all links from the body for easy retrieval
    public static ArrayList extractLinks(String text) {
        ArrayList links = new ArrayList();

        //String regex = "\\(?\\b((http|https)://|www[.])[-A-Za-z0-9+&amp;@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&amp;@#/%=~_()|]";
        String regex = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        while (m.find()) {
            String urlStr = m.group();

            if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }

            links.add(urlStr);
        }

        // no need http, https
        regex = "[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";
        p = Pattern.compile(regex);
        m = p.matcher(text);
        while (m.find()) {
            String urlStr = m.group();

            if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }

            boolean isExist = false;
            for (int i = 0; i < links.size(); i++) {
                String url = (String) links.get(i);
                if (url.contains(urlStr) == true) {
                    isExist = true;
                    break;
                }
            }

            if (isExist == false) {
                links.add(urlStr);
            }
        }
        return links;
    }

    public static boolean isInstalledPackage(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");

        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        if (resInfo.isEmpty()) {
            return false;
        }

        List<Intent> shareIntentList = new ArrayList<Intent>();
        boolean isShare = false;
        for (ResolveInfo info : resInfo) {
            Intent shareIntent = (Intent) intent.clone();

            if (info.activityInfo.packageName.toLowerCase().equals(packageName)) {
                isShare = true;
                break;
            }
        }

        return isShare;
    }

    public static boolean isValidUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            return URLUtil.isValidUrl(urlString) && Patterns.WEB_URL.matcher(urlString).matches();
        } catch (MalformedURLException e) {

        }

        return false;
    }

    public static void vibrate(Context context, long millisecond) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        // Vibrate for 500 milliseconds
        v.vibrate(millisecond);
    }

    public static String getAlphaNumericString(int n) {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    public static String getPhoneNumber(Context context) {
        try {
            TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return Base64.encodeToString("22222222222".getBytes(), Base64.DEFAULT);
                return "";
            }
            String mPhoneNumber = tMgr.getLine1Number();

            if (mPhoneNumber == null || mPhoneNumber.isEmpty() == true) {
                //return Base64.encodeToString("11111111111".getBytes(), Base64.DEFAULT);
                return "";
            }

            //return Base64.encodeToString(mPhoneNumber.getBytes(), Base64.DEFAULT);
            return mPhoneNumber;
        } catch (Exception e) {
            // should never happen
            //return Base64.encodeToString("11111111111".getBytes(), Base64.DEFAULT);
            return "";
        }
    }

    public static void scrollToFirst(RecyclerView view) {
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.scrollToPosition(0);
            }
        }, 200);
    }
}
