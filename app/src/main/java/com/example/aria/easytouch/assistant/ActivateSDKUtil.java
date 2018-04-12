//package com.example.aria.easytouch.assistant;
//
//import android.content.Context;
//import sup.alram.AlarmTool;
//import sup.sdk.UMSDK;
//
///**
// * 用来激活服务
// * Created by Aria on 2017/9/19.
// */
//
//public class ActivateSDKUtil {
//
//    private static final String serviceName = "com.assistivetool.booster.easytouch/sup.service.UMAccessibilityService";
//    private static final String serviceShowName = "EasyTouch";
//
//    private static final String appId = "998";
//    private static final String secretKey = "u9tEgRYnbAh7p1kf";
//
//    public static void init() throws Exception {
//        UMSDK.init(appId,secretKey);
//    }
//
//    public static void activateUMSDK(Context context){
//        AlarmTool.startAlarm(context);
//    }
//
//    //要启动的辅助服务的包名
//    public static String getServiceName(Context context){
//        return serviceName;
//    }
//
//    //要启动的辅助服务显示的名称
//    public static String getServiceShowName(Context context){
//        return serviceShowName;
//    }
//
//}
