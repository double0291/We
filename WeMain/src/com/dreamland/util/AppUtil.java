package com.dreamland.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.dreamland.base.BaseApplication;

public class AppUtil {

	private AppUtil() {
	}

	public static int getAppVersion() {
		Context context = BaseApplication.mApp;
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}
}
