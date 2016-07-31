package com.zigapk.alert;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.zigapk.alert.utils.Util;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void turnOn(View v){
        Util.turnGPSOn(getApplicationContext());
    }

    public void turnOff(View v){
        Util.turnGPSOff(getApplicationContext());
    }
}
