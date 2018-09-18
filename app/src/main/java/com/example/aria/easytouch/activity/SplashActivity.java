package com.example.aria.easytouch.activity;

import android.content.Intent;
import android.os.CountDownTimer;

import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.base.BaseActivity;
import com.example.aria.easytouch.test.TestActivity;

public class SplashActivity extends BaseActivity{

    private CountDownTimer mTimer;
    private boolean isDestoryed = false;

    @Override
    protected int layoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        startCountDown(1);

    }

    //TODO 做一些耗时操作的初始化
    private void init(){

    }

    private void startCountDown(final long second) {
        mTimer = new CountDownTimer(second * 1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isDestoryed) this.cancel();
            }

            @Override
            public void onFinish() {
                startActivity(new Intent(SplashActivity.this,MainAppActivity.class));
                finish();
            }
        };

        mTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestoryed = true;
    }
}
