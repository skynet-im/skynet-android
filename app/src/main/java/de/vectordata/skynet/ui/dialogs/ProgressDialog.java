package de.vectordata.skynet.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.TextView;

import java.util.Objects;

public class ProgressDialog implements DialogInterface.OnClickListener {

    private AlertDialog alertDialog;

    private TextView textView;

    private boolean cancelled = false;

    ProgressDialog(TextView textView) {
        Objects.requireNonNull(textView);
        this.textView = textView;
    }

    void setAlertDialog(AlertDialog alertDialog) {
        Objects.requireNonNull(alertDialog);
        this.alertDialog = alertDialog;
    }

    public void setMessage(int resId) {
        textView.setText(resId);
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isOpen() {
        return alertDialog.isShowing();
    }

    void show() {
        alertDialog.show();
    }

    public void dismiss() {
        alertDialog.dismiss();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == Dialog.BUTTON_NEGATIVE)
            cancelled = true;
    }
}
