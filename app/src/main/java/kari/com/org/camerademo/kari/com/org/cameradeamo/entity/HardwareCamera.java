package kari.com.org.camerademo.kari.com.org.cameradeamo.entity;

import android.hardware.Camera;
import android.util.Log;

import java.util.List;

/**
 * Created by ws-kari on 15-3-31.
 */
public class HardwareCamera {
    final static String TAG = "HardwareCamera";

    private int mCount;
    private int mCurrentId;
    private Camera mCameraBack;
    private Camera mCameraFront;
    private static HardwareCamera sInstance = new HardwareCamera();

    private HardwareCamera() {
        mCurrentId = Camera.CameraInfo.CAMERA_FACING_BACK;
        mCount = Camera.getNumberOfCameras();
        Log.d(TAG, "camera count:" + mCount);
    }

    public static HardwareCamera getsInstance() {
        return sInstance;
    }

    public static void freeInstance() {
        sInstance = null;
    }

    public void freeCamera() {
        if (null != mCameraFront) {
            mCameraFront.stopPreview();
            mCameraFront.release();
            mCameraFront = null;
        }

        if (null != mCameraBack) {
            mCameraBack.stopPreview();
            mCameraBack.release();
            mCameraBack = null;
        }
    }

    public Camera openDefault() {

        if (Camera.CameraInfo.CAMERA_FACING_BACK == mCurrentId) {
            if (null != mCameraBack) {
                return mCameraBack;
            } else {
                mCameraBack = Camera.open(mCurrentId);
                return mCameraBack;
            }
        }

        if (Camera.CameraInfo.CAMERA_FACING_FRONT == mCurrentId) {
            if (null != mCameraFront) {
                return mCameraFront;
            } else {
                mCameraFront = Camera.open(mCurrentId);
                return mCameraFront;
            }
        }

        return null;
    }

    public int getCameraCount() {
        return mCount;
    }

    public Camera switchFrontCamera() {
        if (null != mCameraBack) {
            mCameraBack.stopPreview();
            mCameraBack.release();
            mCameraBack = null;
        }

        mCurrentId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        if (null == mCameraFront) {
            mCameraFront = Camera.open(mCurrentId);
        }
        return mCameraFront;
    }

    public Camera switchBackCamera() {
        if (null != mCameraFront) {
            mCameraFront.stopPreview();
            mCameraFront.release();
            mCameraFront = null;
        }

        mCurrentId = Camera.CameraInfo.CAMERA_FACING_BACK;
        if (null == mCameraBack) {
            mCameraBack = Camera.open(mCurrentId);
        }
        return mCameraBack;
    }

    public Camera switchCamera() {
        if (Camera.CameraInfo.CAMERA_FACING_BACK == mCurrentId) {
            return switchFrontCamera();
        } else {
            return switchBackCamera();
        }
    }

    public Camera.Size getBestSupportedSize() {
        Camera camera = mCameraBack;
        if (null ==  camera) {
            camera = mCameraFront;
        }
        if (null == camera) {
            Log.e(TAG, "camera is null");
            return null;
        }

        Camera.Parameters param = camera.getParameters();
        List<Camera.Size> sizes = param.getSupportedPictureSizes();
        Camera.Size largestSize = sizes.get(0);
        int largestArea = sizes.get(0).height * sizes.get(0).width;
        for (Camera.Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                largestArea = area;
                largestSize = s;
            }
        }
        return largestSize;
    }

}
