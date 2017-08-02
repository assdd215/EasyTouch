package com.example.aria.easytouch.widget.easytouch;

import android.view.View;

/**
 * Created by Aria on 2017/7/21.
 */

public interface OnMenuHolderEventListener {
    void beforeItemPerform(View view);
    void afterItemClick(View view);
    void onDeleteIconClick(View view);
}
