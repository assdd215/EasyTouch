package com.example.aria.easytouch.base;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import com.example.aria.easytouch.activity.MainActivity;
import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;

import java.util.Locale;

/**
 * Created by Aria on 2017/7/19.
 */

public class EasyTouchApplication extends Application{

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


}
