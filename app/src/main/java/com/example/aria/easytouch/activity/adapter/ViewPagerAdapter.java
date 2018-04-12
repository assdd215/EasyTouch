package com.example.aria.easytouch.activity.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Aria on 2017/7/25.
 */

public class ViewPagerAdapter extends PagerAdapter{

    private Context context;
    private List<View> itemList;

    public ViewPagerAdapter(Context context, List<View> itemList){
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(itemList.get(position));
        return itemList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        container.removeView(itemList.get(position));
    }
}
