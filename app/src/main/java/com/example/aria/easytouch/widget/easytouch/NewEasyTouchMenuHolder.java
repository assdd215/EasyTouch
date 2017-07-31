package com.example.aria.easytouch.widget.easytouch;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.aria.easytouch.R;
import com.example.aria.easytouch.model.FloatMenuItem;
import com.example.aria.easytouch.util.Constants;
import com.example.aria.easytouch.widget.easytouch.boost.BoostUtil;
import com.example.aria.easytouch.widget.easytouch.boost.MemoryBoostUtil;
import com.example.aria.easytouch.widget.easytouch.menu.ItemModel;
import com.example.aria.easytouch.widget.easytouch.screenshot.FileUtil;
import com.example.aria.easytouch.widget.easytouch.screenshot.OldScreenShotUtilImpl;
import com.example.aria.easytouch.widget.easytouch.camera.Camera2Impl;
import com.example.aria.easytouch.widget.easytouch.camera.CameraImpl;
import com.example.aria.easytouch.widget.easytouch.camera.LightCamera;
import com.example.aria.easytouch.widget.easytouch.menu.MainView;
import com.example.aria.easytouch.widget.easytouch.screenshot.OnScreenshotEventListener;
import com.example.aria.easytouch.widget.easytouch.screenshot.NewScreenShotUtilImpl;
import com.example.aria.easytouch.widget.easytouch.screenshot.ScreenShotUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aria on 2017/7/21.
 */

public class NewEasyTouchMenuHolder implements OnMenuHolderEventListener{
    private Context context;
    private MainView mainView;
    private OnMenuHolderEventListener onMenuHolderEventListener;
    private LightCamera cameraUtil;
    private ScreenShotUtil screenShotUtil;
    private BoostUtil boostUtil;

    private List<ResolveInfo> resolveInfos;
    private PackageManager packageManager;
    private Gson gson;

    private boolean refresh = true;
//    private AsyncTask refreshInfoListTask = new AsyncTask() {
//        @Override
//        protected Object doInBackground(Object[] params) {
//
//            while (refresh){
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                packageManager = context.getPackageManager();
//                Intent intent = new Intent(Intent.ACTION_MAIN,null);
//                intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                resolveInfos = packageManager.queryIntentActivities(intent,0);
//                Log.d("Counting","refresh InfoList");
//            }
//            return null;
//        }
//    };


