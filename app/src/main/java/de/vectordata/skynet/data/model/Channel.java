package de.vectordata.skynet.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import de.vectordata.skynet.data.model.enums.ChannelType;

@Entity(tableName = "channels")
public class Channel {

    @PrimaryKey
    private long channelId;

    private ChannelType channelType;

    private long counterpartId;

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
}
