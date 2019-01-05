package de.vectordata.skynet.data.sql.converters;

import androidx.room.TypeConverter;
import de.vectordata.skynet.data.model.enums.KeyType;

public class KeyTypeConverter {

    @TypeConverter
    public static KeyType toKeyType(int format) {
        return KeyType.values()[format];
    }

    @TypeConverter
    public static int toInteger(KeyType keyType) {
        return keyType.ordinal();
    }

}
