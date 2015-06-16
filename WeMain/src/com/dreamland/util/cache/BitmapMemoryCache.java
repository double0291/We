package com.dreamland.util.cache;

import android.graphics.Bitmap;

import com.dreamland.util.BitmapUtil;

public class BitmapMemoryCache extends MemoryLruCache<String, Bitmap> {
	// 默认占用内存的1/10
	private static final int MEMORY_CACHE_SIZE_IN_B = (int) (Runtime.getRuntime().maxMemory() / 10);

	/**
	 * 默认构造内存缓存占用总内存的1/10
	 */
	public BitmapMemoryCache() {
		super(MEMORY_CACHE_SIZE_IN_B);
	}

	/**
	 * 构造指定容量的内存缓存
	 * 
	 * @param maxSize
	 *            单位是M
	 */
	public BitmapMemoryCache(int maxSize) {
		super(maxSize * 1024 * 1024);
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return BitmapUtil.getBitmapSize(value);
	}

}
