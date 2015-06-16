package com.dreamland.util.cache;

import java.io.File;
import java.io.IOException;

import android.text.TextUtils;

import com.dreamland.util.AppUtil;
import com.dreamland.util.FileUtil;
import com.dreamland.util.Logger;
import com.dreamland.util.SecurityUtil;

public abstract class DiskCache<V> {
	private int maxSize;

	protected CoreDiskLruCache cache;

	/**
	 * 构造指定容量的磁盘缓存
	 * 
	 * @param cacheSize
	 *            单位为M
	 */
	public DiskCache(int cacheSize) {
		maxSize = cacheSize * 1024 * 1024;
		initCache();
	}

	private void initCache() {
		try {
			File cacheDir = getDiskCacheDir();

			if (!cacheDir.exists())
				cacheDir.mkdir();

			/*
			 * 版本号改变时，缓存数据会被清除
			 */
			cache = CoreDiskLruCache.open(cacheDir, AppUtil.getAppVersion(), 1, maxSize);
		} catch (IOException e) {
			Logger.e("Can't initialize disk cache", false);
		}
	}

	abstract boolean put(String key, V value);

	abstract Object get(String key);

	/**
	 * 返回存储文件夹名
	 */
	abstract String getFolderName();

	public final boolean remove(String key) {
		if (TextUtils.isEmpty(key))
			throw new NullPointerException("key == null");

		try {
			return cache.remove(SecurityUtil.md5(key));
		} catch (IOException e) {
			return false;
		}
	}

	public void clear() {
		try {
			cache.delete();
		} catch (IOException e) {

		} finally {
			initCache();
		}
	}

	public void close() {
		try {
			cache.close();
		} catch (IOException e) {

		}
		cache = null;
	}

	/**
	 * 如果没有SD卡，要用内存里面的data目录
	 */
	private File getDiskCacheDir() {
		String folderName = getFolderName();
		if (TextUtils.isEmpty(folderName)) {
			return new File(FileUtil.getDiskCacheRoot());
		}
		return new File(FileUtil.getDiskCacheRoot() + File.separator + folderName);
	}
}
