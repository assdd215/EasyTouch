package com.example.aria.easytouch.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import com.example.aria.easytouch.service.MyJobService;

/**
 * Created by Aria on 2017/7/24.
 */

public class MyActivity extends AppCompatActivity{

    JobScheduler jobScheduler;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);




    }
}
