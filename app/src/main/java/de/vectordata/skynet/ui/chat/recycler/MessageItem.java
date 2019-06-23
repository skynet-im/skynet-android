package de.vectordata.skynet.ui.chat.recycler;

import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.data.model.enums.MessageState;
import de.vectordata.skynet.ui.util.MessageSide;

public class MessageItem {

    private long messageId;

    private String content;

    private DateTime sentDate;

    private MessageState messageState;

    private MessageSide messageSide;

    public MessageItem(long messageId, String content, DateTime sentDate, MessageState messageState, MessageSide messageSide) {
        this.messageId = messageId;
        this.content = content;
        this.sentDate = sentDate;
        this.messageState = messageState;
        this.messageSide = messageSide;
    }

    public static MessageItem newSystemMessage(String content) {
        return new MessageItem(0, content, null, MessageState.SYSTEM, MessageSide.CENTER);
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    String getContent() {
        return content;
    }

    public DateTime getSentDate() {
        return sentDate;
    }

    public MessageState getMessageState() {
        return messageState;
    }

    public void setMessageState(MessageState messageState) {
        this.messageState = messageState;
    }

    public MessageSide getMessageSide() {
        return messageSide;
    }
}
