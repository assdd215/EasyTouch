package com.example.aria.easytouch.widget.easytouch.screenshot;

/**
 * Created by Aria on 2017/7/24.
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.support.v4.os.AsyncTaskCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.aria.easytouch.R;
import com.example.aria.easytouch.util.ShellUtils;

public class OldScreenShotUtilImpl implements ScreenShotUtil{

    private static final String TAG = "OldScreenShotUtilImpl";

    private static final String CLASS1_NAME = "android.view.SurfaceControl";

    private static final String CLASS2_NAME = "android.view.Surface";

    private static final String METHOD_NAME = "screenshot";

    private static OldScreenShotUtilImpl instance;

    private NewGlobalScreenshot newGlobalScreenshot;

    private OnScreenshotEventListener onScreenshotEventListener;

    private Display mDisplay;

    private DisplayMetrics mDisplayMetrics;

    private Matrix mDisplayMatrix;

    private WindowManager wm;

    private SimpleDateFormat format;

    private Context context;

    private OldScreenShotUtilImpl(Context context)
    {
        this.context = context;
        newGlobalScreenshot = new NewGlobalScreenshot(context);
    }

    public static OldScreenShotUtilImpl getInstance(Context context)
    {
        synchronized (OldScreenShotUtilImpl.class)
        {
            if (instance == null)
            {
                instance = new OldScreenShotUtilImpl(context);
            }
        }
        return instance;
    }

    private Bitmap screenShot(int width, int height)
    {
        Log.i(TAG, "android.os.Build.VERSION.SDK : " + android.os.Build.VERSION.SDK_INT);
        Class<?> surfaceClass = null;
        Method method = null;
        try
        {
            Log.i(TAG, "width : " + width);
            Log.i(TAG, "height : " + height);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2)
            {

                surfaceClass = Class.forName(CLASS1_NAME);
            }
            else
            {
                surfaceClass = Class.forName(CLASS2_NAME);
            }
            method = surfaceClass.getDeclaredMethod(METHOD_NAME, int.class, int.class);
            method.setAccessible(true);
            return (Bitmap) method.invoke(null, width, height);
        }catch (Exception e){
            Log.d("MainActivity","screenShot fail");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Takes a screenshot of the current display and shows an animation.
     */
    @SuppressLint("NewApi")
    public void takeScreenshot(Context context, String fileFullPath)
    {
        if(fileFullPath == ""){
            format = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = format.format(new Date(System.currentTimeMillis())) + ".png";
            fileFullPath = "/data/local/tmp/" + fileName;
        }

        if(ShellUtils.checkRootPermission()){
                ShellUtils.execCommand("/system/bin/screencap -p "+ fileFullPath,true);
            Log.d("MainActivity","screenshot");
            Toast.makeText(context,context.getString(R.string.msg_screenshot_success) +FileUtil.getAppPath(context)+File.separator+FileUtil.SCREENCAPTURE_PATH,Toast.LENGTH_SHORT).show();
            onScreenshotEventListener.afterScreenshot();
        }
        else {
            if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2){
                wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                mDisplay = wm.getDefaultDisplay();
                mDisplayMatrix = new Matrix();
                mDisplayMetrics = new DisplayMetrics();
                // We need to orient the screenshot correctly (and the Surface api seems to take screenshots
                // only in the natural orientation of the device :!)
                mDisplay.getRealMetrics(mDisplayMetrics);
                float[] dims =
                        {
                                mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels
                        };
                float degrees = getDegreesForRotation(mDisplay.getRotation());
                boolean requiresRotation = (degrees > 0);
                if (requiresRotation)
                {
                    // Get the dimensions of the device in its native orientation
                    mDisplayMatrix.reset();
                    mDisplayMatrix.preRotate(-degrees);
                    mDisplayMatrix.mapPoints(dims);
                    dims[0] = Math.abs(dims[0]);
                    dims[1] = Math.abs(dims[1]);
                }

                Bitmap mScreenBitmap = screenShot((int) dims[0], (int) dims[1]);
                if (requiresRotation)
                {
                    // Rotate the screenshot to the current orientation
                    Bitmap ss = Bitmap.createBitmap(mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels,
                            Bitmap.Config.ARGB_8888);
                    Canvas c = new Canvas(ss);
                    c.translate(ss.getWidth() / 2, ss.getHeight() / 2);
                    c.rotate(degrees);
                    c.translate(-dims[0] / 2, -dims[1] / 2);
                    c.drawBitmap(mScreenBitmap, 0, 0, null);
                    c.setBitmap(null);
                    mScreenBitmap = ss;
                    if (ss != null && !ss.isRecycled())
                    {
                        ss.recycle();
                    }
                }

                // If we couldn't take the screenshot, notify the user
                if (mScreenBitmap == null)
                {
                    Toast.makeText(context, context.getString(R.string.msg_screenshot_fail), Toast.LENGTH_SHORT).show();
                }
                onScreenshotEventListener.afterScreenshot();

                // Optimizations
                mScreenBitmap.setHasAlpha(false);
                mScreenBitmap.prepareToDraw();

                SaveTask saveTask = new SaveTask(context,onScreenshotEventListener);
                AsyncTaskCompat.executeParallel(saveTask,mScreenBitmap);
//                saveBitmap2file(context, mScreenBitmap, fileFullPath);
            }else {
                Toast.makeText(context,context.getString(R.string.msg_without_root_screenshot),Toast.LENGTH_SHORT).show();
                onScreenshotEventListener.afterScreenshot();
            }
        }

    }

    //
    public void saveBitmap2file(Context context, Bitmap bmp, String fileName)
    {
        int quality = 100;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, quality, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        byte[] buffer = new byte[1024];
        int len = 0;
        File file = new File(fileName);
        if (!file.exists())
        {
            try
            {
                file.getParentFile().mkdir();
                file.getParentFile().createNewFile();
            }
            catch (IOException e)
            {
                Log.e(TAG, e.toString());
            }
        }
        else
        {
            try
            {
                file.getParentFile().delete();
                file.getParentFile().createNewFile();
            }
            catch (IOException e)
            {
                Log.e(TAG, e.toString());
            }
        }
        FileOutputStream stream = null;
        try
        {
            stream = new FileOutputStream(file);
            while ((len = is.read(buffer)) != -1)
            {
                stream.write(buffer, 0, len);
            }
            stream.flush();
        }
        catch (FileNotFoundException e)
        {
            Log.i(TAG, e.toString());
        }
        catch (IOException e)
        {
            Log.i(TAG, e.toString());
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    Log.i(TAG, e.toString());
                }
            }
            if (stream != null)
            {
                try
                {
                    stream.close();
                }
                catch (IOException e)
                {
                    Log.i(TAG, e.toString());
                }
            }
        }
        if (bmp != null && !bmp.isRecycled())
        {
            bmp.recycle();
        }
    }

    /**
     * @return the current display rotation in degrees
     */
    private float getDegreesForRotation(int value)
    {
        switch (value)
        {
            case Surface.ROTATION_90:
                return 360f - 90f;
            case Surface.ROTATION_180:
                return 360f - 180f;
            case Surface.ROTATION_270:
                return 360f - 270f;
        }
        return 0f;
    }

    @Override
    public void setOnScreenshotEventListener(OnScreenshotEventListener onScreenshotEventListener) {
        this.onScreenshotEventListener = onScreenshotEventListener;
    }

    @Override
    public void startScreenshot() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                takeScreenshot(context,FileUtil.getScreenShotsName(context));
            }
        },30);

    }

    @Override
    public boolean isSupportScreenshot() {

        boolean flag = false;
        if (ShellUtils.checkRootPermission()){
            flag = true;
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2){
            flag = true;
        }

        return flag;
    }

    @Override
    public void setHandler(Handler handler) {

    }
}

