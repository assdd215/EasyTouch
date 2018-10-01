package com.example.aria.easytouch.test;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aria.easytouch.widget.easytouch.pager.EndlessPagerAdapter;

import java.util.List;

public class TestEndlessPagerAdapter extends EndlessPagerAdapter<String>{

    public TestEndlessPagerAdapter(Context context, List<String> list) {
        super(context, list);
    }

    @Override
    protected View initItemView(int position) {

        TextView textView = new TextView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(params);
        textView.setText(list.get(position));
        textView.setGravity(Gravity.CENTER);
        return textView;
    }
}
