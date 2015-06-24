package com.we.capture;

import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.we.util.Logger;

public class CameraHelper {
	private Activity mActivity;
	private Camera mCamera;
	private int mCameraId;
	private boolean mHasInit;

	public CameraHelper(Activity activity) {
		this.mActivity = activity;
	}

	public Camera getCamera() {
		return mCamera;
	}

	public boolean init(SurfaceHolder holder) {
		return init(Camera.CameraInfo.CAMERA_FACING_BACK, holder);
	}

	/**
	 * @param {@link Camera.CameraInfo#CAMERA_FACING_FRONT} {@link Camera.CameraInfo#CAMERA_FACING_BACK}
	 */
	public boolean init(int cameraType, SurfaceHolder holder) {
		if (mHasInit)
			return false;

		if (!open(getCameraId(cameraType)))
			return false;

		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			Logger.e("Error setting camera preview: " + e.getMessage(), false);
			return false;
		}

		if (!config())
			return false;

		mCamera.startPreview();

		mHasInit = true;

		return true;
	}

	public void releaseCamera() {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mHasInit = false;
		}
	}

	public boolean isOpen() {
		return mCamera != null && mHasInit;
	}

	private boolean open(int cameraId) {
		// 如果手机没有摄像头，直接退出
		if (Camera.getNumberOfCameras() <= 0) {
			return false;
		}

		// 默认打开后置摄像头
		if (cameraId < 0)
			cameraId = getCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
		// 没有后置摄像头的话，打开第一个
		if (cameraId == -1)
			cameraId = 0;

		this.mCameraId = cameraId;
		try {
			mCamera = Camera.open(cameraId);
		} catch (Exception e) {
			mCamera = null;
			Logger.e("Error open Camera:" + e.getMessage(), false);
		}
		return mCamera != null;
	}

	private int getCameraId(int cameraType) {
		int numberOfCameras = Camera.getNumberOfCameras();
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		for (int i = 0; i < numberOfCameras; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == cameraType) {
				return i;
			}
		}

		return -1;
	}

	private boolean config() {
		// 设置预览的方向，但是该方法只能支持api 14以及以上，并且在一些机型上面支持性可能也不够
		try {
			mCamera.setDisplayOrientation(getPreviewDegreeRotate());
		} catch (Exception e) {
			Logger.e("Error setDisplayOrientation: " + e.getMessage(), false);
			return false;
		}
		return true;
	}

	private int getPreviewDegreeRotate() {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(mCameraId, info);
		int degrees = getWindowRotate();
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}

		return result;
	}

	private int getWindowRotate() {
		int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		return degrees;
	}
}
