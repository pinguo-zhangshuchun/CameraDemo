package kari.com.org.camerademo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by ws-kari on 15-3-31.
 */
public class MessageDialog {
    public static void exit(final Activity context, String messge) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        builder.setPositiveButton(messge, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                context.finish();
            }
        });
        dialog.show();
    }
}
