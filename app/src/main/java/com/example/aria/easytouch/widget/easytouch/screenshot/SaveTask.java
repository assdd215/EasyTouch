package com.example.aria.easytouch.widget.easytouch.screenshot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.assistivetool.booster.easytouch.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aria on 2017/7/24.
 */

public class SaveTask extends AsyncTask<Bitmap,Void,Map>{


    private static final String RESULT = "RERSULT";
    private static final String BITMAP = "BITMAP";

    private Context context;
    private OnScreenshotEventListener onScreenshotEventListener;
    private NewGlobalScreenshot newGlobalScreenshot;
    public SaveTask(Context context, OnScreenshotEventListener onScreenshotEventListener){
        this.context = context;
        this.onScreenshotEventListener = onScreenshotEventListener;
        newGlobalScreenshot = new NewGlobalScreenshot(context);
    }


    @Override
    protected Map doInBackground(Bitmap... params) {
        Log.d("MainActivity","doinbackground");
        Map<String,Object> map = new HashMap<String,Object>();
        if (params == null || params.length < 1 || params[0] == null) {
            map.put(RESULT,false);
            map.put(BITMAP,null);
            return map;
        }
        Bitmap bitmap = params[0];
        File fileImage = null;
        try {
            fileImage = new File(FileUtil.getScreenShotsName(context));
            if (!fileImage.getParentFile().exists())fileImage.getParentFile().mkdirs();
            if (!fileImage.exists())fileImage.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(fileImage);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            outputStream.flush();
            outputStream.close();

            Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(fileImage);
            media.setData(contentUri);
            context.sendBroadcast(media);

            Log.d("MainActivity","sendBroadcast media");
            map.put(RESULT,true);
            map.put(BITMAP,bitmap);
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }

        map.put(RESULT,false);
        map.put(BITMAP,null);
        return map;
    }

    @Override
    protected void onPostExecute(Map map) {
        super.onPostExecute(map);

        boolean result = (boolean) map.get(RESULT);
        Bitmap bitmap = (Bitmap) map.get(BITMAP);

        if (result){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            newGlobalScreenshot.takeScreenshot(bitmap, new NewGlobalScreenshot.OnScreenshotListener() {
                @Override
                public void startScreenshot() {

                }

                @Override
                public void endScreenshot(boolean success) {
            Toast.makeText(context,context.getString(R.string.msg_screenshot_success) +FileUtil.getAppPath(context)+File.separator+FileUtil.SCREENCAPTURE_PATH,Toast.LENGTH_SHORT).show();
                }
            },true,true);

            else {
                Toast.makeText(context,context.getString(R.string.msg_screenshot_success) +FileUtil.getAppPath(context)+File.separator+FileUtil.SCREENCAPTURE_PATH,Toast.LENGTH_SHORT).show();

            }

        }else {
            Toast.makeText(context,context.getString(R.string.msg_screenshot_fail),Toast.LENGTH_SHORT).show();
        }
        onScreenshotEventListener.onPostImageSaved(result);
    }

}
