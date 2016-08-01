package com.zigapk.alert.data;

import android.content.Context;
import android.location.Location;

import com.google.firebase.database.Exclude;
import com.zigapk.alert.utils.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by zigapk on 8/1/16.
 */
public class Data {
    public double latitude;
    public double longitude;
    public float accuracy;
    public String timestamp;
    public String networkProviderName;

    public Data(){}
    public Data(Location loc, Context context){
        latitude = loc.getLatitude();
        longitude = loc.getLongitude();
        accuracy = loc.getAccuracy();
        timestamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        networkProviderName = Util.networkProviderName(context);
    }

    @Exclude
    public String toSmsText(){
        return  latitude + "\n" + longitude + "\nnatancnost: " + accuracy + "\n" + timestamp +
                "\nnetwork provider: " + networkProviderName;
    }
}
