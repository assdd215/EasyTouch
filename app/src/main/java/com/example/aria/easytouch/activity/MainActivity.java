package com.example.aria.easytouch.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.aria.easytouch.R;
import com.example.aria.easytouch.base.EasyTouchApplication;
import com.example.aria.easytouch.service.EasyTouchService;
import com.example.aria.easytouch.util.Constants;
import com.example.aria.easytouch.widget.easytouch.ScreenShotUtil;
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
        setTitle("HOME");
        initView();
        initListener();


    }

    private void initView(){
        EasyTouchApplication application = (EasyTouchApplication) getApplication();
        if (application.getOpenFloatingWindow())switchView.setOn(true);
        if (application.getOpenScreenShot())screenShotView.setOn(true);
        initToolbar();

    }

    private void initToolbar(){
//        toolbar.setTitle("Home");
        toolbar.inflateMenu(R.menu.setting_menu);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_commend:
                        Toast.makeText(MainActivity.this,"commend is click",Toast.LENGTH_SHORT).show();
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
                    ((EasyTouchApplication)getApplication()).setOpenFloatingWindow(false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        ScreenShotUtil.setData(null);
                    ((EasyTouchApplication)getApplication()).setOpenScreenShot(false);
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
                    if (!((EasyTouchApplication)getApplication()).getOpenFloatingWindow()){
                        ((EasyTouchApplication)getApplication()).setOpenScreenShot(false);
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.msg_request_openFloatWindow),Toast.LENGTH_SHORT).show();
                        screenShotView.post(new Runnable() {
                            @Override
                            public void run() {
                                screenShotView.setOn(false,true);
                            }
                        });
                    } else {
                        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                            if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        REQUEST_WRITE_WRITE_EXTERNAL_STORAGE);
                            }
                        }
                        requestCapturePermission();
                    }

                }else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        ScreenShotUtil.setData(null);
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
                ((EasyTouchApplication)getApplication()).setOpenFloatingWindow(true);

    }

    public boolean checkFloatWindowPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //23后的悬浮窗权限属于危险权限 需要手动开启
            if(!Settings.canDrawOverlays(this)) {
                showAppSettingDialog();
                return false;
            }
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
                        ScreenShotUtil.setData(data);
                        ((EasyTouchApplication)getApplication()).setOpenScreenShot(true);
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
