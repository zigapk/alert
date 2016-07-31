package com.zigapk.alert;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        new MyReceiver().setAlarm(getApplicationContext());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //do some work
                }catch (Exception e){
                    //notify user about fail
                }

            }
        }).start();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
