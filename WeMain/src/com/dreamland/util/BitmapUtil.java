package com.dreamland.util;

import android.graphics.Bitmap;
import android.os.Build;

public class BitmapUtil {
	/**
	 * 获取Bitmap的大小
	 */
	public static int getBitmapSize(Bitmap bitmap) {
		if (bitmap == null) {
			return 0;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			return bitmap.getByteCount();
		} else {
			return bitmap.getRowBytes() * bitmap.getHeight();
		}
	}
}
