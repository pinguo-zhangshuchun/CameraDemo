package kari.com.org.camerademo;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;
import kari.com.org.camerademo.util.SizeUtil;

/**
 * Created by ws-kari on 15-4-7.
 */
public class CameraPreview extends RelativeLayout implements SurfaceHolder.Callback {
    final static String TAG = "CameraPreview";

    private Context mContext;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private int mWidth;
    private int mHeight;

    public CameraPreview(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public CameraPreview(Context context, AttributeSet attrSet) {
        super(context, attrSet);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.surfaceview_camera, this);
        mSurfaceView = (SurfaceView) findViewById(R.id.camera_surfaceview);
        mHolder = mSurfaceView.getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mWidth = getWidth();
        mHeight = getHeight();

        Log.d(TAG, "width:" + mWidth + ",height:" + mHeight);
        mHolder = mSurfaceView.getHolder();
        if (null != mCamera) {
            try {
                mCamera.setPreviewDisplay(mSurfaceView.getHolder());
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCamera.startPreview();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged");
        Log.d(TAG, "width:" + width + ",height:" + height);
        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (Exception e) {
            mCamera.release();
            mCamera = null;
            Log.e(TAG, "Failed setPreviewDisplay");
            e.printStackTrace();
            return;
        }
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed()");
        if (null != mCamera) {
            mCamera.release();
            mCamera = null;
        }
    }

    public void setCamera(Camera camera) {
        if (null == camera) {
            throw new NullPointerException("camera could not be null");
        }

        if (mCamera == camera) {
            return;
        }

        mCamera = camera;
        mCamera.setDisplayOrientation(90);

        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        mCamera.startPreview();
    }

    private void resizePreview(int width, int height) {
        Log.d(TAG, "resizePreview()");
        Log.d(TAG, "width=" + width + ",height=" + height);
        getLayoutParams().width = width;
        getLayoutParams().height = height;
        requestLayout();
    }

    public void setAspectRatio(float ratio) {
        Log.d(TAG, "setAspectRatio()");
        final float currentRatio = mWidth / (float) mHeight;
        Log.d(TAG, "expect ratio=" + ratio + " screen ratio=" + currentRatio);

        // the current view aspect ratio is good
        if (ratio >= currentRatio - SizeUtil.MIN_ERROR && ratio <= currentRatio + SizeUtil.MIN_ERROR) {
            resizePreview(mWidth, mHeight);
            return;
        }

        int w = mWidth;
        int h = mHeight;

        // zoom out  (make short)  the height of SurfaceView's parent layout
        if (ratio > currentRatio + SizeUtil.MIN_ERROR) {
            h = (int) (h * currentRatio / ratio);
            resizePreview(w, h);
            return;
        }

        // zoom out ( make short ) the width of SurfaceView's parent layout.
        if (ratio < currentRatio - SizeUtil.MIN_ERROR) {
            w = (int) (w * ratio / currentRatio);
            resizePreview(w, h);
            return;
        }
    }

}
