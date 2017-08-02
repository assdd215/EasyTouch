package com.example.aria.easytouch.widget.easytouch.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.model.FloatMenuItem;
import com.example.aria.easytouch.util.Constants;
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

    private MenuViewPager viewPager;
    private MenuPagerAdapter menuPagerAdapter;

    private boolean deleteVisible ;

    public MainView(Context context){
        this.context = context;
        initMainView();
    }

    private View.OnLongClickListener onMenuItemsLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            showDeleteButtons();
            return true;
        }
    };

    private void showDeleteButtons(){
        for (ItemModel itemModel:itemModelList) {
            switch (itemModel.getType()){
                case FloatMenuItem.TYPE_EMPTY:
                    itemModel.getImageView().setImageResource(R.drawable.menu_add);
                    itemModel.getImageView().setVisibility(View.VISIBLE);
                    itemModel.getItemLayout().setClickable(true);
                    break;
//                case FloatMenuItem.TYPE_FUNCTION:
                case FloatMenuItem.TYPE_FUNCTION:
                    itemModel.getItemLayout().setClickable(false);
                    itemModel.getItemLayout().findViewById(R.id.titleView).setClickable(false);
                    itemModel.getItemLayout().findViewById(R.id.iconView).setClickable(false);
                    break;
                case FloatMenuItem.TYPE_SHORTCUT:
                    itemModel.getItemLayout().findViewById(R.id.deleteIcon).setVisibility(View.VISIBLE);
                    itemModel.getItemLayout().setClickable(false);
                    itemModel.getItemLayout().findViewById(R.id.titleView).setClickable(false);
                    itemModel.getItemLayout().findViewById(R.id.iconView).setClickable(false);
                    break;
            }
        }
        deleteVisible = true;
    }

    public void setDeleteVisible(boolean deleteVisible) {
        this.deleteVisible = deleteVisible;
    }

    public void  hideDeleteButtons(){
        for (ItemModel itemModel:itemModelList) {
            switch (itemModel.getType()){
                case FloatMenuItem.TYPE_EMPTY:
                itemModel.getImageView().setVisibility(View.INVISIBLE);
                itemModel.getItemLayout().setClickable(false);
                    break;
//                case FloatMenuItem.TYPE_FUNCTION:
                case FloatMenuItem.TYPE_FUNCTION:
                    itemModel.getItemLayout().setClickable(true);
                    break;
                case FloatMenuItem.TYPE_SHORTCUT:
                    itemModel.getItemLayout().findViewById(R.id.deleteIcon).setVisibility(View.GONE);
                    itemModel.getItemLayout().setClickable(true);
                    break;
            }
        }
        deleteVisible = false;
    }

    private void initMainView() {

        itemModelList = new ArrayList<>(9);
        deleteVisible = false;
        menuPagerAdapter = new MenuPagerAdapter(context);
        menuPagerAdapter.setPageLongClickListener(onMenuItemsLongClickListener);

        baseLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        baseLayout.setLayoutParams(params);
        baseLayout.setBackgroundResource(android.R.color.transparent);
        baseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteVisible)
                    hideDeleteButtons();
                else
                    onMenuHolderEventListener.afterItemClick(v);
            }
        });

        viewPager = new MenuViewPager(context);
        viewPager.setAdapter(menuPagerAdapter);
        viewPager.setOffscreenPageLimit(2);

        menuLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams menuParams = new RelativeLayout.LayoutParams(Utils.dip2px(context,270),Utils.dip2px(context,270));
        menuParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        menuLayout.setLayoutParams(menuParams);
        menuLayout.setBackgroundResource(R.drawable.item_group);
        menuLayout.addView(viewPager);
        baseLayout.addView(menuLayout);

    }

    public View getMainView(){
        return baseLayout;
    }


    public boolean addMenuItem(String itemTitle, Drawable icon, View.OnClickListener onClickListener,int type){
        boolean flag = true;
        MenuLayout itemLayout = (MenuLayout) LayoutInflater.from(context).inflate(R.layout.float_menu_item,null);
        ImageView iconView = (ImageView) itemLayout.findViewById(R.id.iconView);
        TextView titleView = (TextView) itemLayout.findViewById(R.id.titleView);
        ImageView deleteIcon = (ImageView) itemLayout.findViewById(R.id.deleteIcon);
        itemLayout.setOnLongClickListener(onMenuItemsLongClickListener);
        itemLayout.setId(itemModelList.size());
        itemLayout.setTag(itemTitle);
        itemLayout.setOnClickListener(onClickListener);
        titleView.setText(itemTitle);
        iconView.setImageDrawable(icon);

        deleteIcon.setVisibility(View.GONE);
        deleteIcon.setTag(itemTitle);
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout relativeLayout = (RelativeLayout) v.getParent();
                ImageView iconView = (ImageView) relativeLayout.findViewById(R.id.iconView);
                TextView titleView = (TextView) relativeLayout.findViewById(R.id.titleView);
                iconView.setImageResource(R.drawable.menu_add);
                titleView.setVisibility(View.INVISIBLE);
                titleView.setText("");
                relativeLayout.setTag(Constants.TYPE_EMPTY);
                v.setVisibility(View.GONE);
                for (ItemModel itemModel:itemModelList){
                    if (itemModel.getItemLayout() == relativeLayout){
                        itemModel.setItemTitle(Constants.TYPE_EMPTY + relativeLayout.getId());
                        itemModel.setType(FloatMenuItem.TYPE_EMPTY);
                        break;
                    }
                }
                onMenuHolderEventListener.onDeleteIconClick(v);
            }
        });
        RelativeLayout.LayoutParams itemLayoutParams = new RelativeLayout.LayoutParams(Utils.dip2px(context,72),Utils.dip2px(context,72));
        setItemPosition(itemLayoutParams,itemModelList.size()%9);
        menuPagerAdapter.addItem(itemLayout,itemLayoutParams);
        itemModelList.add(new ItemModel(iconView,itemLayout,itemTitle,itemModelList.size(), type));
        return flag;
    }

    public boolean addEmptyItem(){
        boolean flag = true;
        MenuLayout itemLayout = (MenuLayout) LayoutInflater.from(context).inflate(R.layout.float_menu_item,null);

        ImageView iconView = (ImageView) itemLayout.findViewById(R.id.iconView);
        TextView titleView = (TextView) itemLayout.findViewById(R.id.titleView);
        ImageView deleteIcon = (ImageView) itemLayout.findViewById(R.id.deleteIcon);
        itemLayout.setOnLongClickListener(onMenuItemsLongClickListener);
        itemLayout.setId(itemModelList.size());
        itemLayout.setTag(Constants.TYPE_EMPTY);
        itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 点击后展示添加应用的窗口，示例代码参考MyActivity


            }
        });
        itemLayout.setClickable(false);


        titleView.setText("");
        titleView.setVisibility(View.INVISIBLE);
        iconView.setVisibility(View.INVISIBLE);
        deleteIcon.setVisibility(View.GONE);

        RelativeLayout.LayoutParams itemLayoutParams = new RelativeLayout.LayoutParams(Utils.dip2px(context,72),Utils.dip2px(context,72));
        setItemPosition(itemLayoutParams,itemModelList.size() % 9);
        menuPagerAdapter.addItem(itemLayout,itemLayoutParams);
        itemModelList.add(new ItemModel(iconView,itemLayout, Constants.TYPE_EMPTY + itemModelList.size(),itemModelList.size()));
        return flag;

    }

    public void fillItems(){
        if (itemModelList.size() == 0)
            addEmptyItem();
        while (itemModelList.size() % 9 != 0){
            addEmptyItem();
        }
    }

    public void setOnMenuHolderEventListener(OnMenuHolderEventListener onMenuHolderEventListener) {
        this.onMenuHolderEventListener = onMenuHolderEventListener;
    }

    public OnMenuHolderEventListener getOnMenuHolderEventListener() {
        return onMenuHolderEventListener;
    }

    private void setItemPosition(RelativeLayout.LayoutParams params, int position){
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
        if (title == null || "".equals(title.trim()))return null;
        for (ItemModel itemModel: itemModelList){
            if (itemModel.getItemTitle().equals(title))
                return itemModel;
        }
        return null;
    }

    public List<ItemModel> findModelsByTitle(String title){
        List<ItemModel> itemModels = new ArrayList<>();
        if (title == null || "".equals(title.trim())) return itemModels;
        for (ItemModel itemModel:itemModelList){
            if (itemModel.getItemTitle().equals(title))
                itemModels.add(itemModel);
        }
        return itemModels;
    }

    public void clearItems(){
        menuPagerAdapter.clearItems();
        itemModelList.clear();
    }
}
