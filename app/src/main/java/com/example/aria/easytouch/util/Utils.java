package com.example.aria.easytouch.util;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.ui.hint.HintActivity;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

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

    //封装了创建dialog的接口
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

    public static void log(String TAG,String message){
        Log.d(TAG,message);
    }

    public static void startHint(Context context,String content){
        Intent intent = new Intent(context, HintActivity.class);
        intent.putExtra(Constants.HINT_TEXT,content);
        context.startActivity(intent);
    }

    //为辅助服务提供，查找子节点
    public static void getAllChildNode(@NonNull AccessibilityNodeInfo nodeInfo, @NonNull ArrayList<AccessibilityNodeInfo> nodeList) {
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo childNodeInfo = nodeInfo.getChild(i);
            nodeList.add(childNodeInfo);

            if (childNodeInfo.getChildCount() > 0) {
                getAllChildNode(childNodeInfo, nodeList);
            }

        }
    }

    //为辅助服务服务，根据文本查找按钮
    public static AccessibilityNodeInfo getButtonByText(AccessibilityService service, @NonNull String text) {
        try {
            AccessibilityNodeInfo rootNodeInfo = service.getRootInActiveWindow();
            if (rootNodeInfo != null) {
                ArrayList<AccessibilityNodeInfo> nodeList = new ArrayList<AccessibilityNodeInfo>();
                getAllChildNode(rootNodeInfo, nodeList);
                for (AccessibilityNodeInfo node : nodeList) {
                    CharSequence clsN = node.getClassName();
                    String getText = "";
                    if (node.getText() != null)
                        getText = node.getText().toString();

                    if (node != null && clsN.equals("android.widget.Button") && text.equalsIgnoreCase(getText)) {
                        return node;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //提供了一个简单封装的网络访问接口
    public static class Networks{
        private static OkHttpClient okHttpClient;

        private static void init(){
            okHttpClient = new OkHttpClient.Builder().readTimeout(5000, TimeUnit.MILLISECONDS)
                    .writeTimeout(5000,TimeUnit.MILLISECONDS)
                    .build();
        }

        public static Call doGet(String url){
            if (okHttpClient == null)
                init();
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            return okHttpClient.newCall(request);
        }

    }
}
