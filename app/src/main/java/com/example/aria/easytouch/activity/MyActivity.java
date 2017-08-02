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
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.assistivetool.booster.easytouch.R;
import com.github.promeg.pinyinhelper.Pinyin;

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
    RelativeLayout layout;
    @BindView(R.id.image)
    ImageView imageView;
    @BindView(R.id.testLayout)
    RelativeLayout testLayout;
    @BindView(R.id.centerImage)
    ImageView centerImage;


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

        Toast.makeText(this, Pinyin.toPinyin("你好"," "),Toast.LENGTH_SHORT).show();

        testLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyActivity.this,"testLayout click!",Toast.LENGTH_SHORT).show();

            }
        });
        Log.d("MainActivity","version:"+ Build.BRAND);

    }

    @OnClick({R.id.btnClearMemory})
    void onClick(View view){
        switch (view.getId()) {
            case R.id.btnClearMemory:
                testLayout.setClickable(false);
                Toast.makeText(MyActivity.this,"testLayout:"+testLayout.isClickable(),Toast.LENGTH_SHORT).show();
                break;
            case R.id.testLayout:
                Toast.makeText(MyActivity.this,"testLayout click!",Toast.LENGTH_SHORT).show();
                break;
        }
    }


}
