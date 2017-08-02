package com.example.aria.easytouch.util;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.assistivetool.booster.easytouch.R;

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

    public static void createDialog(Context context, String title, String message, String positive, DialogInterface.OnClickListener posiviteListener,
                                    String negative, DialogInterface.OnClickListener negativeListener){
        AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title)
                                .setMessage(message)
                                .setPositiveButton(positive,posiviteListener)
                                .setNegativeButton(negative,negativeListener)
                                .create();
        dialog.show();
    }

    public static void createDialog(Context context, String title, String message, String positive, DialogInterface.OnClickListener posiviteListener){

        String negative = context.getString(R.string.msg_close);
        DialogInterface.OnClickListener negativeListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
            }
        };

        createDialog(context,title,message,positive,posiviteListener,negative,negativeListener);
    }
}
