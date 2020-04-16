package de.vectordata.skynet.util.android;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public final class Activities {

    private static final String TAG = "Activities";

    private Activities() {
    }

    public static void setStatusBarTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = activity.getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public static void enableUpButton(AppCompatActivity activity) {
        ActionBar supportActionBar = activity.getSupportActionBar();
        if (supportActionBar != null)
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        else Log.e(TAG, "Failed to enable up button: ActionBar is null");
    }
}
