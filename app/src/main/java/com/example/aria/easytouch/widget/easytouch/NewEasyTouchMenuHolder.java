package com.example.aria.easytouch.widget.easytouch;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.aria.easytouch.R;
import com.example.aria.easytouch.util.Constants;
import com.example.aria.easytouch.widget.easytouch.camera.LightCamera;
import com.example.aria.easytouch.widget.easytouch.menu.MainView;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Aria on 2017/7/21.
 */

public class NewEasyTouchMenuHolder implements OnMenuHolderEventListener{
    private Context context;
    private MainView mainView;
    private OnMenuHolderEventListener onMenuHolderEventListener;
    private LightCamera cameraUtil;

    private BroadcastReceiver customReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,0)){
                    case BluetoothAdapter.STATE_ON:
                        mainView.findViewByTitle(context.getString(R.string.menu_bluetooth)).
                                getImageView().setImageResource(R.drawable.menu_blutooth_on);
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        mainView.findViewByTitle(context.getString(R.string.menu_bluetooth))
                                .getImageView().setImageResource(R.drawable.menu_blutooth_off);
                        break;
                }
            }

            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){
                switch (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,0)){
                    case WifiManager.WIFI_STATE_DISABLED:
                        mainView.findViewByTitle(context.getString(R.string.menu_wifi))
                                .getImageView().setImageResource(R.drawable.menu_wifi_off);
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        mainView.findViewByTitle(context.getString(R.string.menu_wifi))
                                .getImageView().setImageResource(R.drawable.menu_wifi_on);
                        break;
                }
            }

            if (intent.getAction().equals(Constants.ACTIVATE_SCREENSHOT)){
//                btnScreenShot.setClickable(true);
//                btnScreenShot.setBackgroundResource(R.drawable.menu_cut_enable);
            }
        }
    };

    public NewEasyTouchMenuHolder(Context context){
        this.context = context;
        mainView = new MainView(context);
        initMenuItems();
        updateMenuIcons();
        initReceiver();
    }

    private void initMenuItems(){
        mainView.addMenuItem(context.getString(R.string.menu_tel), context.getResources().getDrawable(R.drawable.menu_tel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity","btnTel click");
                onMenuHolderEventListener.beforeItemPerform(v);
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(dialIntent);
                onMenuHolderEventListener.afterItemClick(v);
            }
        });

        mainView.addMenuItem(context.getString(R.string.menu_message), context.getResources().getDrawable(R.drawable.menu_message), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuHolderEventListener.beforeItemPerform(v);
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.setData(Uri.parse("sms:"));
                smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(smsIntent);
                onMenuHolderEventListener.afterItemClick(v);
            }
        });

        mainView.addMenuItem(context.getString(R.string.menu_camera), context.getResources().getDrawable(R.drawable.menu_camera), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuHolderEventListener.beforeItemPerform(v);
                if (EasyPermissions.hasPermissions(context, Manifest.permission.CAMERA)){
                    Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                    cameraIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    context.startActivity(cameraIntent);
                }else {
                    Toast.makeText(context,context.getResources().getString(R.string.msg_without_camera_permission),Toast.LENGTH_SHORT).show();
                }
                onMenuHolderEventListener.afterItemClick(v);
            }
        });

        mainView.addMenuItem(context.getString(R.string.menu_screenshot), context.getResources().getDrawable(R.drawable.menu_cut_enable), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuHolderEventListener.beforeItemPerform(v);
                onMenuHolderEventListener.afterItemClick(v);
            }
        });

        mainView.addMenuItem(context.getString(R.string.menu_bluetooth), context.getResources().getDrawable(R.drawable.menu_blutooth_off), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuHolderEventListener.beforeItemPerform(v);
                onMenuHolderEventListener.afterItemClick(v);
            }
        });

        mainView.addMenuItem(context.getString(R.string.menu_wifi), context.getResources().getDrawable(R.drawable.menu_wifi_off), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuHolderEventListener.beforeItemPerform(v);
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager.isWifiEnabled()){
                    wifiManager.setWifiEnabled(false);
                }else{
                    wifiManager.setWifiEnabled(true);
                }
                onMenuHolderEventListener.afterItemClick(v);
            }
        });

        mainView.addMenuItem(context.getString(R.string.menu_search), context.getResources().getDrawable(R.drawable.menu_browser), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuHolderEventListener.beforeItemPerform(v);
                Log.d("MainActivity","btnSearch click");
                Intent browserIntent = new Intent();
                browserIntent.setAction("android.intent.action.VIEW");
                Uri url = Uri.parse("http://www.baidu.com");
                browserIntent.setData(url);
                browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(browserIntent);
                onMenuHolderEventListener.afterItemClick(v);
            }
        });

        mainView.addMenuItem(context.getString(R.string.menu_light), context.getResources().getDrawable(R.drawable.menu_flashlight_off), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuHolderEventListener.beforeItemPerform(v);
                Log.d("MainActivity","btnLight click");
                if (ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                    Toast.makeText(context,context.getString(R.string.msg_without_camera_permission),Toast.LENGTH_SHORT).show();
                }else cameraUtil.turnOnLight();
//                if (cameraUtil.getOpenCamera())
//                    btnLight.setBackgroundResource(R.drawable.menu_flashlight_on);
//                else btnLight.setBackgroundResource(R.drawable.menu_flashlight_off);
                onMenuHolderEventListener.afterItemClick(v);
            }
        });

        mainView.addMenuItem(context.getString(R.string.menu_home), context.getResources().getDrawable(R.drawable.menu_home), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuHolderEventListener.beforeItemPerform(v);
                Log.d("MainActivity","btnHome click");
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(homeIntent);
                onMenuHolderEventListener.afterItemClick(v);
            }
        });
    }

    private void initReceiver(){
        IntentFilter customFilter = new IntentFilter();
        customFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        customFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        customFilter.addAction(Constants.ACTIVATE_SCREENSHOT);
        context.registerReceiver(customReceiver,customFilter);
    }

    public void updateMenuIcons(){
        //检测wifi
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED){
            mainView.findViewByTitle(context.getString(R.string.menu_wifi)).
                    getImageView().setImageResource(R.drawable.menu_wifi_on);
        }else {
            mainView.findViewByTitle(context.getString(R.string.menu_wifi)).
                    getImageView().setImageResource(R.drawable.menu_wifi_off);
        }
        //检测蓝牙
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            if (bluetoothManager.getAdapter() == null)return;
            if (bluetoothManager.getAdapter().isEnabled()){
                mainView.findViewByTitle(context.getString(R.string.menu_bluetooth)).
                        getImageView().setImageResource(R.drawable.menu_blutooth_on);
            }else {
                mainView.findViewByTitle(context.getString(R.string.menu_bluetooth)).
                        getImageView().setImageResource(R.drawable.menu_blutooth_off);
            }
        }

        //检测手电筒
//        if (cameraUtil.getOpenCamera())
//            btnLight.setBackgroundResource(R.drawable.menu_flashlight_on);
//        else btnLight.setBackgroundResource(R.drawable.menu_flashlight_off);


    }

    public View getMainView() {
        return mainView.getMainView();
    }

    public void setOnMenuHolderEventListener(OnMenuHolderEventListener onMenuHolderEventListener) {
        this.onMenuHolderEventListener = onMenuHolderEventListener;
        mainView.setOnMenuHolderEventListener(onMenuHolderEventListener);
    }

    @Override
    public void beforeItemPerform(View view) {

    }

    @Override
    public void afterItemClick(View view) {

    }
}
