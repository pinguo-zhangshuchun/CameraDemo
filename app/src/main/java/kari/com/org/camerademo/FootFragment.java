package kari.com.org.camerademo;

import android.app.Fragment;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

/**
 * Created by ws-kari on 15-4-1.
 */
public class FootFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private CameraActivity mActivity;
    private SeekBar mSeekBarFocus;
    private Button mBtnShutter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_foot, container, false);
        mActivity = (CameraActivity) getActivity();
        initView(view);
        setListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Camera camera = mActivity.getCamera();
        if (null != camera) {
            int max = camera.getParameters().getMaxZoom();
            Camera.Parameters param = camera.getParameters();
            if (param.isZoomSupported()) {
                mSeekBarFocus.setMax(max);
                mSeekBarFocus.setEnabled(true);
                mSeekBarFocus.setProgress(param.getZoom());
            } else {
                mSeekBarFocus.setEnabled(false);
                mSeekBarFocus.setProgress(0);
            }
        }
    }

    private void initView(View view) {
        mSeekBarFocus = (SeekBar) view.findViewById(R.id.fragment_foot_seekbar_focus);
        mBtnShutter = (Button) view.findViewById(R.id.fragment_foot_btn_shutter);
    }

    private void setListener() {
        mBtnShutter.setOnClickListener(this);
        mSeekBarFocus.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        mActivity.takePhone();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mActivity.changeCameraZoom(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
