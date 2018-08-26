package com.example.aria.easytouch.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import java.lang.ref.WeakReference;

public class StaticHandler extends Handler{
    private WeakReference<Context> contextWeakReference = null;
    private OnMessageListener msgListener;
    public void setContext(Context context){
        this.contextWeakReference = new WeakReference<Context>(context);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        msgListener.handlerMsg(msg);

    }

    public void setMsgListener(OnMessageListener msgListener) {
        this.msgListener = msgListener;
    }

    public WeakReference<Context> getContext() {
        return contextWeakReference;
    }

    public interface OnMessageListener{
        void handlerMsg(Message msg);
    }
}
