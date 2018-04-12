package com.example.aria.easytouch.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.aria.easytouch.service.easytouch.EasyTouchService;
import com.example.aria.easytouch.util.Constants;

/**
 * Created by Aria on 2017/7/25.
 */

public class BootCompleteReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARE_DATA,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.STATE_FLOATWINDOW,true);
        editor.putBoolean(Constants.STATE_SCREENSHOT,false);
        editor.apply();
        Intent intent1 = new Intent(context, EasyTouchService.class);
        context.startService(intent1);
    }
}
