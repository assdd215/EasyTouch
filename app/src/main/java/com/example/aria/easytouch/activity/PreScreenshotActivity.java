package com.example.aria.easytouch.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.aria.easytouch.R;
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

    @OnClick({R.id.btnScreenshot})
    void onClick(View view){
        new ScreenshotTask().execute();
    }

    private class ScreenshotTask extends AsyncTask{

        private Bitmap bitmap;

        @Override
        protected Object doInBackground(Object[] params) {
            Log.d("MainActivity","doInBackground");
            bitmap = Utils.acquireScreenshot(PreScreenshotActivity.this);
            Log.d("MainActivity","return");
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.d("MainActivity","onPostExecute");
            if (bitmap == null)
                Log.d("MainActivity","bitmap is null");
            imageView.setImageBitmap(bitmap);
        }
    }

}
