package com.example.aria.easytouch.service.easytouch;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.base.BaseService;
import com.example.aria.easytouch.iview.BaseMenuItemEventDecerator;
import com.example.aria.easytouch.iview.OnMenuItemEventListener;
import com.example.aria.easytouch.util.Constants;
import com.example.aria.easytouch.widget.easytouch.MenuViewManager;
import com.luzeping.aria.commonutils.notification.NotificationCenter;
import com.luzeping.aria.commonutils.utils.Device;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Aria on 2017/7/17.
 */

public class EasyTouchService extends BaseService{

    private static final String TAG = "EasyTouchService";

    private static final int DIRECTION_LEFT = 0;
    private static final int DIRECTION_TOP = 1;
    private static final int DIRECTION_RIGHT = 2;
    private static final int DIRECTION_BOTTOM = 3;
    private static final int TRANS_SPEED = 48;
    private IconView mIconView;
    private int ICON_SIZE = 44;
    private int iconViewX = 50, iconViewY = 50; // 用来记录icon消失的位置
    private boolean iconAllowClick = true;
    private MenuViewManager menuViewManager;

    private WindowManager windowManager;
    private WindowManager.LayoutParams iconViewLayoutParams;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void registerNotification() {
        NotificationCenter.getInstance().addObserver(this,R.id.STOP_EASY_TOUCH_SERVICE);
    }

    @Override
    protected void unRegisterNotification() {
        NotificationCenter.getInstance().removeObserver(this,R.id.STOP_EASY_TOUCH_SERVICE);
    }

    @Override
    protected void init() {
        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constants.SHARE_DATA,MODE_PRIVATE);
        sharedPreferences.edit()
                .putBoolean(Constants.STATE_FLOATWINDOW,true)
                .apply();

