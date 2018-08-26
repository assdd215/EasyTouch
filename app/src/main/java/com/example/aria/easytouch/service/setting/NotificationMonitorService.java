package com.example.aria.easytouch.service.setting;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.util.Log;

@RequiresApi(api = 21)
public class NotificationMonitorService extends NotificationListenerService{

    private static final String TAG = "Notification";

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBind");
        return super.onBind(intent);
    }

    @Override
    public void onInterruptionFilterChanged(int interruptionFilter) {
        super.onInterruptionFilterChanged(interruptionFilter);
        Log.d(TAG,"onINterruptionFilterChanged");
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.d(TAG,"onListenerConnected");
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Log.d(TAG,"onNotificationRemoved");
    }
}
