package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;

import de.vectordata.skynet.util.date.DateTime;

@Entity(tableName = "fileInfos", foreignKeys = @ForeignKey(
        entity = ChannelMessage.class,
        parentColumns = {"channelId", "messageId"},
        childColumns = {"channelId", "messageId"},
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
        primaryKeys = {"channelId", "messageId"}
)
public class FileInfo {

    private long channelId;

    private long messageId;

    private String name;

    private DateTime creationTime;

    private DateTime lastWriteTime;

    private String thumbnailContentType;

    private byte[] thumbnail;

    private String contentType;

    private long length;

    private byte[] key;

    public FileInfo(long channelId, long messageId, String name, DateTime creationTime, DateTime lastWriteTime, String thumbnailContentType, byte[] thumbnail) {
        this.channelId = channelId;
        this.messageId = messageId;
        this.name = name;
        this.creationTime = creationTime;
        this.lastWriteTime = lastWriteTime;
        this.thumbnailContentType = thumbnailContentType;
        this.thumbnail = thumbnail;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(DateTime creationTime) {
        this.creationTime = creationTime;
    }

    public DateTime getLastWriteTime() {
        return lastWriteTime;
    }

    public void setLastWriteTime(DateTime lastWriteTime) {
        this.lastWriteTime = lastWriteTime;
    }

    public String getThumbnailContentType() {
        return thumbnailContentType;
    }

    public void setThumbnailContentType(String thumbnailContentType) {
        this.thumbnailContentType = thumbnailContentType;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }
}
