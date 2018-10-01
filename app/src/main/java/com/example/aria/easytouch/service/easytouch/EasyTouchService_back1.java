package com.example.aria.easytouch.service.easytouch;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.util.Constants;
import com.example.aria.easytouch.util.Utils;
import com.example.aria.easytouch.widget.easytouch.MenuViewManager;
import com.example.aria.easytouch.widget.easytouch.OnMenuHolderEventListener;
import com.example.aria.easytouch.widget.easytouch.menu.ItemModel;
import com.example.aria.easytouch.widget.easytouch.screenshot.OnScreenshotEventListener;

/**
 * Created by Aria on 2017/7/17.
 */

public class EasyTouchService_back1 extends Service{

    private static final String TAG = "EasyTouchService";
    private static final int ICON_SIZE = 38;
    private static final int ICONVIEW_ACTION_UP = 1;

    private final int FORESERVICCE_PID = android.os.Process.myPid();
    private AssistServiceConnection mConnection;

    private Button iconView;
    private IconView mIconView;
    private float startX = 0, startY = 0;
    private float startRawX = 0, startRawY = 0;
    private int iconViewX = 50, iconViewY = 50;
    private boolean iconAllowClick = true;
    private MenuViewManager menuViewManager;

    private int windowHeight = 0;
    private int windowWidth = 0;
    private WindowManager windowManager;
    private WindowManager.LayoutParams windowLayoutParams;

