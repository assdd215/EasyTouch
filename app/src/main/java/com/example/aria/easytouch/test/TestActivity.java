package com.example.aria.easytouch.test;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.activity.MainActivity;
import com.luzeping.aria.commonutils.utils.StringUtils;
import com.luzeping.aria.commonutils.view.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.button1)
    Button btn1;
    @BindView(R.id.button2)
    Button btn2;

    private ArrayMap<Integer,String> map = new ArrayMap<Integer, String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        MyViewModel model = ViewModelProviders.of(this).get(MyViewModel.class);
        Log.d("MainActivity","s:" + model.getS());
        model.setS("currentTime:" + System.currentTimeMillis());

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestActivity.this,Test2Activity.class);
                intent.putExtra("key","value");
                startActivity(intent);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestActivity.this,MainActivity.class));
                finish();
            }
        });

        map.put(1,"22");
        Toast.makeText(this,map.get(2),Toast.LENGTH_SHORT).show();
        btn1.setText(StringUtils.parseSpecialString("测试1",14, Color.WHITE, Typeface.DEFAULT,"",12,Color.RED,Typeface.DEFAULT_BOLD,"测试3",24,Color.WHITE,Typeface.DEFAULT_BOLD));
    }

    @Override
    public void onRefresh(SwipeRefreshLayout parent, View refreshView, int state, float offsetY) {

    }


}
