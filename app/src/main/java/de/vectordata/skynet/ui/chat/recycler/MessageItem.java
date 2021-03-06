package de.vectordata.skynet.ui.chat.recycler;

import de.vectordata.skynet.data.model.enums.MessageState;
import de.vectordata.skynet.ui.util.MessageSide;
import de.vectordata.skynet.util.date.DateTime;

public class MessageItem {

    private long messageId;

    private String content;

    private DateTime sentDate;

    private MessageState messageState;

    private MessageSide messageSide;

    private QuotedMessage quotedMessage;

    private boolean isEdited;

    private boolean isCorrupted;

    public MessageItem(long messageId, String content, DateTime sentDate, MessageState messageState, MessageSide messageSide, QuotedMessage quotedMessage, boolean isEdited, boolean isCorrupted) {
        this.messageId = messageId;
        this.content = content;
        this.sentDate = sentDate;
        this.messageState = messageState;
        this.messageSide = messageSide;
        this.quotedMessage = quotedMessage;
        this.isEdited = isEdited;
        this.isCorrupted = isCorrupted;
    }

    public static MessageItem newSystemMessage(String content) {
        return new MessageItem(0, content, null, MessageState.SYSTEM, MessageSide.CENTER, null, false, false);
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    boolean hasQuote() {
        return quotedMessage != null;
    }

    QuotedMessage getQuotedMessage() {
        return quotedMessage;
    }

    boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        this.isEdited = true;
    }

    public boolean isCorrupted() {
        return isCorrupted;
    }
}
