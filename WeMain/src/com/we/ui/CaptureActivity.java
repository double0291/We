package com.we.ui;

import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

import com.we.R;
import com.we.base.BaseActivity;
import com.we.capture.CameraHelper;

public class CaptureActivity extends BaseActivity implements SurfaceHolder.Callback {
	private SurfaceView mSurfaceView;
	private CameraHelper mCameraHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_photo_take);

		initView();
		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mSurfaceView.getHolder().removeCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCameraHelper.init(holder);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCameraHelper.isOpen())
			mCameraHelper.releaseCamera();
	}

	private void initData() {
		mCameraHelper = new CameraHelper(this);
		mSurfaceView.getHolder().addCallback(this);
	}

	private void initView() {
		mSurfaceView = (SurfaceView) findViewById(R.id.preview);
	}
}
