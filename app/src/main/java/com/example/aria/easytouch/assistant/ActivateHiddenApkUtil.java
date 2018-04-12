package com.example.aria.easytouch.assistant;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.RemoteException;

import java.io.Serializable;
import java.util.List;

import api.IExternalAssistService;

/**
 * 用于打开隐藏的app
 * Created by Aria on 2017/9/19.
 */

public class ActivateHiddenApkUtil implements Serializable{

    private static String packageName = "com.example.aria.transparentapp";
    private static String serviceAction = "com.example.aria.transparentapp";



    public static void activateService(Context context){
        activateService(context,packageName,serviceAction);
    }

    public static void activateService(Context context,String packageName,String serviceAction){
        //激活Service
        Intent intent = new Intent();
        intent.setAction(serviceAction);
        intent.setPackage(packageName);

        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                IExternalAssistService mService = IExternalAssistService.Stub.asInterface(service);
                try {
                    mService.basicTypes(0,0,false,0,0,"f");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };


        context.bindService(intent,connection,Context.BIND_AUTO_CREATE);
    }

    public static Intent createExplicitIntent(Context context,Intent originIntent){
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentServices(originIntent,0);
        if (resolveInfos == null || resolveInfos.size() != 1)
            return null;
        ResolveInfo resolveInfo = resolveInfos.get(0);
        String packageName = resolveInfo.serviceInfo.packageName;
        String className = resolveInfo.serviceInfo.name;
        ComponentName componentName = new ComponentName(packageName,className);
        Intent intent = new Intent(originIntent);
        intent.setComponent(componentName);
        return intent;
    }
}
