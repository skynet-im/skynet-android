package de.vectordata.skynet.data.sql.converters;

import androidx.room.TypeConverter;
import de.vectordata.skynet.net.model.MessageType;

public class MessageTypeConverter {

    @TypeConverter
    public static MessageType toMessageType(int messageType) {
        return MessageType.values()[messageType];
    }

    @TypeConverter
    public static int toInteger(MessageType messageType) {
        return messageType.ordinal();
    }

}
