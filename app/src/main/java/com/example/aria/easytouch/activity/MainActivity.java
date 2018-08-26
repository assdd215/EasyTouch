package com.example.aria.easytouch.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.service.easytouch.EasyTouchService;
import com.example.aria.easytouch.ui.hint.HintActivity;
import com.example.aria.easytouch.util.Constants;
import com.example.aria.easytouch.util.Utils;
import com.example.aria.easytouch.widget.easytouch.screenshot.FileUtil;
import com.sevenheaven.iosswitch.ShSwitchView;


import java.io.File;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CAMERA_CODE = 1;
    private static final int REQUEST_PROJECTION_CODE = 2;
    private static final int REQUEST_WRITE_WRITE_EXTERNAL_STORAGE = 3;
    private static final int REQUEST_PERMISSIONS = 4;
    private static final int REQUEST_OPEN_ACCESSIBILITY = 5;
    private static final int MSG_HINT_ACTIVITY = 6;

    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission_group.LOCATION};

    @BindView(R.id.floatWindowSwitch)
    ShSwitchView switchView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        initView();
        initListener();
        myHandler.setContext(this);
        requestPermissions(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initData(){
    }

    private void initView(){

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constants.SHARE_DATA,MODE_PRIVATE);
        boolean stateFloatWindow = sharedPreferences.getBoolean(Constants.STATE_FLOATWINDOW,false);

        if (stateFloatWindow)switchView.setOn(true);
        initToolbar();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private boolean deleteItemInfo(){
        File file = new File(FileUtil.getItemJsonFileName(MainActivity.this));
        if (file.isFile() && file.exists())
            return file.delete();
        return false;
    }

    private void initToolbar(){
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.icon_more));
        toolbar.inflateMenu(R.menu.setting_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_reset:
                        if (deleteItemInfo()){
                            Toast.makeText(MainActivity.this,"删除成功！",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this,"删除失败！",Toast.LENGTH_SHORT).show();
                        }
                        break;

                }
                return false;
            }
        });

    }

    private void initListener(){
        switchView.setOnSwitchStateChangeListener(new ShSwitchView.OnSwitchStateChangeListener() {
            @Override
            public void onSwitchStateChange(boolean isOn) {
                if (isOn){
                    if (checkFloatWindowPermission()){
                        openFloatingWindow();
                    }else {
                        switchView.post(new Runnable() {
                            @Override
                            public void run() {
                                switchView.setOn(false,true);
                            }
                        });
                    }

                }else {
                    Intent intent = new Intent();
                    intent.setAction(Constants.STOP_SERVICE);
                    sendBroadcast(intent);
                }
            }
        });
    }

    private void openFloatingWindow(){
            if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA_CODE);
            }
                //开启悬浮球
                Intent intent = new Intent(MainActivity.this, EasyTouchService.class);
                startService(intent);

    }

    public boolean checkFloatWindowPermission(){
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //23后的悬浮窗权限属于危险权限 需要手动开启
            if(!Settings.canDrawOverlays(this)) {
                Log.d("MainActivity","!Settings.canDrawOverlays(this)");
                showAppSettingDialog();
                return false;
            }
        }else if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_DENIED){
            showAppSettingDialog();
            return false;
        }else {
            Log.d("MainActivity","Permission:"+(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_DENIED));
        }
        return true;
    }

    private void showAppSettingDialog(){
        Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
        startActivity(intent);
        myHandler.sendMessageDelayed(myHandler.obtainMessage(MSG_HINT_ACTIVITY,getString(R.string.hint_open_float_window)),200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_OPEN_ACCESSIBILITY:
                requestPermissions(true);
                break;
        }
    }

    private void requestPermissions(boolean fromActivityResult){

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,permissions,REQUEST_PERMISSIONS);
        }


    }

    //判断服务是否开启
    private boolean checkStealFeature(String service){
        int ok = 0;
        try {
            ok = Settings.Secure.getInt(getApplicationContext().getContentResolver(),Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        TextUtils.SimpleStringSplitter ms = new TextUtils.SimpleStringSplitter(':');
        if (ok == 1){
            String settingValue = Settings.Secure.getString(getApplicationContext().getContentResolver(),Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null){
                ms.setString(settingValue);
                while (ms.hasNext()){
                    String accessibilityService = ms.next();
                    Utils.log(TAG,accessibilityService);
                    if (accessibilityService.equalsIgnoreCase(service))
                        return true;
                }
            }
        }


        return false;
    }

    private static final MyHandler myHandler = new MyHandler();

    static class MyHandler extends Handler{

        WeakReference<Context> contextWeakReference = null;
        public void setContext(Context context){
            this.contextWeakReference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_HINT_ACTIVITY:
                    Log.d(TAG,"handler");
                    if (contextWeakReference != null){
                        Intent intent = new Intent(contextWeakReference.get(),HintActivity.class);
                        intent.putExtra(Constants.HINT_TEXT,msg.obj.toString());
                        contextWeakReference.get().startActivity(intent);
                    }
                    break;
            }
        }
    }
}
