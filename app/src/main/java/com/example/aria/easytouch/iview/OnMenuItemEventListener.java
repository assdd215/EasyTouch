package com.example.aria.easytouch.iview;

import android.view.View;

public interface OnMenuItemEventListener {

    int ACTION_CLOSE_MENU = 0;
    int ACTION_KEEP_MENU = 1;

    int beforeItemPerform(View view,int page,int pos);
    void onItemClick(View view,int page,int pos);
    int afterItemClick(View view,int page,int pos);
    void onEmptyItemClick(View view,int page,int pos);
    void onDeleteItemClick(View view,int page,int pos);
}
