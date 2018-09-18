package com.example.aria.easytouch.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.activity.NotificationSettingActivity;
import com.example.aria.easytouch.activity.SettingActivity;
import com.example.aria.easytouch.iview.IMainAppView;
import com.example.aria.easytouch.model.MenuItem;
import com.example.aria.easytouch.ui.hint.HintActivity;
import com.example.aria.easytouch.util.Constants;
import com.example.aria.easytouch.util.StaticHandler;
import com.luzeping.aria.commonutils.notification.NotificationCenter;

import java.util.ArrayList;
import java.util.List;

public class NewContainerAdapter extends RecyclerView.Adapter<MenuItemHolder>{

    private static final String TAG = "NewContainerAdapter";

    private List<MenuItem> itemList;
    private Context context;
    private IMainAppView mainAppView;
    public NewContainerAdapter(Context context){
        this.context = context;
        initData();
    }

    public NewContainerAdapter(Context context,IMainAppView mainAppView){
        this.context = context;
        this.mainAppView = mainAppView;
        initData();
    }

    private StaticHandler handler = new StaticHandler();

    private void initData(){
        itemList = new ArrayList<>();
        itemList.add(new MenuItem(R.drawable.ic_flare_black,R.string.menu_easytouch_enable));
        itemList.add(new MenuItem(R.drawable.ic_activity_setting_initial,R.string.menu_init_setting));
        itemList.add(new MenuItem(R.drawable.ic_activity_themeshop_title,R.string.menu_theme));
        itemList.add(new MenuItem(R.drawable.ic_activity_notification_setting_title,R.string.menu_notification_setting));
        itemList.add(new MenuItem(R.drawable.gesture_click,R.string.menu_gesture));
        itemList.add(new MenuItem(R.drawable.ic_setting_system_diaplay,R.string.menu_display_setting));
        itemList.add(new MenuItem(R.drawable.ic_setting_system_advanced,R.string.menu_advanced_setting));
        itemList.add(new MenuItem(R.drawable.ic_settings_rate,R.string.menu_rate_us));
        itemList.add(new MenuItem(R.drawable.ic_share,R.string.menu_share));
        itemList.add(new MenuItem(R.drawable.ic_help,R.string.menu_help));
        itemList.get(0).withSwitch = true;

        handler.setContext(context);
        handler.setMsgListener(new StaticHandler.OnMessageListener() {
            @Override
            public void handlerMsg(Message msg) {
                switch (msg.what){
                    case Constants.TYPE_HINT:
                        if (handler.getContext() != null){
                            Intent intent = new Intent(handler.getContext().get(),HintActivity.class);
                            intent.putExtra(Constants.HINT_TEXT,msg.obj.toString());
                            handler.getContext().get().startActivity(intent);

                        }
                }
            }
        });
    }

    private boolean isNotificationOpen(){
        String pkgName = context.getPackageName();
        final String flat = Settings.Secure.getString(context.getContentResolver(),"enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)){
            final String[] names = flat.split(":");
            for (int i = 0;i < names.length;i++){
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null){
                    if (TextUtils.equals(pkgName,cn.getPackageName()))
                        return true;
                }
            }
        }
        return false;
    }

    @NonNull
    @Override
    public  MenuItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MenuItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu,null));
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemHolder holder, final int position) {
        holder.icon.setImageResource(itemList.get(position).imgId);
        holder.title.setText(context.getString(itemList.get(position).titleId));
        holder.aSwitch.setVisibility(itemList.get(position).withSwitch?View.VISIBLE:View.GONE);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v,position);
            }
        });

        holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onItemClickListener.onItemSwitch(buttonView,position,isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, int pos) {
            String title = "";
            int type = -1;
            Class cl = SettingActivity.class;
            switch (itemList.get(pos).titleId){
                case R.string.menu_init_setting:
                    title = context.getString(R.string.menu_init_setting);
                    type = SettingAdapterFactory.AdapterType.TYPE_INIT_SETTING;
                    break;

                case R.string.menu_gesture:
                    title = context.getString(R.string.menu_gesture);
                    type = SettingAdapterFactory.AdapterType.TYPE_GESTURE;
                    break;

                case R.string.menu_notification_setting:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        if (!isNotificationOpen()){
                            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                            context.startActivity(intent);
                            HintActivity.show(context,context.getString(R.string.msg_activate_notification),Constants.HINT_DELAY);
                            return;


                        }else {
                            title = context.getString(R.string.menu_notification_setting);
                            type = SettingAdapterFactory.AdapterType.TYPE_NOTIFICATION;
                            cl = NotificationSettingActivity.class;
                        }
                    }
                    break;
                case R.string.menu_advanced_setting:
                    NotificationCenter.getInstance().postNotificationName(R.id.TEST_ID,"ceshi");
                default:
                    return;

            }
            Intent intent = new Intent(context, cl);
            intent.putExtra(Constants.SETTING_TITLE,title);
            intent.putExtra(Constants.SETTING_TYPE, type);
            context.startActivity(intent);
        }

        @Override
        public void onItemSwitch(CompoundButton buttonView,int pos,boolean isChecked) {
            switch (itemList.get(pos).titleId) {
                case R.string.menu_easytouch_enable:
                    if (isChecked) {
                        mainAppView.openEasyTouch();
                    }else
                        mainAppView.closeEasyTouch();
            }
        }
    };

    private interface OnItemClickListener{
        void onItemClick(View view,int pos);
        void onItemSwitch(CompoundButton buttonView,int pos,boolean isChecked);
    }


}


