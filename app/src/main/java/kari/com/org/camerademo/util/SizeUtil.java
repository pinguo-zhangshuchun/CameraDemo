package kari.com.org.camerademo.util;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.WindowManager;

import java.util.List;

import kari.com.org.camerademo.CameraManager;

/**
 * Created by ws-kari on 15-4-3.
 */
public final class SizeUtil {
    final static String TAG = "SizeUtil";
    final static float MIN_ERROR = 0.01f;
    static SizeUtil _sInstance;
    private float mScreenAspectRatio;
    private Context mContext;

    public static SizeUtil get(Context context) {
        if (null == _sInstance) {
            synchronized (SizeUtil.class) {
                if (null == _sInstance) {
                    _sInstance = new SizeUtil(context);
                }
            }
        }

        return _sInstance;
    }

    private SizeUtil(Context context) {
        mContext = context;
        getScreenAspectRatio(context);
    }

    private void getScreenAspectRatio(Context context) {
        if (null == context) {
            throw new NullPointerException("activity is null");
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        mScreenAspectRatio = width / (float) height;
        Log.d(TAG, "width:" + width + ",height:" + height + ",ratio:" + mScreenAspectRatio);
    }

    public Camera.Size getBestPreviewSize(Camera.Size picSize) {
        Camera camera = CameraManager.getsInstance().openDefault();
        if (null == camera) {
            throw new RuntimeException("CameraManager.openDefault return null");
        }

        Log.d(TAG, "picSize width:" + picSize.width + ",height:" + picSize.height + ",ratio:" + picSize.width / (float) picSize.height);

        List<Camera.Size> prevSizes = camera.getParameters().getSupportedPreviewSizes();
        Camera.Size bestSize = prevSizes.get(0);
        for (Camera.Size s : prevSizes) {
            float ratio = s.width / (float) s.height;
            if (ratio >= mScreenAspectRatio - MIN_ERROR && ratio <= mScreenAspectRatio + MIN_ERROR) {
                if (s.width > bestSize.width) {
                    bestSize = s;
                }
            }
        }

        Log.d(TAG, "best size width:" + bestSize.width + ",height:" + bestSize.height + ",ratio:" + bestSize.width / (float) bestSize.height);
        return bestSize;
    }
}
