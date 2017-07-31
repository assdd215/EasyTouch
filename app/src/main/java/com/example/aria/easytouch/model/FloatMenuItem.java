package com.example.aria.easytouch.model;

/**
 * Created by Aria on 2017/7/31.
 */

public class FloatMenuItem {
    public static final int TYPE_SHORTCUT = 0;
    public static final int TYPE_FUNCTION = 1;
    public static final int TYPE_EMPTY = 2;

    private int itemTitleId;
    private int position;
    private int type;
    private int iconId;
    private String packageName;

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

    public void setItemTitleId(int itemTitleId) {
        this.itemTitleId = itemTitleId;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getItemTitleId() {
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

    public String getPackageName() {
        return packageName;
    }
}
