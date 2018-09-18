package com.example.aria.easytouch.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class MyLayout extends FrameLayout {

    private float downY ;

    public MyLayout(@NonNull Context context) {
        super(context);
    }

    public MyLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        switch (event.getActionMasked()) {
//            case MotionEvent.ACTION_DOWN:
//                Log.d("MainActivity","down");
//                downY = event.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Log.d("MainActivity","move");
//                scrollTo(0, (int) (event.getY() - downY));
//                break;
//
//        }
//        Log.d("MainActivity","event end");
//        return super.onTouchEvent(event);
//    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("MainActivity","down");
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                scrollTo((int) event.getX(), (int) (event.getY() - downY));
                Log.d("MainActivity","move:" + (event.getY() - downY));
                break;
        }

        return true ;
    }
}
