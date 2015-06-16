package com.dreamland.util.cache;

import android.graphics.Bitmap;

import com.android.volley.toolbox.ImageLoader;

/**
 * 结合内存缓存和磁盘缓存
 *
 */
public class BitmapCache implements ImageLoader.ImageCache {
	private static BitmapMemoryCache mMemoryCache = new BitmapMemoryCache();
	private static BitmapDiskCache mDiskCache = new BitmapDiskCache();

	@Override
	public Bitmap getBitmap(String url) {
		// 先读内存缓存
		Bitmap bitmap = mMemoryCache.get(url);
		// 内存中没有，则读磁盘缓存
		if (bitmap == null) {
			bitmap = mDiskCache.get(url);
		}
		return bitmap;
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		// 塞入磁盘缓存
		mDiskCache.put(url, bitmap);
		// 塞入内存缓存
		mMemoryCache.put(url, bitmap);
	}
}
