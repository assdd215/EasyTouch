package com.example.aria.easytouch.widget.easytouch.pager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class EndlessViewPager extends ViewPager{

    private boolean scrollable = true;

    public EndlessViewPager(@NonNull Context context) {
        super(context);
    }

    public EndlessViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (scrollable) {
            if (getCurrentItem() == 0 && getChildCount() == 0)
                return false;
            return super.onTouchEvent(ev);
        }
        return true;
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(this.scrollable) {
            if (getCurrentItem() == 0 && getChildCount() == 0) {
                return false;
            }
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }
}
