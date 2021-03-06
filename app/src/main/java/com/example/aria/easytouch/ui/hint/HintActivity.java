package com.example.aria.easytouch.ui.hint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.TextView;

import com.assistivetool.booster.easytouch.R;
import com.example.aria.easytouch.util.Constants;
import com.example.aria.easytouch.util.Utils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Aria on 2017/9/5.
 */

public class HintActivity extends Activity{

    private static final String TAG = "HintActivity";

    @BindView(R.id.btnHint)
    AppCompatButton btnHint;
    @BindView(R.id.hint_title)
    TextView hintText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Utils.log(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hint);
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        String hintTitle = getIntent().getStringExtra(Constants.HINT_TEXT);
        if (hintTitle != null && !hintTitle.equals("")){
            hintText.setText(hintTitle);
        }else {
            HintActivity.this.finish();
        }
    }

    @OnClick({R.id.btnHint})
    void onClick(View view){
        switch (view.getId()){
            case R.id.btnHint:
                HintActivity.this.finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    public static void show(final Context context, final String message, long millTimes) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(context,HintActivity.class);
                intent.putExtra(Constants.HINT_TEXT,message);
                context.startActivity(intent);
            }
        },millTimes);
    }
}
