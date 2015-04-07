package kari.com.org.camerademo;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

/**
 * Created by ws-kari on 15-4-7.
 */
public class CameraPreview extends RelativeLayout implements SurfaceHolder.Callback {
    final static String TAG = "CameraSurfaceView";

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

        mWidth = getWidth();
        mHeight = getHeight();

        Log.d(TAG, "width:" + mWidth + ",height:" + mHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
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

    public void setAspectRatio(float ratio) {

    }

}
