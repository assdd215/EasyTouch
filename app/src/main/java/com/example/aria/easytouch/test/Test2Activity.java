package com.example.aria.easytouch.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.assistivetool.booster.easytouch.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Test2Activity extends AppCompatActivity{

    @BindView(R.id.button1)
    Button btn1;
    @BindView(R.id.button2)
    Button btn2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Test2Activity.this,"toast:" + getIntent().getStringExtra("key"),Toast.LENGTH_SHORT).show();
            }
        });

    }
}
