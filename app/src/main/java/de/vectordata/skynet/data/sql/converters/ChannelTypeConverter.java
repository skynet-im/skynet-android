package de.vectordata.skynet.data.sql.converters;

import androidx.room.TypeConverter;

import de.vectordata.skynet.data.model.enums.ChannelType;

public class ChannelTypeConverter {

    @TypeConverter
    public static ChannelType toChannelType(int channelType) {
        return ChannelType.values()[channelType];
    }

    @TypeConverter
    public static int toInteger(ChannelType channelType) {
        return channelType.ordinal();
    }

}
