package com.example.aria.easytouch.model;

public class MenuItem {
    public int imgId;
    public int titleId;
    public int subTitleId;
    public boolean withSwitch = false;
    public MenuItem(int imgId,int titleId){
        this.imgId = imgId;
        this.titleId = titleId;
    }

    public MenuItem(){}

    public MenuItem(int titleId,int subTitleId,boolean withSwitch){
        this.titleId = titleId;
        this.subTitleId = subTitleId;
        this.withSwitch = withSwitch;
    }
}

