package com.example.aria.easytouch.widget.easytouch.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.widget.Toast;

import com.example.aria.easytouch.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aria on 2017/7/18.
 */


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2Impl implements LightCamera{

    private static final String TAG = "CAMERAUTIL";

    private CameraDevice cameraDevice;
    private boolean isOpenCamera = false;
    private Context context;
    private CaptureRequest captureRequest;
    private CameraCaptureSession cameraCaptureSession = null;
    private Surface surface;
    private SurfaceTexture surfaceTexture;
    private CameraManager cameraManager;

    private final CameraCaptureSession.StateCallback stateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            cameraCaptureSession = session;
            CaptureRequest.Builder builder;
            try {
                builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
                builder.addTarget(surface);
                CaptureRequest request = builder.build();
                cameraCaptureSession.capture(request,null,null);
                captureRequest = request;
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            cameraCaptureSession = session;
        }
    };

    public Camera2Impl(Context context) {
        this.context = context;
    }

    public void turnOnLight() {

        if (!isSupportFlash()){
            Toast.makeText(context,context.getString(R.string.msg_not_support_flashlight),Toast.LENGTH_SHORT).show();
            // TODO 设备不支持闪光灯
            return;
        }
        if (isOpenCamera) {
            isOpenCamera = false;
            cameraDevice.close();
        } else {
            isOpenCamera = true;
            turnOnCamera2();
        }

    }

    private void turnOnCamera2() {
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) continue;
                if (characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) == null) continue;
                if (characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context,context.getString(R.string.msg_without_camera_permission),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                        @Override
                        public void onOpened(@NonNull CameraDevice camera) {
                            cameraDevice = camera;
                            surfaceTexture = new SurfaceTexture(0,false);
                            Size localSize = getSize(cameraDevice.getId());
                            surfaceTexture.setDefaultBufferSize(localSize.getWidth(),localSize.getHeight());
                            surface = new Surface(surfaceTexture);
                            ArrayList localArrayList = new ArrayList(1);
                            localArrayList.add(surface);
                            try {
                                cameraDevice.createCaptureSession(localArrayList,stateCallback,null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onDisconnected(@NonNull CameraDevice camera) {
                            cameraDevice = camera;
                        }

                        @Override
                        public void onError(@NonNull CameraDevice camera, int error) {
                            cameraDevice = camera;
                        }
                    },null);

                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private Size getSize(String paramString){
        Size[] arrayOfSize = null;
        try {
            arrayOfSize = ((StreamConfigurationMap)cameraManager.getCameraCharacteristics(paramString)
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP))
                    .getOutputSizes(SurfaceTexture.class);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        if ((arrayOfSize == null) || (arrayOfSize.length == 0))throw new IllegalStateException("Camera " + paramString
                + "doesn't support any outputSize.");
        Size localSize = arrayOfSize[0];
        int i1 = arrayOfSize.length;
        int i2 = 0;
        Object localObject1 = localSize;
        Object localObject2 = null;
        if (i2 < i1) {
            localObject2 = arrayOfSize[i2];
            if ((((Size) localObject1).getWidth() < ((Size) localObject2)
                    .getWidth())
                    || (((Size) localObject1).getHeight() < ((Size) localObject2)
                    .getHeight()))
                return (Size) localObject1;
        }
            i2++;
            localObject1 = localObject2;
        return (Size) localObject1;

    }

    private boolean isSupportFlash(){
        boolean flag = false;
            cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            try {
                for (String cameraId:cameraManager.getCameraIdList()){
                    CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                    Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT)
                        continue;
                    StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    if (map == null)continue;
                    flag = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        return flag;
    }

    public boolean getOpenCamera(){
        return isOpenCamera;
    }

}
