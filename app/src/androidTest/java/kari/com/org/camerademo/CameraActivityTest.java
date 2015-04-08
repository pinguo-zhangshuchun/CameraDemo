package kari.com.org.camerademo;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;

/**
 * Created by ws-kari on 15-4-8.
 */
public class CameraActivityTest extends ActivityInstrumentationTestCase2<CameraActivity> {

    private Instrumentation mInstrumentation;
    private CameraActivity mActivity;

    public CameraActivityTest() {
        super(CameraActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mInstrumentation = getInstrumentation();
        mActivity = getActivity();
    }

    public void testSwitchCamera() {
        mActivity.switchCamera();
    }

    public void testShowSeekbar() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivity.showFocusSeekbar();
            }
        });
    }

    public void testHideSeekbar() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivity.hideFocusSeekbar();
            }
        });
    }

    public void testTakePhoto() {
        mActivity.takePhone();
        try {
            Thread.sleep(2000);  // wait for writing image
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testPressHome() {
        mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_HOME);
    }

    public void testPressBack() {
        mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
    }

}
