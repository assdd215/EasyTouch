package com.example.aria.easytouch.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Aria on 2017/8/15.
 */

public class CommonAppInfo {
    private String label;
    private Drawable icon;
    private String packageName;
    private String launchName;

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setLaunchName(String launchName) {
        this.launchName = launchName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getLabel() {
        return label;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getLaunchName() {
        return launchName;
    }
}
