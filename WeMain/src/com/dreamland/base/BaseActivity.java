package com.dreamland.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class BaseActivity extends FragmentActivity {
    protected BaseApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = BaseApplication.mApp;
    }

    protected void startActivity(Class<?> clazz) {
        startActivity(clazz, null);
    }

    protected void startActivity(Class<?> clazz, Bundle extras) {
        Intent intent = new Intent();
        intent.setClass(this, clazz);
        if (extras != null) {
            intent.putExtras(extras);
        }
        startActivity(intent);
    }

    protected void startActivity(String action) {
        startActivity(action, null);
    }

    protected void startActivity(String action, Bundle extras) {
        Intent intent = new Intent();
        intent.setAction(action);
        if (extras != null) {
            intent.putExtras(extras);
        }
        startActivity(intent);
    }
}
