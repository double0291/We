package com.we.capture;

import java.io.IOException;

import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.we.util.Logger;

public class CameraHelper {
	private Camera mCamera;
	private int mCameraId;
	private boolean mHasInit;

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

		mCamera.setDisplayOrientation(90);

		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			Logger.e("Error setting camera preview: " + e.getMessage(), false);
			return false;
		}

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
			cameraId = getBackCameraId();
		// 没有后置摄像头的话，打开第一个
		if (cameraId == -1)
			cameraId = 0;

		this.mCameraId = cameraId;
		try {
			mCamera = Camera.open(cameraId);
		} catch (Exception e) {
			mCamera = null;
			Logger.e("open Camera failed!", false);
		}
		return mCamera != null;
	}

	/**
	 * @return 前置摄像头CameraId，没有的话，返回－1
	 */
	public int getFrontCameraId() {
		return getCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
	}

	/**
	 * @return 后置摄像头CameraId，没有的话，返回－1
	 */
	public int getBackCameraId() {
		return getCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
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
}
