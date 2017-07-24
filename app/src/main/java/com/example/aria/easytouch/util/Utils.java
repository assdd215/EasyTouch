package com.example.aria.easytouch.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.Image;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.aria.easytouch.R;

import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by Aria on 2017/7/18.
 */

public class Utils {
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static Bitmap image2Bitmap(Image image){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            int width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            //每个像素的间距
            int pixelStride = planes[0].getPixelStride();
            //总的间距
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            image.close();
            return bitmap;
        }
        return null;
    }

    public static Bitmap acquireScreenshot(Context context){
        WindowManager mWinManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = mWinManager.getDefaultDisplay();
        display.getMetrics(metrics);
        // 屏幕高
        int height = metrics.heightPixels;
        // 屏幕的宽
        int width = metrics.widthPixels;

        int pixelformat = display.getPixelFormat();
        PixelFormat localPixelFormat1 = new PixelFormat();
        PixelFormat.getPixelFormatInfo(pixelformat, localPixelFormat1);
        // 位深
        int deepth = localPixelFormat1.bytesPerPixel;

        byte[] arrayOfByte = new byte[height * width * deepth];
        try {
            // 读取设备缓存，获取屏幕图像流
            InputStream localInputStream = readAsRoot();
            Log.d("MainActivity","after readAsRoot:"+(localInputStream == null));
            DataInputStream localDataInputStream = new DataInputStream(localInputStream);
            Log.d("MainActivity","DataInputStream:"+(localDataInputStream == null));
            Log.d("MainActivity","size of byte:" + arrayOfByte.length);

            localDataInputStream.readFully(arrayOfByte);
            localInputStream.close();

            int[] tmpColor = new int[width * height];
            int r, g, b;
            for (int j = 0; j < width * height * deepth; j += deepth) {
                b = arrayOfByte[j] & 0xff;
                g = arrayOfByte[j + 1] & 0xff;
                r = arrayOfByte[j + 2] & 0xff;
                tmpColor[j / deepth] = (r << 16) | (g << 8) | b | (0xff000000);
            }
            // 构建bitmap
            Bitmap scrBitmap = Bitmap.createBitmap(tmpColor, width, height,
                    Bitmap.Config.ARGB_8888);
            return scrBitmap;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private static InputStream readAsRoot()throws Exception{
        File deviceFile = new File("/dev/graphics/fb0");
        Process localProcess = Runtime.getRuntime().exec("su");
        String str = "cat " + deviceFile.getAbsolutePath() + "\n";
        localProcess.getOutputStream().write(str.getBytes());
        return localProcess.getInputStream();
    }
}
