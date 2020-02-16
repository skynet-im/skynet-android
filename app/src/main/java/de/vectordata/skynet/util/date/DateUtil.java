package de.vectordata.skynet.util.date;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

import de.vectordata.skynet.R;

public final class DateUtil {

    public static String toLastSeen(Context context, DateTime dateTime) {
        if (dateTime.isToday())
            return String.format(context.getString(R.string.state_last_seen_today), dateTime.toTimeString(context));
        else if (dateTime.isYesterday())
            return String.format(context.getString(R.string.state_last_seen_yesterday), dateTime.toTimeString(context));
        else
            return String.format(context.getString(R.string.state_last_seen), dateTime.toDateTimeString(context));
    }

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

    static Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal.getTime();
    }

    static Date createDate(int year, int month, int day, int hour, int min, int sec) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, sec);
        return cal.getTime();
    }


}
