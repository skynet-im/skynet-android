package de.vectordata.skynet.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import de.vectordata.skynet.R;

/**
 * Created by Twometer on 11.12.2018.
 * (c) 2018 Twometer
 */
public class Dialogs {

    public static void showMessageBox(Context context, int titleRes, int contentRes) {
        new AlertDialog.Builder(context)
                .setTitle(titleRes)
                .setMessage(contentRes)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    public static void showMessageBox(Context context, int titleRes, int contentRes, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(titleRes)
                .setMessage(contentRes)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    public static void showYesNoBox(Context context, int titleRes, int contentRes, DialogInterface.OnClickListener yesListener, DialogInterface.OnClickListener noListener) {
        new AlertDialog.Builder(context)
                .setTitle(titleRes)
                .setMessage(contentRes)
                .setPositiveButton(R.string.yes, yesListener)
                .setPositiveButton(R.string.no, noListener)
                .show();
    }

    public static void showMessageBox(Context context, int titleRes, String content) {
        new AlertDialog.Builder(context)
                .setTitle(titleRes)
                .setMessage(content)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    public static ProgressDialog showProgressDialog(Context context, int contentRes, boolean cancellable) {
        View rootView = View.inflate(context, R.layout.dialog_progress, null);
        TextView textView = rootView.findViewById(R.id.label_progress);
        textView.setText(contentRes);

        ProgressDialog progressDialog = new ProgressDialog(textView);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(rootView)
                .setTitle(R.string.progress_header);
        if (cancellable)
            builder.setNegativeButton(R.string.cancel, progressDialog);

        progressDialog.setAlertDialog(builder.show());
        return progressDialog;
    }

}
