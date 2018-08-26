package com.example.aria.easytouch.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.assistivetool.booster.easytouch.R;

public class MenuItemHolder extends RecyclerView.ViewHolder{

    public TextView title;
    public TextView subTitle;
    public TextView hint;
    public ImageView icon;
    public RelativeLayout root;
    public Switch aSwitch;
    public MenuItemHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.item_title);
        subTitle = itemView.findViewById(R.id.item_sub_title);
        icon = itemView.findViewById(R.id.item_icon);
        root = itemView.findViewById(R.id.root);
        aSwitch = itemView.findViewById(R.id.item_switch);
        hint = itemView.findViewById(R.id.item_hint);
    }
}
