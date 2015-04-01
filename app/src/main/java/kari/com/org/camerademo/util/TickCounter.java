package kari.com.org.camerademo.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by ws-kari on 15-4-1.
 */
public final class TickCounter implements Runnable {

    final static String TAG = "TickCounter";
    final static int TIME_ESCAPED = 0x10;

    private boolean mPauseFlag;
    private boolean mRunFlag;
    private long mCounter;
    private final int mCircle;
    private onNotifyCallback mCallback;
    private Runnable mRunnable;

    public TickCounter() {
        mPauseFlag = true;
        mRunFlag = true;
        mCounter = 0;
        mCircle = 0;
        create();
    }

    private void create() {
        new Thread(this).start();
    }

    /**
     * @param circle   the notify circle (in second)
     * @param callback
     */
    public void setNotifyCallback(int circle, onNotifyCallback callback) {
        if (circle <= 0) {
            Log.e(TAG, "circle must be positive number ");
            throw new RuntimeException("circle must be positive number ");
        }

        if (null != mCallback) {
            Log.e(TAG, "Only one client can be registered");
            throw new RuntimeException("Only one client can be registered");
        }
        mCallback = callback;

        mRunnable = new Runnable() {
            @Override
            public void run() {
                mCallback.onEscaped();
            }
        };
    }

    /**
     * Start tick count
     */
    public void start() {
        mPauseFlag = false;
        mCounter = 0;
        mHandler.postDelayed(mRunnable, mCircle * 1000);
    }

    /**
     * Pause tick count
     */
    public void pause() {
        mPauseFlag = true;
        mCounter = 0;
        mHandler.removeCallbacks(mRunnable);
    }

    /**
     * Stop notify tick escaped and free the resource.
     *
     * @notice: Remember call me after not use TickCounter any more
     */
    public void stop() {
        mRunFlag = false;
    }

    private Handler mHandler = new Handler() {

        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                mCallback.onEscaped();
            }
        };

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handleMessage()");
            start();
        }
    };

    @Override
    public void run() {
        while (mRunFlag) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*
            if (mPauseFlag) {
                continue;
            }
            */

            ++mCounter;
            //avoid over flow
            if (mCounter >= 10000) {
                mCounter = 10000;
            }
            if (mCounter / 10 >= mCircle) {
                mHandler.sendEmptyMessage(TIME_ESCAPED);
            }
        }
    }

    public interface onNotifyCallback {
        public void onEscaped();
    }
}