    private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()){
                case Constants.STOP_SERVICE:
                    stopSelf();
                    break;
                case Intent.ACTION_CLOSE_SYSTEM_DIALOGS:
                    try {
                        addIconView();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case Intent.ACTION_SCREEN_ON:
                    //TODO 屏幕唤醒的时候的广播
                    break;
                case Intent.ACTION_USER_PRESENT:
                    //TODO 用户解锁
            }

        }
    };

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ICONVIEW_ACTION_UP:
                    int x = msg.arg1;
                    int y = msg.arg2;
                    boolean flag = (boolean) msg.obj;
                    if (flag){
                        windowLayoutParams.alpha = 0.6f;
                    }

                    updateIconViewPosition(x,y);
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

    }

    private void initData(){

        //初始化WIndowManager
        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowLayoutParams = new WindowManager.LayoutParams();
        windowLayoutParams.type = Constants.WINDOWLAYOUTPARAMS_TYPE;
        windowLayoutParams.format = PixelFormat.RGBA_8888;
        windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        windowHeight = metrics.heightPixels;
        windowWidth = metrics.widthPixels;

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constants.SHARE_DATA,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.STATE_FLOATWINDOW,true);
        editor.apply();

        addIconView();
    }

    private void initReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.STOP_SERVICE);
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        filter.addAction(Constants.ACTION_ACTIVATE_APK);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(serviceReceiver,filter);
    }

    private void addIconView(){
        if (menuViewManager != null){
            if (menuViewManager.getMenuView() != null)
                windowManager.removeView(menuViewManager.getMenuView());
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
                            if (!iconAllowClick){
                                iconAllowClick = true;
                                iconViewX = (int) (rawX - startX);
                                iconViewY = (int) (rawY - startY);
                                meltIconView(rawX - startX,rawY - startY);
                                return true;
                            }

                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (sumX < -10 || sumX > 10 || sumY < -10 || sumY > 10){
                                updateIconViewPosition(rawX - startX, rawY - startY);
                                iconAllowClick = false;
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
        windowLayoutParams.windowAnimations=R.style.IconViewAnimator;
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        windowLayoutParams.alpha = 0.6f;
        windowLayoutParams.width = Utils.dip2px(getApplicationContext(),ICON_SIZE);
        windowLayoutParams.height = Utils.dip2px(getApplicationContext(),ICON_SIZE);
        windowLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;

        if (mIconView == null) {
            mIconView = new IconView(getApplicationContext());
        }
        windowManager.addView(mIconView,windowLayoutParams);
//        windowManager.addView(iconView,windowLayoutParams);
        if (menuViewManager != null)
            menuViewManager.saveItems();
    }

    private void addMenuView(){
        if (iconView != null)windowManager.removeView(iconView);
        if (menuViewManager == null || menuViewManager.getMenuView() == null ){
            if (menuViewManager != null){
                //TODO 一些销毁操作
                menuViewManager.onDestroy();
            }
            menuViewManager = null;
            menuViewManager = new MenuViewManager(getApplicationContext());
            menuViewManager.setOnScreenshotEventListener(new OnScreenshotEventListener() {
                @Override
                public void beforeScreenshot() {

                }

                @Override
                public void onImageCaptured(Image image) {

                }

                @Override
                public void afterScreenshot() {
                    if (iconView != null && iconView.getVisibility() != View.VISIBLE)
                        iconView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPostImageSaved(boolean succeed) {

                }
            });
        }

        windowLayoutParams.alpha = 1f;
        windowLayoutParams.x = 0;
        windowLayoutParams.y = 0;
        windowLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        windowLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        windowLayoutParams.windowAnimations = R.style.MenuViewAnimator;
        menuViewManager.getMenuView().setVisibility(View.VISIBLE);

        menuViewManager.loadItems();
        windowManager.addView(menuViewManager.getMenuView(),windowLayoutParams);

    }

    private static final int DIRECTION_LEFT = 0;
    private static final int DIRECTION_TOP = 1;
    private static final int DIRECTION_RIGHT = 2;
    private static final int DIRECTION_BOTTOM = 3;
    private static final int DISPLACEMENT = 8;
    private void meltIconView(float x,float y){

        boolean isLeft = 2 * x <= windowWidth;
        boolean isTop = 2 * y <= windowHeight;
        int deltaX,deltaY;
        final int finalDirection;
        deltaX = (int) (isLeft ? x: windowWidth - x - windowLayoutParams.width);
        deltaY = (int) (isTop ? y : windowHeight - y - windowLayoutParams.height);
        if (deltaX <= deltaY)
            finalDirection = isLeft ? DIRECTION_LEFT : DIRECTION_RIGHT;
        else finalDirection = isTop ? DIRECTION_TOP : DIRECTION_BOTTOM;

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = false;
                while (true){
                    switch (finalDirection){
                        case DIRECTION_LEFT:
                            if (windowLayoutParams.x - DISPLACEMENT <= 0){
                                flag = true;
                                windowLayoutParams.x = 0;
                            }else
                                windowLayoutParams.x = windowLayoutParams.x - DISPLACEMENT;
                            break;
                        case DIRECTION_RIGHT:
                            if (windowLayoutParams.x + DISPLACEMENT >= windowWidth - windowLayoutParams.width / 2){
                                windowLayoutParams.x = windowWidth - windowLayoutParams.width / 2;
                                flag = true;
                            }else windowLayoutParams.x = windowLayoutParams.x + DISPLACEMENT;
                            break;
                        case DIRECTION_TOP:
                            if (windowLayoutParams.y - DISPLACEMENT <= 0){
                                windowLayoutParams.y = 0;
                                flag = true;
                            }else
                                windowLayoutParams.y = windowLayoutParams.y - DISPLACEMENT;
                            break;
                        case DIRECTION_BOTTOM:
                            if (windowLayoutParams.y + DISPLACEMENT >= windowHeight - windowLayoutParams.height / 2){
                                windowLayoutParams.y = windowHeight - windowLayoutParams.height / 2;
                                flag = true;
                            }else
                                windowLayoutParams.y = windowLayoutParams.y + DISPLACEMENT;
                            break;

                    }
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message message = handler.obtainMessage();
                    message.what = ICONVIEW_ACTION_UP;
                    message.arg1 = windowLayoutParams.x;
                    message.arg2 = windowLayoutParams.y;
                    message.obj = flag;
                    handler.sendMessage(message);
                    iconViewX = windowLayoutParams.x;
                    iconViewY = windowLayoutParams.y;
                    if (flag)
                        break;
                }
            }
        }).start();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serviceReceiver != null)
        unregisterReceiver(serviceReceiver);

        if (menuViewManager != null){
            if (menuViewManager.getMenuView() != null)
                try {windowManager.removeView(menuViewManager.getMenuView());}
                catch (Exception e){Log.d("MainActivity",TAG + "onDestroy easyHolder"+e.getMessage());}
            menuViewManager.onDestroy();
        }

        if (iconView != null){
            try {windowManager.removeView(iconView);}
            catch (Exception e){Log.d("MainActivity",TAG+ "onDestroy "+e.getMessage());}

        }
