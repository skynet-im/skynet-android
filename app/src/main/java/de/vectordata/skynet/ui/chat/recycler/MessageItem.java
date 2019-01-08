package de.vectordata.skynet.ui.chat.recycler;

import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.ui.util.MessageSide;
import de.vectordata.skynet.ui.util.MessageState;

public class MessageItem {

    private String content;

    private DateTime sentDate;

    private MessageState messageState;

    private MessageSide messageSide;

    public MessageItem(String content, DateTime sentDate, MessageState messageState, MessageSide messageSide) {
        this.content = content;
        this.sentDate = sentDate;
        this.messageState = messageState;
        this.messageSide = messageSide;
    }

    public static MessageItem newSystemMessage(String content) {
        return new MessageItem(content, null, MessageState.SYSTEM, MessageSide.CENTER);
    }

    String getContent() {
        return content;
    }

    DateTime getSentDate() {
        return sentDate;
    }

    MessageState getMessageState() {
        return messageState;
    }

    MessageSide getMessageSide() {
        return messageSide;
    }
}
