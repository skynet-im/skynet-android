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

    private byte[] keyHash;

    public static PasswordUpdate fromPacket(P15PasswordUpdate packet) {
        PasswordUpdate update = new PasswordUpdate();
        update.channelId = packet.channelId;
        update.messageId = packet.messageId;
        update.keyHash = packet.keyHash;
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

    public byte[] getKeyHash() {
        return keyHash;
    }

    public void setKeyHash(byte[] keyHash) {
        this.keyHash = keyHash;
    }
}