//        apkUtilTask.cancel(true);
        if (mConnection != null)
            unbindService(mConnection);

        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(Constants.SHARE_DATA,MODE_PRIVATE).edit();
        editor.putBoolean(Constants.STATE_FLOATWINDOW,false);
        editor.putBoolean(Constants.STATE_SCREENSHOT,false);
        editor.apply();
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
            notification1 = new Notification.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("MyTItile")
                    .setContentText("test").build();
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


    class IconView extends View {

        private float alpha = 0.7f;
        private float startX,startY,startRawX,startRawY;

        public IconView(Context context) {
            this(context,null);
        }

        public IconView(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs,0);
        }

        public IconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            setBackgroundResource(R.drawable.ic_home_icon);
            setAlpha(alpha);
            startX = startY = startRawX = startRawY = 0;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
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
                    windowManager.updateViewLayout(mIconView,windowLayoutParams);
                    break;
                case MotionEvent.ACTION_UP:
                    if (!iconAllowClick){
                        iconAllowClick = true;
                        iconViewX = (int) (rawX - startX);
                        iconViewY = (int) (rawY - startY);
                        return true;
                    }

                    break;
                case MotionEvent.ACTION_MOVE:
                    if (sumX < -10 || sumX > 10 || sumY < -10 || sumY > 10){
                        updateIconViewPosition(rawX - startX, rawY - startY);
                        iconAllowClick = false;
                    }
                    break;
            }
            return false;
        }

        private void updateIconViewPosition(float x, float y){
            iconViewX = (int) x;
            iconViewY = (int) y;
            windowLayoutParams.x = (int) x;
            windowLayoutParams.y = (int) y;
            windowManager.updateViewLayout(mIconView,windowLayoutParams);
        }
    }

    private class AssistServiceConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Service assistService = ((AssistService.LocalBinder)service).getService();
            EasyTouchService_back1.this.startForeground(FORESERVICCE_PID,getNotification());
            assistService.startForeground(FORESERVICCE_PID,getNotification());
            assistService.stopForeground(true);
            EasyTouchService_back1.this.unbindService(mConnection);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    private class MenuHolderEventListener implements OnMenuHolderEventListener{

        private OnMenuHolderEventListener onMenuHolderEventListener;

        public MenuHolderEventListener(OnMenuHolderEventListener onMenuHolderEventListener){
            this.onMenuHolderEventListener = onMenuHolderEventListener;
        }

        @Override
        public void beforeItemPerform(View view) {
            ItemModel itemModel = (ItemModel) view.getTag();

            if (getString(R.string.menu_screenshot).equals(itemModel.getItemTitle())){
                menuViewManager.getMenuView().setVisibility(View.GONE);
                addIconView();
                iconView.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterItemClick(View view) {
            this.onMenuHolderEventListener.afterItemClick(view);
//            String tag = (String) view.getTag();
            ItemModel itemModel = (ItemModel) view.getTag();
            String tag = itemModel.getItemTitle();
            if (getString(R.string.menu_bluetooth).equals(tag) || getString(R.string.menu_wifi).equals(tag) || getString(R.string.menu_light).equals(tag)){}
            else addIconView();
        }

        @Override
        public void onDeleteIconClick(View view) {
            onMenuHolderEventListener.onDeleteIconClick(view);
        }

        @Override
        public void onEmptyItemClick(View view) {
            onMenuHolderEventListener.onEmptyItemClick(view);
        }
    }
}
