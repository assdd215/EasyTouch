package com.example.aria.easytouch.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Aria on 2017/7/25.
 */

public class MyService extends Service{

    @Override
    public void onCreate() {
        super.onCreate();
        Handler handler = new Handler();
        Toast.makeText(this,"Service onCreate",Toast.LENGTH_SHORT).show();
        Log.d("MainActivity","onBind");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("MainActivity","run");
                Intent intent1 = new Intent();
                intent1.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent1);
            }
        },2000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
