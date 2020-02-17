package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.TypeConverters;

import de.vectordata.skynet.data.sql.converters.DateTimeConverter;
import de.vectordata.skynet.data.sql.converters.OnlineStateConverter;
import de.vectordata.skynet.net.packet.P2BOnlineState;
import de.vectordata.skynet.net.packet.model.OnlineState;
import de.vectordata.skynet.util.date.DateTime;

@Entity(tableName = "onlineStates", foreignKeys = @ForeignKey(
        entity = ChannelMessage.class,
        parentColumns = {"channelId", "messageId"},
        childColumns = {"channelId", "messageId"},
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
        primaryKeys = {"channelId"},
        indices = {@Index(value = {"channelId", "messageId"}, unique = true)}
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
        onlineState.channelId = packet.channelId;
        onlineState.messageId = packet.messageId;
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
