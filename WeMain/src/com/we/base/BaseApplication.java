package com.we.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.Volley;
import com.we.R;
import com.we.database.EntityManagerFactory;
import com.we.util.Constants.HttpCmd;
import com.we.util.cache.BitmapCache;

public class BaseApplication extends Application {
	private static String PREFERENCE_NAME = "info";

	public static BaseApplication mApp;

	private SharedPreferences mSP;
	private boolean mIsLogin = false;
	private String mUin;

	private EntityManagerFactory mEntityManagerFactory;

	// 网络请求队列
	public RequestQueue mRequestQueue;
	// 异步图片加载器
	public ImageLoader mImageLoader;

	@Override
	public void onCreate() {
		mApp = this;

		super.onCreate();
		mSP = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);

		mIsLogin = mSP.getBoolean("isLogin", false);
		if (mIsLogin) {
			mUin = mSP.getString("uin", "");
		}

		mRequestQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mRequestQueue, new BitmapCache());
	}

	public String getUin() {
		return mUin;
	}

	public EntityManagerFactory getEntityManagerFactory() {
		if (TextUtils.isEmpty(mUin)) {
			throw new IllegalStateException("Can not create a entity factory when the uin is null.");
		}
		synchronized (this) {
			if (mEntityManagerFactory == null) {
				mEntityManagerFactory = new EntityManagerFactory(mUin);
			}
		}
		return mEntityManagerFactory;
	}

	public void loadImage(View view, String url) {
		view.setBackgroundDrawable(mApp.getResources().getDrawable(R.drawable.image_default));
		mImageLoader.get(url, getImageListener(view));
	}

	private static ImageLoader.ImageListener getImageListener(final View view) {
		return new ImageLoader.ImageListener() {
			@Override
			public void onErrorResponse(HttpCmd cmd, VolleyError error) {
				view.setBackgroundDrawable(mApp.getResources().getDrawable(R.drawable.image_fail));
			}

			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				if (response.getBitmap() != null) {
					view.setBackgroundDrawable(new BitmapDrawable(response.getBitmap()));
				}
			}
		};
	}
}
