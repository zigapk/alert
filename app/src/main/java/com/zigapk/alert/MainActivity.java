package com.zigapk.alert;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zigapk.alert.data.Settings;
import com.zigapk.alert.receivers.BootReceiver;
import com.zigapk.alert.utils.InternetUtils;
import com.zigapk.alert.utils.Util;

public class MainActivity extends AppCompatActivity {

    public static final int SMS_PERMISSION = 0;
    public static final int LOCATION_PERMISSION = 1;

    BootReceiver alarm = new BootReceiver();
    private static String tempPermissionCheckNumberHolder = "";
    private static String tempPermissionCheckContentHolder = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //alarm.cancelAlarm(getApplicationContext());
        //alarm.setAlarm(getApplicationContext());

        if (Util.isFirstTime(getApplicationContext())){
            if (!InternetUtils.isOnline(getApplicationContext())){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Prvic moras biti online!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        }).setCancelable(false)
                        .create()
                        .show();
            }else {
                sendSmsWithPermissionCheck(new Settings().number, "teso tester te pozdravlja");
                Util.setFirstTime(false, getApplicationContext());

                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                //enable settings sync
                DatabaseReference settingsRef = FirebaseDatabase.getInstance().getReference("settings");
                settingsRef.keepSynced(true);
                //settingsRef.setValue(new Settings());
                //enable data sync
                DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("data");
                dataRef.keepSynced(true);
                Util.saveToLog("Init successful!");
            }
        }

        alarm.cancelAlarm(getApplicationContext());
        alarm.setAlarm(1000, getApplicationContext());



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

            tempPermissionCheckNumberHolder = number;
            tempPermissionCheckContentHolder = content;
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
                    Util.sendSMS(tempPermissionCheckNumberHolder, tempPermissionCheckContentHolder);
                    getLocationPermissoion();
                } else {
                    sendSmsWithPermissionCheck(tempPermissionCheckNumberHolder, tempPermissionCheckContentHolder);
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
