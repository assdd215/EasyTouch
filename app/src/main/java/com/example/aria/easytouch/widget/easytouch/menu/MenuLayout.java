package com.example.aria.easytouch.widget.easytouch.menu;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by Aria on 2017/8/1.
 */

public class MenuLayout extends RelativeLayout{

    private boolean isLongClickModule = false;
    float xDown,yDown;

    public MenuLayout(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return super.onInterceptTouchEvent(ev);
    }

    private boolean isLongPressed(float lastX,float lastY,
                                  float thisX,float thisY,
                                  long lastDownTime,long thisEventTime,
                                  long longPressTime){
        float offsetX = Math.abs(thisX - lastX);
        float offsetY = Math.abs(thisY - lastY);
        long intervalTime = thisEventTime - lastDownTime;
        if(offsetX <= 10 && offsetY <= 10 && intervalTime >= longPressTime){
            return true;
        }
        return false;
    }
}
