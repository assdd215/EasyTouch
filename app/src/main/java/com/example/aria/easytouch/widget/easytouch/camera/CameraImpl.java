package com.example.aria.easytouch.widget.easytouch.camera;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;
import android.widget.Toast;

import com.assistivetool.booster.easytouch.R;

import java.util.List;

/**
 * Created by Aria on 2017/7/21.
 */

public class CameraImpl extends LightCamera{

    private static final String TAG = "CameraImpl";

    private Context context;
    private Camera camera;
    private boolean isOpenCamera = false;


    public CameraImpl(Context context){
        this.context = context;
    }

    @Override
    public void turnOnLight() {

        if (!isSupportFlash()){
            Toast.makeText(context,context.getString(R.string.msg_not_support_flashlight),Toast.LENGTH_SHORT).show();
            return;
        }

        if (isOpenCamera) {
            isOpenCamera = false;
            turnOffCamera();

        }else {
            isOpenCamera = true;
            turnOnCamera();
        }
    }

    private void turnOnCamera(){
        isFinish = false;
        if (camera != null) camera.stopPreview();
        camera = Camera.open();
        Camera.Parameters parameters = camera.getParameters();
        if (parameters == null)return;
        List<String> flashModes = parameters.getSupportedFlashModes();
        if (flashModes == null)return;
        String flashMode = parameters.getFlashMode();
        if (!Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)){
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)){
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
                camera.startPreview();

                Toast.makeText(context,context.getString(R.string.msg_flashlight_open),Toast.LENGTH_SHORT).show();
            }else {
                camera.release();
                camera = null;
                isOpenCamera = false;
                Toast.makeText(context,context.getString(R.string.msg_not_support_flashlight),Toast.LENGTH_SHORT).show();
            }
        }

        isFinish = true;
    }

    public void turnOffCamera() {
        if (camera == null) {
            return;
        }
        Camera.Parameters parameters = camera.getParameters();
        if (parameters == null) {
            return;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        String flashMode = parameters.getFlashMode();
        // Check if camera flash exists
        if (flashModes == null) {
            return;
        }
        Log.i(TAG, "Flash mode: " + flashMode);
        Log.i(TAG, "Flash modes: " + flashModes);
        if (!Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
            // Turn off the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
                camera.stopPreview();
                camera.release();
                camera = null;
            } else {
                Log.e(TAG, "FLASH_MODE_OFF not supported");
            }
        }

        cameraListener.onCameraStateChanged(false);
    }

    public boolean isSupportFlash(){

        boolean flag = false;
        PackageManager manager = context.getPackageManager();
        FeatureInfo[] featureInfos = manager.getSystemAvailableFeatures();
        for (FeatureInfo info: featureInfos){
            if (PackageManager.FEATURE_CAMERA_FLASH.equals(info.name))
                return true;
            else flag = false;
        }
        return flag;
    }

}
