package com.example.aria.easytouch.widget.easytouch.menu;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.aria.easytouch.model.FloatMenuItem;

/**
 * Created by Aria on 2017/7/21.
 */

public class ItemModel {
    private ImageView imageView;
    private RelativeLayout itemLayout;
    private String itemTitle;
    private int position;
    private int type;

    public ItemModel(ImageView imageView,RelativeLayout itemLayout,String  itemTitle,int position){
        this.imageView = imageView;
        this.itemLayout = itemLayout;
        this.itemTitle = itemTitle;
        this.position = position;
        this.type = FloatMenuItem.TYPE_EMPTY;
    }

    public ItemModel(ImageView imageView,RelativeLayout itemLayout,String  itemTitle,int position,int type){
        this.imageView = imageView;
        this.itemLayout = itemLayout;
        this.itemTitle = itemTitle;
        this.position = position;
        this.type = type;
    }

    public ItemModel(){}

    public void setPosition(int position) {
        this.position = position;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setItemLayout(RelativeLayout itemLayout) {
        this.itemLayout = itemLayout;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public RelativeLayout getItemLayout() {
        return itemLayout;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public int getPosition() {
        return position;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}

