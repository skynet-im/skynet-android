package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.TypeConverters;
import de.vectordata.skynet.data.sql.converters.MessageTypeConverter;
import de.vectordata.skynet.net.model.MessageType;
import de.vectordata.skynet.net.packet.P20ChatMessage;

@Entity(tableName = "chatMessages", foreignKeys = @ForeignKey(
        entity = ChannelMessage.class,
        parentColumns = {"channelId", "messageId"},
        childColumns = {"channelId", "messageId"},
        onDelete = ForeignKey.CASCADE),
        primaryKeys = {"channelId", "messageId"}
)
public class ChatMessage {

    private long channelId;

    private long messageId;

    @TypeConverters(MessageTypeConverter.class)
    private MessageType messageType;

    private String text;

    private long quotedMessage;

    private boolean isEdited;

    public static ChatMessage fromPacket(P20ChatMessage packet) {
        ChatMessage message = new ChatMessage();
        message.channelId = packet.getParent().channelId;
        message.messageId = packet.getParent().messageId;
        message.messageType = packet.messageType;
        message.text = packet.text;
        message.quotedMessage = packet.quotedMessage;
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

    public long getQuotedMessage() {
        return quotedMessage;
    }

    public void setQuotedMessage(long quotedMessage) {
        this.quotedMessage = quotedMessage;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }
}
