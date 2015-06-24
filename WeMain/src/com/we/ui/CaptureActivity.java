package com.we.ui;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.Window;
import android.widget.FrameLayout;

import com.we.R;
import com.we.base.BaseActivity;
import com.we.capture.CameraHelper;

public class CaptureActivity extends BaseActivity {
	private CameraHelper mCameraHelper;
	private CameraPreview mCameraPreview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_photo_take);

		initData();
		initView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCameraHelper.releaseCamera();
	}

	private void initData() {
		mCameraHelper = new CameraHelper();
		mCameraHelper.init(Camera.CameraInfo.CAMERA_FACING_BACK);
	}

	private void initView() {
		Camera camera = mCameraHelper.getCamera();
		// Create our Preview view and set it as the content of our activity.
		mCameraPreview = new CameraPreview(this, camera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mCameraPreview);
	}
}
