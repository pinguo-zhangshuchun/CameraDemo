package kari.com.org.camerademo;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

/**
 * Created by ws-kari on 15-4-7.
 */
public class SettingView extends RelativeLayout {
    final static String TAG = "SettingView";
    private Context mContext;
    private SeekBar mSeekBarExposure;

    public SettingView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public SettingView(Context context, AttributeSet attrSet) {
        super(context, attrSet);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.view_setting, this);
        mSeekBarExposure = (SeekBar) findViewById(R.id.settingview_seekbar_exposure);
        initExposureSeekBar();
    }

    public void initExposureSeekBar() {
        Camera camera = CameraManager.getsInstance().openDefault();
        Camera.Parameters p = camera.getParameters();
        int max = p.getMaxExposureCompensation();
        int min = p.getMinExposureCompensation();
        int cur = p.getExposureCompensation();
        float step = p.getExposureCompensationStep();

        Log.d(TAG, "max=" + max + ",min=" + min + ",cur=" + cur + ",step=" + step);

        if (max <= min || (step >= -0.001 && step <= 0.001)) {
            mSeekBarExposure.setEnabled(false);
            return;
        }

        mSeekBarExposure.setMax((int) ((max - min) / step));
        mSeekBarExposure.setProgress((int) ((cur - min) / step));
    }

    public SeekBar getSeekBarExposure() {
        return mSeekBarExposure;
    }
}
