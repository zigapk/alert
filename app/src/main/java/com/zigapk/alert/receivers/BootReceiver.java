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

import java.util.Calendar;

//gets called on boot and the first start
public class BootReceiver extends WakefulBroadcastReceiver {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    public BootReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        cancelAlarm(context);

        //set the other two receivers
        //get settings
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
                            LocationReceiver locationReceiver = new LocationReceiver();
                            locationReceiver.cancelAlarm(context);
                            locationReceiver.setAlarm(calendar, context);
                            DataUploadReciever dataUploadReciever = new DataUploadReciever();
                            dataUploadReciever.cancelAlarm(context);
                            dataUploadReciever.setAlarm(calendar, context);
                        }else {
                            LocationReceiver locationReceiver = new LocationReceiver();
                            locationReceiver.cancelAlarm(context);
                            locationReceiver.setAlarm(settings.retrieveLocationInterval*60*1000, context);
                            DataUploadReciever dataUploadReciever = new DataUploadReciever();
                            dataUploadReciever.cancelAlarm(context);
                            dataUploadReciever.setAlarm(settings.uploadDataInterval*60*1000, context);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("asdf", "getUser:onCancelled", databaseError.toException());
                    }
                });

    }

    public void setAlarm(long milis, Context context) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BootReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + milis, alarmIntent);
    }

    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }
    }
}
