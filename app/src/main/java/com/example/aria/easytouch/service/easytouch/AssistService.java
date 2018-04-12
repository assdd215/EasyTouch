package com.example.aria.easytouch.service.easytouch;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Aria on 2017/7/19.
 */

public class AssistService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    public class LocalBinder extends Binder{
        public AssistService getService(){
            return AssistService.this;
        }
    }
}