        addIconView();
    }

    private void addIconView(){

        if (menuViewManager != null && menuViewManager.getMenuView() != null) {
            menuViewManager.getMenuView().setVisibility(View.GONE);
        }
        if (mIconView == null) {
            mIconView = new IconView(getApplicationContext());

            iconViewLayoutParams = new WindowManager.LayoutParams();
            iconViewLayoutParams.type = Constants.WINDOWLAYOUTPARAMS_TYPE;
            iconViewLayoutParams.format = PixelFormat.RGBA_8888; //设置窗口透明
            iconViewLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL // 设置窗口外可接受点击事件
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 设置不接收输入
            iconViewLayoutParams.x = iconViewX;
            iconViewLayoutParams.y = iconViewY;
            iconViewLayoutParams.windowAnimations = R.style.IconViewAnimator;
            iconViewLayoutParams.width = Device.dip2px(getApplicationContext(),ICON_SIZE);
            iconViewLayoutParams.height = Device.dip2px(getApplicationContext(),ICON_SIZE);
            iconViewLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
            windowManager.addView(mIconView,iconViewLayoutParams);
        }
        mIconView.setVisibility(View.VISIBLE);
    }

    private void addMenuView() {
        if (mIconView != null) {
            mIconView.setVisibility(View.GONE);
        }
        if (menuViewManager == null || menuViewManager.getMenuView() == null) {
            if (menuViewManager != null)
                menuViewManager.onDestroy();
            menuViewManager = null;
            menuViewManager = new MenuViewManager(getApplicationContext());
            menuViewManager.setOnMenuItemEventListener(new MenuViewItemEventDecorator(menuViewManager));
            WindowManager.LayoutParams windowLayoutParams =
                    new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            windowLayoutParams.type = Constants.WINDOWLAYOUTPARAMS_TYPE;
            windowLayoutParams.format = PixelFormat.TRANSLUCENT; //设置窗口透明
            windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN; // 设置不接收输入
            windowLayoutParams.alpha = 1f;
            windowLayoutParams.x = 0;
            windowLayoutParams.y = 0;
            windowLayoutParams.windowAnimations = R.style.MenuViewAnimator;
            menuViewManager.getMenuView().setVisibility(View.VISIBLE);
            menuViewManager.loadItems();
            windowManager.addView(menuViewManager.getMenuView(),windowLayoutParams);
        }
        menuViewManager.getMenuView().setVisibility(View.VISIBLE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (menuViewManager != null){
            if (menuViewManager.getMenuView() != null)
                try {windowManager.removeView(menuViewManager.getMenuView());}
                catch (Exception e){Log.d("MainActivity",TAG + "onDestroy easyHolder"+e.getMessage());}
            menuViewManager.onDestroy();
        }

        if (mIconView != null) {
            try {
                windowManager.removeView(mIconView);
            }catch (Exception e) {
                e.printStackTrace();
            }

        }

        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(Constants.SHARE_DATA,MODE_PRIVATE).edit();
        editor.putBoolean(Constants.STATE_FLOATWINDOW,false);
        editor.putBoolean(Constants.STATE_SCREENSHOT,false);
        editor.apply();
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        switch (id) {
            case R.id.STOP_EASY_TOUCH_SERVICE:
                stopSelf();
        }
    }

    /**
     * 悬浮球
     */
    class IconView extends View {

        private float alpha = 0.7f;
        private float startX, startY, startRawX, startRawY;

        public IconView(Context context) {
            this(context, null);
        }

        public IconView(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public IconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            setBackgroundResource(R.drawable.ic_home_icon);
            setAlpha(alpha);
            startX = startY = startRawX = startRawY = 0;

            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    addMenuView();

                }
            });
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float rawX = event.getRawX();
            float rawY = event.getRawY();
            int sumX = (int) (rawX - startRawX);
            int sumY = (int) (event.getRawY() - startRawY);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    startY = event.getY();
                    startRawX = event.getRawX();
                    startRawY = event.getRawY();
                    iconViewLayoutParams.alpha = 1f;
                    windowManager.updateViewLayout(mIconView, iconViewLayoutParams);
                    break;
                case MotionEvent.ACTION_UP:
                    if (!iconAllowClick) {
                        iconAllowClick = true;
                        iconViewX = (int) (rawX - startX);
                        iconViewY = (int) (rawY - startY);
                        transIconView(rawX,rawY);
                        return true;
                    } else {
                        addMenuView();
                    }

                    break;
                case MotionEvent.ACTION_MOVE:
                    if (sumX < -10 || sumX > 10 || sumY < -10 || sumY > 10) {
                        updateIconViewPosition(rawX - startX, rawY - startY);
                        iconAllowClick = false;
                    }
                    break;
            }
            return false;
        }


        /**
         * 根据坐标更新悬浮球位置
         * @param x
         * @param y
         */
        private void updateIconViewPosition(float x, float y) {
            iconViewX = (int) x;
            iconViewY = (int) y;
            iconViewLayoutParams.x = (int) x;
            iconViewLayoutParams.y = (int) y;
            windowManager.updateViewLayout(mIconView, iconViewLayoutParams);
        }

        private void transIconView(float x,float y) {
            boolean isLeft = 2 * x <= Device.getScreenWidth();
            boolean isTop = 2 * y <= Device.getScreenHeight();
            final int finalDirection; // 判断悬浮球最终是往屏幕的哪个方向吸附
            int deltaX = (int) (isLeft ? x : Device.getScreenWidth() - x - iconViewLayoutParams.width);
            int deltaY = (int) (isTop ? y : Device.getScreenHeight() - y - iconViewLayoutParams.height);
            if (deltaX <= deltaY)
                finalDirection = isLeft ? DIRECTION_LEFT : DIRECTION_RIGHT;
            else
                finalDirection = isTop ? DIRECTION_TOP : DIRECTION_BOTTOM;
            calculateTrans(finalDirection);
        }

        /**
         * 计算悬浮球的滚动路径
         * @param finalDirection
         */
        private void calculateTrans(final int finalDirection) {
            Observable.create(new ObservableOnSubscribe<Object>() {
                @Override
                public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                    Thread.sleep(10);
                    switch (finalDirection) {
                        case DIRECTION_LEFT:
                            if (iconViewLayoutParams.x - TRANS_SPEED <= 0){
                                iconViewLayoutParams.x = 0;
                            } else
                                iconViewLayoutParams.x = iconViewLayoutParams.x - TRANS_SPEED;
                            break;

                        case DIRECTION_RIGHT:
                            if (iconViewLayoutParams.x + TRANS_SPEED >= Device.getScreenWidth() - iconViewLayoutParams.width / 2) {
                                iconViewLayoutParams.x = Device.getScreenWidth() - iconViewLayoutParams.width / 2;
                            } else
                                iconViewLayoutParams.x = iconViewLayoutParams.x + TRANS_SPEED;
                            break;

                        case DIRECTION_TOP:
                            if (iconViewLayoutParams.y - TRANS_SPEED <= 0) {
                                iconViewLayoutParams.y = 0;
                            } else
                                iconViewLayoutParams.y = iconViewLayoutParams.y - TRANS_SPEED;
                            break;

                        case DIRECTION_BOTTOM:
                            if (iconViewLayoutParams.y + TRANS_SPEED >= Device.getScreenHeight() - iconViewLayoutParams.height / 2) {
                                iconViewLayoutParams.y = Device.getScreenHeight() - iconViewLayoutParams.height / 2;
                            } else
                                iconViewLayoutParams.y = iconViewLayoutParams.y + TRANS_SPEED;
                            break;
                    }
                    emitter.onNext(finalDirection);
                }
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Object>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Object o) {
                            windowManager.updateViewLayout(IconView.this, iconViewLayoutParams);
                            if (iconViewLayoutParams.x != 0 && iconViewLayoutParams.x != Device.getScreenWidth() - iconViewLayoutParams.width / 2 &&
                                    iconViewLayoutParams.y != 0 && iconViewLayoutParams.y != Device.getScreenHeight() - iconViewLayoutParams.height / 2)
                                calculateTrans(finalDirection); // 检测是否到边界，没有到达则继续滚动
                            else {
                                iconViewX = iconViewLayoutParams.x;
                                iconViewY = iconViewLayoutParams.y;
                            }


                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }

    }

    private class MenuViewItemEventDecorator extends BaseMenuItemEventDecerator {

        public MenuViewItemEventDecorator(OnMenuItemEventListener listener) {
            super(listener);
        }

        @Override
        public int beforeItemPerform(View view, int page, int pos) {
            return super.beforeItemPerform(view, page, pos);
        }

        @Override
        public void onItemClick(View view, int page, int pos) {
            super.onItemClick(view, page, pos);
        }

        @Override
        public int afterItemClick(View view, int page, int pos) {
            int item = super.afterItemClick(view, page, pos);
            switch (item) {
                case ACTION_KEEP_MENU:
                    break;
                case ACTION_CLOSE_MENU:
                    addIconView();
                    break;
            }
            return item;
        }

        @Override
        public void onEmptyItemClick(View view, int page, int pos) {
            super.onEmptyItemClick(view, page, pos);
        }

        @Override
        public void onDeleteItemClick(View view, int page, int pos) {
            super.onDeleteItemClick(view, page, pos);
        }
    }

}
