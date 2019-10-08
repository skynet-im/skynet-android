package de.vectordata.skynet.data.sql.converters;

import androidx.room.TypeConverter;

import de.vectordata.skynet.data.model.enums.MessageState;

public class MessageStateConverter {

    @TypeConverter
    public static MessageState toMessageState(int state) {
        return MessageState.values()[state];
    }

    @TypeConverter
    public static int toInteger(MessageState state) {
        return state.ordinal();
    }

}
