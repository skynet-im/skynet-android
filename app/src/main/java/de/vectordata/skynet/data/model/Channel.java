package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import de.vectordata.skynet.auth.Session;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.data.sql.converters.ChannelTypeConverter;
import de.vectordata.skynet.net.packet.P0ACreateChannel;

@Entity(tableName = "channels", indices = {@Index(value = {"channelId"}, unique = true)})
public class Channel {

    @PrimaryKey(autoGenerate = true)
    private long internalId;

    private long channelId;

    @TypeConverters(ChannelTypeConverter.class)
    private ChannelType channelType;

    private long ownerId;

    private long counterpartId;

    private long latestMessage;

    public static Channel fromPacket(P0ACreateChannel packet) {
        Channel channel = new Channel();
        channel.channelId = packet.channelId;
        channel.channelType = packet.channelType;
        channel.ownerId = packet.ownerId;
        channel.counterpartId = packet.counterpartId;
        return channel;
    }

    public long getInternalId() {
        return internalId;
    }

    public void setInternalId(long internalId) {
        this.internalId = internalId;
    }

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

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
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

    public long getOther() {
        if (channelType != ChannelType.DIRECT)
            throw new IllegalStateException("getOther() can only be called on a direct channel");
        Session session = Storage.getSession();
        return session.getAccountId() == ownerId ? counterpartId : ownerId;
    }
}
