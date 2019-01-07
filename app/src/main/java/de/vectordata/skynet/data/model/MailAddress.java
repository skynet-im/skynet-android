package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import de.vectordata.skynet.net.packet.P14MailAddress;

@Entity(tableName = "mailAddresses", foreignKeys = @ForeignKey(
        entity = ChannelMessage.class,
        parentColumns = {"channelId", "messageId"},
        childColumns = {"channelId", "messageId"},
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
        primaryKeys = {"channelId", "messageId"}
)
public class MailAddress {

    private long channelId;

    private long messageId;

    private String mailAddress;

    public static MailAddress fromPacket(P14MailAddress packet) {
        MailAddress address = new MailAddress();
        address.channelId = packet.getParent().channelId;
        address.messageId = packet.getParent().messageId;
        address.setMailAddress(packet.mailAddress);
        return address;
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

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }
}
