package com.example.aria.easytouch.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.aria.easytouch.R;
import com.example.aria.easytouch.util.Constants;
import com.example.aria.easytouch.util.Utils;
import com.example.aria.easytouch.widget.easytouch.EasyTouchMenuHolder;
import com.example.aria.easytouch.widget.easytouch.ScreenShotUtil;

/**
 * Created by Aria on 2017/7/17.
 */

public class EasyTouchService extends Service{

    private static final String TAG = "EasyTouchService";

    private final int FORESERVICCE_PID = android.os.Process.myPid();
    private AssistServiceConnection mConnection;

    private static final int ICON_SIZE = 38;
    private volatile boolean stop = false;

    MyAsyncTask myAsyncTask;

    private Button iconView;
    private float startX = 0, startY = 0;
    private float startRawX = 0, startRawY = 0;
    private int iconViewX = 50, iconViewY = 50;
    private EasyTouchMenuHolder easyTouchMenuHolder;

    private WindowManager windowManager;
    private WindowManager.LayoutParams windowLayoutParams;

    private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.STOP_SERVICE.equals(intent.getAction())) {
                stopSelf();
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
        initReceiver();
        setForeground();
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();

    }

    private void initData(){
        //初始化WIndowManager
        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowLayoutParams = new WindowManager.LayoutParams();
        windowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        windowLayoutParams.format = PixelFormat.RGBA_8888;
        windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;


        addIconView();
    }

    private void initReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.STOP_SERVICE);
        registerReceiver(serviceReceiver,filter);
    }

    private void addIconView(){
        if (easyTouchMenuHolder != null){
            if (easyTouchMenuHolder.getMainView() != null)
                windowManager.removeView(easyTouchMenuHolder.getMainView());
        }
        if (iconView == null){
            iconView = new Button(getApplicationContext());
            iconView.setBackgroundResource(R.drawable.selector_btn_launcher);
            iconView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    float rawX = event.getRawX();
                    float rawY = event.getRawY();
                    int sumX = (int) (rawX - startRawX);
                    int sumY = (int) (event.getRawY() - startRawY);
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            startX = event.getX();
                            startY = event.getY();
                            startRawX = event.getRawX();
                            startRawY = event.getRawY();
                            windowLayoutParams.alpha = 1f;
                            windowManager.updateViewLayout(iconView,windowLayoutParams);
                            break;
                        case MotionEvent.ACTION_UP:
                            windowLayoutParams.alpha = 0.6f;
                            windowManager.updateViewLayout(iconView,windowLayoutParams);
                            startX = 0;
                            startY = 0;
                            startRawY = 0;
                            startRawX = 0;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (sumX < -10 || sumX > 10 || sumY < -10 || sumY > 10){
                                updateIconViewPosition(rawX - startX, rawY - startY);
                            }
                            break;
                    }
                    return false;
                }
            });

            iconView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addMenuView();
                }
            });
        }
        windowLayoutParams.x = iconViewX;
        windowLayoutParams.y = iconViewY;
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        Log.d("MainActivity","dpi:"+metrics.density);
        windowLayoutParams.width = Utils.dip2px(getApplicationContext(),ICON_SIZE);
        windowLayoutParams.height = Utils.dip2px(getApplicationContext(),ICON_SIZE);
        windowLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        windowManager.addView(iconView,windowLayoutParams);
    }

    private void addMenuView(){
        if (iconView != null)windowManager.removeView(iconView);
        if (easyTouchMenuHolder == null || easyTouchMenuHolder.getMainView() == null){
            Log.d("MainActivity","new easyTouchMenuHolder");
            if (easyTouchMenuHolder!= null) easyTouchMenuHolder.onDestory();
            easyTouchMenuHolder = null;
            easyTouchMenuHolder = new EasyTouchMenuHolder(getApplicationContext());
            easyTouchMenuHolder.setOnHolderScreenshotEventListener(new OnHolderScreenshotEventListener(easyTouchMenuHolder));
            easyTouchMenuHolder.setOnHolderEventListener(new EasyTouchMenuHolder.onHolderEventListener() {
                @Override
                public void beforeItemPerform(View view) {
                    switch (view.getId()){
                        case R.id.ll_screenshot:
                            addIconView();
                            iconView.setVisibility(View.GONE);
                            break;
                    }
                }

                @Override
                public void afterItemClick(View view) {
                    addIconView();
                }
            });
        }

        windowLayoutParams.alpha = 1f;
        windowLayoutParams.x = 0;
        windowLayoutParams.y = 0;
        windowLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        windowLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        windowManager.addView(easyTouchMenuHolder.getMainView(),windowLayoutParams);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serviceReceiver != null)
        unregisterReceiver(serviceReceiver);

        if (easyTouchMenuHolder != null){
            if (easyTouchMenuHolder.getMainView()!=null)
                try {windowManager.removeView(easyTouchMenuHolder.getMainView());}
                catch (Exception e){Log.d("MainActivity",TAG + "onDestory easyHolder"+e.getMessage());}
            easyTouchMenuHolder.onDestory();
        }

        if (iconView != null){
            try {windowManager.removeView(iconView);}
            catch (Exception e){Log.d("MainActivity",TAG+ "onDestory "+e.getMessage());}

        }
        stop = true;
        myAsyncTask.cancel(true);
        if (mConnection != null)
            unbindService(mConnection);
    }

    private class MyAsyncTask extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {

            while (!stop){
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d("Counting","state:");
            }
            return null;
        }
    }

    private void updateIconViewPosition(float x, float y){
        iconViewX = (int) x;
        iconViewY = (int) y;
        windowLayoutParams.x = (int) x;
        windowLayoutParams.y = (int) y;
        windowManager.updateViewLayout(iconView,windowLayoutParams);
    }

    private Notification getNotification(){
        //定义一个notification
        Notification notification1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            Notification.Builder builder1 = new Notification.Builder(this);
            builder1.setSmallIcon(R.mipmap.ic_launcher); //设置图标
            builder1.setContentTitle("My title"); //设置标题
            builder1.setContentText("My content"); //消息内容
            notification1 = builder1.build();
        }else {
            // TODO JELLY_BEAN前创建notification的方法
            notification1 = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("MyTItile")
                    .setContentText("test").getNotification();
        }
        return notification1;
    }

    private void setForeground(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            if (null == mConnection)
                mConnection  =new AssistServiceConnection();
            this.bindService(new Intent(this,AssistService.class),mConnection,Service.BIND_AUTO_CREATE);
        }
    }

    private class AssistServiceConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Service assistService = ((AssistService.LocalBinder)service).getService();
            EasyTouchService.this.startForeground(FORESERVICCE_PID,getNotification());
            assistService.startForeground(FORESERVICCE_PID,getNotification());
            assistService.stopForeground(true);
            EasyTouchService.this.unbindService(mConnection);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }


    private class OnHolderScreenshotEventListener implements ScreenShotUtil.OnScreenshotEventListener{

        private ScreenShotUtil.OnScreenshotEventListener onScreenshotEventListener;

        public OnHolderScreenshotEventListener(ScreenShotUtil.OnScreenshotEventListener onScreenshotEventListener){
            this.onScreenshotEventListener = onScreenshotEventListener;
        }

        @Override
        public void beforeScreenshot() {
            onScreenshotEventListener.beforeScreenshot();
        }

        @Override
        public void onImageCaptured(Image image) {
            onScreenshotEventListener.onImageCaptured(image);
        }

        @Override
        public void afterScreenshot() {
            onScreenshotEventListener.afterScreenshot();
            if (iconView != null && iconView.getVisibility() != View.VISIBLE)
            iconView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPostImageSaved(boolean succeed) {
            onScreenshotEventListener.onPostImageSaved(succeed);
        }
    }
}
