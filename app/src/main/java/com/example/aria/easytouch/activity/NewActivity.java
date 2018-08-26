package com.example.aria.easytouch.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.adapter.NewContainerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewActivity extends AppCompatActivity{

    private static final String TAG = "NewActivity";

    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_content)
    LinearLayout toolbar_content;

    private Animation toolbar_content_animation;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        ButterKnife.bind(this);
        RecyclerView Container = findViewById(R.id.Container);
        Container.setAdapter(new NewContainerAdapter(this));
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
}

abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {

    public enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    private State mCurrentState = State.IDLE;

    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (i == 0) {
            if (mCurrentState != State.EXPANDED) {
                onStateChanged(appBarLayout, State.EXPANDED);
            }
            mCurrentState = State.EXPANDED;
        } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
            if (mCurrentState != State.COLLAPSED) {
                onStateChanged(appBarLayout, State.COLLAPSED);
            }
            mCurrentState = State.COLLAPSED;
        } else {
            if (mCurrentState != State.IDLE) {
                onStateChanged(appBarLayout, State.IDLE);
            }
            mCurrentState = State.IDLE;
        }
    }

    public abstract void onStateChanged(AppBarLayout appBarLayout, State state);
}
