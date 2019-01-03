package de.vectordata.skynet.data.sql.converters;

import androidx.room.TypeConverter;
import de.vectordata.skynet.net.model.KeyRole;

public class KeyRoleConverter {

    @TypeConverter
    public static KeyRole toKeyRole(int role) {
        return KeyRole.values()[role];
    }

    @TypeConverter
    public static int toInteger(KeyRole role) {
        return role.ordinal();
    }

}
