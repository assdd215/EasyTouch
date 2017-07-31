package com.example.aria.easytouch.widget.easytouch.menu;

import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Aria on 2017/7/21.
 */

public class ItemModel {
    private ImageView imageView;
    private LinearLayout itemLayout;
    private String itemTitle;
    private int position;

    public ItemModel(ImageView imageView,LinearLayout itemLayout,String  itemTitle,int position){
        this.imageView = imageView;
        this.itemLayout = itemLayout;
        this.itemTitle = itemTitle;
        this.position = position;
    }

    public ItemModel(){}

    public void setPosition(int position) {
        this.position = position;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setItemLayout(LinearLayout itemLayout) {
        this.itemLayout = itemLayout;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public LinearLayout getItemLayout() {
        return itemLayout;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public int getPosition() {
        return position;
    }
}
