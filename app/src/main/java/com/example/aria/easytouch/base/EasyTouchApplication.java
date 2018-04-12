package com.example.aria.easytouch.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;


/**
 * Created by Aria on 2017/7/19.
 */

public class EasyTouchApplication extends MultiDexApplication{

    private static final String FLURRY_API_KEY = "GPXPBBQKSHPM6WWTN88S";

    private FlurryAgentListener flurryAgentListener;

    @Override
    public void onCreate() {
        super.onCreate();
        initFlurry();

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



    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
