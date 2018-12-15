package de.vectordata.skynet.data.model;

import androidx.room.Entity;

@Entity(tableName = "dependencies", primaryKeys = {"srcChannelId", "srcMessageId"})
public class Dependency {

    private long srcChannelId;

    private long srcMessageId;

    private long dstAccountId;

    private long dstChannelId;

    private long dstMessageId;

    public long getSrcChannelId() {
        return srcChannelId;
    }

    public void setSrcChannelId(long srcChannelId) {
        this.srcChannelId = srcChannelId;
    }

    public long getSrcMessageId() {
        return srcMessageId;
    }

    public void setSrcMessageId(long srcMessageId) {
        this.srcMessageId = srcMessageId;
    }

    public long getDstAccountId() {
        return dstAccountId;
    }

    public void setDstAccountId(long dstAccountId) {
        this.dstAccountId = dstAccountId;
    }

    public long getDstChannelId() {
        return dstChannelId;
    }

    public void setDstChannelId(long dstChannelId) {
        this.dstChannelId = dstChannelId;
    }

    public long getDstMessageId() {
        return dstMessageId;
    }

    public void setDstMessageId(long dstMessageId) {
        this.dstMessageId = dstMessageId;
    }
}
