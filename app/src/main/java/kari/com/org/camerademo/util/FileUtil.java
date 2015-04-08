package kari.com.org.camerademo.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * Created by ws-kari on 15-4-1.
 */
public final class FileUtil {
    final static String TAG = "FileUtil";

    private final static String ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
    private final static String DIR = "camerademo";
    public final static String PATH = ROOT + "/" + DIR;

    public static void scanFile(Context context, String fName) {
        Uri data = Uri.fromFile(new File(fName));
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
    }

    public static String genJpgName() {
        Date currentTime = new Date();
        StringBuffer buffer = new StringBuffer();
        buffer.append(PATH + "/");
        buffer.append(currentTime.getYear() + 1900 + "-");
        buffer.append(currentTime.getMonth() + 1 + "-");
        buffer.append(currentTime.getDate() + "-");
        buffer.append(currentTime.getHours() + "-");
        buffer.append(currentTime.getMinutes() + "-");
        buffer.append(currentTime.getSeconds());
        buffer.append(".jpg");
        return buffer.toString();
    }

    /**
     * Write the jpeg data into file
     *
     * @param data     jpeg data in memory
     * @param listener save result listener
     * @notice listener  not run in UI thread
     */
    public static void saveJpeg(final byte[] data, final OnSavedListener listener) {
        final File path = new File(PATH);
        if (!path.exists()) {
            path.mkdirs();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                String fileName = genJpgName();
                File file = new File(fileName);

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    fos.write(data);
                    fos.close();
                    fos = null;
                } catch (Exception e) {
                    e.printStackTrace();
                    if (null != listener) {
                        listener.onFailed(e.getMessage());
                    }
                }

                if (null != listener) {
                    listener.onSuccess(fileName);
                }

                Log.d(TAG, "save jpeg ok");
            }
        }).start();
    }

    public interface OnSavedListener {
        public void onSuccess(String path);

        public void onFailed(String errorMsg);
    }
}
