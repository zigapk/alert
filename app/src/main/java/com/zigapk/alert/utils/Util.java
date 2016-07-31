package com.zigapk.alert.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.zigapk.alert.R;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by zigapk on 29.7.2016.
 */
public class Util {
    public static void turnGPSOn(Context context){
       sudo("settings put secure location_providers_allowed gps");
    }

    public static void turnGPSOff(Context context){
        sudo("settings put secure location_providers_allowed ' '");
    }

    public static void sudo(String...strings) {
        try{
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            for (String s : strings) {
                outputStream.writeBytes(s+"\n");
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
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void sendSMS(String phoneNo, String msg){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isFirstTime(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("first_time", true);
    }

    public static void setFirstTime(boolean value, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("lastAppVersion", value);
        editor.commit();
    }
}
