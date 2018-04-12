package com.example.aria.easytouch.util;

/**
 * Created by Aria on 2017/9/15.
 */

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.List;

/**
 * Android N 适配工具类
 */
public class NougatTools {

    private static final String auth = "com.example.aria.easytouch.provider.EasyTouchFileProvider";


    /**
     * 将普通uri转化成适应7.0的content://形式  针对文件格式
     *
     * @param context    上下文
     * @param file       文件路径
     * @param intent     intent
     * @param type       图片或者文件,0表示图片,1表示文件
     * @param intentType intent.setDataAndType
     * @return
     */
    public static Intent formatFileProviderIntent(
            Context context, File file, Intent intent, String intentType) {

        Uri uri = FileProvider.getUriForFile(context, auth, file);
        // 表示文件类型
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, intentType);

        return intent;
    }

    /**
     * 将普通uri转化成适应7.0的content://形式  针对图片格式
     *
     * @param context    上下文
     * @param file       文件路径
     * @param intent     intent
     * @return
     */
    public static Intent formatFileProviderPicIntent(
            Context context, File file, Intent intent) {

        Uri uri = FileProvider.getUriForFile(context, auth, file);
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        // 表示图片类型
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }
    /**
     * 将普通uri转化成适应7.0的content://形式
     *
     * @return
     */
    public static Uri formatFileProviderUri(Context context, File file) {
        Uri uri = FileProvider.getUriForFile(context, auth, file);
        return uri;
    }
}