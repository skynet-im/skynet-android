package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;

import de.vectordata.skynet.net.packet.P15PasswordUpdate;

@Entity(tableName = "passwordUpdates", foreignKeys = @ForeignKey(
        entity = ChannelMessage.class,
        parentColumns = {"channelId", "messageId"},
        childColumns = {"channelId", "messageId"},
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
        primaryKeys = {"channelId", "messageId"}
)
public class PasswordUpdate {

    private long channelId;

    private long messageId;

    private byte[] previousKeyHash;

    private byte[] keyHash;

    private byte[] previousKey;

    public static PasswordUpdate fromPacket(P15PasswordUpdate packet) {
        PasswordUpdate update = new PasswordUpdate();
        update.channelId = packet.channelId;
        update.messageId = packet.messageId;
        update.previousKeyHash = packet.previousKeyHash;
        update.keyHash = packet.keyHash;
        update.previousKey = packet.previousKey;
        return update;
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

    public byte[] getPreviousKeyHash() {
        return previousKeyHash;
    }

    public void setPreviousKeyHash(byte[] previousKeyHash) {
        this.previousKeyHash = previousKeyHash;
    }

    public byte[] getKeyHash() {
        return keyHash;
    }

    public void setKeyHash(byte[] keyHash) {
        this.keyHash = keyHash;
    }

    public byte[] getPreviousKey() {
        return previousKey;
    }

    public void setPreviousKey(byte[] previousKey) {
        this.previousKey = previousKey;
    }
}
