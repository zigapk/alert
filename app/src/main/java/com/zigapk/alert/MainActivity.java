package com.zigapk.alert;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.zigapk.alert.utils.Util;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public static final int SMS_PERMISSION = 0;
    public static final String NUMBER = "+38640211890";

    MyReceiver alarm = new MyReceiver();
    private static String tempPermissoionCheckNumberHolder = "";
    private static String tempPermissoionCheckContentHolder = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //alarm.cancelAlarm(getApplicationContext());
        //alarm.setAlarm(getApplicationContext());

        if (Util.isFirstTime(getApplicationContext())){
            sendSmsWithPermissionCheck(NUMBER, "teso tester te pozdravlja");
            Util.setFirstTime(false, getApplicationContext());
        }

    }

    public void sendSmsWithPermissionCheck(String number, String content) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            tempPermissoionCheckNumberHolder = number;
            tempPermissoionCheckContentHolder = content;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION);
        }else {
            Util.sendSMS(number, content);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SMS_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Util.sendSMS(tempPermissoionCheckNumberHolder, tempPermissoionCheckContentHolder);
                } else {
                    sendSmsWithPermissionCheck(tempPermissoionCheckNumberHolder, tempPermissoionCheckContentHolder);
                }
                return;
            }
        }
    }
}
