package com.example.aria.easytouch.widget.easytouch.menu;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.iview.OnMenuItemEventListener;
import com.example.aria.easytouch.model.FloatMenuItem;
import com.example.aria.easytouch.model.MenuPageModel;
import com.example.aria.easytouch.model.NewMenuItem;
import com.example.aria.easytouch.widget.easytouch.pager.EndlessPagerAdapter;
import com.example.aria.easytouch.widget.easytouch.pager.EndlessViewPager;
import com.luzeping.aria.commonutils.utils.Device;
import com.luzeping.aria.commonutils.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aria on 2017/7/21.
 */

public class MenuView extends RelativeLayout implements ViewPager.OnPageChangeListener,View.OnClickListener,View.OnLongClickListener {

    private static final int ITEM_SIZE = 72;
    public static final int ITEM_SATE_NORMAL = 1;
    public static final int ITEM_STATE_DELETE_VISIBLE = 2;

    private OnMenuItemEventListener onMenuItemEventListener;

    @BindView(R.id.root)
    RelativeLayout root;
    @BindView(R.id.tv_search)
    AppCompatTextView tvSearch;
    @BindView(R.id.rl_menu_table)
    RelativeLayout menuTableLayout;
    @BindView(R.id.viewPager)
    EndlessViewPager viewPager;
    MenuPagerAdapter pagerAdapter;
    int currentItem;

    public int ITEM_STATE = ITEM_SATE_NORMAL;
    private MenuPageModel emptyPageModel;

