package com.example.aria.easytouch.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.assistivetool.booster.easytouch.R;

import org.jetbrains.annotations.TestOnly;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aria on 2017/7/24.
 */

/**
 * 测试用
 */

@TestOnly
public class MyActivity extends AppCompatActivity{

    private static final String TAG = "MyActivity";

    @BindView(R.id.textView)
    TextView textView;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(MyActivity.this,msg.obj.toString(),Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ButterKnife.bind(this);
//        getApkMessage();
        testUri();

//        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferen)
        Intent intent = new Intent();

//        intent.putExtra("ffff",new ActivateHiddenApkUtil());
        String filePath = getExternalFilesDir(null).getPath();
        Log.d(TAG,"filePath:" + filePath);
    }

    private void testUri(){
        Uri uri  =Uri.parse("http://www.google.com");
        Log.d(TAG,uri.toString());
    }




}
