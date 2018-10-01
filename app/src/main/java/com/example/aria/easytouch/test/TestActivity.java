package com.example.aria.easytouch.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.Toast;

import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.widget.easytouch.pager.EndlessPagerAdapter;
import com.example.aria.easytouch.widget.easytouch.pager.EndlessViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity {

    EndlessPagerAdapter adapter;
    List<String> list;

    @BindView(R.id.btn1)
    AppCompatImageView btn1;
    @BindView(R.id.btn2)
    AppCompatImageView btn2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TestActivity.this,"btn1 click",Toast.LENGTH_SHORT).show();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TestActivity.this,"btn2 click",Toast.LENGTH_SHORT).show();
            }
        });
        btn2.setVisibility(View.INVISIBLE);
    }
}
