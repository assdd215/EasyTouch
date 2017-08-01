package com.example.aria.easytouch.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.service.MyService;
import com.example.aria.easytouch.util.Utils;
import com.example.aria.easytouch.widget.easytouch.screenshot.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Aria on 2017/7/24.
 */

public class PreScreenshotActivity extends Activity{

    @BindView(R.id.btnScreenshot)
    Button btnScreenshot;
    @BindView(R.id.img)
    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshot);
        ButterKnife.bind(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @OnClick({R.id.btnScreenshot})
    void onClick(View view){
        Log.d("MainActivity","view:"+view.getId());
        Intent intent = new Intent(PreScreenshotActivity.this, MyService.class);
        startService(intent);
        Log.d("MainActivity","after startService");

    }



}
