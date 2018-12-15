package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.TypeConverters;
import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.data.sql.converters.DateTimeConverter;

@Entity(tableName = "channelMessages", primaryKeys = {"channelId", "messageId"})
public class ChannelMessage {

    private long channelId;

    private long messageId;

    private long senderId;

    @TypeConverters(DateTimeConverter.class)
    private DateTime dispatchTime;

    private byte messageFlags;

    private long fileId;

    private byte[] fileKey;

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

    public byte[] getFileKey() {
        return fileKey;
    }

    public void setFileKey(byte[] fileKey) {
        this.fileKey = fileKey;
    }
}
