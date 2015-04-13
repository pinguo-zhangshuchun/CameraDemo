package kari.com.org.camerademo;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import java.util.List;

/**
 * Created by ws-kari on 15-4-7.
 */
public class SettingView extends RelativeLayout {
    final static String TAG = "SettingView";
    private Context mContext;
    private SeekBar mSeekBarExposure;
    private SeekBar mSeekBarBalance;

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
        mSeekBarBalance = (SeekBar) findViewById(R.id.settingview_seekbar_balance);
        initExposureSeekBar();
        initBalanceSeekBar();
    }

    public void initBalanceSeekBar() {
        Camera camera = CameraManager.getsInstance().openDefault();
        Camera.Parameters p = camera.getParameters();
        String balance = p.getWhiteBalance();
        List<String> balanceList = p.getSupportedWhiteBalance();
        Log.d(TAG, "balanceList.size="+balanceList.size());
        mSeekBarBalance.setMax(balanceList.size()-1);
        int idx = balanceList.indexOf(balance);
        Log.d(TAG, "idx="+idx);
        mSeekBarBalance.setProgress(idx);
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

    public SeekBar getmSeekBarBalance() {
        return mSeekBarBalance;
    }
}
