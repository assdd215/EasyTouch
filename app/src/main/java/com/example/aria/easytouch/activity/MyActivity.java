package com.example.aria.easytouch.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.util.Utils;
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

/**
 * 测试用
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
        addIcons();
    }

    private void addIcons(){
        ScrollView scrollView = new ScrollView(this);
        RelativeLayout.LayoutParams scrollLayoutParams = new RelativeLayout.LayoutParams(Utils.dip2px(this,325),Utils.dip2px(this,90));
        scrollLayoutParams.addRule(RelativeLayout.BELOW,R.id.btnClearMemory);
        scrollLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        scrollView.setBackgroundResource(R.color.colorCommonBackground);
        layout.addView(scrollView,scrollLayoutParams);

        RelativeLayout baseLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        PackageManager manager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN,null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = manager.queryIntentActivities(intent,0);
        for (int i = 0;i<resolveInfos.size();i++){
            ResolveInfo info = resolveInfos.get(i);
            String appName = info.loadLabel(manager).toString();
            Drawable icon = info.loadIcon(manager);
            RelativeLayout itemLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.float_menu_item,null);
            ImageView iconView = (ImageView) itemLayout.findViewById(R.id.iconView);
            TextView titleView = (TextView) itemLayout.findViewById(R.id.titleView);
            titleView.setText(appName);
            iconView.setImageDrawable(icon);

            RelativeLayout.LayoutParams itemParams = new RelativeLayout.LayoutParams(Utils.dip2px(this,75),Utils.dip2px(this,160));
            itemParams.leftMargin = Utils.dip2px(MyActivity.this,(i%3 +1) *5 + i%3 * 75 );
            itemParams.topMargin = Utils.dip2px(MyActivity.this,(i/3 + 1) *5 + i/3 * 75 );
            baseLayout.addView(itemLayout,itemParams);
        }



        scrollView.addView(baseLayout,params);


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
