package com.example.aria.easytouch.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.example.aria.easytouch.R;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Aria on 2017/7/24.
 */

public class MyActivity extends AppCompatActivity{

    @BindView(R.id.btnClearMemory)
    Button btnClearMemory;
    @BindView(R.id.layout)
    LinearLayout layout;
    @BindView(R.id.image)
    ImageView imageView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ButterKnife.bind(this);
        List<View> list = new ArrayList<>();
        for (int i = 0;i<5;i++){
            View view = getLayoutInflater().inflate(R.layout.settings_item,null,false);
            list.add(view);
        }

        Log.d("MainActivity","version:"+ Build.BRAND);

    }

    @OnClick({R.id.btnClearMemory})
    void onClick(View view){
        switch (view.getId()) {
            case R.id.btnClearMemory:
                Log.d("MainActivity","clear click");
                PackageManager packageManager = getPackageManager();
                Intent mainIntent = new Intent(Intent.ACTION_MAIN,null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(mainIntent,0);
                for (ResolveInfo info: resolveInfos){
                    Log.d("MainActivity","appName:"+info.loadLabel(packageManager).toString());
                    Log.d("MainActivity","packageName:"+info.activityInfo.packageName);
                    Log.d("MainActivity","first:"+info.activityInfo.name);
                    if ("com.tencent.mobileqq".equals(info.activityInfo.packageName)){
                        ComponentName componentName = new ComponentName(info.activityInfo.packageName,info.activityInfo.name);
                        imageView.setImageDrawable(info.loadIcon(packageManager));
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        intent.setComponent(componentName);
                        startActivity(intent);
                    }
                }
                break;
        }
    }


}
