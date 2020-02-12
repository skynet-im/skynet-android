package de.vectordata.skynet.util.date;

import android.content.Context;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Twometer on 07.03.2018.
 * (c) 2018 Twometer
 */

@SuppressWarnings("WeakerAccess")
public class DateTime {

    // This is a java port of the .NET implementation of DateTime, based on
    // https://referencesource.microsoft.com/#mscorlib/system/datetime.cs

    private static final long TicksPerMillisecond = 10000;
    private static final long TicksPerSecond = TicksPerMillisecond * 1000;
    private static final long TicksPerMinute = TicksPerSecond * 60;
    private static final long TicksPerHour = TicksPerMinute * 60;
    private static final long TicksPerDay = TicksPerHour * 24;
    private static final long TicksCeiling = 0x4000000000000000L;
    private static final long LocalMask = 0x8000000000000000L;

    private static final int DaysPerYear = 365;
    private static final int DaysPer4Years = ((DaysPerYear * 4) + 1);
    private static final int DaysPer100Years = ((DaysPer4Years * 25) - 1);
    private static final int DaysPer400Years = ((DaysPer100Years * 4) + 1);

    private static final int[] DaysToMonth365 = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365};
    private static final int[] DaysToMonth366 = {0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366};
    private static final long TicksMask = 0x3FFFFFFFFFFFFFFFL;

    private static final int DatePartYear = 0;
    private static final int DatePartDayOfYear = 1;
    private static final int DatePartMonth = 2;
    private static final int DatePartDay = 3;

    private final long dateData;

    public DateTime(int year, int month, int day, int hour, int minute, int second) {
        this.dateData = dateToTicks(year, month, day) + timeToTicks(hour, minute, second);
    }

    private DateTime(long ticks) {
        this.dateData = ticks;
    }

    public static DateTime fromBinary(long bin) {
        if ((bin & LocalMask) != 0) {
            long ticks = bin & TicksMask;

            if (ticks > TicksCeiling - TicksPerDay) {
                ticks = ticks - TicksCeiling;
            }

            long millisecondsOffset = TimeZone.getDefault().getRawOffset();

            long offsetTicks = TicksPerMillisecond * millisecondsOffset;

            ticks += offsetTicks;

            // Is it daylight savings time?
            if (TimeZone.getDefault().inDaylightTime(new Date())) {
                ticks += TicksPerHour;
            }

            if (ticks < 0)
                ticks += TicksPerDay;
            return new DateTime(ticks);
        } else {
            long ticks = bin & TicksMask;
            return new DateTime(ticks);
        }

    }

    public static DateTime now() {
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        return new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }

    public static DateTime fromMillis(long millis) {
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        calendar.setTime(new Date(millis));
        return new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }

    private long timeToTicks(int hour, int minute, int second) {
        if (hour >= 0 && hour < 24 && minute >= 0 && minute < 60 && second >= 0 && second < 60) {
            long totalSeconds = (long) hour * 3600 + (long) minute * 60 + (long) second;
            return totalSeconds * TicksPerSecond;
        }
        throw new IllegalArgumentException("Bad hour, minute or second.");
    }

    private long dateToTicks(int year, int month, int day) {
        if (year >= 1 && year <= 9999 && month >= 1 && month <= 12) {
            int[] days = isLeapYear(year) ? DaysToMonth366 : DaysToMonth365;
            if (day >= 1 && day <= days[month] - days[month - 1]) {
                int y = year - 1;
                int n = y * 365 + y / 4 - y / 100 + y / 400 + days[month - 1] + day - 1;
                return n * TicksPerDay;
            }
        }
        throw new IllegalArgumentException("Bad year, month or day.");
    }

    private boolean isLeapYear(int year) {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
    }

    private int GetDatePart(int part) {
        long ticks = getTicks();
        // n = number of days since 1/1/0001
        int n = (int) (ticks / TicksPerDay);
        // y400 = number of whole 400-year periods since 1/1/0001
        int y400 = n / DaysPer400Years;
        // n = day number within 400-year period
        n -= y400 * DaysPer400Years;
        // y100 = number of whole 100-year periods within 400-year period
        int y100 = n / DaysPer100Years;
        // Last 100-year period has an extra day, so decrement result if 4
        if (y100 == 4) y100 = 3;
        // n = day number within 100-year period
        n -= y100 * DaysPer100Years;
        // y4 = number of whole 4-year periods within 100-year period
        int y4 = n / DaysPer4Years;
        // n = day number within 4-year period
        n -= y4 * DaysPer4Years;
        // y1 = number of whole years within 4-year period
        int y1 = n / DaysPerYear;
        // Last year has an extra day, so decrement result if 4
        if (y1 == 4) y1 = 3;
        // If year was requested, compute and return it
        if (part == DatePartYear) {
            return y400 * 400 + y100 * 100 + y4 * 4 + y1 + 1;
        }
        // n = day number within year
        n -= y1 * DaysPerYear;
        // If day-of-year was requested, return it
        if (part == DatePartDayOfYear) return n + 1;
        // Leap year calculation looks different from IsLeapYear since y1, y4,
        // and y100 are relative to year 1, not year 0
        boolean leapYear = y1 == 3 && (y4 != 24 || y100 == 3);
        int[] days = leapYear ? DaysToMonth366 : DaysToMonth365;
        // All months have less than 32 days, so n >> 5 is a good conservative
        // estimate for the month
        int m = n >> 5 + 1;
        // m = 1-based month number
        while (n >= days[m]) m++;
        // If month was requested, return it
        if (part == DatePartMonth) return m;
        // Return 1-based day-of-month
        return n - days[m - 1] + 1;
    }

    public long toBinary() {
        long millisecondsOffset = TimeZone.getDefault().getRawOffset();

        long offset = TicksPerMillisecond * millisecondsOffset;

        long ticks = getTicks();

        // Normalize to UTC - no daylight savings time
        if (TimeZone.getDefault().inDaylightTime(new Date())) {
            ticks -= TicksPerHour;
        }

        long storedTicks = ticks - offset;
        if (storedTicks < 0) {
            storedTicks = TicksCeiling + storedTicks;
        }
        return storedTicks | LocalMask;
    }

    public int getHour() {
        return (int) ((getTicks() / TicksPerHour) % 24);
    }

    public int getMinute() {
        return (int) ((getTicks() / TicksPerMinute) % 60);
    }

    public int getSecond() {
        return (int) ((getTicks() / TicksPerSecond) % 60);
    }

    public int getDay() {
        return GetDatePart(DatePartDay);
    }

    public int getMonth() {
        return GetDatePart(DatePartMonth);
    }

    public int getYear() {
        return GetDatePart(DatePartYear);
    }

    public long getTicks() {
        return dateData & TicksMask;
    }

    public DateTime previousDay() {
        return new DateTime(getTicks() - TicksPerDay);
    }

    public boolean isYesterday() {
        return isSameDay(DateTime.now().previousDay());
    }

    public boolean isToday() {
        return isSameDay(DateTime.now());
    }

    public boolean isSameDay(DateTime other) {
        return this.getDay() == other.getDay() && this.getMonth() == other.getMonth() && this.getYear() == other.getYear();
    }

    public Date toJavaDate() {
        return DateUtil.createDate(getYear(), getMonth(), getDay(), getHour(), getMinute(), getSecond());
    }

    public String toDateTimeString(Context context) {
        Date date = DateUtil.createDate(getYear(), getMonth(), getDay());
        return DateFormat.getDateFormat(context).format(date) + " " + DateFormat.getTimeFormat(context).format(date);
    }

    public String toDateString(Context context) {
        Date date = DateUtil.createDate(getYear(), getMonth(), getDay());
        return DateFormat.getDateFormat(context).format(date);
    }

    public String toTimeString(Context context) {
        Date date = DateUtil.createDate(getYear(), getMonth(), getDay(), getHour(), getMinute(), getSecond());
        return DateFormat.getTimeFormat(context).format(date);
    }
}
