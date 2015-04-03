package kari.com.org.camerademo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by ws-kari on 15-4-1.
 */
public class TitleFragment extends Fragment implements View.OnClickListener {
    private cameraSwitchedListener mListener;
    private Button mBtnSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_title, container, false);
        mBtnSwitch = (Button) view.findViewById(R.id.title_btn_switch);
        mBtnSwitch.setOnClickListener(this);
        return view;
    }

    public Button getBtnSwitch() {
        return mBtnSwitch;
    }

    @Override
    public void onClick(View v) {
        CameraActivity activity = (CameraActivity) getActivity();
        activity.switchCamera();
        if (null != mListener) {
            mListener.onSwitched();
        }
    }

    public void setCameraSwitchedListener(cameraSwitchedListener listener) {
        mListener = listener;
    }

    public interface cameraSwitchedListener {
        public void onSwitched();
    }
}
