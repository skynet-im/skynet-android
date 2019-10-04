package de.vectordata.skynet.data.sql.converters;

import androidx.room.TypeConverter;

import de.vectordata.skynet.net.packet.model.OnlineState;

public class OnlineStateConverter {

    @TypeConverter
    public static OnlineState toMessageState(int state) {
        return OnlineState.values()[state];
    }

    @TypeConverter
    public static int toInteger(OnlineState state) {
        return state.ordinal();
    }

}
