package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "messageDrafts", foreignKeys = @ForeignKey(
        entity = Channel.class,
        parentColumns = "channelId",
        childColumns = "channelId",
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
        indices = {@Index(value = {"channelId"}, unique = true)})
public class MessageDraft {

    @PrimaryKey
    private long channelId;

    private String text;

    public MessageDraft(long channelId, String text) {
        this.channelId = channelId;
        this.text = text;
    }

    public MessageDraft() {
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}


