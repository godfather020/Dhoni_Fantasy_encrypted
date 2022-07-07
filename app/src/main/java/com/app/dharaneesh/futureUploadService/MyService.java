package com.app.dharaneesh.futureUploadService;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {

    final Handler handler = new Handler();
    final int delay = 1000; // 1000 milliseconds == 1 second

    @Override
    public void onCreate() {
        super.onCreate();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("thisIsTheStupidTimer", "inService");
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // START YOUR TASKS

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // STOP YOUR TASKS
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}