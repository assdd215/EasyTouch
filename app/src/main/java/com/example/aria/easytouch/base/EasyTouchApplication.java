package com.example.aria.easytouch.base;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.view.WindowManager;

import com.example.aria.easytouch.util.Constants;
import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;
import com.luzeping.aria.commonutils.notification.NotificationCenter;
import com.luzeping.aria.commonutils.utils.Device;


/**
 * Created by Aria on 2017/7/19.
 */

public class EasyTouchApplication extends MultiDexApplication{

    private static final String FLURRY_API_KEY = "GPXPBBQKSHPM6WWTN88S";

    private FlurryAgentListener flurryAgentListener;

    @Override
    public void onCreate() {
        super.onCreate();
        initConstants();

        NotificationCenter.init(this);
    }

    private void initFlurry(){

        flurryAgentListener = new FlurryAgentListener() {
            @Override
            public void onSessionStarted() {
                Log.d("MainActivity","onSessionStarted");
            }
        };

        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .withCaptureUncaughtExceptions(true)
                .withContinueSessionMillis(10)
                .withLogEnabled(true)
                .withLogLevel(Log.VERBOSE)
                .withListener(flurryAgentListener)
                .build(this, FLURRY_API_KEY);
    }

    private void initConstants() {

        Device.setUp(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Constants.WINDOWLAYOUTPARAMS_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
            Constants.WINDOWLAYOUTPARAMS_TYPE = WindowManager.LayoutParams.TYPE_PHONE;
        }
    }



    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
