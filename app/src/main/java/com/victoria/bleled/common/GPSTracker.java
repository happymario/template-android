package com.victoria.bleled.common;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.victoria.bleled.data.DataRepository;
import com.victoria.bleled.data.local.IPrefDataSource;

import java.util.List;
import java.util.Locale;

public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location lastLocation; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

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

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    @TargetApi(23)
    public Location getLocation() {
        Location location = new Location("default");

        // get last location from preference
        IPrefDataSource dataManager = DataRepository.provideDataRepository().getPrefDataSource();
        double latitude = dataManager.getLatitude();
        double longitude = dataManager.getLongitude();

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            setLastLocation(latitude, longitude);
            return location;
        }

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
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
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
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
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

        } catch (Exception e) {
        }

        if (lastLocation == null) {
            setLastLocation(latitude, longitude);
        } else {
            setLastLocation(lastLocation.getLatitude(), lastLocation.getLongitude());
        }

        return lastLocation;
    }

    private void setLastLocation(double latitude, double longitude) {
        if (latitude == 0 || longitude == 0) {
            return;
        }
        lastLocation = new Location("default");
        lastLocation.setLatitude(latitude);
        lastLocation.setLongitude(longitude);

        IPrefDataSource dataManager = DataRepository.provideDataRepository().getPrefDataSource();

        dataManager.setLatitude(latitude);
        dataManager.setLongitude(longitude);
    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app.
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
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
                        mContext.startActivity(intent);
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

    @Override
    public void onLocationChanged(Location location) {
//        float bestAccuracy = -1f;
//        if (location.getAccuracy() != 0.0f
//                && (location.getAccuracy() < bestAccuracy) || bestAccuracy == -1f) {
//            locationManager.removeUpdates(this);
//        }
//        bestAccuracy = location.getAccuracy();
        lastLocation = location;
        if (lastLocation != null) {
            IPrefDataSource dataManager = DataRepository.provideDataRepository().getPrefDataSource();

            dataManager.setLatitude(lastLocation.getLatitude());
            dataManager.setLongitude(lastLocation.getLongitude());
        }

        Intent retIntent = new Intent(Constants.BROADCAST_UPDATE_GPS);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(retIntent);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
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

    public interface AddressListener {
        void onGetAddresses(List<Address> addressList);
    }
}

