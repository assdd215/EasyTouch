package com.example.aria.easytouch.widget.easytouch.camera;

/**
 * Created by Aria on 2017/7/21.
 */

public abstract class LightCamera {

    protected boolean isFinish = true;
    protected boolean isOpenCamera = false;
    protected CameraListener cameraListener;

    public boolean getOpenCamera(){
        return isOpenCamera;
    };
    public void setOpenCamera(boolean isOpen){
        this.isOpenCamera = isOpen;
    };
    public abstract void turnOnLight();
    public abstract boolean isSupportFlash();

    public boolean isFinish(){
        return isFinish;
    }

    public void setCameraListener(CameraListener cameraListener){
        this.cameraListener = cameraListener;
    }


    public interface CameraListener{
        void onCameraStateChanged(boolean isOn);
    }
}
