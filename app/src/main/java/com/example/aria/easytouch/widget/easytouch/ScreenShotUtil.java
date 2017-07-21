package com.example.aria.easytouch.widget.easytouch;

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
import android.support.annotation.RequiresApi;
import android.support.v4.os.AsyncTaskCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.aria.easytouch.R;
import com.example.aria.easytouch.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Aria on 2017/7/19.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScreenShotUtil {

    private Context context;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private static Intent data;

    private int screenWidth;
    private int screenHeight;
    private int screenDensity;

    private ImageReader imageReader;

    private OnScreenshotEventListener onScreenshotEventListener;

    public ScreenShotUtil(Context context){
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


    public void startScreenShot(){
        onScreenshotEventListener.beforeScreenshot();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (startVirtual()) startCapture();
                onScreenshotEventListener.afterScreenshot();
            }
        },10);

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
            startScreenShot();
        }else {
            SaveTask saveTask = new SaveTask();
            AsyncTaskCompat.executeParallel(saveTask,image);
            onScreenshotEventListener.onImageCaptured(image);
        }
    }

    public static void setData(Intent data1) {
        data = data1;
    }

    public void setOnScreenshotEventListener(OnScreenshotEventListener onScreenshotEventListener) {
        this.onScreenshotEventListener = onScreenshotEventListener;
    }

    private class SaveTask extends AsyncTask<Image,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Image... params) {
            if (params == null || params.length < 1 || params[0] == null) return false;

            Image image = params[0];
            Bitmap bitmap = Utils.image2Bitmap(image);
            File fileImage = null;
            if (bitmap != null) {
                try {
                    fileImage = new File(FileUtil.getScreenShotsName(context));
                    if (!fileImage.getParentFile().exists())fileImage.getParentFile().mkdirs();
                    if (!fileImage.exists())fileImage.createNewFile();
                    FileOutputStream outputStream = new FileOutputStream(fileImage);
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
                    outputStream.flush();
                    outputStream.close();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
                return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean){
                Toast.makeText(context,context.getString(R.string.msg_screenshot_success) +FileUtil.getAppPath(context)+File.separator+FileUtil.SCREENCAPTURE_PATH,Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context,context.getString(R.string.msg_screenshot_fail),Toast.LENGTH_SHORT).show();
            }

            onScreenshotEventListener.onPostImageSaved(aBoolean);
        }
    }


    public interface OnScreenshotEventListener {
        void beforeScreenshot();
        void onImageCaptured(Image image);
        void afterScreenshot();
        void onPostImageSaved(boolean succeed);
    }


}
