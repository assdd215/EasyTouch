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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.aria.easytouch.R;
import com.example.aria.easytouch.util.Constants;
import com.example.aria.easytouch.widget.easytouch.camera.Camera2Impl;
import com.example.aria.easytouch.widget.easytouch.camera.CameraImpl;
import com.example.aria.easytouch.widget.easytouch.camera.LightCamera;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Aria on 2017/7/17.
 */

public class EasyTouchMenuHolder implements ScreenShotUtil.OnScreenshotEventListener{

    private Context context;

    private View mainView;
    private View backGround;

    private ImageView btnTel;
    private ImageView btnMessage;
    private ImageView btnCamera;
    private ImageView btnScreenShot;
    private ImageView btnBluetooth;
    private ImageView btnWifi;
    private ImageView btnSearch;
    private ImageView btnLight;
    private ImageView btnHome;

    private LinearLayout ll_tel;
    private LinearLayout ll_message;
    private LinearLayout ll_camera;
    private LinearLayout ll_screenshot;
    private LinearLayout ll_blt;
    private LinearLayout ll_wifi;
    private LinearLayout ll_browser;
    private LinearLayout ll_light;
    private LinearLayout ll_home;

    private onHolderEventListener onHolderEventListener;
    private LightCamera cameraUtil;
    private ScreenShotUtil screenShotUtil;


    private BroadcastReceiver customReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,0)){
                    case BluetoothAdapter.STATE_ON:
                        btnBluetooth.setBackgroundResource(R.drawable.menu_blutooth_on);
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        btnBluetooth.setBackgroundResource(R.drawable.menu_blutooth_off);
                        break;
                }
            }

            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){
                switch (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,0)){
                    case WifiManager.WIFI_STATE_DISABLED:
                        btnWifi.setBackgroundResource(R.drawable.menu_wifi_off);
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        btnWifi.setBackgroundResource(R.drawable.menu_wifi_on);
                        break;
                }
            }

            if (intent.getAction().equals(Constants.ACTIVATE_SCREENSHOT)){
                btnScreenShot.setClickable(true);
                btnScreenShot.setBackgroundResource(R.drawable.menu_cut_enable);
            }
        }
    };

    public EasyTouchMenuHolder(Context context){
        this.context = context;
        initMenu();
        initReceiver();
    }



    private void initMenu(){
        mainView = LayoutInflater.from(context).inflate(R.layout.easy_touch_layout,null);
        backGround = mainView.findViewById(R.id.bg_view);
        btnTel = (ImageView) mainView.findViewById(R.id.btnTel);
        btnMessage = (ImageView) mainView.findViewById(R.id.btnMessage);
        btnCamera = (ImageView) mainView.findViewById(R.id.btnCamera);
        btnScreenShot = (ImageView) mainView.findViewById(R.id.btnScreenShot);
        btnBluetooth = (ImageView) mainView.findViewById(R.id.btnBluetooth);
        btnWifi = (ImageView) mainView.findViewById(R.id.btnWIFI);
        btnSearch = (ImageView) mainView.findViewById(R.id.btnSearch);
        btnLight = (ImageView) mainView.findViewById(R.id.btnLight);
        btnHome = (ImageView) mainView.findViewById(R.id.btnHome);

        ll_tel = (LinearLayout) mainView.findViewById(R.id.ll_tel);
        ll_message = (LinearLayout) mainView.findViewById(R.id.ll_message);
        ll_camera = (LinearLayout) mainView.findViewById(R.id.ll_camera);
        ll_screenshot = (LinearLayout) mainView.findViewById(R.id.ll_screenshot);
        ll_blt = (LinearLayout) mainView.findViewById(R.id.ll_blt);
        ll_wifi = (LinearLayout) mainView.findViewById(R.id.ll_wifi);
        ll_browser = (LinearLayout) mainView.findViewById(R.id.ll_browser);
        ll_light = (LinearLayout) mainView.findViewById(R.id.ll_light);
        ll_home = (LinearLayout) mainView.findViewById(R.id.ll_home);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            cameraUtil = new Camera2Impl(context);
        else cameraUtil = new CameraImpl(context);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            screenShotUtil = new ScreenShotUtil(context);

        initView();
        initListener();
    }

    private void initView(){
        updateMenuIcons();
    }

    private void initListener(){
        backGround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHolderEventListener.afterItemClick(v);
            }
        });

        ll_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity","btnTel click");
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(dialIntent);
                onHolderEventListener.afterItemClick(v);
            }
        });

        ll_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity","btnMessage click");
