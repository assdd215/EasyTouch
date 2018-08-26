package com.example.aria.easytouch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.adapter.BaseSettingAdapter;
import com.example.aria.easytouch.adapter.SettingAdapterFactory;
import com.example.aria.easytouch.util.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity{

    @BindView(R.id.Container)
    RecyclerView Container;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    BaseSettingAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId());
        ButterKnife.bind(this);
        initData();
    }

    protected int layoutId(){
        return R.layout.activity_setting;
    }

    private void initData(){
        Intent intent = getIntent();
        String title = intent.getStringExtra(Constants.SETTING_TITLE);
        setTitle(title != null?title:"初始设置");
        int adapter_type = intent.getIntExtra(Constants.SETTING_TYPE, SettingAdapterFactory.AdapterType.TYPE_INIT_SETTING);
        adapter = SettingAdapterFactory.getAdapter(this,adapter_type);
        Container.setAdapter(adapter);
        Container.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.this.finish();
            }
        });

    }
}
