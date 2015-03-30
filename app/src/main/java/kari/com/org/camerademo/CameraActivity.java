package kari.com.org.camerademo;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Camera Activity
 */
public class CameraActivity extends Activity implements SurfaceHolder.Callback {

    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        findView();
        initHolder();
    }

    private void initHolder() {
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void findView() {
        mSurfaceView = (SurfaceView)findViewById(R.id.camera_surfaceview);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open();
        if (null == mCamera) {
            Toast.makeText(this, "camera not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
        }

        mCamera.startPreview();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.release();
    }
}