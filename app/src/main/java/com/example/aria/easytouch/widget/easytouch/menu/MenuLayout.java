package com.example.aria.easytouch.widget.easytouch.menu;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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

    public MenuLayout(Context context, AttributeSet attr){
        super(context,attr);

    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        return super.performAccessibilityAction(action, arguments);
    }



    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean flag = isClickable();
//        if (!flag) return false;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isClickable())return false;
        return super.onTouchEvent(event);
    }

    private boolean isLongPressed(float lastX, float lastY,
                                  float thisX, float thisY,
                                  long lastDownTime, long thisEventTime,
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
