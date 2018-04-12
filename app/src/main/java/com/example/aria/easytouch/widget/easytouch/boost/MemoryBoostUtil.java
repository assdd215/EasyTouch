package com.example.aria.easytouch.widget.easytouch.boost;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.assistivetool.booster.easytouch.R;

import java.util.List;

/**
 * Created by Aria on 2017/7/27.
 */

public class MemoryBoostUtil implements BoostUtil {

    private Context context;

    public  MemoryBoostUtil(Context context){
        this.context = context;
        initData();
    }

    private void initData(){}


    @Override
    public void clearMemory() {

        Toast.makeText(context,clearStr(stategyTwo()),Toast.LENGTH_SHORT).show();
    }

    private long stategyTwo(){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long beforeMemory = memoryInfo.availMem;
        Log.d("MainActivity","before:"+beforeMemory);

        List<ActivityManager.RunningAppProcessInfo> taskList = activityManager.getRunningAppProcesses();
        long before = memoryInfo.availMem;

        for (int i = 0; i < taskList.size(); i++) {
            Log.d("MainActivity",
                    taskList.get(i).processName + " pid: "
                            + taskList.get(i).pid + " importance: "
                            + taskList.get(i).importance + " reason: "
                            + taskList.get(i).importanceReasonCode);
        }

        for (int i = 0;i<taskList.size();i++){
            ActivityManager.RunningAppProcessInfo processInfo = taskList.get(i);
            int importance = processInfo.importance;
            int pid = processInfo.pid;
            String pName = processInfo.processName;

            if (pName.equals("com.assistivetool.booster.easytouch")) {// kill other accept own package
                continue;
            }

            if (pName.equals("android")// important process or system process
                    || pName.equals("com.android.bluetooth")
                    || pName.equals("android.process.acore")
                    || pName.equals("system")
                    || pName.equals("com.android.phone")
                    || pName.equals("com.android.systemui")
                    || pName.equals("com.android.launcher")) {
                continue;
            }
            Log.v("MainActivity", "task " + pName + " pid: " + pid
                    + " has importance: " + importance + " WILL KILL");
            int count = 0;
            while (count < 3) {// attempt to kill three times
                activityManager.killBackgroundProcesses(taskList.get(i).processName);
                count++;
            }
        }

        taskList = activityManager.getRunningAppProcesses();
        for (int i = 0; i < taskList.size(); i++) {// after killing tasks
            Log.v("MainActivity",
                    taskList.get(i).processName + " pid:" + taskList.get(i).pid
                            + " importance: " + taskList.get(i).importance
                            + " reason: "
                            + taskList.get(i).importanceReasonCode);
        }

        activityManager.getMemoryInfo(memoryInfo);

        Log.d("MainActivity","after:"+memoryInfo.availMem);
        long after = memoryInfo.availMem;
        return after - before;
    }

    private void stategyOne(){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infoList = manager.getRunningAppProcesses();
        List<ActivityManager.RunningServiceInfo> serviceInfos = manager.getRunningServices(100);
        long beforeMem = getAvaliMemory(context);
        Log.d("MainActivity","name:"+context.getPackageName());
        Log.d("MainActivity","before:"+beforeMem);

        int count = 0;
        if (infoList != null){
            for (int i=0;i<infoList.size();i++){
                ActivityManager.RunningAppProcessInfo appProcessInfo = infoList.get(i);
                if (appProcessInfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE && !appProcessInfo.processName.equals(context.getPackageName())){
                    Log.d("MainActivity",appProcessInfo.processName);
                    String[] pkgList = appProcessInfo.pkgList;
                    for (int j = 0;j<pkgList.length;j++){
                        Log.d("MainActivity","pkg:"+pkgList[j]);
                        manager.killBackgroundProcesses(pkgList[j]);
                        count++;
                    }
                }
            }
        }
        Log.d("MainActivity","after:"+getAvaliMemory(context));
    }

    private long getAvaliMemory(Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(info);
        return info.availMem;
    }

    private String clearStr(long killedMem){
        if (killedMem < 1024){
            return context.getString(R.string.msg_memory_cleared);
        }
        return context.getString(R.string.msg_memory_clear_with_number) + formatMemSize(killedMem,0);
    }

    public static String formatMemSize(long size, int value) {

        String result = "";
        if (1024L > size) {// size less than 1024, for byte result
            String info = String.valueOf(size);
            result = (new StringBuilder(info)).append(" B").toString();
        } else if (1048576L > size) {// for KB result
            String s2 = (new StringBuilder("%.")).append(value).append("f")
                    .toString();
            Object aobj[] = new Object[1];
            Float float1 = Float.valueOf((float) size / 1024F);
            aobj[0] = float1;
            String s3 = String.valueOf(String.format(s2, aobj));
            result = (new StringBuilder(s3)).append(" KB").toString();
        } else if (1073741824L > size) {// for MB result
            String s4 = (new StringBuilder("%.")).append(value).append("f")
                    .toString();
            Object aobj1[] = new Object[1];
            Float float2 = Float.valueOf((float) size / 1048576F);
            aobj1[0] = float2;
            String s5 = String.valueOf(String.format(s4, aobj1));
            result = (new StringBuilder(s5)).append(" MB").toString();
        } else {// for GB Result
            Object aobj2[] = new Object[1];
            Float float3 = Float.valueOf((float) size / 1.073742E+009F);
            aobj2[0] = float3;
            String s6 = String.valueOf(String.format("%.2f", aobj2));
            result = (new StringBuilder(s6)).append(" GB").toString();
        }

        return " "+result;
    }
}
