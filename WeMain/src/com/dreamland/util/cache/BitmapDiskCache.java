package com.dreamland.util.cache;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.dreamland.util.SecurityUtil;

public class BitmapDiskCache extends DiskCache<Bitmap> {
	private final String FOLDER_NAME = "image";

	// 每次写入缓存的Buffer大小
	private static final int DEFAULT_BUFFER_SIZE = 32 * 1024; // 32 KB
	// 默认缓存文件夹容量
	private static final int DEFAULT_CACHE_MAX_SIZE = 100;// 100M

	/**
	 * 默认磁盘缓存占用100M
	 */
	public BitmapDiskCache() {
		super(DEFAULT_CACHE_MAX_SIZE);
	}

	/**
	 * 构造指定容量的磁盘缓存
	 * 
	 * @param cacheSize
	 *            单位为M
	 */
	public BitmapDiskCache(int cacheSize) {
		super(cacheSize);
	}

	@Override
	public final boolean put(String key, Bitmap value) {
		if (TextUtils.isEmpty(key) || value == null) {
			throw new NullPointerException("key == null || value == null");
		}

		try {
			CoreDiskLruCache.Editor editor = cache.edit(SecurityUtil.md5(key));
			// save
			BufferedOutputStream output = new BufferedOutputStream(editor.newOutputStream(0),
					DEFAULT_BUFFER_SIZE);
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				String lowerUrl = key.toLowerCase();
				// 根据URL后缀压缩
				if (lowerUrl.endsWith("jpg") || lowerUrl.endsWith("jpeg")) {
					value.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				} else if (lowerUrl.endsWith("png")) {
					value.compress(Bitmap.CompressFormat.PNG, 100, baos);
				} else {
					throw new NullPointerException("Bitmap.CompressFormat invalid");
				}
				output.write(baos.toByteArray());
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

	@Override
	public final Bitmap get(String key) {
		if (TextUtils.isEmpty(key)) {
			throw new NullPointerException("key == null");
		}

		CoreDiskLruCache.Snapshot snapshot = null;
		try {
			snapshot = cache.get(SecurityUtil.md5(key));
			if (snapshot != null) {
				InputStream is = snapshot.getInputStream(0);
				return BitmapFactory.decodeStream(is);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} finally {
			if (snapshot != null)
				snapshot.close();
		}
		return null;
	}
	
	@Override
	String getFolderName() {
		return FOLDER_NAME;
	}
}
