package com.example.aria.easytouch.base;

import android.app.Service;
import android.util.TypedValue;

import com.luzeping.aria.commonutils.notification.NotificationCenterDelegate;

public abstract class BaseService extends Service implements NotificationCenterDelegate{

    protected abstract void registerNotification();

    protected abstract void unRegisterNotification();
    protected abstract void init();

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        registerNotification();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterNotification();
    }
}
