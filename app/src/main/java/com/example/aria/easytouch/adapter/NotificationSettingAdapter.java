package com.example.aria.easytouch.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.model.CommonAppInfo;

import java.util.ArrayList;
import java.util.List;

public class NotificationSettingAdapter extends BaseSettingAdapter<CommonAppInfo,NotificationViewHolder>{

    private ColorMatrixColorFilter matrixColorFilter;

    NotificationSettingAdapter(Context context){
        super(context);
    }

    @Override
    protected void initData() {
        itemList = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN,null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent,0);
        for (int i = 0;i < resolveInfos.size();i++){
            ResolveInfo info = resolveInfos.get(i);
            CommonAppInfo appInfo = new CommonAppInfo();
            appInfo.setLabel(info.loadLabel(packageManager).toString());
            appInfo.setIcon(info.loadIcon(packageManager));
            appInfo.setPackageName(info.activityInfo.packageName);
            appInfo.setLaunchName(info.activityInfo.name);
            itemList.add(appInfo);
        }
    }

    @Override
    protected NotificationViewHolder createViewHolder() {
        return new NotificationViewHolder(LayoutInflater.from(context).inflate(R.layout.item_notification_content,null,false));
    }

    @Override
    protected void bindViews(NotificationViewHolder holder, int position) {
        holder.icon.setImageDrawable(itemList.get(position).getIcon());
        holder.title.setText(itemList.get(position).getLabel());
    }

}

class NotificationViewHolder extends RecyclerView.ViewHolder{

    ImageView icon;
    ImageView delete;
    TextView title;
    public NotificationViewHolder(View itemView) {
        super(itemView);
        icon = itemView.findViewById(R.id.icon);
        delete = itemView.findViewById(R.id.delete);
        title = itemView.findViewById(R.id.title);
    }
}