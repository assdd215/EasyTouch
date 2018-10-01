package com.example.aria.easytouch.iview;

import android.view.View;

public class BaseMenuItemEventDecerator implements OnMenuItemEventListener{

    private OnMenuItemEventListener listener;

    public BaseMenuItemEventDecerator(OnMenuItemEventListener listener) {
        this.listener = listener;
    }

    @Override
    public int beforeItemPerform(View view, int page, int pos) {
        return listener.beforeItemPerform(view,page,pos);
    }

    @Override
    public void onItemClick(View view, int page, int pos) {
        listener.onItemClick(view, page, pos);
    }

    @Override
    public int afterItemClick(View view, int page, int pos) {
        return listener.afterItemClick(view, page, pos);
    }

    @Override
    public void onEmptyItemClick(View view, int page, int pos) {
        listener.afterItemClick(view, page, pos);
    }

    @Override
    public void onDeleteItemClick(View view, int page, int pos) {
        listener.onDeleteItemClick(view, page, pos);
    }
}
