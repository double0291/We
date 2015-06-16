package com.dreamland.util.cache;

import java.io.BufferedOutputStream;
import java.io.IOException;

import android.text.TextUtils;

import com.dreamland.util.SecurityUtil;

public class FileDiskCache extends DiskCache<String> {
	private final String FOLDER_NAME = "file";
	// 每次写入缓存的Buffer大小
	private static final int DEFAULT_BUFFER_SIZE = 2 * 1024; // 2 KB
	// 默认缓存文件夹容量
	private static final int DEFAULT_CACHE_MAX_SIZE = 20;// 20M

	/**
	 * 默认磁盘缓存占用20M
	 */
	public FileDiskCache() {
		super(DEFAULT_CACHE_MAX_SIZE);
	}

	/**
	 * 构造指定容量的磁盘缓存
	 * 
	 * @param cacheSize
	 *            单位为M
	 */
	public FileDiskCache(int cacheSize) {
		super(cacheSize);
	}

	public final boolean put(String key, String value) {
		if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
			throw new NullPointerException("key == null || value == null");
		}

		try {
			CoreDiskLruCache.Editor editor = cache.edit(SecurityUtil.md5(key));
			// save
			BufferedOutputStream output = new BufferedOutputStream(editor.newOutputStream(0),
					DEFAULT_BUFFER_SIZE);
			try {
				output.write(value.getBytes());
				editor.commit();
				return true;
			} catch (IOException e1) {
				editor.abort();
				e1.printStackTrace();
			} finally {
				if (output != null) {
					output.flush();
					output.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	public final String get(String key) {
		if (TextUtils.isEmpty(key)) {
			throw new NullPointerException("key == null");
		}

		CoreDiskLruCache.Snapshot snapshot = null;
		try {
			snapshot = cache.get(SecurityUtil.md5(key));
			// get string in cache
			return snapshot.getString(0);
		} catch (IOException e) {
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		} finally {
			if (snapshot != null)
				snapshot.close();
		}
	}

	@Override
	String getFolderName() {
		return FOLDER_NAME;
	}

}
