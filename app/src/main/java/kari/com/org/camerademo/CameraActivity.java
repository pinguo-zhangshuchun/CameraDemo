package kari.com.org.camerademo;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.SeekBar;

import java.util.List;

import kari.com.org.camerademo.util.FileUtil;
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
        mTickCounter.restart();
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

        mFootFragment.initSeekbar(mCamera);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTickCounter.pause();
        if (null != mCamera) {
            mCamera.stopPreview();
        }
        CameraManager.getsInstance().freeCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch");
                showFocusSeekbar();
                mTickCounter.restart();
                return false;
            }
        };
        mSurfaceView.setOnTouchListener(touchListener);
        mLayoutController.setOnTouchListener(touchListener);

        mTitleFragment.setCameraSwitchedListener(new TitleFragment.cameraSwitchedListener() {
            @Override
            public void onSwitched() {
                mTickCounter.restart();
                updateSurfaceView();
            }
        });

        mTickCounter.setNotifyCallback(5 * 1000, new TickCounter.OnNotifyCallback() {
            @Override
            public void onEscaped() {
                Log.d(TAG, "onEscapted()");
                hideFocusSeekbar();
            }
        });

        mTickCounter.setLongNotifiyCallback(25 * 1000, new TickCounter.OnNotifyCallback() {
            @Override
            public void onEscaped() {
                Log.d(TAG, "onLongEscapted()");
                MessageDialog.info(CameraActivity.this, getString(R.string.no_operation_tips));
            }
        });

        mFootFragment.getSeekBar().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changeCameraZoom(progress);
                mTickCounter.restart();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mTickCounter.restart();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mFootFragment.getButtonGallery().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setType("image/*");
                startActivity(intent);
            }
        });

        mFootFragment.getButtonShutter().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhone();
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

            List<Camera.Size> picSizes = param.getSupportedPictureSizes();
            Log.d(TAG, "picture size:" + picSizes.size());
            for (Camera.Size size : picSizes) {
                Log.d(TAG, "width:" + size.width + ",height:" + size.height);
            }

            List<Camera.Size> prevSizes = param.getSupportedPreviewSizes();
            Log.d(TAG, "preview size:" + prevSizes.size());
            for (Camera.Size size : prevSizes) {
                Log.d(TAG, "width:" + size.width + ",height:" + size.height);
            }

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
                FileUtil.saveJpeg(data, new FileUtil.OnSavedListener() {
                    @Override
                    public void onSuccess(String path) {
                        Log.d(TAG, "save success," + path);
                        FileUtil.scanFile(CameraActivity.this, path);
                    }

                    @Override
                    public void onFailed(String errorMsg) {
                        Log.e(TAG, "Failed to save jpeg file," + errorMsg);
                    }
                });
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

    public void hideFocusSeekbar() {
        mFootFragment.hideSeekbar();
    }

    public void showFocusSeekbar() {
        mFootFragment.showSeekbar();
    }
}