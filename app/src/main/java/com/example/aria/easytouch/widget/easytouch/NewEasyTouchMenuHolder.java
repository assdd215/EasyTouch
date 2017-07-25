package com.example.aria.easytouch.widget.easytouch;

import android.Manifest;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.aria.easytouch.R;
import com.example.aria.easytouch.activity.MyActivity;
import com.example.aria.easytouch.activity.PreScreenshotActivity;
import com.example.aria.easytouch.util.Constants;
import com.example.aria.easytouch.widget.easytouch.screenshot.OldScreenShotUtilImpl;
import com.example.aria.easytouch.util.Utils;
import com.example.aria.easytouch.widget.easytouch.camera.Camera2Impl;
import com.example.aria.easytouch.widget.easytouch.camera.CameraImpl;
import com.example.aria.easytouch.widget.easytouch.camera.LightCamera;
import com.example.aria.easytouch.widget.easytouch.menu.MainView;
import com.example.aria.easytouch.widget.easytouch.screenshot.OnScreenshotEventListener;
import com.example.aria.easytouch.widget.easytouch.screenshot.SaveTask;
import com.example.aria.easytouch.widget.easytouch.screenshot.NewScreenShotUtilImpl;
import com.example.aria.easytouch.widget.easytouch.screenshot.ScreenShotUtil;

import java.io.File;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Aria on 2017/7/21.
 */

public class NewEasyTouchMenuHolder implements OnMenuHolderEventListener{
    private Context context;
    private MainView mainView;
    private OnMenuHolderEventListener onMenuHolderEventListener;
    private OnScreenshotEventListener onScreenshotEventListener;
    private LightCamera cameraUtil;
    private ScreenShotUtil screenShotUtil;

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
        }
    };

    public NewEasyTouchMenuHolder(Context context){
        this.context = context;
        mainView = new MainView(context);
        initData();
        initMenuItems();
        updateMenuIcons();
        initReceiver();
    }


    private void initData(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            cameraUtil = new Camera2Impl(context);
        else cameraUtil = new CameraImpl(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            screenShotUtil = new NewScreenShotUtilImpl(context);
        else screenShotUtil = OldScreenShotUtilImpl.getInstance(context);
    }

    private void initMenuItems(){
        mainView.addMenuItem(context.getString(R.string.menu_tel), context.getResources().getDrawable(R.drawable.menu_tel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    if (ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                        Toast.makeText(context,context.getResources().getString(R.string.msg_without_camera_permission),Toast.LENGTH_SHORT).show();
                    }
                Intent cameraIntent = new Intent();
                cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(cameraIntent);
                onMenuHolderEventListener.afterItemClick(v);
            }
        });

        mainView.addMenuItem(context.getString(R.string.menu_screenshot), context.getResources().getDrawable(R.drawable.menu_cut_enable), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    Toast.makeText(context,context.getResources().getString(R.string.msg_without_write_file_permission),Toast.LENGTH_SHORT).show();
                    onMenuHolderEventListener.afterItemClick(v);
                    return;
                }
                onMenuHolderEventListener.beforeItemPerform(v);
                screenShotUtil.startScreenshot();
            }
        });

        mainView.addMenuItem(context.getString(R.string.menu_bluetooth), context.getResources().getDrawable(R.drawable.menu_blutooth_off), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuHolderEventListener.beforeItemPerform(v);
                BluetoothAdapter bluetoothAdapter = getBltAdapter();
                if (bluetoothAdapter == null){
                    Toast.makeText(context,context.getString(R.string.msg_unsupport_blt),Toast.LENGTH_SHORT).show();
                    return;
                }
                if (bluetoothAdapter.isEnabled()){
                    bluetoothAdapter.disable();
                }else {
                    bluetoothAdapter.enable();
                }
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
                if (ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                    Toast.makeText(context,context.getString(R.string.msg_without_camera_permission),Toast.LENGTH_SHORT).show();
                }else cameraUtil.turnOnLight();
                if (cameraUtil.getOpenCamera())
                    mainView.findViewByTitle(context.getString(R.string.menu_light)).
                            getImageView().setImageResource(R.drawable.menu_flashlight_on);
                else mainView.findViewByTitle(context.getString(R.string.menu_light)).getImageView()
                        .setImageResource(R.drawable.menu_flashlight_off);
                onMenuHolderEventListener.afterItemClick(v);
            }
        });

        mainView.addMenuItem(context.getString(R.string.menu_home), context.getResources().getDrawable(R.drawable.menu_home), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuHolderEventListener.beforeItemPerform(v);
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
//        customFilter.addAction();
        context.registerReceiver(customReceiver,customFilter);
    }

    public void setOnScreenshotEventListener(OnScreenshotEventListener onScreenshotEventListener){
        screenShotUtil.setOnScreenshotEventListener(onScreenshotEventListener);
    }

    public void onDestory(){
        context.unregisterReceiver(customReceiver);
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
        BluetoothAdapter bluetoothAdapter = getBltAdapter();
        if (bluetoothAdapter == null)return;
        if (bluetoothAdapter.isEnabled()){
            mainView.findViewByTitle(context.getString(R.string.menu_bluetooth)).
                    getImageView().setImageResource(R.drawable.menu_blutooth_on);
        }else {
            mainView.findViewByTitle(context.getString(R.string.menu_bluetooth)).
                    getImageView().setImageResource(R.drawable.menu_blutooth_off);
        }

        //检测手电筒
        if (cameraUtil.getOpenCamera())
            mainView.findViewByTitle(context.getString(R.string.menu_light)).
                    getImageView().setImageResource(R.drawable.menu_flashlight_on);
        else mainView.findViewByTitle(context.getString(R.string.menu_light)).
        getImageView().setImageResource(R.drawable.menu_flashlight_off);


    }

    private void clearMemory(){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infoList = manager.getRunningAppProcesses();
        List<ActivityManager.RunningServiceInfo> serviceInfos = manager.getRunningServices(100);
        long beforeMem = getAvaliMemory(context);
        Log.d("MainActivity","before:"+beforeMem);
        int count = 0;
        if (infoList != null){
            for (int i=0;i<infoList.size();i++){
                ActivityManager.RunningAppProcessInfo appProcessInfo = infoList.get(i);
                if (appProcessInfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE){
                    String[] pkgList = appProcessInfo.pkgList;
                    for (int j = 0;j<pkgList.length;j++){
                        manager.killBackgroundProcesses(pkgList[j]);
                        count++;
                    }
                }
            }
        }

        Log.d("MainActivity","after:"+getAvaliMemory(context));
    }

    private long getAvaliMemory(Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(info);
        return info.availMem;
    }

    private BluetoothAdapter getBltAdapter(){
        BluetoothAdapter bluetoothAdapter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter =bluetoothManager.getAdapter();
        }else
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter;
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
