package com.example.aria.easytouch.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.assistivetool.booster.easytouch.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aria on 2017/7/25.
 */

public class SettingActivity extends AppCompatActivity{

    public static final String SETTING_AUTO_LAUNCH = "allow auto-launch";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.Container)
    RecyclerView Container;
    private SettingRecyclerAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private List<String> itemList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        initData();
        initView();
        initListener();
    }

    private void initData(){
        itemList = new ArrayList<>();
        itemList.add(SETTING_AUTO_LAUNCH);
    }

    private void initView(){
        initToolbar();
        adapter = new SettingRecyclerAdapter(this,itemList);
        linearLayoutManager = new LinearLayoutManager(this);
        Container.setAdapter(adapter);
        Container.setLayoutManager(linearLayoutManager);
    }

    private void initListener(){
        adapter.setOnItemClickListener(new SettingRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                Log.d("MainActivity","onItemClick");
                String tag = (String) view.getTag();
                Log.d("MainActivity","tag:"+tag);
                if (SETTING_AUTO_LAUNCH.equals(tag)){
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", getPackageName(), null));
                    startActivity(intent);
                }
            }
        });

    }

    private void initToolbar(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity","toolbar onClick");
                SettingActivity.this.finish();
            }
        });

    }
}
