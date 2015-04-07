package kari.com.org.camerademo.util;

import android.hardware.Camera;
import android.util.Log;

import java.util.List;

import kari.com.org.camerademo.CameraManager;

/**
 * Created by ws-kari on 15-4-3.
 */
public final class SizeUtil {
    final static String TAG = "SizeUtil";
    final static float MIN_ERROR = 0.01f;
    static SizeUtil _sInstance;

    public static SizeUtil get() {
        if (null == _sInstance) {
            synchronized (SizeUtil.class) {
                if (null == _sInstance) {
                    _sInstance = new SizeUtil();
                }
            }
        }

        return _sInstance;
    }

    private SizeUtil() {

    }

    public Camera.Size getBestPreviewSize(Camera.Size picSize) {
        Camera camera = CameraManager.getsInstance().openDefault();
        if (null == camera) {
            throw new RuntimeException("CameraManager.openDefault return null");
        }
        final float picRatio = picSize.width / (float) picSize.height;
        Log.d(TAG, "picSize width:" + picSize.width + ",height:" + picSize.height + ",ratio:" + picRatio);

        List<Camera.Size> prevSizes = camera.getParameters().getSupportedPreviewSizes();
        Camera.Size bestSize = null;
        for (Camera.Size s : prevSizes) {
            float ratio = s.width / (float) s.height;
            if (ratio >= picRatio - MIN_ERROR && ratio <= picRatio + MIN_ERROR) {
                Log.d(TAG, "width:" + s.width + ",height:" + s.height + ",ratio:" + s.width / (float) s.height);
                if (null == bestSize || s.width > bestSize.width) {
                    bestSize = s;
                }
            }
        }

        if (null == bestSize) {
            Log.d(TAG, "Failed find the best preview size. So select the first suported prevsize");
            bestSize = prevSizes.get(0);
        }

        Log.d(TAG, "best size width:" + bestSize.width + ",height:" + bestSize.height + ",ratio:" + bestSize.width / (float) bestSize.height);
        return bestSize;
    }
}
