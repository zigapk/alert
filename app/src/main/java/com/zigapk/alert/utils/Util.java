package com.zigapk.alert.utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;

import com.zigapk.alert.MainActivity;
import com.zigapk.alert.MyLocationListener;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by zigapk on 29.7.2016.
 */
public class Util {
    public static Context tempContextHolder;
    public static LocationManager locationManager;
    public static LocationListener locationListener;

    public static void turnGPSOn(Context context) {
        sudo("settings put secure location_providers_allowed gps");
    }

    public static void turnGPSOff(Context context) {
        sudo("settings put secure location_providers_allowed ' '");
    }

    public static void sudo(String... strings) {
        try {
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            for (String s : strings) {
                outputStream.writeBytes(s + "\n");
                outputStream.flush();
            }

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            try {
                su.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isFirstTime(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("first_time", true);
    }

    public static void setFirstTime(boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("first_time", value);
        editor.commit();
    }

    public static void startLocationGetter(final Context context) {
        tempContextHolder = context;
        turnGPSOn(context);
        locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }

        //start a thread which will attempt to kill location after 5 min
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5 * 60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    try {
                        locationManager.removeUpdates(locationListener);
                    }catch (Exception e) {}

                    try {
                        turnGPSOff(context);
                    }catch (Exception e) {}
                }
            }
        }).start();
    }

    public static void locationCallback(Location loc) {
        turnGPSOff(tempContextHolder);
        String text = loc.getLatitude() + "\n" + loc.getLongitude() + "\nnatancnost: " + loc.getAccuracy();
        sendSMS(MainActivity.NUMBER, text);
    }
}
