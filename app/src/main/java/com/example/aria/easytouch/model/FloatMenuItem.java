package com.example.aria.easytouch.model;

/**
 * Created by Aria on 2017/7/31.
 */

public class FloatMenuItem {
    public static final int TYPE_SHORTCUT = 0;
    public static final int TYPE_FUNCTION = 1;
    public static final int TYPE_EMPTY = 2;

    private String itemTitleId;
    private int position;
    private int type;
    private String titleName;
    private int iconId;

    public FloatMenuItem(){}

    public void setType(int type) {
        this.type = type;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setItemTitleId(String itemTitleId) {
        this.itemTitleId = itemTitleId;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getItemTitleId() {
        return itemTitleId;
    }

    public int getType() {
        return type;
    }

    public int getPosition() {
        return position;
    }

    public int getIconId() {
        return iconId;
    }

    public String getTitleName() {
        return titleName;
    }
}
