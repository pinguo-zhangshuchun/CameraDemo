package kari.com.org.camerademo;

import android.app.Fragment;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

/**
 * Created by ws-kari on 15-4-1.
 */
public class TitleFragment extends Fragment implements View.OnClickListener {
    final static String TAG = "TitleFragment";
    private cameraSwitchedListener mListener;
    private CameraActivity mActivity;
    private Button mBtnSwitch;
    private Button mBtnMore;
    private SettingView mSettingView;
    private PopupWindow mPopupWindow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_title, container, false);
        mBtnSwitch = (Button) view.findViewById(R.id.title_btn_switch);
        mBtnMore = (Button) view.findViewById(R.id.title_btn_more);
        mActivity = (CameraActivity) getActivity();
        mSettingView = new SettingView(mActivity);
        mPopupWindow = new PopupWindow(mSettingView,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                false);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        setListener();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    private void setListener() {
        mBtnSwitch.setOnClickListener(this);
        mBtnMore.setOnClickListener(this);
        mSettingView.getSeekBarExposure().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int seekProgress = seekBar.getProgress();
                if (seekProgress < 13) {
                    seekBar.setProgress(0);
                    mActivity.setExposureCompensation(-2);
                    Log.d(TAG, "exposure -2");
                } else if (seekProgress >= 13 && seekProgress < 38) {
                    seekBar.setProgress(25);
                    mActivity.setExposureCompensation(-1);
                    Log.d(TAG, "exposure -1");
                } else if (seekProgress >= 38 && seekProgress < 63) {
                    seekBar.setProgress(50);
                    mActivity.setExposureCompensation(0);
                    Log.d(TAG, "exposure normal");
                } else if (seekProgress >= 63 && seekProgress < 88) {
                    seekBar.setProgress(75);
                    mActivity.setExposureCompensation(+1);
                    Log.d(TAG, "exposure +1");
                } else if (seekProgress >= 88) {
                    seekBar.setProgress(100);
                    mActivity.setExposureCompensation(+2);
                    Log.d(TAG, "exposure +2");
                }
            }
        });
    }

    public Button getBtnSwitch() {
        return mBtnSwitch;
    }

    public Button getmBtnMore() {
        return mBtnMore;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_btn_switch:
                mActivity.switchCamera();
                mSettingView.initExposureSeekBar();
                if (null != mListener) {
                    mListener.onSwitched();
                }
                break;

            case R.id.title_btn_more:
                if (!mPopupWindow.isShowing()) {
                    mPopupWindow.showAsDropDown(getView());
                } else {
                    mPopupWindow.dismiss();
                }
                break;
        }
    }

    public void setCameraSwitchedListener(cameraSwitchedListener listener) {
        mListener = listener;
    }

    public interface cameraSwitchedListener {
        public void onSwitched();
    }
}
