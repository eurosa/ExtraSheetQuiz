package com.upsun.quizz.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MyService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this,"Service is running",Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public boolean stopService(Intent name) {

//        Toast.makeText(this,"Service stopped",Toast.LENGTH_LONG).show();
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
