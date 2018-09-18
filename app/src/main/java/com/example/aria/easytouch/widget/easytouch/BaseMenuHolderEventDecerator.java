package com.example.aria.easytouch.widget.easytouch;

import android.view.View;

public class BaseMenuHolderEventDecerator implements OnMenuHolderEventListener{

    private OnMenuHolderEventListener listener;

    public BaseMenuHolderEventDecerator(OnMenuHolderEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void beforeItemPerform(View view) {
        listener.beforeItemPerform(view);
    }

    @Override
    public void afterItemClick(View view) {
        listener.afterItemClick(view);
    }

    @Override
    public void onDeleteIconClick(View view) {
        listener.onDeleteIconClick(view);
    }

    @Override
    public void onEmptyItemClick(View view) {
        listener.onEmptyItemClick(view);
    }
}
