package com.example.aria.easytouch.model;

public class NewMenuItem {

    public static final int TYPE_FEATURES = 1;
    public static final int TYPE_EMPTY = 2;
    public static final int TYPE_APPS = 3;
    private String itemTitle;
    private int iconId;
    private int type;

    public NewMenuItem(String itemTitle, int iconId, int type) {
        this.itemTitle = itemTitle;
        this.iconId = iconId;
        this.type = type;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static NewMenuItem EMTY_INSTANCE = new NewMenuItem("",-1,TYPE_EMPTY);
}
