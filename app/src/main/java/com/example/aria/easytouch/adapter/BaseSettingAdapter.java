package com.example.aria.easytouch.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.model.MenuItem;

import java.util.List;

public abstract class BaseSettingAdapter<D,T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T>{

    protected List<D> itemList;
    protected Context context;
    protected OnItemClickListener onItemClickListener;

    protected BaseSettingAdapter(Context context){
        this.context = context;
        initData();
    }

    abstract protected void initData();

    @NonNull
    @Override
    public T onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return createViewHolder();
    }

    abstract protected T createViewHolder();

    @Override
    public void onBindViewHolder(@NonNull T holder, int position) {
        bindViews(holder,position);
    }

    protected abstract void bindViews(T holder,int position);

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    protected interface OnItemClickListener{
        void onItemClick(View v,int pos);
    }

}


