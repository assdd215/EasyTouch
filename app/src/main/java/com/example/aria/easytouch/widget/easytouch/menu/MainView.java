package com.example.aria.easytouch.widget.easytouch.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.aria.easytouch.R;
import com.example.aria.easytouch.util.Utils;
import com.example.aria.easytouch.widget.easytouch.OnMenuHolderEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aria on 2017/7/21.
 */

public class MainView {

    private Context context;
    private RelativeLayout baseLayout;
    private RelativeLayout menuLayout;
    private OnMenuHolderEventListener onMenuHolderEventListener;
    private List<ItemModel> itemModelList;



    public MainView(Context context){
        this.context = context;
        initMainView();
    }

    private void initMainView() {

        itemModelList = new ArrayList<>(9);

        baseLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        baseLayout.setLayoutParams(params);
        baseLayout.setBackgroundResource(android.R.color.transparent);
        baseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuHolderEventListener.afterItemClick(v);
            }
        });

        menuLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams menuParams = new RelativeLayout.LayoutParams(Utils.dip2px(context,270),Utils.dip2px(context,270));
        menuParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        menuLayout.setLayoutParams(menuParams);
        menuLayout.setBackgroundResource(R.drawable.item_group);
        baseLayout.addView(menuLayout);

    }

    public View getMainView(){
        return baseLayout;
    }

    public boolean addMenuItem(String itemTitle, Drawable icon){
        return addMenuItem(itemTitle,icon,null);

    }

    public boolean addMenuItem(String itemTitle, Drawable icon, View.OnClickListener onClickListener){
        boolean flag = true;
        LinearLayout itemLayout = new LinearLayout(context);
        ImageView iconView = new ImageView(context);
        TextView titleView = new TextView(context);

        itemLayout.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams itemLayoutParams = new RelativeLayout.LayoutParams(Utils.dip2px(context,72),Utils.dip2px(context,72));
        itemLayout.setOnClickListener(onClickListener);
        itemLayout.setTag(itemTitle);

        iconView.setImageDrawable(icon);
        iconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams itemIconParams = new LinearLayout.LayoutParams(Utils.dip2px(context,32),Utils.dip2px(context,32));
        itemIconParams.gravity = Gravity.CENTER;

        titleView.setText(itemTitle);
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);
        titleView.setTextSize(12);
        titleView.setTextColor(context.getResources().getColor(R.color.colorWhite));
        LinearLayout.LayoutParams itemTitleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        itemTitleParams.setMargins(0,Utils.dip2px(context,5),0,0);
        itemTitleParams.gravity = Gravity.CENTER_HORIZONTAL;


        itemLayout.addView(iconView,itemIconParams);
        itemLayout.addView(titleView,itemTitleParams);
        setItemPosition(itemLayoutParams,itemModelList.size());
        menuLayout.addView(itemLayout,itemLayoutParams);

        itemModelList.add(new ItemModel(iconView,itemLayout,itemTitle,itemModelList.size()));
        return flag;
    }

    public void setOnMenuHolderEventListener(OnMenuHolderEventListener onMenuHolderEventListener) {
        this.onMenuHolderEventListener = onMenuHolderEventListener;
    }

    public OnMenuHolderEventListener getOnMenuHolderEventListener() {
        return onMenuHolderEventListener;
    }

    private void setItemPosition(RelativeLayout.LayoutParams params, int position){
        Log.d("MainActivity","position:"+position);
        switch (position){
            case 0:
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//                params.addRule(RelativeLayout.ALIGN_PARENT_START);
                break;
            case 1:
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                break;
            case 2:
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                break;
            case 3:
                params.addRule(RelativeLayout.CENTER_VERTICAL);
//                params.addRule(RelativeLayout.ALIGN_PARENT_START);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                break;
            case 4:
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                break;
            case 5:
                params.addRule(RelativeLayout.CENTER_VERTICAL);
//                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                break;
            case 6:
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                params.addRule(RelativeLayout.ALIGN_PARENT_START);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                break;
            case 7:
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                break;
            case 8:
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                break;
        }
    }

    public ItemModel findViewByPosition(int position){
        if (position >= itemModelList.size())return null;
        return itemModelList.get(position);
    }

    public ItemModel findViewByTitle(String title){
        if ("".equals(title)||"".equals(title.trim()))return null;
        for (ItemModel itemModel: itemModelList){
            if (itemModel.getItemTitle().equals(title)){
                Log.d("MainActivity","title:"+title);
                return itemModel;
            }
        }
        Log.d("MainActivity","return null");
        return null;
    }
}
