package com.zigapk.alert.utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zigapk.alert.MyLocationListener;
import com.zigapk.alert.data.Data;
import com.zigapk.alert.data.ExceptionHolder;
import com.zigapk.alert.data.LogHolder;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by zigapk on 29.7.2016.
 */
public class Util {
    public static Context tempContextHolder;
    public static LocationManager locationManager;
    public static LocationListener locationListener;

    public static void turnGPSOn() {
        sudo("settings put secure location_providers_allowed gps");
    }

    public static void turnGPSOff() {
        sudo("settings put secure location_providers_allowed ' '");
    }

    public static void enableMobileData(){
        sudo("svc data enable");
    }

    public static void disableMobileData(){
        sudo("svc data disable");
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
                saveToLog(e);
            }
            outputStream.close();
        } catch (IOException e) {
            saveToLog(e);
        }
    }

    public static void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
        } catch (Exception ex) {
            saveToLog(ex);
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

    public static String getLastSmsText(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("sms_text", "");
    }

    public static void setLastSmsText(String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("sms_text", value);
        editor.commit();
    }

    public static void startLocationGetter(final Context context) {
        if (!Util.isAirplaneModeOn(context)) {
            tempContextHolder = context;
            turnGPSOn();
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
                        Util.saveToLog(e);
                    }
                    if (ContextCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        try {
                            locationManager.removeUpdates(locationListener);
                        } catch (Exception e) {
                            Util.saveToLog(e);
                        }

                        try {
                            turnGPSOff();
                        } catch (Exception e) {
                            Util.saveToLog(e);
                        }
                    }
                }
            }).start();
        }
    }

    public static void locationCallback(Location loc) {
        turnGPSOff();

        //push data to local db
        Data data = new Data(loc, tempContextHolder);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String key = mDatabase.child("data").push().getKey();
        mDatabase.child("data").child(key).setValue(data);

        setLastSmsText(data.toSmsText(), tempContextHolder);
    }

    public static String networkProviderName(Context context){
        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getNetworkOperatorName();
    }

    public static boolean isAirplaneModeOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) != 0;

    }

    public static void saveToLog(Exception e){
        //push data to local db
        ExceptionHolder exceptionHolder = new ExceptionHolder(e);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String key = mDatabase.child("log").push().getKey();
        mDatabase.child("log").child(key).setValue(exceptionHolder);
    }

    public static void saveToLog(String log){
        //push data to local db
        LogHolder logHolder = new LogHolder(log);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String key = mDatabase.child("log").push().getKey();
        mDatabase.child("log").child(key).setValue(logHolder);
    }
}
