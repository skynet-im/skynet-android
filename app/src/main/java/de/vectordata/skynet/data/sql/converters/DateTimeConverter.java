package de.vectordata.skynet.data.sql.converters;

import androidx.room.TypeConverter;

import de.vectordata.skynet.util.date.DateTime;

public class DateTimeConverter {

    @TypeConverter
    public static DateTime toDateTime(long binary) {
        if (binary == 0)
            return null;
        return DateTime.fromBinary(binary);
    }

    @TypeConverter
    public static long toBinary(DateTime dateTime) {
        if (dateTime == null)
            return 0;
        return dateTime.toBinary();
    }

}
