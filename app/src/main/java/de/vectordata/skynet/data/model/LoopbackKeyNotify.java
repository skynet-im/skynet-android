package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import de.vectordata.skynet.net.packet.P16LoopbackKeyNotify;

@Entity(tableName = "loopbackKeyNotifys", foreignKeys = @ForeignKey(
        entity = ChannelMessage.class,
        parentColumns = {"channelId", "messageId"},
        childColumns = {"channelId", "messageId"},
        onDelete = ForeignKey.CASCADE),
        primaryKeys = {"channelId", "messageId"}
)
public class LoopbackKeyNotify {

    private long channelId;

    private long messageId;

    private byte[] key;

    public static LoopbackKeyNotify fromPacket(P16LoopbackKeyNotify packet) {
        LoopbackKeyNotify notify = new LoopbackKeyNotify();
        notify.channelId = packet.getParent().channelId;
        notify.messageId = packet.getParent().messageId;
        notify.key = packet.key;
        return notify;
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

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }
}
