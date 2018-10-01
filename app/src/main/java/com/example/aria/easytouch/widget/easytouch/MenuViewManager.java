package com.example.aria.easytouch.widget.easytouch;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.iview.OnMenuItemEventListener;
import com.example.aria.easytouch.model.MenuPageModel;
import com.example.aria.easytouch.model.NewMenuItem;
import com.example.aria.easytouch.widget.easytouch.menu.MenuView;
import com.example.aria.easytouch.widget.easytouch.screenshot.OnScreenshotEventListener;
import com.luzeping.aria.commonutils.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aria on 2017/7/21.
 */

public class MenuViewManager implements OnMenuItemEventListener{

    private static final String TAG = "MenuViewManager";

    private Context context;
    private MenuView menuView;
    /**
     * 新增的
     *
     */
    private List<MenuPageModel> pageModelList = new ArrayList<>();

    private BroadcastReceiver receiver;

    public MenuViewManager(Context context){
        this.context = context;
        menuView = new MenuView(context);
        initReceiver();
        initData();
    }

    private void initData(){

        /**
         * 新增
         */

        //TODO 加载pageModel
        pageModelList.clear();
        initFeaturesItem();
        menuView.init(pageModelList);
//        menuView.post(new Runnable() {
//            @Override
//            public void run() {
//                updateFeatureIcons();
//            }
//        });

    }

    private void initReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null && !StringUtils.isEmpty(intent.getAction())) {
                    switch (intent.getAction()) {
                        case BluetoothAdapter.ACTION_STATE_CHANGED:
                            int extra = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,0);
                            switch (extra) {
                                case BluetoothAdapter.STATE_ON:
                                    menuView.updateIconByIconId(R.drawable.ic_bluetooth_off,R.drawable.ic_bluetooth_on);
                                    break;
                                case BluetoothAdapter.STATE_OFF:
                                    menuView.updateIconByIconId(R.drawable.ic_bluetooth_on,R.drawable.ic_bluetooth_off);
                                    break;
                            }
                    }
                }

            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        context.registerReceiver(receiver,filter);
    }

    /**
     * 主题、手电筒、相机、截屏、主屏、加速、WIFI、蓝牙
     */
    private int[] featureIcons = new int[] {R.drawable.ic_activity_themeshop_title,
            R.drawable.ic_flashlight_off,
            R.drawable.ic_camera,
            R.drawable.ic_screenshot,
            R.drawable.ic_home,
            R.drawable.ic_clean_memory,
            R.drawable.ic_wifi_off,
            R.drawable.ic_bluetooth_off};

    /**
     * 初始化默认的功能item
     */
    private void initFeaturesItem() {
       String[] featureIconTitles = context.getResources().getStringArray(R.array.array_feature_icons);

       for (int page = 0; page <= featureIconTitles.length / 9; page ++) {
           MenuPageModel pageModel = new MenuPageModel();
           pageModel.menuItems = new ArrayList<>();
           for (int i = 0; i < featureIconTitles.length; i ++) {
               NewMenuItem item = new NewMenuItem(featureIconTitles[i],featureIcons[i],NewMenuItem.TYPE_FEATURES);
               pageModel.menuItems.add(item);
           }
           pageModelList.add(pageModel);
       }

    }

    public void loadItems(){

    }

    public void saveItems(){
    }

    public void setOnScreenshotEventListener(OnScreenshotEventListener onScreenshotEventListener){
    }

    public void onDestroy(){
        if (receiver != null) context.unregisterReceiver(receiver);
    }

    public View getMenuView() {
        return menuView;
    }

    public void setOnMenuItemEventListener(OnMenuItemEventListener listener) {
        menuView.setOnMenuItemEventListener(listener);
    }

    private void handleFeaturesItemClickAction(int iconId) {
        switch (iconId) {
            case R.drawable.ic_home:
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(homeIntent);
                break;
            case R.drawable.ic_camera:
                //TODO 先试试这样调不调的起来
                Intent cameraIntent = new Intent();
                cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(cameraIntent);
                break;
            case R.drawable.ic_bluetooth_off:
            case R.drawable.ic_bluetooth_on:
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
                break;

        }
    }

    private int handleFeaturesItemAfterClickAction(int iconId) {
        switch (iconId) {
            case R.drawable.ic_bluetooth_off:
            case R.drawable.ic_bluetooth_on:
                return ACTION_KEEP_MENU;
        }
        return ACTION_CLOSE_MENU;
    }

    private int handleFeaturesItemBeforeClickAction(int iconId) {
        switch (iconId) {
            case R.drawable.ic_bluetooth_off:
            case R.drawable.ic_bluetooth_on:
            case R.drawable.ic_home:
            case R.drawable.ic_camera:
                return ACTION_KEEP_MENU;
        }
        return ACTION_KEEP_MENU;
    }

    private void updateFeatureIcons() {
        BluetoothAdapter bluetoothAdapter = getBltAdapter();
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled()) {
                menuView.updateIconByIconId(R.drawable.ic_bluetooth_off,R.drawable.ic_bluetooth_on);
            } else  {
                menuView.updateIconByIconId(R.drawable.ic_bluetooth_on,R.drawable.ic_bluetooth_off);
            }
        }

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                menuView.updateIconByIconId(R.drawable.ic_wifi_off,R.drawable.ic_wifi_on);
            }else {
                menuView.updateIconByIconId(R.drawable.ic_wifi_on,R.drawable.ic_wifi_off);
            }
        }
    }

    @Override
    public int beforeItemPerform(View view, int page, int pos) {
        if (!isPageAndPosValid(page, pos)) return OnMenuItemEventListener.ACTION_KEEP_MENU;
        if (pos >= pageModelList.get(page).menuItems.size()) return ACTION_KEEP_MENU;
        MenuPageModel pageModel = pageModelList.get(page);
        if (pageModel == null) return -1;
        if (pos < pageModel.menuItems.size()) {
            NewMenuItem menuItem = pageModel.menuItems.get(pos);
            switch (menuItem.getType()) {
                case NewMenuItem.TYPE_FEATURES:
                    return handleFeaturesItemBeforeClickAction(menuItem.getIconId());
            }
        }
        return OnMenuItemEventListener.ACTION_KEEP_MENU;
    }

    @Override
    public void onItemClick(View view, int page, int pos) {
        if (!isPageAndPosValid(page, pos)) return;
        MenuPageModel pageModel = pageModelList.get(page);
        if (pageModel == null) return;
        if (pos < pageModel.menuItems.size()) {
            NewMenuItem menuItem = pageModel.menuItems.get(pos);
            switch (menuItem.getType()) {
                case NewMenuItem.TYPE_FEATURES:
                    handleFeaturesItemClickAction(menuItem.getIconId());
            }
        }
    }

    @Override
    public int afterItemClick(View view, int page, int pos) {
        if (isPageAndPosValid(page, pos)) {
            if (pos >= pageModelList.get(page).menuItems.size()) return ACTION_KEEP_MENU;
           NewMenuItem item = pageModelList.get(page).menuItems.get(pos);
           switch (item.getType()) {
               case NewMenuItem.TYPE_FEATURES:
                   return handleFeaturesItemAfterClickAction(item.getIconId());
           }
        }
        return ACTION_CLOSE_MENU;
    }

    @Override
    public void onEmptyItemClick(View view, int page, int pos) {

    }

    @Override
    public void onDeleteItemClick(View view, int page, int pos) {

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

    private boolean isPageAndPosValid(int page,int pos) {
        return !(page < 0 || page >= pageModelList.size() || pos < 0 || pos >= 9);
    }
}


