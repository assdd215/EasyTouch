package com.example.aria.easytouch.widget.easytouch.screenshot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.os.AsyncTaskCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.util.Utils;
import com.example.aria.easytouch.widget.easytouch.screenshot.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Aria on 2017/7/19.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NewScreenShotUtilImpl implements ScreenShotUtil{

    private Context context;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private static Intent data;

    private int screenWidth;
    private int screenHeight;
    private int screenDensity;

    private ImageReader imageReader;

    private OnScreenshotEventListener onScreenshotEventListener;

    public NewScreenShotUtilImpl(Context context){
        this.context = context;
        initData();
    }

    private void initData(){
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        screenDensity = displayMetrics.densityDpi;

        onScreenshotEventListener = new OnScreenshotEventListener() {
            @Override
            public void beforeScreenshot() {

            }

            @Override
            public void onImageCaptured(Image image) {

            }

            @Override
            public void afterScreenshot() {

            }

            @Override
            public void onPostImageSaved(boolean succeed) {

            }


        };
        imageReader = ImageReader.newInstance(screenWidth,screenHeight, PixelFormat.RGBA_8888,1);
    }

    private boolean startVirtual(){
        if (data == null){
            Toast.makeText(context,context.getResources().getString(R.string.msg_reOpen_screenshot),Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mediaProjection == null)mediaProjection = ((MediaProjectionManager)context.getSystemService(Context.MEDIA_PROJECTION_SERVICE)).
                    getMediaProjection(Activity.RESULT_OK,data);

        virtualDisplay = mediaProjection.createVirtualDisplay("screen_mirror",screenWidth,screenHeight,screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(),null,null);
        return true;
    }

    private void startCapture(){
        Image image = null;
        if (imageReader != null) image = imageReader.acquireLatestImage();
        if (image == null && data!=null){
            Log.d("MainActivity","startCapture");
            if (data == null)return;
            startScreenshot();
        }else {
            Bitmap bitmap = Utils.image2Bitmap(image);
            SaveTask saveTask = new SaveTask(context,onScreenshotEventListener);
            AsyncTaskCompat.executeParallel(saveTask,bitmap);
            onScreenshotEventListener.onImageCaptured(image);
        }
    }

    public static void setData(Intent data1) {
        data = data1;
    }


    @Override
    public void setOnScreenshotEventListener(OnScreenshotEventListener onScreenshotEventListener) {
        this.onScreenshotEventListener = onScreenshotEventListener;
    }

    @Override
    public void startScreenshot() {
        Log.d("MainActivity","new screenshot");
        onScreenshotEventListener.beforeScreenshot();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (startVirtual()) startCapture();
                onScreenshotEventListener.afterScreenshot();
            }
        },50);
    }

    @Override
    public boolean isSupportScreenshot() {
        return true;
    }

    @Override
    public void setHandler(Handler handler) {

    }


}
