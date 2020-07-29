package com.victoria.bleled.util.feature;


import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.victoria.bleled.util.CommonUtil;

import java.util.List;

/**
 * Created by smarques84 on 15/05/16.
 */
public class MockLocationDetector {

    private static final String TAG = MockLocationDetector.class.getSimpleName();

    /*
      if (MockLocationDetector.checkForAllowMockLocationsApps(this)) {
            CommonUtil.showToast(this, "Fake GPS Setting!!!");
            getLocation();
        }
     */
    //Always return false on Marshmallow because the settings have been updated and its not possible to check if an app has been allowed
    // to perform mock locations. Please use isLocationFromMockProvider instead
    @Deprecated
    public static boolean isAllowMockLocationsOn(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            if (Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
                return false;
            else {
                return true;
            }
        } else {
            return false;
        }
    }

    public static boolean checkForAllowMockLocationsApps(Context context) {

        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i]
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Got exception " + e.getMessage());
            }
        }

        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    //http://stackoverflow.com/questions/6880232/disable-check-for-mock-location-prevent-gps-spoofing
    public static boolean isLocationFromMockProvider(Context context, Location location) {
        boolean isMock = false;
        if (Build.VERSION.SDK_INT >= 18) {
            isMock = location.isFromMockProvider();
        } else {
            //isMock = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
            if (Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
                return false;
            else {
                return true;
            }
        }
        return isMock;
    }

    public static void getLocation(Context context) {
        LocationRequest request = new LocationRequest();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(request, new LocationCallback() {
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (MockLocationDetector.isLocationFromMockProvider(context, locationResult.getLastLocation())) {
                    CommonUtil.showToast(context, "Fake GPS Setting!!!");
                }
            }
        }, Looper.getMainLooper()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
}
