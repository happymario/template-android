package com.victoria.bleled.util.feature;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.victoria.bleled.R;
import com.victoria.bleled.util.CommonUtil;


public class PermissionUtil {
    public static boolean hasPermission(Activity activity, String permission) {
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean hasPermission(Activity activity, String[] permissions) {
        for (String permission : permissions)
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, permission))
                return false;
        return true;
    }

    public static boolean isPermisionsRevoked(Activity activity, String[] permissions) {
        boolean isRevoked = false;

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED &&
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) == false) {
                isRevoked = true;
                break;
            }
        }
        return isRevoked;
    }

    public static void requestPermission(Activity p_context, String[] p_requiredPermissions, int requestCode) {
        ActivityCompat.requestPermissions(p_context, p_requiredPermissions, requestCode);
    }

    public static void showPermissionGuide(Activity activity, final int RC) {
        new AlertDialog.Builder(activity)
                .setMessage(activity.getString(R.string.pms_des))
                .setPositiveButton(R.string.confirm, (dialog, which) -> CommonUtil.gotoSetting(activity, RC))
                .show();
    }
}
