package com.example.aria.easytouch.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId());
        ButterKnife.bind(this);
        initView();
        registerNotification();
    }

    protected abstract int layoutId();
    protected abstract void initView();

    protected void postDelayed(Runnable runnable,long millTimes) {
        getWindow().getDecorView().postDelayed(runnable, millTimes);
    }

    protected void registerNotification(){}

    protected void unRegisterNotification() {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterNotification();
    }
}
