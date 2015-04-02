package kari.com.org.camerademo.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by ws-kari on 15-4-1.
 */
public final class TickCounter {
    final static String TAG = "TickCounter";

    final static int TIME_ESCAPED = 0x1000;
    final static int LONG_TIME_ESCAPED = 0X1001;

    private Handler mHandler;
    private int mCircle;
    private int mCircleLong;
    private OnNotifyCallback mCallback;
    private OnNotifyCallback mCallbackLong;

    public TickCounter() {
        mCircle = 0;
        mCircleLong = 0;
    }

    /**
     * @param circle   the notify circle (in millisecond)
     * @param callback the callback
     * @see setLongNotifyCallback(int circle2, OnNotifyCallback callback2);  circle2 must > circle
     */
    public void setNotifyCallback(int circle, final OnNotifyCallback callback) {
        if (circle <= 0) {
            Log.e(TAG, "circle must be positive number ");
            throw new RuntimeException("circle must be positive number ");
        }

        if (null != mCallback) {
            Log.e(TAG, "setNotifyCallback can be called only once");
            throw new RuntimeException("setNotifyCallback can be called only once");
        }

        mCircle = circle;
        mCallback = callback;
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case TIME_ESCAPED:
                        callback.onEscaped();
                        if (null != mCallbackLong) {
                            sendEmptyMessageDelayed(LONG_TIME_ESCAPED, mCircleLong);
                        }
                        break;

                    case LONG_TIME_ESCAPED:
                        mCallbackLong.onEscaped();
                        break;
                }
            }
        };
    }

    /**
     * @param circle   the notify circle (in millisecond)
     * @param callback the callback
     * @see setNotifyCallback(int circle1, OnNotifyCallback callback2);  circle must > circle1
     */
    public void setLongNotifiyCallback(int circle, OnNotifyCallback callback) {
        if (mCircle <= 0) {
            Log.e(TAG, "please call setNotifyCallback first");
            return;
        }

        if (circle < mCircle) {
            Log.e(TAG, "circle must > circle2 in setNotifyCallback");
            return;
        }

        if (null != mCallbackLong) {
            Log.e(TAG, "setLongNotifyCallback can be called only once");
            throw new RuntimeException("setLongNotifyCallback can be called only once");
        }

        mCircleLong = circle;
        mCallbackLong = callback;
    }

    /**
     * Start tick count
     */
    public void start() {
        if (null == mHandler) {
            Log.e(TAG, "please invoke setNotifyCallback first");
            return;
        }
        mHandler.sendEmptyMessageDelayed(TIME_ESCAPED, mCircle);
    }

    /**
     * Restart tick count
     */
    public void restart() {
        if (null == mHandler) {
            Log.e(TAG, "please invoke setNotifyCallback first");
            return;
        }
        mHandler.removeMessages(TIME_ESCAPED);
        mHandler.sendEmptyMessageDelayed(TIME_ESCAPED, mCircle);
        mHandler.removeMessages(LONG_TIME_ESCAPED);
    }

    public interface OnNotifyCallback {
        public void onEscaped();
    }
}
