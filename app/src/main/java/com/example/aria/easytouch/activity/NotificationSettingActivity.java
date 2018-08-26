package com.example.aria.easytouch.activity;

import android.graphics.Color;
import android.view.Menu;
import android.widget.Switch;

import com.assistivetool.booster.easytouch.R;

import butterknife.BindView;

public class NotificationSettingActivity extends SettingActivity{

    public static boolean enable_notfiy = false;

    @BindView(R.id.switch_receive_notification)
    Switch aSwitch;

    @Override
    protected int layoutId() {
        return R.layout.activity_notification;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notification_setting,menu);
        return true;
    }
}
