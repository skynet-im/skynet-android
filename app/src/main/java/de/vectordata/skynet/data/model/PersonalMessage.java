package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import de.vectordata.skynet.net.packet.P26PersonalMessage;

@Entity(tableName = "personalMessages", foreignKeys = @ForeignKey(
        entity = ChannelMessage.class,
        parentColumns = {"channelId", "messageId"},
        childColumns = {"channelId", "messageId"},
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
        primaryKeys = {"channelId", "messageId"}
)
public class PersonalMessage {

    private long channelId;

    private long messageId;

    private String personalMessage;

    public static PersonalMessage fromPacket(P26PersonalMessage packet) {
        PersonalMessage personalMessage = new PersonalMessage();
        personalMessage.channelId = packet.getParent().channelId;
        personalMessage.messageId = packet.getParent().messageId;
        personalMessage.personalMessage = packet.personalMessage;
        return personalMessage;
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

    public String getPersonalMessage() {
        return personalMessage;
    }

    public void setPersonalMessage(String personalMessage) {
        this.personalMessage = personalMessage;
    }
}
