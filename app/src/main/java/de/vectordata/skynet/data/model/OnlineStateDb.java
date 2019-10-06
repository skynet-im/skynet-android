package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.TypeConverters;

import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.data.sql.converters.DateTimeConverter;
import de.vectordata.skynet.data.sql.converters.OnlineStateConverter;
import de.vectordata.skynet.net.packet.P2BOnlineState;
import de.vectordata.skynet.net.packet.model.OnlineState;

@Entity(tableName = "onlineStates", foreignKeys = @ForeignKey(
        entity = ChannelMessage.class,
        parentColumns = {"channelId", "messageId"},
        childColumns = {"channelId", "messageId"},
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
        primaryKeys = {"channelId", "messageId"}
)
public class OnlineStateDb {

    private long channelId;

    private long messageId;

    @TypeConverters(OnlineStateConverter.class)
    private OnlineState onlineState;

    @TypeConverters(DateTimeConverter.class)
    private DateTime lastSeen;

    public static OnlineStateDb fromPacket(P2BOnlineState packet) {
        OnlineStateDb onlineState = new OnlineStateDb();
        onlineState.channelId = packet.getParent().channelId;
        onlineState.messageId = packet.getParent().messageId;
        onlineState.onlineState = packet.onlineState;
        onlineState.lastSeen = packet.lastActive;
        return onlineState;
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public OnlineState getOnlineState() {
        return onlineState;
    }

    public void setOnlineState(OnlineState onlineState) {
        this.onlineState = onlineState;
    }

    public DateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(DateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

}