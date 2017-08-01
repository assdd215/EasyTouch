package com.example.aria.easytouch.widget.easytouch.menu;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aria on 2017/7/25.
 */

public class MenuPagerAdapter extends PagerAdapter{

    private Context context;
    private List<View> pageList;
    private int totalItems;
    private float preX = 0;

    private View.OnLongClickListener pageLongClickListener;

    public MenuPagerAdapter(Context context){
        this.context = context;
        pageList = new ArrayList<>();
        totalItems = 0;
    }

    public void setPageLongClickListener(View.OnLongClickListener pageLongClickListener) {
        this.pageLongClickListener = pageLongClickListener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(pageList.get(position));
        pageList.get(position).setTag(position);
        return pageList.get(position);
    }

    @Override
    public int getCount() {
        return pageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        View temp = (View) object;
        int i1 = (int) view.getTag();
        int i2 = (int) temp.getTag();
        return i1 == i2;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(pageList.get(position));
    }

    public void addItem(View item){
        totalItems ++;
        if (pageList.size() * 9 < totalItems){
            RelativeLayout layout = new RelativeLayout(context);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            layout.setLayoutParams(params);
            pageList.add(layout);
        }
        RelativeLayout relativeLayout = (RelativeLayout) pageList.get(pageList.size() -1 );
        relativeLayout.addView(item);
        notifyDataSetChanged();
    }

    public void addItem(View item, RelativeLayout.LayoutParams params){
        totalItems ++;
        if (pageList.size() * 9 < totalItems){
            RelativeLayout layout = new RelativeLayout(context);
            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            layout.setLayoutParams(params1);
            pageList.add(layout);

        }
        RelativeLayout relativeLayout = (RelativeLayout) pageList.get((totalItems - 1) / 9);
        relativeLayout.setOnLongClickListener(pageLongClickListener);
        relativeLayout.addView(item,params);
        notifyDataSetChanged();
    }

    public void clearItems(){
        for (View view:pageList){
            RelativeLayout layout = (RelativeLayout) view;
            layout.removeAllViews();
        }
        totalItems = 0;
    }


}
