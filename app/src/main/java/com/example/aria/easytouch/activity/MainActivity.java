package com.example.aria.easytouch.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.aria.easytouch.R;
import com.example.aria.easytouch.service.EasyTouchService;
import com.example.aria.easytouch.ui.setting.SettingActivity;
import com.example.aria.easytouch.util.Constants;
import com.example.aria.easytouch.util.Utils;
import com.example.aria.easytouch.widget.easytouch.screenshot.NewScreenShotUtilImpl;
import com.sevenheaven.iosswitch.ShSwitchView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{


    private static final int REQUEST_CAMERA_CODE = 1;
    private static final int REQUEST_PROJECTION_CODE = 2;
    private static final int REQUEST_WRITE_WRITE_EXTERNAL_STORAGE = 3;
    private AlertDialog dialog;

    private String[] screenshotPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @BindView(R.id.floatWindowSwitch)
    ShSwitchView switchView;
    @BindView(R.id.screenShotSwitch)
    ShSwitchView screenShotView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initListener();

        firstStart();
    }

    private void initView(){

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constants.SHARE_DATA,MODE_PRIVATE);
        boolean stateFloatWindow = sharedPreferences.getBoolean(Constants.STATE_FLOATWINDOW,false);
        boolean stateScreenshot = sharedPreferences.getBoolean(Constants.STATE_SCREENSHOT,false);

        if (stateFloatWindow)switchView.setOn(true);
        if (stateScreenshot)screenShotView.setOn(true);
        initToolbar();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_commend:
                Toast.makeText(MainActivity.this,"commend is click",Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_settings:
                Intent intent = new Intent(MainActivity.this,SettingActivity.class);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar(){
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.icon_more));
        setSupportActionBar(toolbar);

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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        NewScreenShotUtilImpl.setData(null);
                    screenShotView.post(new Runnable() {
                        @Override
                        public void run() {
                            screenShotView.setOn(false,true);
                        }
                    });

                }
            }
        });

        screenShotView.setOnSwitchStateChangeListener(new ShSwitchView.OnSwitchStateChangeListener() {
            @Override
            public void onSwitchStateChange(boolean isOn) {
                if (isOn){
                    SharedPreferences preferences = getApplicationContext().getSharedPreferences(Constants.SHARE_DATA,MODE_PRIVATE);
                    boolean stateFloatWindow = preferences.getBoolean(Constants.STATE_FLOATWINDOW,false);
                    if (!stateFloatWindow){
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.msg_request_openFloatWindow),Toast.LENGTH_SHORT).show();
                        screenShotView.post(new Runnable() {
                            @Override
                            public void run() {
                                screenShotView.setOn(false,true);
                            }
                        });
                    } else {
                        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_WRITE_WRITE_EXTERNAL_STORAGE);
                        }
                        requestCapturePermission();
                    }

                }else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        NewScreenShotUtilImpl.setData(null);
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constants.SHARE_DATA,MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(Constants.STATE_SCREENSHOT,false);
                    editor.apply();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
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

    private boolean requestCapturePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(),REQUEST_PROJECTION_CODE);
            return true;
        }
        return false;
    }

    private void firstStart(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARE_DATA,Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(Constants.FIRST_START,true)){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.FIRST_START,false);
            editor.apply();
            Utils.createDialog(this, getString(R.string.dialog_first_start_app_titile), getString(R.string.dialog_first_start_app_content),
                    getString(R.string.msg_open), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MainActivity.this,SettingActivity.class);
                            startActivity(intent);
                            dialog.cancel();
                            dialog.dismiss();
                        }
                    });
        }
    }

    private void showAppSettingDialog(){
        dialog = new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.dialog_request_open_floatwindow_permission_title))
                .setMessage(getResources().getString(R.string.dialog_request_open_floatwindow_pemission_content))
                .setPositiveButton(getResources().getString(R.string.msg_open), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                        startActivity(intent);
                    }
                }).setNegativeButton(getResources().getString(R.string.msg_close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_PROJECTION_CODE:
                if (resultCode == RESULT_OK && data != null){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        NewScreenShotUtilImpl.setData(data);
                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constants.SHARE_DATA,MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Constants.STATE_SCREENSHOT,true);
                        editor.apply();
                    }
                    else {
                        Toast.makeText(this,getResources().getString(R.string.msg_open_screenshot_fail),Toast.LENGTH_SHORT).show();
                        screenShotView.post(new Runnable() {
                            @Override
                            public void run() {
                                screenShotView.setOn(false,true);
                            }
                        });
                    }
                }
                break;
        }
    }


}
