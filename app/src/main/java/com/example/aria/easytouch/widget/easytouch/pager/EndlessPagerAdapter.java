package com.example.aria.easytouch.widget.easytouch.pager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public abstract class EndlessPagerAdapter<T> extends PagerAdapter{

    protected Context context;
    protected List<T> list;
    protected List<View> viewList;


    public EndlessPagerAdapter(Context context,List<T> list) {
        this.context = context;
        this.list = list;
        initView();
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    private void initView() {
        viewList = new ArrayList<>();
        for (int i = 0; i <= list.size() + 1; i ++) {
            View view;
            if (i == 0)
                view = initItemView(list.size() - 1);
            else if (i == list.size() + 1)
                view = initItemView(0);
            else
                view = initItemView(i - 1);
            viewList.add(view);
        }
    }

    public List<T> getList() {
        return list;
    }

    protected abstract View initItemView(int position);

    public void updateList(List<T> list) {
        this.list = list;
        viewList.clear();
        initView();
    }

}