    private DataSetObserver adapterDatasetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (ITEM_STATE == ITEM_STATE_DELETE_VISIBLE) {
                changeStateFromNornal2Editable();
            } else if (ITEM_STATE == ITEM_SATE_NORMAL) {
                changeStateFromEditable2Normal();
            }
            pagerAdapter.unregisterDataSetObserver(adapterDatasetObserver);
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    };

    private void updateMenuViewState() {
        pagerAdapter.registerDataSetObserver(adapterDatasetObserver);
        switch (ITEM_STATE) {
            case ITEM_SATE_NORMAL:
                pagerAdapter.getList().remove(emptyPageModel);
                break;
            case ITEM_STATE_DELETE_VISIBLE:
                if (emptyPageModel == null) emptyPageModel = new MenuPageModel();
                if (!pagerAdapter.getList().contains(emptyPageModel)) pagerAdapter.getList().add(emptyPageModel);
                break;
        }
        pagerAdapter.notifyDataSetChanged();
    }

    private void changeStateFromEditable2Normal() {
        List<MenuPageModel> pageModelList = pagerAdapter.getList();
        for (int page = 0; page < pageModelList.size(); page++) {
            MenuPageModel pageModel = pageModelList.get(page);
            for (int pos = 0; pos < 9; pos ++) {
                View itemView = pagerAdapter.pageItemViewList.get(page + 1)[pos];
                AppCompatImageView iconDelete = itemView.findViewById(R.id.icon_delete);
                AppCompatImageView icon = itemView.findViewById(R.id.icon);
                AppCompatImageView iconAdd = itemView.findViewById(R.id.icon_add);
                if (pos < pageModel.menuItems.size()) {
                    iconDelete.setVisibility(INVISIBLE);
                }else {
                    iconAdd.setVisibility(INVISIBLE);
                    icon.setVisibility(VISIBLE);
                }
                if (page == 0) {
                    View itemView1 = pagerAdapter.pageItemViewList.get(pagerAdapter.pageItemViewList.size() - 1)[pos];
                    AppCompatImageView iconDelete1 = itemView1.findViewById(R.id.icon_delete);
                    AppCompatImageView icon1 = itemView1.findViewById(R.id.icon);
                    AppCompatImageView iconAdd1 = itemView1.findViewById(R.id.icon_add);
                    if (pos < pageModel.menuItems.size()) {
                        iconDelete1.setVisibility(INVISIBLE);
                    }else {
                        iconAdd1.setVisibility(INVISIBLE);
                        icon1.setVisibility(VISIBLE);
                    }
                }

                if (page == pageModelList.size() - 1) {
                    View itemView2 = pagerAdapter.pageItemViewList.get(0)[pos];
                    AppCompatImageView iconDelete2 = itemView2.findViewById(R.id.icon_delete);
                    AppCompatImageView icon2 = itemView2.findViewById(R.id.icon);
                    AppCompatImageView iconAdd2 = itemView2.findViewById(R.id.icon_add);
                    if (pos < pageModel.menuItems.size()) {
                        iconDelete2.setVisibility(INVISIBLE);
                    }else {
                        iconAdd2.setVisibility(INVISIBLE);
                        icon2.setVisibility(VISIBLE);
                    }
                }
            }
        }
        if (pageModelList.size() == 1) viewPager.setScrollable(false);
    }

    private void changeStateFromNornal2Editable() {
        List<MenuPageModel> pageModelList = pagerAdapter.getList();
        for (int page = 0; page < pageModelList.size(); page++) {
            MenuPageModel pageModel = pageModelList.get(page);
            for (int pos = 0; pos < 9; pos ++) {
                View itemView = pagerAdapter.pageItemViewList.get(page + 1)[pos];
                AppCompatImageView iconDelete = itemView.findViewById(R.id.icon_delete);
                AppCompatImageView icon = itemView.findViewById(R.id.icon);
                AppCompatImageView iconAdd = itemView.findViewById(R.id.icon_add);
                if (pos < pageModel.menuItems.size()) {
                    iconDelete.setVisibility(VISIBLE);
                }else {
                    iconAdd.setVisibility(VISIBLE);
                    icon.setVisibility(GONE);
                }
                if (page == 0) {
                    View itemView1 = pagerAdapter.pageItemViewList.get(pagerAdapter.pageItemViewList.size() - 1)[pos];
                    AppCompatImageView iconDelete1 = itemView1.findViewById(R.id.icon_delete);
                    AppCompatImageView icon1 = itemView1.findViewById(R.id.icon);
                    AppCompatImageView iconAdd1 = itemView1.findViewById(R.id.icon_add);
                    if (pos < pageModel.menuItems.size()) {
                        iconDelete1.setVisibility(VISIBLE);
                    }else {
                        iconAdd1.setVisibility(VISIBLE);
                        icon1.setVisibility(GONE);
                    }
                }

                if (page == pageModelList.size() - 1) {
                    View itemView2 = pagerAdapter.pageItemViewList.get(0)[pos];
                    AppCompatImageView iconDelete2 = itemView2.findViewById(R.id.icon_delete);
                    AppCompatImageView icon2 = itemView2.findViewById(R.id.icon);
                    AppCompatImageView iconAdd2 = itemView2.findViewById(R.id.icon_add);
                    if (pos < pageModel.menuItems.size()) {
                        iconDelete2.setVisibility(VISIBLE);
                    }else {
                        iconAdd2.setVisibility(VISIBLE);
                        icon2.setVisibility(GONE);
                    }
                }
                itemView.setOnClickListener(MenuView.this);
                itemView.setOnLongClickListener(MenuView.this);
            }
        }
        viewPager.setScrollable(true);
    }

    public MenuView(Context context) {
        this(context,null);
    }

    public MenuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_menu_view,this,true);
        ButterKnife.bind(view,this);
    }

    public void init(List<MenuPageModel> modelList) {
        if (modelList == null || modelList.size() == 0)return;
        //TODO 初始化viewPager
        if (pagerAdapter == null) {
            pagerAdapter = new MenuPagerAdapter(getContext(),modelList);
            viewPager.setAdapter(pagerAdapter);
        }
        else
            pagerAdapter.updateList(modelList);
        currentItem = 1;
        viewPager.setCurrentItem(1);
        viewPager.addOnPageChangeListener(this);
        root.setOnClickListener(this);
        if (modelList.size() == 1) viewPager.setScrollable(false);
    }

    public boolean addMenuItem(String itemTitle, Drawable icon, View.OnClickListener onClickListener,int type){
        return false;
    }

    public boolean addEmptyItem(){
        return addMenuItem(null,null,null,FloatMenuItem.TYPE_EMPTY);
    }

    public boolean updateItemByPosition(String itemTitle, Drawable icon, View.OnClickListener onClickListener,int type,int position){
        boolean flag = true;
        return flag;
    }

    public void fillItems(){
    }

    public ItemModel findViewByPosition(int position){
        return null;
    }

    public List<ItemModel> findModelsByTitle(String title){
        return null;
    }

    public void clearItems(){
    }

    public void setOnMenuItemEventListener(OnMenuItemEventListener onMenuItemEventListener) {
        this.onMenuItemEventListener = onMenuItemEventListener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        currentItem = position;
        if (position == 0) position = pagerAdapter.getList().size();
        if (position > pagerAdapter.getList().size()) position = 1;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:

                if (currentItem == 0)
                    viewPager.setCurrentItem(pagerAdapter.getList().size(),false);
                else if (currentItem == pagerAdapter.getList().size() + 1)
                    viewPager.setCurrentItem(1,false);
                break;

            case ViewPager.SCROLL_STATE_DRAGGING:
                if (currentItem == pagerAdapter.getList().size() + 1) {
                    viewPager.setCurrentItem(1,false);
                } else if (currentItem == 0 ) {
                    viewPager.setCurrentItem(pagerAdapter.getList().size(),false);
                }
                break;

            case ViewPager.SCROLL_STATE_SETTLING:
                break;
        }
    }

    /**
     * 更新icon
     * 按照数据集的逻辑来做
     * @param oldIcon
     * @param newIcon
     */
    public void updateIconByIconId(int oldIcon,int newIcon) {
        List<View[]> pageItemViewList = pagerAdapter.pageItemViewList;
        List<MenuPageModel> pageModelList = pagerAdapter.getList();
        for (int page = 0; page < pageModelList.size(); page ++) {
            MenuPageModel pageModel = pageModelList.get(page);
            for (int pos = 0; pos < pageModel.menuItems.size();pos ++) {
                NewMenuItem menuItem = pageModel.menuItems.get(pos);
                if (menuItem.getType() == NewMenuItem.TYPE_FEATURES && menuItem.getIconId() == oldIcon) {
                    AppCompatImageView icon = pageItemViewList.get(page + 1)[pos].findViewById(R.id.icon);
                    icon.setImageResource(newIcon);
                    menuItem.setIconId(newIcon);
                    if (page == 0) {
                        AppCompatImageView icon1 = pageItemViewList.get(pageItemViewList.size() - 1)[pos].findViewById(R.id.icon);
                        icon1.setImageResource(newIcon);
                    }
                    if (page == pageModelList.size() -1) {
                        AppCompatImageView icon2 = pageItemViewList.get(0)[pos].findViewById(R.id.icon);
                        icon2.setImageResource(newIcon);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (onMenuItemEventListener != null) {
            if (ITEM_STATE == ITEM_SATE_NORMAL)
                onMenuItemEventListener.afterItemClick(v,-1,-1);
            else if (ITEM_STATE == ITEM_STATE_DELETE_VISIBLE) {
                ITEM_STATE = ITEM_SATE_NORMAL;
                changeStateFromEditable2Normal();
            }

        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (ITEM_STATE == ITEM_STATE_DELETE_VISIBLE) return true;
        ITEM_STATE = ITEM_STATE_DELETE_VISIBLE;
        changeStateFromNornal2Editable();
        return true;
    }

    public class MenuPagerAdapter extends EndlessPagerAdapter<MenuPageModel> {

        private List<View[]> pageItemViewList;

        public MenuPagerAdapter(Context context, List<MenuPageModel> list) {
            super(context, list);
        }

        @Override
        protected View initItemView(final int page) {

            if (pageItemViewList == null)
                pageItemViewList = new ArrayList<>();

            View view = LayoutInflater.from(context).inflate(R.layout.item_menu_page,null);

            MenuPageModel pageModel = list.get(page);
            GridLayout gridLayout = view.findViewById(R.id.Container);

            View[] views = new View[9];
            for (int i = 0;i < 9;i ++) {
                final int pos = i;
                View itemView = LayoutInflater.from(context).inflate(R.layout.item_menu_icon,null);
                RelativeLayout root = itemView.findViewById(R.id.root);
                AppCompatImageView icon = itemView.findViewById(R.id.icon);
                AppCompatImageView icon_add = itemView.findViewById(R.id.icon_add);
                AppCompatImageView icon_delete = itemView.findViewById(R.id.icon_delete);
                AppCompatTextView title = itemView.findViewById(R.id.title);
                views[i] = itemView;
                if (i < pageModel.menuItems.size()) {
                    //TODO 控件有东西
                    NewMenuItem menuItem = pageModel.menuItems.get(i);
                    if (menuItem.getIconId() != 0) icon.setImageResource(menuItem.getIconId());
                    if (!StringUtils.isEmpty(menuItem.getItemTitle())) title.setText(menuItem.getItemTitle());
                    icon_delete.setVisibility(View.GONE);
                    icon_add.setVisibility(View.INVISIBLE);
                }else {
                    //TODO 控件需要不显示
                    icon.setImageResource(R.drawable.bg_transparent);
                    icon_add.setVisibility(View.INVISIBLE);
                    icon_delete.setVisibility(View.INVISIBLE);
                    title.setVisibility(View.INVISIBLE);
                }

                icon_add.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getVisibility() == View.VISIBLE) {
                            onMenuItemEventListener.onEmptyItemClick(v,page,pos);
                        }
                    }
                });
                icon.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ITEM_STATE == ITEM_SATE_NORMAL) {
                            onMenuItemEventListener.beforeItemPerform(v,page,pos);
                            onMenuItemEventListener.onItemClick(v,page,pos);
                            onMenuItemEventListener.afterItemClick(v,page,pos);
                        } else if (ITEM_STATE == ITEM_STATE_DELETE_VISIBLE){
                            ITEM_STATE = ITEM_SATE_NORMAL;
                            changeStateFromEditable2Normal();
//                            updateMenuViewState();
                        }

                    }
                });
                icon.setOnLongClickListener(MenuView.this);
                icon_delete.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getVisibility() == View.VISIBLE) {
                            onMenuItemEventListener.onDeleteItemClick(v,page,pos);
                        }
                    }
                });
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(i / 3,1),GridLayout.spec(i % 3,1));
                params.width = Device.dip2px(context,100);
                params.height = Device.dip2px(context,96);
                params.setGravity(Gravity.FILL);
                itemView.setLayoutParams(params);
                gridLayout.addView(itemView);
            }

            pageItemViewList.add(views);
            return view;
        }

        @Override
        public void updateList(List<MenuPageModel> list) {
            pageItemViewList.clear();
            super.updateList(list);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.destroyItem(container, position, object);
            pageItemViewList.remove(position);
        }

        private View getItemByPosition(int page, int pos) {
            if (page < 0 || page >= pagerAdapter.getList().size() || pos < 0 || pos >= 9)return null;
            return pageItemViewList.get(page + 1)[pos];
        }

        /**
         *
         * @param truePage 真实的第几页面
         * @param pos
         * @return
         */
        private NewMenuItem getMenuModelByPosition(int truePage,int pos) {
            int totalPage = list.size();
            try {
                return list.get((truePage + totalPage - 1) % totalPage).menuItems.get(pos);
            }catch (Exception e) {
                return NewMenuItem.EMTY_INSTANCE;
            }
        }

    }


}