//                Intent messageIntent = new Intent(Intent.ACTION_VIEW);
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.setData(Uri.parse("sms:"));
                smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(smsIntent);
                onHolderEventListener.afterItemClick(v);
            }
        });

        ll_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(context, Manifest.permission.CAMERA)){
                    Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                    cameraIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    context.startActivity(cameraIntent);
                }else {
                    Toast.makeText(context,context.getResources().getString(R.string.msg_without_camera_permission),Toast.LENGTH_SHORT).show();
                }
                onHolderEventListener.afterItemClick(v);
            }
        });

        ll_blt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity","btnBluetooth click");
                openBlt();
                onHolderEventListener.afterItemClick(v);
            }
        });



        ll_screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity","btnScreenshot Click");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    if (ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        Toast.makeText(context,context.getResources().getString(R.string.msg_without_write_file_permission),Toast.LENGTH_SHORT).show();
                        onHolderEventListener.afterItemClick(v);
                        return;
                    }
                    onHolderEventListener.beforeItemPerform(v);
                    screenShotUtil.startScreenShot();
                }
                else {
                    Toast.makeText(context,context.getResources().getString(R.string.msg_version_too_old),Toast.LENGTH_SHORT).show();
                    onHolderEventListener.afterItemClick(v);
                }
            }
        });


        ll_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity","btnWifi click");
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager.isWifiEnabled()){
                    wifiManager.setWifiEnabled(false);
                }else{
                    wifiManager.setWifiEnabled(true);
                }
                onHolderEventListener.afterItemClick(v);
            }
        });

        ll_wifi.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("MainActivity","btnWifi click");
                Intent wifiIntent = new Intent("android.settings.WIFI_SETTINGS");
                wifiIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(wifiIntent);
                onHolderEventListener.afterItemClick(v);
                return true;
            }
        });

        ll_browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity","btnSearch click");
                Intent browserIntent = new Intent();
                browserIntent.setAction("android.intent.action.VIEW");
                Uri url = Uri.parse("http://www.baidu.com");
                browserIntent.setData(url);
                browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(browserIntent);
                onHolderEventListener.afterItemClick(v);
            }
        });

        ll_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity","btnLight click");
                if (ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                    Toast.makeText(context,context.getString(R.string.msg_without_camera_permission),Toast.LENGTH_SHORT).show();
                }else cameraUtil.turnOnLight();
                if (cameraUtil.getOpenCamera())
                    btnLight.setBackgroundResource(R.drawable.menu_flashlight_on);
                else btnLight.setBackgroundResource(R.drawable.menu_flashlight_off);
                onHolderEventListener.afterItemClick(v);
            }
        });

        ll_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity","btnHome click");
                onHolderEventListener.beforeItemPerform(v);
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(homeIntent);
                onHolderEventListener.afterItemClick(v);
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

    public void setOnHolderEventListener(onHolderEventListener onHolderEventListener) {
        this.onHolderEventListener = onHolderEventListener;
    }

    public View getMainView(){
        return mainView;
    }

    private void openBlt(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            BluetoothAdapter adapter =  ((BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
            if (adapter == null){
                Toast.makeText(context,context.getString(R.string.msg_unsupport_blt),Toast.LENGTH_SHORT).show();
                return;
            }
            if (adapter.isEnabled()){
                adapter.disable();
            }else {
                adapter.enable();
            }
        }
    }

    @Override
    public void beforeScreenshot() {

    }

    @Override
    public void onImageCaptured(Image image) {

    }

    @Override
    public void afterScreenshot() {

    }

    @Override
    public void onPostImageSaved(boolean succeed) {

    }

    public void setOnHolderScreenshotEventListener(ScreenShotUtil.OnScreenshotEventListener onHolderEventListener){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            screenShotUtil.setOnScreenshotEventListener(onHolderEventListener);
        }

    }


    public interface onHolderEventListener {
        void beforeItemPerform(View view);
        void afterItemClick(View view);
    }

    public void updateMenuIcons(){
        //检测wifi
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED){
            btnWifi.setBackgroundResource(R.drawable.menu_wifi_on);
        }else {
            btnWifi.setBackgroundResource(R.drawable.menu_wifi_off);
        }
        //检测蓝牙
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            if (bluetoothManager.getAdapter() == null)return;
            if (bluetoothManager.getAdapter().isEnabled()){
                btnBluetooth.setBackgroundResource(R.drawable.menu_blutooth_on);
            }else {
                btnBluetooth.setBackgroundResource(R.drawable.menu_blutooth_off);
            }
        }

        //检测手电筒
        if (cameraUtil.getOpenCamera())
            btnLight.setBackgroundResource(R.drawable.menu_flashlight_on);
        else btnLight.setBackgroundResource(R.drawable.menu_flashlight_off);


    }

    public void onDestory(){
        context.unregisterReceiver(customReceiver);
    }

}
