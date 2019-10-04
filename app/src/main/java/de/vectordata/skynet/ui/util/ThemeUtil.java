package de.vectordata.skynet.ui.util;

import android.content.Context;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import de.vectordata.skynet.R;

public class ThemeUtil {

    public static void resetTextViewColor(TextView view) {
        Context context = view.getContext();
        view.setTextColor(ContextCompat.getColor(context, R.color.gray));
    }

}
