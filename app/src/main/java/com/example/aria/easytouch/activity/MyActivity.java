package com.example.aria.easytouch.activity;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.aria.easytouch.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Aria on 2017/7/24.
 */

public class MyActivity extends AppCompatActivity{

    @BindView(R.id.btnClearMemory)
    Button btnClearMemory;
    @BindView(R.id.layout)
    LinearLayout layout;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ButterKnife.bind(this);
        List<View> list = new ArrayList<>();
        for (int i = 0;i<5;i++){
            View view = getLayoutInflater().inflate(R.layout.settings_item,null,false);
            list.add(view);
        }
    }

    @OnClick({R.id.btnClearMemory})
    void onClick(View view){
        switch (view.getId()){
            case R.id.btnClearMemory:
//                clearMemory();

                final Button button = new Button(MyActivity.this);
                button.setText("新增的 按钮");
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layout.addView(button,params);

                button.post(new Runnable() {
                    @Override
                    public void run() {
                        Animation alphaAnimation = new AlphaAnimation(-1,1);
                        alphaAnimation.setDuration(1500);
                        button.startAnimation(alphaAnimation);
                        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                Log.d("MainActivity","start");
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                Log.d("MainActivity","end");
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                });


                break;
        }
    }

    private void clearMemory(){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infoList = manager.getRunningAppProcesses();
        List<ActivityManager.RunningServiceInfo> serviceInfos = manager.getRunningServices(100);
        long beforeMem = getAvaliMemory(MyActivity.this);
        Log.d("MainActivity","before:"+beforeMem);
        int count = 0;
        if (infoList != null){
            for (int i=0;i<infoList.size();i++){
                ActivityManager.RunningAppProcessInfo appProcessInfo = infoList.get(i);
                if (appProcessInfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE){
                    String[] pkgList = appProcessInfo.pkgList;
                    for (int j = 0;j<pkgList.length;j++){
                        manager.killBackgroundProcesses(pkgList[j]);
                        count++;
                    }
                }
            }
        }

        Log.d("MainActivity","after:"+getAvaliMemory(MyActivity.this));
    }

    private long getAvaliMemory(Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(info);
        return info.availMem;
    }
}
