package com.example.aria.easytouch.widget.easytouch.dialog;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.model.CommonAppInfo;
import com.github.promeg.pinyinhelper.Pinyin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Aria on 2017/8/15.
 */

public class DialogItemAdapter extends RecyclerView.Adapter<MyViewHolder>{

    private static final char[] charList = new char[]{'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T'
            ,'U','V','W','X','Y','Z','#'};
    private Context context;
    private ArrayMap<Character,List<CommonAppInfo>> commonAppsMap;
    Comparator<CommonAppInfo> com;
    LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;

    public DialogItemAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_commonapps,null,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final List<CommonAppInfo> infoList = commonAppsMap.get(charList[position]);
        if (infoList == null) {
            holder.itemLayout.setVisibility(View.GONE);
            holder.itemAppGrid.setVisibility(View.GONE);
            holder.charTitle.setVisibility(View.GONE);
        } else {
            holder.itemLayout.setVisibility(View.VISIBLE);
            holder.itemAppGrid.setVisibility(View.VISIBLE);
            holder.charTitle.setVisibility(View.VISIBLE);
            holder.charTitle.setText(String.valueOf(charList[position]));
            holder.itemAppGrid.removeAllViews();
            for (int i = 0;i<infoList.size();i++){
                View view = generateAppView(infoList.get(i),i);
                holder.itemAppGrid.addView(view);
            }

            if (infoList.size() % 4 != 0){
                int size = infoList.size();
                while (size % 4 != 0){
                    holder.itemAppGrid.addView(generateStubView(size));
                    size++;
                }
            }

            holder.itemAppGrid.postInvalidate();
        }
    }

    private View generateStubView(int pos){
        View view = new View(context);
        GridLayout.Spec rowSpec = GridLayout.spec(pos / 4);
        GridLayout.Spec columnSpec = GridLayout.spec(pos % 4 , 1f);
        GridLayout.LayoutParams parmas = new GridLayout.LayoutParams(rowSpec,columnSpec);
        parmas.width = 0;
        parmas.height = 3;
        view.setLayoutParams(parmas);
        return view;
    }

    private View generateAppView(CommonAppInfo commonAppInfo,int pos){
        View view = inflater.inflate(R.layout.item_common_app_icon,null,false);
        ImageView iconView = (ImageView) view.findViewById(R.id.app_icon);
        TextView labelView = (TextView) view.findViewById(R.id.app_label);
        iconView.setImageDrawable(commonAppInfo.getIcon());
        labelView.setText(commonAppInfo.getLabel());
        view.setTag(commonAppInfo);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(v);
            }
        });

        GridLayout.Spec rowSpec = GridLayout.spec(pos / 4);
        GridLayout.Spec columnSpec = GridLayout.spec(pos % 4 , 1f);

        GridLayout.LayoutParams parmas = new GridLayout.LayoutParams(rowSpec,columnSpec);
        parmas.width = 0;
        view.setLayoutParams(parmas);

        return view;
    }

    @Override
    public int getItemCount() {
        return charList.length;
    }



    private void updateAppList(){
        if (commonAppsMap == null)
            commonAppsMap = new ArrayMap<>();
        commonAppsMap.clear();

        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN,null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent,0);
        for (ResolveInfo info:resolveInfos){
            String label = info.loadLabel(packageManager).toString();
            char c = label.charAt(0);

            //TODO 这里可以设计根据不同国家语言来对char进行取值
            if (Pinyin.isChinese(c))
                c = Pinyin.toPinyin(c).charAt(0);
            c = (c >= 'A' && c <='Z') || (c >= 'a' && c<= 'z') ? Character.toUpperCase(c) : '#';

            CommonAppInfo appInfo = new CommonAppInfo();
            appInfo.setIcon(info.loadIcon(packageManager));
            appInfo.setLabel(label);
            appInfo.setPackageName(info.activityInfo.packageName);
            appInfo.setLaunchName(info.activityInfo.name);

            List<CommonAppInfo> infoList = commonAppsMap.get(c);
            if (infoList == null){
                infoList = new ArrayList<>();
                commonAppsMap.put(c,infoList);
            }

            infoList.add(appInfo);
        }

        com = new Comparator<CommonAppInfo>() {
            @Override
            public int compare(CommonAppInfo info1, CommonAppInfo info2) {
                String label1 = Pinyin.toPinyin(info1.getLabel()," ");
                String label2 = Pinyin.toPinyin(info2.getLabel()," ");
                return label1.compareTo(label2);
            }
        };
        for (char c:charList){
            List<CommonAppInfo> commonAppInfos = commonAppsMap.get(c);
            if (commonAppInfos == null)
                continue;
            Collections.sort(commonAppInfos,com);
        }

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        AsyncTaskCompat.executeParallel(new UpdateListTask());
    }

    public interface OnItemClickListener{
        void onItemClick(View view);
        void onPostDataLoaded(DialogItemAdapter adapter);
    }

    class UpdateListTask extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            updateAppList();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            onItemClickListener.onPostDataLoaded(DialogItemAdapter.this);
        }
    }
}

class MyViewHolder extends RecyclerView.ViewHolder{

    TextView charTitle;
    RelativeLayout itemLayout;
    GridLayout itemAppGrid;

    public MyViewHolder(View itemView) {
        super(itemView);

        charTitle = (TextView) itemView.findViewById(R.id.item_char_title);
        itemAppGrid = (GridLayout) itemView.findViewById(R.id.item_app_grid);
        itemLayout = (RelativeLayout) itemView.findViewById(R.id.item_layout);
    }
}
