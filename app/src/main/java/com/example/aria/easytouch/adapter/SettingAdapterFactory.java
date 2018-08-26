package com.example.aria.easytouch.adapter;

import android.content.Context;

public class SettingAdapterFactory {
    public static BaseSettingAdapter getAdapter(Context context, int type){
        switch (type){
            case AdapterType.TYPE_INIT_SETTING:
                return new InitSettingAdapter(context);

            case AdapterType.TYPE_GESTURE:
                return new GestureSettingAdapter(context);

            case AdapterType.TYPE_NOTIFICATION:
                return new NotificationSettingAdapter(context);

            default:
                return null;

        }
    }

    public static class AdapterType{
        public static final int TYPE_INIT_SETTING = 0;
        public static final int TYPE_GESTURE = 1;
        public static final int TYPE_NOTIFICATION = 2;
    }
}
