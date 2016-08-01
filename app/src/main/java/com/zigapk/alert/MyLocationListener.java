package com.zigapk.alert;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.zigapk.alert.utils.Util;

/**
 * Created by zigapk on 7/31/16.
 */
public class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location loc) {
        if (loc.getAccuracy() < 50){
            Util.locationCallback(loc);
            if (ContextCompat.checkSelfPermission(Util.tempContextHolder,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Util.locationManager.removeUpdates(Util.locationListener);
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}