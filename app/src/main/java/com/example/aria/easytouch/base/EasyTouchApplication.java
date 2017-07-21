package com.example.aria.easytouch.base;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import com.example.aria.easytouch.activity.MainActivity;

import java.util.Locale;

/**
 * Created by Aria on 2017/7/19.
 */

public class EasyTouchApplication extends Application{

    private boolean openFloatingWindow;
    private boolean openScreenShot;

    @Override
    public void onCreate() {
        super.onCreate();
//        updateLanguage();
    }

    public void setOpenFloatingWindow(boolean openFloatingWindow) {
        this.openFloatingWindow = openFloatingWindow;
    }

    public void setOpenScreenShot(boolean openScreenShot) {
        this.openScreenShot = openScreenShot;
    }

    public boolean getOpenFloatingWindow(){
        return openFloatingWindow;
    }

    public boolean getOpenScreenShot(){
        return openScreenShot;
    }
}
