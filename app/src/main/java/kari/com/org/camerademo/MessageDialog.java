package kari.com.org.camerademo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

/**
 * Created by ws-kari on 15-3-31.
 */
public class MessageDialog {
    public static void exit(final Activity context, String messge) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(messge, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.finish();
            }
        });
        builder.create().show();
    }

    public static void info(final Activity context, String messge) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        dialog.setMessage(messge);
        dialog.setButton("OK", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
