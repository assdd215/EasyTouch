package com.example.aria.easytouch.model;

/**
 * Created by Aria on 2017/7/31.
 */

public enum  FunctionMenuItem {
    TEL(101),MESSAGE(102),CAMERA(103),SCREENSHOT(104),BLUETOOTH(105),WIFI(106),BROWSER(107),FLASHLIGHT(108),HOME(109),BOOST(110);
    int value;
    FunctionMenuItem(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
