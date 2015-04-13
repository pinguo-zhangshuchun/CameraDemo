package kari.com.org.camerademo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;

import java.util.List;

import kari.com.org.camerademo.util.FileUtil;
import kari.com.org.camerademo.util.SizeUtil;
import kari.com.org.camerademo.util.TickCounter;

/**
 * Camera Activity
 */
public class CameraActivity extends Activity {
    final static String TAG = "CameraActivity";

    private Camera mCamera;
    private CameraPreview mPreview;
    private View mLayoutController;
    private TitleFragment mTitleFragment;
    private FootFragment mFootFragment;
    private TickCounter mTickCounter;
    private ResolutionPopupWindow mPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        findView();
        initView();
        mTickCounter = new TickCounter();
        setListener();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        mTickCounter.restart();
        mCamera = CameraManager.getsInstance().openDefault();
        if (null != mCamera) {
            Camera.Parameters param = mCamera.getParameters();
            mCamera.setParameters(param);
            List<Camera.Size> picSizes = param.getSupportedPictureSizes();
            mPopupWindow.setDataSource(picSizes);
            mPopupWindow.setCurrCameraSize(param.getPictureSize());
            mPreview.setCamera(mCamera);
        }

        mFootFragment.initSeekbar(mCamera);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        mTickCounter.pause();
        if (null != mCamera) {
            mCamera.stopPreview();
        }
        CameraManager.getsInstance().freeCamera();

        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
        }
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

        mPopupWindow = new ResolutionPopupWindow(this);
    }

    private void findView() {
        mPreview = (CameraPreview) findViewById(R.id.camera_preview);
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
        mPreview.setOnTouchListener(touchListener);
        mLayoutController.setOnTouchListener(touchListener);

        mTitleFragment.setCameraSwitchedListener(new TitleFragment.cameraSwitchedListener() {
            @Override
            public void onSwitched() {
                mPreview.startAnimation(AnimationUtils.loadAnimation(CameraActivity.this, R.anim.camera_switch));
                mTickCounter.restart();
                mCamera = CameraManager.getsInstance().openDefault();
                Camera.Parameters param = mCamera.getParameters();
                mPopupWindow.setDataSource(param.getSupportedPictureSizes());
                mPopupWindow.setCurrCameraSize(param.getPictureSize());
                mPopupWindow.dismiss();
                mPreview.setCamera(mCamera);
                mFootFragment.initSeekbar(mCamera);
            }
        });

        mTickCounter.setNotifyCallback(3 * 1000, new TickCounter.OnNotifyCallback() {
            @Override
            public void onEscaped() {
                Log.d(TAG, "onEscapted()");
                hideFocusSeekbar();
            }
        });

        mTickCounter.setLongNotifiyCallback(2 * 60 * 1000, new TickCounter.OnNotifyCallback() {
            @Override
            public void onEscaped() {
                Log.d(TAG, "onLongEscapted()");
                MessageDialog.info(CameraActivity.this, getString(R.string.no_operation_tips), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCamera.startPreview();
                    }
                });
                mCamera.stopPreview();
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
                mTickCounter.pause();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setType("image/*");
                startActivity(intent);
            }
        });

        mFootFragment.getButtonShutter().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTickCounter.restart();
                takePhone();
            }
        });

        mFootFragment.getButtonSetting().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTickCounter.restart();
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                } else {
                    View anchor = mTitleFragment.getBtnSwitch();
                    int w = mPopupWindow.getContentView().getWidth();
                    int x = mTitleFragment.getBtnSwitch().getLeft();
                    int y = mTitleFragment.getBtnSwitch().getBottom();
                    mPopupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, x - w, y);
                }
            }
        });

        mPopupWindow.setPictureSizeChangedListener(new ResolutionPopupWindow.OnPictureSizeChangeListener() {
            @Override
            public void onChanged(Camera.Size size) {
                Log.d(TAG, "onSizeChanged:" + size.width + " x " + size.height);
                Camera.Size bestSize = SizeUtil.get().getBestPreviewSize(size);
                mCamera = CameraManager.getsInstance().openDefault();
                Camera.Parameters param = mCamera.getParameters();
                param.setPictureSize(size.width, size.height);
                param.setPreviewSize(bestSize.width, bestSize.height);
                mCamera.setParameters(param);
                mPreview.setAspectRatio(size.height / (float) size.width);
            }
        });
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
    }

    public void hideFocusSeekbar() {
        mFootFragment.hideSeekbar();
    }

    public void showFocusSeekbar() {
        mFootFragment.showSeekbar();
    }

    public void setExposureCompensation(int value) {
        Camera camera = CameraManager.getsInstance().openDefault();
        Camera.Parameters p = camera.getParameters();
        int max = p.getMaxExposureCompensation();
        int min = p.getMinExposureCompensation();
        float step = p.getExposureCompensationStep();
        int MAX = (int) ((max - min) / step);
        int curr = (int) (value * step) + min;
        Log.d(TAG, "value=" + value);
        Log.d(TAG, "setExposureCompensation = " + curr);
        p.setExposureCompensation(curr);
        camera.setParameters(p);
    }

    public void setWhiteBalance(int value) {
        Camera camera = CameraManager.getsInstance().openDefault();
        Camera.Parameters p = camera.getParameters();
        List<String> balanceList = p.getSupportedWhiteBalance();
        Log.d(TAG, "setWhiteBalance value=" + value);
        Log.d(TAG, "balanceList.size=" + balanceList.size());
        p.setWhiteBalance(balanceList.get(value));
        camera.setParameters(p);
    }

}