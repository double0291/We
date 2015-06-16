package com.dreamland.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class Toaster {
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * 私有构造函数，防止被初始化
     */
    private Toaster() {
    }

    /**
     * 在主线程中显示Toast
     *
     * @param context
     * @param text
     * @param duration
     */
    public static void show(final Context context, final String text, final int duration) {
        // 用handler保证在主线程中toast
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, duration).show();
            }
        });
    }

    /**
     * 在主线程中显示Toast
     *
     * @param context
     * @param resId
     * @param duration
     */
    public static void show(final Context context, final int resId, final int duration) {
        // 用handler保证在主线程中toast
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, resId, duration).show();
            }
        });
    }

}
