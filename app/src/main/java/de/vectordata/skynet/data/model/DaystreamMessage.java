package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.TypeConverters;
import de.vectordata.skynet.data.sql.converters.MessageTypeConverter;
import de.vectordata.skynet.net.packet.P24DaystreamMessage;
import de.vectordata.skynet.net.packet.model.MessageType;

/**
 * Created by Twometer on 18.12.2018.
 * (c) 2018 Twometer
 */
@Entity(tableName = "daystreamMessages", foreignKeys = @ForeignKey(
        entity = ChannelMessage.class,
        parentColumns = {"channelId", "messageId"},
        childColumns = {"channelId", "messageId"},
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
        primaryKeys = {"channelId", "messageId"}
)
public class DaystreamMessage {

    private long channelId;

    private long messageId;

    @TypeConverters(MessageTypeConverter.class)
    private MessageType messageType;

    private String text;

    private boolean isEdited;

    public static DaystreamMessage fromPacket(P24DaystreamMessage packet) {
        DaystreamMessage message = new DaystreamMessage();
        message.channelId = packet.getParent().channelId;
        message.messageId = packet.getParent().messageId;
        message.messageType = packet.messageType;
        message.text = packet.text;
        return message;
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

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }
}
