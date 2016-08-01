package com.zigapk.alert.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zigapk.alert.data.Settings;
import com.zigapk.alert.utils.Util;

import java.util.Calendar;

public class LocationReceiver extends WakefulBroadcastReceiver {
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    public LocationReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("settings").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Settings settings = dataSnapshot.getValue(Settings.class);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        if (calendar.HOUR > settings.silentStart || calendar.HOUR < settings.silentStop){
                            calendar.set(Calendar.HOUR_OF_DAY, settings.silentStop);
                            calendar.set(Calendar.MINUTE, 1);
                            cancelAlarm(context);
                            setAlarm(calendar, context);
                        }else {
                            cancelAlarm(context);
                            setAlarm(settings.retrieveLocationInterval*60*1000, context);
                        }

                        Util.startLocationGetter(context);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("asdf", "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void setAlarm(long milis, Context context) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, LocationReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + milis, alarmIntent);
    }

    public void setAlarm(Calendar calendar, Context context) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, LocationReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
    }

    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }
    }
}
