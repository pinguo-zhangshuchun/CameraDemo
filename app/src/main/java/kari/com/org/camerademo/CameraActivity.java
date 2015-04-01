package kari.com.org.camerademo;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.SoftReference;
import java.util.List;
import kari.com.org.camerademo.kari.com.org.cameradeamo.entity.EventCode;
import kari.com.org.camerademo.kari.com.org.cameradeamo.entity.HardwareCamera;
import kari.com.org.camerademo.kari.com.org.camerademo.util.FileUtil;

/**
 * Camera Activity
 */
public class CameraActivity extends Activity
        implements SurfaceHolder.Callback, View.OnClickListener, Runnable {

    final static String TAG = "CameraActivity";

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Button btnSwitch;
    private Button btnShutter;
    private SeekBar seekBarFocus;
    private View mLayoutController;
    private MyHandler mHandler;
    private int mEscapeCounter = 0;
    private boolean mRunFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        findView();
        initHolder();
        initView();
        setListener();

        mHandler = new MyHandler(this);
        new Thread(this).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCamera = HardwareCamera.getsInstance().openDefault();
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
        HardwareCamera.getsInstance().freeCamera();
    }

    @Override
    protected void onDestroy() {
        mRunFlag = false;
        super.onDestroy();
    }

    private void initView() {
        if (1 == HardwareCamera.getsInstance().getCameraCount()) {
            btnSwitch.setVisibility(View.GONE);
        } else if (0 == HardwareCamera.getsInstance().getCameraCount()) {
            String tips = getString(R.string.no_camera_tips);
            MessageDialog.exit(this, tips);
            return;
        }
    }

    private void initHolder() {
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void findView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.camera_surfaceview);
        btnShutter = (Button) findViewById(R.id.camera_btn_shutter);
        btnSwitch = (Button) findViewById(R.id.camera_btn_switch);
        seekBarFocus = (SeekBar) findViewById(R.id.camera_seekbar_focus);
        mLayoutController = findViewById(R.id.camera_layout_controller);
    }

    private void setListener() {
        btnShutter.setOnClickListener(this);
        btnSwitch.setOnClickListener(this);
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    mEscapeCounter = 0;
                    showCameraController();
                }

                return false;
            }
        });
        seekBarFocus.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mEscapeCounter = 0;
                Camera.Parameters param = mCamera.getParameters();
                if (param.isZoomSupported()) {
                    param.setZoom(progress);
                } else {
                    Log.d(TAG, "Not support setZoom");
                }
                mCamera.setParameters(param);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mEscapeCounter = 0;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mEscapeCounter = 0;
            }
        });

        btnShutter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "onLongClick");
                return false;
            }
        });

        btnShutter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    Log.d(TAG, "action up");
                }
                return false;
            }
        });
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");

        if (null == mCamera) {
            mCamera = HardwareCamera.getsInstance().openDefault();
        }

        if (null != mCamera) {
            Camera.Parameters param = mCamera.getParameters();
            seekBarFocus.setMax(param.getMaxZoom());
           // param.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);

            if (!param.isZoomSupported()) {
                seekBarFocus.setVisibility(View.GONE);
            }
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

    @Override
    public void onClick(View v) {
        mEscapeCounter = 0;

        switch (v.getId()) {
            case R.id.camera_btn_shutter:
                Log.d(TAG, "onClick");
                mCamera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        mCamera.startPreview();
                        FileUtil.saveJpeg(data, null);
                    }
                });
                break;

            case R.id.camera_btn_switch:
                seekBarFocus.setProgress(0);
                mCamera = HardwareCamera.getsInstance().switchCamera();
                try {
                    mCamera.setPreviewDisplay(mSurfaceHolder);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                mCamera.setDisplayOrientation(90);
                mCamera.startPreview();
                break;
        }
    }

    public void hideCameraController() {
        mLayoutController.setVisibility(View.GONE);
    }

    public void showCameraController() {
        mLayoutController.setVisibility(View.VISIBLE);
        Message msg = mHandler.obtainMessage();
        msg.what = EventCode.Event_User_Touch;
        mHandler.sendMessage(msg);
    }

    @Override
    public void run() {
        while (mRunFlag) {
            try {
                Thread.sleep(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mEscapeCounter++;

            // time escaped more than 200 * 25 = 5000 seconds
            if (mEscapeCounter >= 25) {
                Message msg = mHandler.obtainMessage();
                msg.what = EventCode.Event_Time_Escaped;
                mHandler.sendMessage(msg);

                // avoid over flow
                mEscapeCounter = 25;
            }
        }
    }

    class MyHandler extends Handler {
        SoftReference<CameraActivity> weakRef;

        public MyHandler(CameraActivity activity) {
            weakRef = new SoftReference<CameraActivity>(activity);
        }

        private Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                CameraActivity activity = weakRef.get();
                if (null == activity) {
                    Log.e(TAG, "activity is null in SoftReference");
                    return;
                }

                activity.hideCameraController();
            }
        };

        @Override
        public void handleMessage(Message msg) {
            final CameraActivity activity = weakRef.get();
            if (null == activity) {
                Log.e(TAG, "activity is null in SoftReference");
                return;
            }

            switch (msg.what) {
                case EventCode.Event_Time_Escaped:
                    this.postDelayed(mRunnable, 5000);
                    break;

                case EventCode.Event_User_Touch:
                    this.removeCallbacks(mRunnable);
                    break;
            }
        }
    }
}