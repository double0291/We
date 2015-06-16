package com.dreamland.util;

import android.content.Context;
import android.os.Environment;

import com.dreamland.base.BaseApplication;

import java.io.File;

public class FileUtil {
    /**
     * 获取文件缓存的根目录，Android目录，会随APP卸载一并清除
     * 主要用来缓存图片，网络请求返回数据等
     */
    public static String getDiskCacheRoot() {
        Context context = BaseApplication.mApp;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            return context.getExternalCacheDir().getPath();
        } else {
            return context.getCacheDir().getPath();
        }
    }

    /**
     * 获取下载文件（apk，视频等）的缓存根目录
     * 如果有SD卡，就值SD卡下的APP名目录，不随APP卸载清除
     * 如果没有SD卡，存在Android目录，会随APP卸载一并清除
     */
    public static String getDownloadCacheRoot() {
        Context context = BaseApplication.mApp;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                    Constants.APP_NAME;
        } else {
            return context.getCacheDir().getPath() + File.separator + "download";
        }
    }
}
