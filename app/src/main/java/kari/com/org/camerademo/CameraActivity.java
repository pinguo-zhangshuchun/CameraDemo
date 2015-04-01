package kari.com.org.camerademo;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import kari.com.org.camerademo.kari.com.org.camerademo.util.FileUtil;
import kari.com.org.camerademo.util.TickCounter;

/**
 * Camera Activity
 */
public class CameraActivity extends Activity implements SurfaceHolder.Callback {
    final static String TAG = "CameraActivity";

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private View mLayoutController;
    private TitleFragment mTitleFragment;
    private FootFragment mFootFragment;
    private TickCounter mTickCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        findView();
        initHolder();
        initView();
        mTickCounter = new TickCounter();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTickCounter.start();

        mCamera = CameraManager.getsInstance().openDefault();
        if (null != mCamera) {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
            } catch (Exception e) {
                Log.e(TAG, "Failed setPreviewDisplay");
            }

            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTickCounter.pause();
        CameraManager.getsInstance().freeCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTickCounter.stop();
    }

    public Camera getCamera() {
        return mCamera;
    }

    private void initView() {
        if (0 == CameraManager.getsInstance().getCameraCount()) {
            String tips = getString(R.string.no_camera_tips);
            MessageDialog.exit(this, tips);
        }
    }

    private void initHolder() {
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void findView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.camera_surfaceview);
        mLayoutController = findViewById(R.id.camera_layout_controller);
        mTitleFragment = (TitleFragment) getFragmentManager().findFragmentById(R.id.fragment_title);
        mFootFragment = (FootFragment) getFragmentManager().findFragmentById(R.id.fragment_foot);
    }

    public void changeCameraZoom(int progress) {
        Camera.Parameters param = mCamera.getParameters();
        if (param.isZoomSupported()) {
            param.setZoom(progress);
        } else {
            Log.d(TAG, "Not support setZoom");
        }
        mCamera.setParameters(param);
    }

    private void setListener() {
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    showCameraController();
                    mTickCounter.pause();
                }
                return false;
            }
        });

        mTitleFragment.setCameraSwitchedListener(new TitleFragment.cameraSwitchedListener() {
            @Override
            public void onSwitched() {
                updateSurfaceView();
            }
        });

        mTickCounter.setNotifyCallback(5, new TickCounter.onNotifyCallback() {
            @Override
            public void onEscaped() {
                Log.d(TAG, "onEscapted()");
                hideCameraController();
            }
        });
    }

    private void updateSurfaceView() {
        mFootFragment.onResume();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
        if (null == mCamera) {
            mCamera = CameraManager.getsInstance().openDefault();
        }
        if (null != mCamera) {
            Camera.Parameters param = mCamera.getParameters();
            mCamera.setParameters(param);
            mCamera.setDisplayOrientation(90);
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged");
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (Exception e) {
            mCamera.release();
            mCamera = null;
            Log.e(TAG, "Failed setPreviewDisplay");
            e.printStackTrace();
            return;
        }
        mCamera.startPreview();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (null != mCamera) {
            mCamera.release();
            mCamera = null;
        }
    }

    public void takePhone() {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                mCamera.startPreview();
                FileUtil.saveJpeg(data, null);
            }
        });
    }

    public void switchCamera() {
        mCamera = CameraManager.getsInstance().switchCamera();
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
    }

    public void hideCameraController() {
        mLayoutController.setVisibility(View.GONE);
    }

    public void showCameraController() {
        mLayoutController.setVisibility(View.VISIBLE);
    }
}