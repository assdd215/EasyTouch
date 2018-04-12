package com.example.aria.easytouch.widget.easytouch.screenshot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.aria.easytouch.util.Constants;
import com.example.aria.easytouch.util.Utils;

/**
 * Created by Aria on 2017/9/7.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BlankActivity extends Activity{

    private static final String TAG = "BlankActivity";

    private static final int REQUEST_PROJECTION_CODE = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(),REQUEST_PROJECTION_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_PROJECTION_CODE:
                Utils.log(TAG,"onActivityResult:"+resultCode);
                NewScreenShotUtilImpl.setData(data);
                Intent intent = new Intent();
                intent.putExtra("result",resultCode);
                intent.setAction(Constants.ACTION_SCREENSHOT);
                sendBroadcast(intent);
                BlankActivity.this.finish();
        }
    }
}
