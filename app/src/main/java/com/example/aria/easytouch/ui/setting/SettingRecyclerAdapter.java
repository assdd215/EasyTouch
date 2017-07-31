package com.example.aria.easytouch.ui.setting;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aria.easytouch.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Aria on 2017/7/25.
 */

public class SettingRecyclerAdapter extends RecyclerView.Adapter{

    private Context context;
    private List<String> items;
    private OnItemClickListener onItemClickListener;

    public SettingRecyclerAdapter(@NotNull Context context,@NotNull List<String> items){
        this.context = context;
        this.items = items;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder holder = new SettingItemViewHolder(LayoutInflater.from(context).inflate(R.layout.settings_item,parent,false));

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SettingItemViewHolder){
            SettingItemViewHolder settingItemViewHolder = (SettingItemViewHolder) holder;
            settingItemViewHolder.item_textview.setText(items.get(position));
            settingItemViewHolder.item_textview.setTag(items.get(position));
            settingItemViewHolder.item_textview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(View view);
    }
}


class SettingItemViewHolder extends RecyclerView.ViewHolder{

    public TextView item_textview;

    public SettingItemViewHolder(View itemView) {
        super(itemView);
        item_textview = (TextView) itemView.findViewById(R.id.item_textview);
    }
}