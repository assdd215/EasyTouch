package com.example.aria.easytouch.widget.easytouch.screenshot;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.aria.easytouch.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Aria on 2017/7/24.
 */

public class SaveTask extends AsyncTask<Bitmap,Void,Boolean>{

    private Context context;
    private OnScreenshotEventListener onScreenshotEventListener;
    public SaveTask(Context context, OnScreenshotEventListener onScreenshotEventListener){
        this.context = context;
        this.onScreenshotEventListener = onScreenshotEventListener;
    }


    @Override
    protected Boolean doInBackground(Bitmap... params) {
        if (params == null || params.length < 1 || params[0] == null) return false;
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
            return true;
        } catch (IOException e) {
            e.printStackTrace();
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
