package de.vectordata.skynet.util;

import android.app.AlertDialog;
import android.content.Context;

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

    public static void showMessageBox(Context context, int titleRes, String content) {
        new AlertDialog.Builder(context)
                .setTitle(titleRes)
                .setMessage(content)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

}
