package com.example.aria.easytouch.widget.easytouch.screenshot;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.util.Constants;
import com.example.aria.easytouch.util.Utils;

/**
 * Created by Aria on 2017/7/19.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NewScreenShotUtilImpl implements ScreenShotUtil{

    private static final String TAG = "NewScreenShotUtilImpl";

    private Context context;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private static Intent data;

    private int screenWidth;
    private int screenHeight;
    private int screenDensity;

    private ImageReader imageReader;

    private OnScreenshotEventListener onScreenshotEventListener;

    private BroadcastReceiver screenshotReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ACTION_SCREENSHOT)){
                int result = intent.getIntExtra("result",-99999);
                if (result == Activity.RESULT_OK){
                    screenShot();
                }else {
                    Toast.makeText(context,context.getString(R.string.msg_screen_fail),Toast.LENGTH_SHORT).show();
                    onScreenshotEventListener.afterScreenshot();
                }
            }
        }
    };


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

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_SCREENSHOT);
        context.registerReceiver(screenshotReceiver,intentFilter);
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
            Log.d(TAG,"startCapture");
            if (data == null)return;
            screenShot();
        }else {
            Bitmap bitmap = Utils.image2Bitmap(image);
            SaveTask saveTask = new SaveTask(context,onScreenshotEventListener);
            saveTask.execute(bitmap);
            onScreenshotEventListener.onImageCaptured(image);
        }
    }

    //真正开始截图功能的操作
    private void screenShot(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (startVirtual()) startCapture();
                onScreenshotEventListener.afterScreenshot();
            }
        },100);
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
        Log.d(TAG,"new screenshot");
        onScreenshotEventListener.beforeScreenshot();
        Intent intent = new Intent(context, BlankActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }

    @Override
    public boolean isSupportScreenshot() {
        return true;
    }

    @Override
    public void setHandler(Handler handler) {

    }

    @Override
    public void onDestroy() {
        context.unregisterReceiver(screenshotReceiver);
    }


}
