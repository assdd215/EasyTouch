package com.example.aria.easytouch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.model.MenuItem;

import java.util.ArrayList;

public class GestureSettingAdapter extends BaseSettingAdapter<MenuItem,MenuItemHolder>{

    protected GestureSettingAdapter(Context context) {
        super(context);
    }

    @Override
    protected void initData() {
        itemList = new ArrayList<>();
        itemList.add(new MenuItem(R.drawable.gesture_click,R.string.menu_gesture_click));
        itemList.add(new MenuItem(R.drawable.gesture_double_click,R.string.menu_gesture_double_click));
        itemList.add(new MenuItem(R.drawable.gesture_long_click,R.string.menu_gesture_long_click));
        itemList.add(new MenuItem(R.drawable.gesture_three_click,R.string.menu_gesture_three_click));

    }

    @Override
    protected MenuItemHolder createViewHolder() {
        MenuItemHolder holder = new MenuItemHolder(LayoutInflater.from(context).inflate(R.layout.item_menu,null,false));
        return holder;
    }

    @Override
    protected void bindViews(MenuItemHolder holder, int position) {
        holder.hint.setVisibility(View.VISIBLE);
        holder.hint.setText("空");
        holder.title.setText(itemList.get(position).titleId);
        holder.subTitle.setVisibility(View.GONE);
        holder.icon.setImageResource(itemList.get(position).imgId);
        holder.aSwitch.setVisibility(View.GONE);
    }
}
