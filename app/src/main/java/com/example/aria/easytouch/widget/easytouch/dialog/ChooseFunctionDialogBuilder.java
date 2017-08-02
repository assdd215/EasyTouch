package com.example.aria.easytouch.widget.easytouch.dialog;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by Aria on 2017/8/2.
 */

public class ChooseFunctionDialogBuilder {

    private Context context;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;

    public ChooseFunctionDialogBuilder(Context context){
        this.context = context;
        initData();
    }

    private void initData(){
        builder = new AlertDialog.Builder(context);
    }

    public void show(){}
}