    private BroadcastReceiver customReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,0)){
                    case BluetoothAdapter.STATE_ON:
                        if (mainView.findViewByTitle(context.getString(R.string.menu_bluetooth))!= null)
                        mainView.findViewByTitle(context.getString(R.string.menu_bluetooth)).
                                getImageView().setImageResource(R.drawable.menu_blutooth_on);
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        if (mainView.findViewByTitle(context.getString(R.string.menu_bluetooth))!= null)
                        mainView.findViewByTitle(context.getString(R.string.menu_bluetooth))
                                .getImageView().setImageResource(R.drawable.menu_blutooth_off);
                        break;
                }
            }

            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){
                switch (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,0)){
                    case WifiManager.WIFI_STATE_DISABLED:
                        if (mainView.findViewByTitle(context.getString(R.string.menu_wifi))!= null)
                        mainView.findViewByTitle(context.getString(R.string.menu_wifi))
                                .getImageView().setImageResource(R.drawable.menu_wifi_off);
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        if (mainView.findViewByTitle(context.getString(R.string.menu_wifi))!= null)
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
//        initMenuItems();
        loadItems();
//        updateMenuIcons();
        initReceiver();
    }

    private void initData(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            cameraUtil = new Camera2Impl(context);
        else cameraUtil = new CameraImpl(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            screenShotUtil = new NewScreenShotUtilImpl(context);
        else screenShotUtil = OldScreenShotUtilImpl.getInstance(context);

        boostUtil = new MemoryBoostUtil(context);

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARE_DATA,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.apply();
        gson = new Gson();

//        AsyncTaskCompat.executeParallel(refreshInfoListTask);
    }

    private List<FloatMenuItem> initMenuItems(){

        List<FloatMenuItem> modelList = new ArrayList<>(10);

        FloatMenuItem model;

        model = new FloatMenuItem();
        model.setIconId(R.drawable.menu_tel);
        model.setType(ItemModel.TYPE_FUNCTION);
        model.setItemTitleId(R.string.menu_tel);
        modelList.add(model);
        mainView.addMenuItem(context.getString(R.string.menu_tel), context.getResources().getDrawable(R.drawable.menu_tel), functionItemListener);

        model = new FloatMenuItem();
        model.setIconId(R.drawable.menu_message);
        model.setType(ItemModel.TYPE_FUNCTION);
        model.setItemTitleId(R.string.menu_message);
        modelList.add(model);
        mainView.addMenuItem(context.getString(R.string.menu_message), context.getResources().getDrawable(R.drawable.menu_message), functionItemListener);

        model = new FloatMenuItem();
        model.setIconId(R.drawable.menu_camera);
        model.setType(ItemModel.TYPE_FUNCTION);
        model.setItemTitleId(R.string.menu_camera);
        modelList.add(model);
        mainView.addMenuItem(context.getString(R.string.menu_camera), context.getResources().getDrawable(R.drawable.menu_camera), functionItemListener);


        if (screenShotUtil.isSupportScreenshot()){
            model = new FloatMenuItem();
            model.setIconId(R.drawable.menu_cut_enable);
            model.setType(ItemModel.TYPE_FUNCTION);
            model.setItemTitleId(R.string.menu_screenshot);
            modelList.add(model);
            mainView.addMenuItem(context.getString(R.string.menu_screenshot), context.getResources().getDrawable(R.drawable.menu_cut_enable), functionItemListener);
        }

        model = new FloatMenuItem();
        model.setIconId(R.drawable.menu_blutooth_off);
        model.setType(ItemModel.TYPE_FUNCTION);
        model.setItemTitleId(R.string.menu_bluetooth);
        modelList.add(model);
        mainView.addMenuItem(context.getString(R.string.menu_bluetooth), context.getResources().getDrawable(R.drawable.menu_blutooth_off), functionItemListener);

        model = new FloatMenuItem();
        model.setIconId(R.drawable.menu_wifi_off);
        model.setType(ItemModel.TYPE_FUNCTION);
        model.setItemTitleId(R.string.menu_wifi);
        modelList.add(model);
        mainView.addMenuItem(context.getString(R.string.menu_wifi), context.getResources().getDrawable(R.drawable.menu_wifi_off), functionItemListener);

        model = new FloatMenuItem();
        model.setIconId(R.drawable.menu_browser);
        model.setType(ItemModel.TYPE_FUNCTION);
        model.setItemTitleId(R.string.menu_search);
        modelList.add(model);
        mainView.addMenuItem(context.getString(R.string.menu_search), context.getResources().getDrawable(R.drawable.menu_browser), functionItemListener);

        if (cameraUtil.isSupportFlash()){
            model = new FloatMenuItem();
            model.setIconId(R.drawable.menu_flashlight_off);
            model.setType(ItemModel.TYPE_FUNCTION);
            model.setItemTitleId(R.string.menu_light);
            modelList.add(model);
            mainView.addMenuItem(context.getString(R.string.menu_light), context.getResources().getDrawable(R.drawable.menu_flashlight_off), functionItemListener);
        }

        model = new FloatMenuItem();
        model.setIconId(R.drawable.menu_home);
        model.setType(ItemModel.TYPE_FUNCTION);
        model.setItemTitleId(R.string.menu_home);
        modelList.add(model);
        mainView.addMenuItem(context.getString(R.string.menu_home), context.getResources().getDrawable(R.drawable.menu_home), functionItemListener);

        model = new FloatMenuItem();
        model.setIconId(R.drawable.menu_boost);
        model.setType(ItemModel.TYPE_FUNCTION);
        model.setItemTitleId(R.string.menu_boost);
        modelList.add(model);
        mainView.addMenuItem(context.getString(R.string.menu_boost), context.getResources().getDrawable(R.drawable.menu_boost), functionItemListener);

//        addCommonlyApps();
//        initCommonlyApps(modelList);
        return modelList;
    }

    public void loadItems(){
        packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN,null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveInfos = packageManager.queryIntentActivities(intent,0);


        String filePath = FileUtil.getItemJsonFileName(context);
        File file = new File(filePath);
        FileReader reader = null;

        try {
            if (!file.exists()){
                if (!file.getParentFile().exists())file.getParentFile().mkdirs();
                file.createNewFile();
                List<FloatMenuItem> modelList = initMenuItems();
                Gson gson = new Gson();
                FileWriter writer = new FileWriter(file,false);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.write(gson.toJson(modelList));
                bufferedWriter.close();
                writer.close();
            }else {
                reader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(reader);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = bufferedReader.readLine())!=null){
                    builder.append(line);
                }
                bufferedReader.close();
                reader.close();
                List<FloatMenuItem> modelList =  gson.fromJson(builder.toString(),new TypeToken<ArrayList<FloatMenuItem>>(){}.getType());
                setMenuItems(modelList);

            }
        }catch (Exception e){e.printStackTrace();}

        updateMenuIcons();
    }

    private void setMenuItems(List<FloatMenuItem> modelList){
        for (FloatMenuItem item:modelList){
            switch (item.getType()){

                case FloatMenuItem.TYPE_FUNCTION:
                    mainView.addMenuItem(context.getString(item.getItemTitleId()),context.getResources().getDrawable(item.getIconId()),functionItemListener);
                    break;
                case FloatMenuItem.TYPE_SHORTCUT:
                    for (ResolveInfo info: resolveInfos){
                        if (info.activityInfo.packageName.equals(item.getPackageName())){
                            addCommonlyApp(info);
                            break;
                        }
                    }
                    break;
                case FloatMenuItem.TYPE_EMPTY:
                    break;
                default:
                    break;
            }

        }
    }

    private void initCommonlyApps(List<FloatMenuItem> modelList){
        String[] commonApps = context.getResources().getStringArray(R.array.commonly_used_apps);
        Log.d("MainActivity","commonApps:"+commonApps.length+"    "+commonApps.toString());
        for (ResolveInfo info: resolveInfos){
            for (String app: commonApps){
                if (info.activityInfo.packageName.equals(app)){
                    FloatMenuItem item = new FloatMenuItem();
                    item.setType(FloatMenuItem.TYPE_SHORTCUT);
                    item.setPackageName(app);
                    modelList.add(item);
                    addCommonlyApp(info);
                }
            }
        }

    }

    private void addCommonlyApp(ResolveInfo info){
        final String packageName = info.activityInfo.packageName;
        final String lauchName = info.activityInfo.name;
        mainView.addMenuItem(info.loadLabel(packageManager).toString(), info.loadIcon(packageManager), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuHolderEventListener.beforeItemPerform(v);
                ComponentName componentName = new ComponentName(packageName,lauchName);
                Intent intent1 = new Intent(Intent.ACTION_MAIN);
                intent1.addCategory(Intent.CATEGORY_LAUNCHER);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent1.setComponent(componentName);
                onMenuHolderEventListener.afterItemClick(v);
                context.startActivity(intent1);
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

    public void setOnScreenshotEventListener(OnScreenshotEventListener onScreenshotEventListener){
        screenShotUtil.setOnScreenshotEventListener(onScreenshotEventListener);
    }

    public void onDestory(){
        context.unregisterReceiver(customReceiver);
    }

    public void updateMenuIcons(){
        //检测wifi

        if (mainView.findViewByTitle(context.getString(R.string.menu_wifi)) != null){
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED){
                mainView.findViewByTitle(context.getString(R.string.menu_wifi)).
                        getImageView().setImageResource(R.drawable.menu_wifi_on);
            }else {
                mainView.findViewByTitle(context.getString(R.string.menu_wifi)).
                        getImageView().setImageResource(R.drawable.menu_wifi_off);
            }
        }

        //检测蓝牙
        if (mainView.findViewByTitle(context.getString(R.string.menu_bluetooth))!= null){
            BluetoothAdapter bluetoothAdapter = getBltAdapter();
            if (bluetoothAdapter == null)return;
            if (bluetoothAdapter.isEnabled()){
                mainView.findViewByTitle(context.getString(R.string.menu_bluetooth)).
                        getImageView().setImageResource(R.drawable.menu_blutooth_on);
            }else {
                mainView.findViewByTitle(context.getString(R.string.menu_bluetooth)).
                        getImageView().setImageResource(R.drawable.menu_blutooth_off);
            }
        }

        if (mainView.findViewByTitle(context.getString(R.string.menu_light)) != null){
            //检测手电筒
            if (cameraUtil.getOpenCamera())
                mainView.findViewByTitle(context.getString(R.string.menu_light)).
                        getImageView().setImageResource(R.drawable.menu_flashlight_on);
            else mainView.findViewByTitle(context.getString(R.string.menu_light)).
                    getImageView().setImageResource(R.drawable.menu_flashlight_off);
        }



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

    private View.OnClickListener functionItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String tag = (String) v.getTag();
            if (tag.equals(context.getString(R.string.menu_tel))){
                onTelClick(v);

            }else if (tag.equals(context.getString(R.string.menu_message))){
                onMessageClick(v);

            }else if (tag.equals(context.getString(R.string.menu_camera))){
                onCameraClick(v);

            }else if (tag.equals(context.getString(R.string.menu_screenshot))){
                onScreenshotClick(v);

            }else if (tag.equals(context.getString(R.string.menu_bluetooth))){
                onBltClick(v);

            }else if (tag.equals(context.getString(R.string.menu_wifi))){
                onWifiClick(v);

            }else if (tag.equals(context.getString(R.string.menu_search))){
                onBrowserClick(v);

            }else if (tag.equals(context.getString(R.string.menu_light))){
                onFlashLightClick(v);

            }else if (tag.equals(context.getString(R.string.menu_home))){
                onHomeClick(v);

            }else if (tag.equals(context.getString(R.string.menu_boost))){
                onBoostClick(v);
            }
        }
    };


    private void onTelClick(View v){
        onMenuHolderEventListener.beforeItemPerform(v);
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(dialIntent);
        onMenuHolderEventListener.afterItemClick(v);
    }

    private void onMessageClick(View v){
        onMenuHolderEventListener.beforeItemPerform(v);
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.setData(Uri.parse("sms:"));
        smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(smsIntent);
        onMenuHolderEventListener.afterItemClick(v);
    }

    private void onCameraClick(View v){
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

    private void onScreenshotClick(View v){
        if (ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            Toast.makeText(context,context.getResources().getString(R.string.msg_without_write_file_permission),Toast.LENGTH_SHORT).show();
            onMenuHolderEventListener.afterItemClick(v);
            return;
        }
        onMenuHolderEventListener.beforeItemPerform(v);
        screenShotUtil.startScreenshot();
    }

    private void onBltClick(View v){
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

    private void onWifiClick(View v){
        onMenuHolderEventListener.beforeItemPerform(v);
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(false);
        }else{
            wifiManager.setWifiEnabled(true);
        }
        onMenuHolderEventListener.afterItemClick(v);
    }

    private void onBrowserClick(View v){
        onMenuHolderEventListener.beforeItemPerform(v);
        Intent browserIntent = new Intent();
        browserIntent.setAction("android.intent.action.VIEW");
        Uri url = Uri.parse("http://www.baidu.com");
        browserIntent.setData(url);
        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(browserIntent);
        onMenuHolderEventListener.afterItemClick(v);
    }

    private void onFlashLightClick(View v){
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

    private void onHomeClick(View v){
        onMenuHolderEventListener.beforeItemPerform(v);
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(homeIntent);
        onMenuHolderEventListener.afterItemClick(v);
    }

    private void onBoostClick(View v){
        onMenuHolderEventListener.beforeItemPerform(v);
        boostUtil.clearMemory();
        onMenuHolderEventListener.afterItemClick(v);
    }


}


