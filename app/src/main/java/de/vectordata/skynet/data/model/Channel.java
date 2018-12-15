package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.data.sql.converters.ChannelTypeConverter;

@Entity(tableName = "channels")
public class Channel {

    @PrimaryKey
    private long channelId;

    @TypeConverters(ChannelTypeConverter.class)
    private ChannelType channelType;

    private long counterpartId;

    private long latestMessage;

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public ChannelType getChannelType() {
        return channelType;
    }

    public void setChannelType(ChannelType channelType) {
        this.channelType = channelType;
    }

    public long getCounterpartId() {
        return counterpartId;
    }

    public void setCounterpartId(long counterpartId) {
        this.counterpartId = counterpartId;
    }

    public long getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(long latestMessage) {
        this.latestMessage = latestMessage;
    }
}
