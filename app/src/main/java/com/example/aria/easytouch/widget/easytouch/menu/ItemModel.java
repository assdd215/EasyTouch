package com.example.aria.easytouch.widget.easytouch.menu;

import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Aria on 2017/7/21.
 */

public class ItemModel {

    public static final int TYPE_SHORTCUT = 0;
    public static final int TYPE_FUNCTION = 1;
    public static final int TYPE_EMPTY = 2;

    private ImageView imageView;
    private LinearLayout itemLayout;
    private String itemTitle;
    private int position;
    private int iconId;
    private int type;

    public ItemModel(ImageView imageView,LinearLayout itemLayout,String  itemTitle,int position){
        this.imageView = imageView;
        this.itemLayout = itemLayout;
        this.itemTitle = itemTitle;
        this.position = position;
    }

    public ItemModel(){}

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public int getIconId() {
        return iconId;
    }

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

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}

