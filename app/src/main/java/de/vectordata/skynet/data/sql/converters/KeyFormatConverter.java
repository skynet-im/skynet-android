package de.vectordata.skynet.data.sql.converters;

import androidx.room.TypeConverter;
import de.vectordata.skynet.net.packet.model.KeyFormat;

public class KeyFormatConverter {

    @TypeConverter
    public static KeyFormat toKeyFormat(int format) {
        return KeyFormat.values()[format];
    }

    @TypeConverter
    public static int toInteger(KeyFormat format) {
        return format.ordinal();
    }

}
