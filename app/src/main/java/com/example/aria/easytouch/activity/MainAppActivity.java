package com.example.aria.easytouch.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.adapter.NewContainerAdapter;
import com.example.aria.easytouch.base.BaseActivity;
import com.example.aria.easytouch.iview.IMainAppView;
import com.example.aria.easytouch.service.easytouch.EasyTouchService;
import com.example.aria.easytouch.ui.hint.HintActivity;
import com.luzeping.aria.commonutils.notification.NotificationCenter;
import com.luzeping.aria.commonutils.notification.NotificationCenterDelegate;
import com.luzeping.aria.commonutils.utils.PermissionHelper;

import butterknife.BindView;

public class MainAppActivity extends BaseActivity implements IMainAppView,NotificationCenterDelegate{

    private static final String TAG = "MainAppActivity";
    private static final int REQUEST_CODE_FROM_SETTINGS = 1;

    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_content)
    LinearLayout toolbar_content;
    @BindView(R.id.Container)
    RecyclerView Container;
    private NewContainerAdapter adapter;

    private Animation toolbar_content_animation;

    @Override
    protected int layoutId() {
        return R.layout.activity_new;
    }

    @Override
    protected void initView() {
        adapter = new NewContainerAdapter(this,this);
        Container.setAdapter(adapter);
        Container.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.icon_more));
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int deltaHeight = appBarLayout.getHeight() - toolbar.getHeight();
                int delta = deltaHeight + verticalOffset;
                if (delta == 0) //TODO 已到顶端
                    executeToolbarTitleAnim(false);
                if (delta == deltaHeight) //TODO 说明已到底端
                    executeToolbarTitleAnim(true);
            }
        });
    }

    private void executeToolbarTitleAnim(boolean isExpand){
        if (toolbar_content_animation != null) {
            toolbar_content_animation.cancel();
            toolbar_content_animation = null;
        }
        toolbar_content_animation = AnimationUtils.loadAnimation(this,
                isExpand?R.anim.anim_to_transparent:R.anim.anim_from_transparent);
        toolbar_content_animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                toolbar_content_animation = null;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        toolbar_content.startAnimation(toolbar_content_animation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new,menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!hasFloatWindowPermission()) jumpToAlertSetting();
    }

    private boolean hasFloatWindowPermission() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                Settings.canDrawOverlays(this)) ||
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.M &&
                        PermissionHelper.hasPermission(this,
                                new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}));
    }

    private void jumpToAlertSetting() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
        startActivityForResult(intent,REQUEST_CODE_FROM_SETTINGS);
        HintActivity.show(this,getString(R.string.hint_open_float_window),200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_FROM_SETTINGS:
                if (!hasFloatWindowPermission()) finish();
                break;
        }
    }

    @Override
    public void openEasyTouch() {
        if (hasFloatWindowPermission()){
            startService(new Intent(MainAppActivity.this, EasyTouchService.class));
        }else
            jumpToAlertSetting();
    }

    @Override
    public void closeEasyTouch() {
        NotificationCenter.getInstance().postNotificationName(R.id.STOP_EASY_TOUCH_SERVICE);
    }

    @Override
    protected void registerNotification() {
        super.registerNotification();
        NotificationCenter.getInstance().addObserver(this,R.id.TEST_ID);
    }

    @Override
    protected void unRegisterNotification() {
        super.unRegisterNotification();
        NotificationCenter.getInstance().removeObserver(this,R.id.TEST_ID);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        switch (id) {
            case R.id.TEST_ID:
                String s = (String) args[0];
                Toast.makeText(this,"test_id" + s,Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
