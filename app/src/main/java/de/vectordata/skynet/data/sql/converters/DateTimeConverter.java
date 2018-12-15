package de.vectordata.skynet.data.sql.converters;

import androidx.room.TypeConverter;
import de.vectordata.libjvsl.util.cscompat.DateTime;

public class DateTimeConverter {

    @TypeConverter
    public static DateTime toDateTime(long binary) {
        return DateTime.fromBinary(binary);
    }

    @TypeConverter
    public static long toBinary(DateTime dateTime) {
        return dateTime.toBinary();
    }

}
