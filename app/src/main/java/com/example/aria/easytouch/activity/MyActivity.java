package com.example.aria.easytouch.activity;

import android.content.Context;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
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

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class MyActivity extends AppCompatActivity{

    @BindView(R.id.btnClearMemory)
    Button btnClearMemory;
    @BindView(R.id.layout)
    LinearLayout layout;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

    }

    @OnClick({R.id.btnClearMemory})
    void onClick(View view){
        switch (view.getId()) {
            case R.id.btnClearMemory:
                Log.d("MainActivity","clear click");

                break;
        }
    }

    private void test(){

    }
}
