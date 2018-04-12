package com.example.aria.easytouch.widget.easytouch.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.model.FloatMenuItem;
import com.example.aria.easytouch.util.Utils;
import com.example.aria.easytouch.widget.easytouch.OnMenuHolderEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aria on 2017/7/21.
 */

public class MainView {

    private static final int ITEM_SIZE = 72;

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

    private View.OnClickListener onEmptyItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onMenuHolderEventListener.onEmptyItemClick(v);
        }
    };

    private void showDeleteButtons(){
        for (ItemModel itemModel:itemModelList) {
            switch (itemModel.getType()){
                case FloatMenuItem.TYPE_EMPTY:
                    itemModel.getImageView().setImageResource(R.drawable.menu_add);
                    itemModel.getImageView().setVisibility(View.VISIBLE);
                    itemModel.getItemLayout().setClickable(true);
                    itemModel.getItemLayout().setVisibility(View.VISIBLE);
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
        ItemModel itemModel = new ItemModel();
        itemModel.setItemTitle("baseLayout");
        itemModel.setPosition(-1);
        baseLayout.setTag(itemModel);
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
        itemLayout.setId(itemModelList.size()); //itemLayout的id即为对应的pos


        switch (type){
            case FloatMenuItem.TYPE_SHORTCUT:
            case FloatMenuItem.TYPE_FUNCTION:
                itemLayout.setOnClickListener(onClickListener);
                titleView.setText(itemTitle);
                iconView.setImageDrawable(icon);
                break;

            case FloatMenuItem.TYPE_EMPTY:
                itemTitle = "";
                titleView.setVisibility(View.GONE);
                iconView.setVisibility(View.INVISIBLE);
                itemLayout.setOnClickListener(onEmptyItemClickListener);
                itemLayout.setClickable(false);
                break;
        }

        deleteIcon.setVisibility(View.GONE);
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout relativeLayout = (RelativeLayout) v.getParent();
                ImageView iconView = (ImageView) relativeLayout.findViewById(R.id.iconView);
                TextView titleView = (TextView) relativeLayout.findViewById(R.id.titleView);

                relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onMenuHolderEventListener.onEmptyItemClick(v);
                    }
                });

                ItemModel model = (ItemModel) relativeLayout.getTag();
                model.setType(FloatMenuItem.TYPE_EMPTY);
                model.setItemTitle("");

                iconView.setImageResource(R.drawable.menu_add);
                titleView.setText(model.getItemTitle());
                titleView.setVisibility(View.GONE);
                v.setVisibility(View.GONE);

                onMenuHolderEventListener.onDeleteIconClick(v);
            }
        });


        RelativeLayout.LayoutParams itemLayoutParams = new RelativeLayout.LayoutParams(Utils.dip2px(context,ITEM_SIZE),Utils.dip2px(context, ITEM_SIZE));
        setItemPosition(itemLayoutParams,itemModelList.size() % 9);
        menuPagerAdapter.addItem(itemLayout,itemLayoutParams);


        ItemModel itemModel = new ItemModel(iconView,itemLayout,itemTitle,itemModelList.size(), type);
        itemLayout.setTag(itemModel);
        itemModelList.add(itemModel);
        return flag;
    }

    public boolean addEmptyItem(){
        return addMenuItem(null,null,null,FloatMenuItem.TYPE_EMPTY);
    }

    public boolean updateItemByPosition(String itemTitle, Drawable icon, View.OnClickListener onClickListener,int type,int position){
        boolean flag = true;
        if (position >= itemModelList.size())
            return false;

        //更新数据
        ItemModel model = itemModelList.get(position);
        model.setType(type);

        ImageView iconView = model.getImageView();
        RelativeLayout itemLayout = model.getItemLayout();
        TextView titleView = (TextView) itemLayout.findViewById(R.id.titleView);
        ImageView deleteIcon = (ImageView) itemLayout.findViewById(R.id.deleteIcon);
        switch (type){
            case FloatMenuItem.TYPE_SHORTCUT:
            case FloatMenuItem.TYPE_FUNCTION:
                model.setItemTitle(itemTitle);
                iconView.setImageDrawable(icon);
                itemLayout.setOnClickListener(onClickListener);
                break;
            case FloatMenuItem.TYPE_EMPTY:
                model.setItemTitle("");
                iconView.setImageResource(R.drawable.menu_add);
                titleView.setVisibility(View.INVISIBLE);
                deleteIcon.setVisibility(View.GONE);
                break;
        }

        titleView.setText(model.getItemTitle());
        itemLayout.setTag(model);
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

    private void setItemPosition(RelativeLayout.LayoutParams params, int position){
        switch (position){
            case 0:
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                break;
            case 1:
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                break;
            case 2:
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                break;
            case 3:
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                break;
            case 4:
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                break;
            case 5:
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                break;
            case 6:
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                break;
            case 7:
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                break;
            case 8:
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                break;
        }
    }

    public ItemModel findViewByPosition(int position){
        if (position >= itemModelList.size())return null;
        return itemModelList.get(position);
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
