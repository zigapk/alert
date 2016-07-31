package com.zigapk.alert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.zigapk.alert.utils.Util;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    MyReceiver alarm = new MyReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarm.cancelAlarm(getApplicationContext());
        alarm.setAlarm(getApplicationContext());
    }
}
