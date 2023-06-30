package com.victoria.bleled.common.manager;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;
import com.victoria.bleled.common.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPSManager extends Service {
    public static final int RC_GPS_ENABLE = 9901;

    // The minimum distance to change Updates in meters
    public static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0.5f; // 1 meters

    // The minimum time between updates in milliseconds
    private static final long UPDATE_INTERVAL_MS = 2000; // 1s
    private static final int FASTEST_UPDATE_INTERVAL_MS = 1000; // 1s

    public static float getDistance(double from_lat, double from_lng, double to_lat, double to_lng) {
        if (from_lat <= 0 || from_lng <= 0 || to_lat <= 0 || to_lng <= 0) {
            return -1;
        }

        Location leftGeo = new Location("default");
        leftGeo.setLatitude(from_lat);
        leftGeo.setLongitude(from_lng);

        Location rightGeo = new Location("default");
        rightGeo.setLatitude(to_lat);
        rightGeo.setLongitude(to_lng);

        return rightGeo.distanceTo(leftGeo);
    }

    public final static String[] getPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
        }

        return new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        };
    }

    private Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    private Location lastLocation; // location
    private LatLng lastLatLng;

    // Declaring a Location Manager
    private LocationManager locationManager;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
//        float bestAccuracy = -1f;
//        if (location.getAccuracy() != 0.0f
//                && (location.getAccuracy() < bestAccuracy) || bestAccuracy == -1f) {
//            locationManager.removeUpdates(this);
//        }
//        bestAccuracy = location.getAccuracy();
            lastLocation = location;
            if (lastLocation != null) {
//                PrefManager dataManager = PrefManager.getInstance(mContext);
//
//                dataManager.setLatitude(lastLocation.getLatitude());
//                dataManager.setLongitude(lastLocation.getLongitude());
            }

            Intent retIntent = new Intent(Constants.BROADCAST_UPDATE_GPS);
            retIntent.putExtra("latitude", lastLocation.getLatitude());
            retIntent.putExtra("longitude", lastLocation.getLongitude());
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(retIntent);
        }
    };

    // Battery Optimizing
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                lastLocation = locationList.get(locationList.size() - 1);
                if (lastLocation != null) {
//                    PrefManager dataManager = PrefManager.getInstance(mContext);
//
//                    dataManager.setLatitude(lastLocation.getLatitude());
//                    dataManager.setLongitude(lastLocation.getLongitude());
                }

                Intent retIntent = new Intent(Constants.BROADCAST_UPDATE_GPS);
                retIntent.putExtra("latitude", lastLocation.getLatitude());
                retIntent.putExtra("longitude", lastLocation.getLongitude());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(retIntent);
            }
        }
    };

    private boolean useFusedLocation = true;

    public GPSManager(Context context) {
        this.mContext = context;
        this.useFusedLocation = true;
        start(false);
    }

    public GPSManager(Context context, boolean isOld) {
        this.mContext = context;
        this.useFusedLocation = isOld ? false : true;
        start(false);
    }

    private void setLastLocation(double latitude, double longitude) {
        if (latitude == 0 || longitude == 0) {
            return;
        }
        lastLocation = new Location("default");
        lastLocation.setLatitude(latitude);
        lastLocation.setLongitude(longitude);

//        PrefManager dataManager = PrefManager.getInstance(mContext);
//
//        dataManager.setLatitude(latitude);
//        dataManager.setLongitude(longitude);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        start(false);
        return null;
    }


    public void setContext(Context context) {
        mContext = context;
    }


    public Location start(boolean showAlert) {
        Location location = new Location("default");

        // get last location from preference
//        PrefManager dataManager = PrefManager.getInstance(mContext);
        double latitude = 0;//dataManager.getLatitude();
        double longitude = 0;//dataManager.getLongitude();
//        setLastLocation(latitude, longitude);

        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                this.canGetLocation = false;

                if (showAlert == true) {
                    showSettingsAlert();
                }
            } else {
                if (Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return location;
                }

                this.canGetLocation = true;
                if (useFusedLocation) {
                    if (locationRequest != null) {
                        fusedLocationClient.removeLocationUpdates(locationCallback);
                    }

                    locationRequest = new LocationRequest()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(UPDATE_INTERVAL_MS)
                            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS)
                            .setSmallestDisplacement(MIN_DISTANCE_CHANGE_FOR_UPDATES);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                    builder.addLocationRequest(locationRequest);

                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                } else {
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    criteria.setPowerRequirement(Criteria.POWER_HIGH);
                    criteria.setAltitudeRequired(false);
                    criteria.setSpeedRequired(false);
                    criteria.setCostAllowed(true);
                    criteria.setBearingRequired(false);

                    //API level 9 and up
                    criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
                    criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                UPDATE_INTERVAL_MS,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (lastLocation != null) {
                                latitude = lastLocation.getLatitude();
                                longitude = lastLocation.getLongitude();
                            }
                        }
                    }
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        if (lastLocation == null) {
//                            locationManager.requestLocationUpdates(
//                                    LocationManager.GPS_PROVIDER,
//                                    UPDATE_INTERVAL_MS,
//                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                            locationManager.requestLocationUpdates(UPDATE_INTERVAL_MS, MIN_DISTANCE_CHANGE_FOR_UPDATES, criteria, locationListener, Looper.myLooper());
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                lastLocation = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (lastLocation != null) {
                                    latitude = lastLocation.getLatitude();
                                    longitude = lastLocation.getLongitude();
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (lastLocation == null) {
            setLastLocation(latitude, longitude);
        } else {
            setLastLocation(lastLocation.getLatitude(), lastLocation.getLongitude());
        }

        return lastLocation;
    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app.
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        double latitude = 0;
        if (lastLocation != null) {
            latitude = lastLocation.getLatitude();
        }
        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        double longitude = 0;
        if (lastLocation != null) {
            longitude = lastLocation.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting DialogHelp Title
        alertDialog.setTitle("GPS is settings");

        // Setting DialogHelp Message
        alertDialog
                .setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        ((Activity) mContext).startActivityForResult(intent, RC_GPS_ENABLE);
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }

    public float getAccurecy() {
        return lastLocation.getAccuracy();
    }

    public int getDistance(double latitude, double longitude) {
        if (lastLocation == null || latitude <= 0 || longitude <= 0 || lastLocation.getLatitude() == 0 || lastLocation.getLongitude() == 0) {
            return -1;
        }

        Location leftGeo = new Location("default");
        leftGeo.setLatitude(latitude);
        leftGeo.setLongitude(longitude);
        int leftDistance = (int) (Math.ceil(lastLocation.distanceTo(leftGeo) / 1000.0f));

        return leftDistance;
    }

    public void getAddressList(final AddressListener listener) {
        final Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                String address = "";
                try {
                    List<Address> addresses = geocoder.getFromLocation(
                            lastLocation.getLatitude(), lastLocation.getLongitude(), 1);

                    if (listener != null) {
                        listener.onGetAddresses(addresses);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onGetAddresses(null);
                    }
                }
            }
        });
    }

    public LatLngBounds toBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }

    public String getAddress(LatLng latlng) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            return "잘못된 GPS 좌표";

        }

        if (addresses == null || addresses.size() == 0) {
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }

    public interface AddressListener {
        void onGetAddresses(List<Address> addressList);
    }
}


