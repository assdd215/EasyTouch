package com.example.aria.easytouch.widget.easytouch.menu;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;

/**
 * Created by Aria on 2017/7/25.
 */

public class MenuViewPager extends ViewPager{

    private float preX = 0;
    private float mPointX = 0;
    private float mPointY = 0;

    public MenuViewPager(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPointX = ev.getX();
                mPointY = ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getX() - mPointX) > Math.abs(ev.getY() - mPointY)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
