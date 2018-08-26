package com.example.aria.easytouch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.model.MenuItem;

import java.util.ArrayList;

public class InitSettingAdapter extends BaseSettingAdapter<MenuItem,MenuItemHolder>{
    public InitSettingAdapter(Context context) {
        super(context);
    }

    @Override
    protected void initData() {
        itemList = new ArrayList<>();
        itemList.add(new MenuItem(R.string.menu_enable_boot,R.string.menu_enable_boot_sub,false));
        onItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                switch (itemList.get(pos).titleId){
                    case R.string.menu_enable_boot:

                }
            }
        };
    }

    @Override
    protected MenuItemHolder createViewHolder() {
        MenuItemHolder holder = new MenuItemHolder(LayoutInflater.from(context).inflate(R.layout.item_menu,null,false));
        return holder;
    }

    @Override
    protected void bindViews(MenuItemHolder holder, int position) {
        holder.subTitle.setText(itemList.get(position).subTitleId);
        holder.subTitle.setVisibility(View.VISIBLE);
        holder.title.setText(itemList.get(position).titleId);
        holder.aSwitch.setVisibility(itemList.get(position).withSwitch?View.VISIBLE:View.GONE);
        holder.icon.setVisibility(View.GONE);
    }
}
