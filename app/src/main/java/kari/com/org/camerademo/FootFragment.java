package kari.com.org.camerademo;

import android.app.Fragment;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

/**
 * Created by ws-kari on 15-4-1.
 */
public class FootFragment extends Fragment {
    final static String TAG = "FootFragment";

    private SeekBar mSeekBarFocus;
    private Button mBtnShutter;
    private Button mBtnGallery;
    private Button mBtnSetting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_foot, container, false);
        initView(view);
        return view;
    }

    public void initSeekbar(Camera camera) {
        if (null == camera) {
            Log.e(TAG, "camera is null");
            return;
        }
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

    private void initView(View view) {
        mSeekBarFocus = (SeekBar) view.findViewById(R.id.fragment_foot_seekbar_focus);
        mBtnShutter = (Button) view.findViewById(R.id.fragment_foot_btn_shutter);
        mBtnGallery = (Button) view.findViewById(R.id.fragment_foot_btn_gallery);
        mBtnSetting = (Button) view.findViewById(R.id.fragment_foot_btn_setting);
    }

    public void hideSeekbar() {
        mSeekBarFocus.setVisibility(View.GONE);
    }

    public void showSeekbar() {
        mSeekBarFocus.setVisibility(View.VISIBLE);
    }

    public Button getButtonShutter() {
        return mBtnShutter;
    }

    public Button getButtonGallery() {
        return mBtnGallery;
    }

    public Button getButtonSetting() {
        return mBtnSetting;
    }

    public SeekBar getSeekBar() {
        return mSeekBarFocus;
    }
}
