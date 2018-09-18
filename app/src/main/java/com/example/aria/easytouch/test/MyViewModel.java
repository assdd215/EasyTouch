package com.example.aria.easytouch.test;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

public class MyViewModel extends AndroidViewModel {

    private String s = "hhhh";

    public MyViewModel(@NonNull Application application) {
        super(application);
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }
}
