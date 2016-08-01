package com.zigapk.alert;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zigapk.alert.utils.Util;

public class MainActivity extends AppCompatActivity {

    public static final int SMS_PERMISSION = 0;
    public static final int LOCATION_PERMISSION = 1;
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

    public void get(View view){
        Util.startLocationGetter(getApplicationContext());
    }

    public void getLocationPermissoion(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION);
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
                    getLocationPermissoion();
                } else {
                    sendSmsWithPermissionCheck(tempPermissoionCheckNumberHolder, tempPermissoionCheckContentHolder);
                }
                break;
            }case LOCATION_PERMISSION: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getLocationPermissoion();
                }
            }
        }
    }
}
