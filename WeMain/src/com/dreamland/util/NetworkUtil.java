package com.dreamland.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
    /**
     * 判断当前网络环境是否WIFI
     */
    public static boolean isWifi(Context context) {
        if (context == null) {
            return false;
        }

        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        return info == null ? false : (info.getType() ==
                ConnectivityManager.TYPE_WIFI ? true : false);
    }
}
