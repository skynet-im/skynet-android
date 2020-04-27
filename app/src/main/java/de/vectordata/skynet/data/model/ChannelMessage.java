package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import de.vectordata.skynet.data.sql.converters.DateTimeConverter;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.util.date.DateTime;

@Entity(tableName = "channelMessages", foreignKeys = @ForeignKey(
        entity = Channel.class,
        parentColumns = "channelId",
        childColumns = "channelId",
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
        indices = {@Index(value = {"channelId", "messageId"}, unique = true)})
public class ChannelMessage {

    @PrimaryKey(autoGenerate = true)
    private long internalId;

    private long channelId;

    private long messageId;

    private long senderId;

    @TypeConverters(DateTimeConverter.class)
    private DateTime dispatchTime;

    private byte messageFlags;

    private long fileId;

    private boolean corrupted;

    public static ChannelMessage fromPacket(ChannelMessagePacket packet) {
        ChannelMessage message = new ChannelMessage();
        message.channelId = packet.channelId;
        message.messageId = packet.messageId;
        message.senderId = packet.senderId;
        message.dispatchTime = packet.dispatchTime;
        message.messageFlags = packet.messageFlags;
        message.fileId = packet.fileId;
        message.corrupted = packet.isCorrupted;
        return message;
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

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public DateTime getDispatchTime() {
        return dispatchTime;
    }

    public void setDispatchTime(DateTime dispatchTime) {
        this.dispatchTime = dispatchTime;
    }

    public byte getMessageFlags() {
        return messageFlags;
    }

    public void setMessageFlags(byte messageFlags) {
        this.messageFlags = messageFlags;
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public boolean isCorrupted() {
        return corrupted;
    }

    public void setCorrupted(boolean corrupted) {
        this.corrupted = corrupted;
    }
}
