package de.vectordata.skynet.ui.util;

import android.content.Context;

import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.R;

public class DateUtil {

    public static String toDateString(Context context, DateTime dateTime) {
        if (dateTime.isToday())
            return context.getString(R.string.today);
        else if (dateTime.isYesterday())
            return context.getString(R.string.yesterday);
        else
            return dateTime.toDateString(context);
    }

    public static String toString(Context context, DateTime dateTime) {
        if (dateTime.isToday())
            return dateTime.toTimeString(context);
        else if (dateTime.isYesterday())
            return context.getString(R.string.yesterday);
        else
            return dateTime.toDateString(context);
    }

}
