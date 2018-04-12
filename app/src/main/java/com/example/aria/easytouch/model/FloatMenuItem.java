package com.example.aria.easytouch.model;

/**
 * Created by Aria on 2017/7/31.
 */


//与ItemModel数据结构通过position形成一种外键关联关系
public class FloatMenuItem {
    public static final int TYPE_SHORTCUT = 0;
    public static final int TYPE_FUNCTION = 1;
    public static final int TYPE_EMPTY = 2;

    private String itemTitleId; // TODO  存储内置功能的标题的id  或者存储内功能app的activityInfo.packageName
    private int position;
    private int type;
    private String titleName;  //TODO label名称
    private int iconId;  //TODO 内置功能的标题的icon id

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
