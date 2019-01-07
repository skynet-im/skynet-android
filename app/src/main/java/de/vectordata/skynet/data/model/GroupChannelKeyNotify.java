package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import de.vectordata.skynet.net.packet.P1DGroupChannelKeyNotify;

@Entity(tableName = "groupChannelKeyNotifys", foreignKeys = @ForeignKey(
        entity = ChannelMessage.class,
        parentColumns = {"channelId", "messageId"},
        childColumns = {"channelId", "messageId"},
        onDelete = ForeignKey.CASCADE),
        primaryKeys = {"channelId", "messageId"}
)
public class GroupChannelKeyNotify {

    private long channelId;

    private long messageId;

    private long groupChannelId;

    private byte[] newKey;

    private byte[] historyKey;

    public static GroupChannelKeyNotify fromPacket(P1DGroupChannelKeyNotify packet) {
        GroupChannelKeyNotify notify = new GroupChannelKeyNotify();
        notify.channelId = packet.getParent().channelId;
        notify.messageId = packet.getParent().messageId;
        notify.groupChannelId = packet.channelId;
        notify.newKey = packet.newKey;
        notify.historyKey = packet.historyKey;
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

    public long getGroupChannelId() {
        return groupChannelId;
    }

    public void setGroupChannelId(long groupChannelId) {
        this.groupChannelId = groupChannelId;
    }

    public byte[] getNewKey() {
        return newKey;
    }

    public void setNewKey(byte[] newKey) {
        this.newKey = newKey;
    }

    public byte[] getHistoryKey() {
        return historyKey;
    }

    public void setHistoryKey(byte[] historyKey) {
        this.historyKey = historyKey;
    }
}
