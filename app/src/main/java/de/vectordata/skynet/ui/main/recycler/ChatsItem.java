package de.vectordata.skynet.ui.main.recycler;

import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChatMessage;
import de.vectordata.skynet.data.model.enums.MessageState;
import de.vectordata.skynet.ui.util.MessageSide;
import de.vectordata.skynet.util.date.DateTime;

/**
 * Created by Twometer on 14.12.2018.
 * (c) 2018 Twometer
 */
public class ChatsItem {

    private String header;

    private String content;

    private DateTime lastActiveDate;

    private long profileImageId;

    private int unreadMessages;

    private MessageState messageState;

    private MessageSide messageSide;

    private long channelId;

    private long counterpartId;

    private Type type;

    public ChatsItem(String header, DateTime lastActiveDate, Channel channel) {
        this.header = header;
        this.lastActiveDate = lastActiveDate;
        this.channelId = channel.getChannelId();
        this.counterpartId = channel.getCounterpartId();
        this.type = Type.NORMAL;
    }

    public ChatsItem(String header, String content, DateTime lastActiveDate, long profileImageId, int unreadMessages, long channelId, long counterpartId) {
        this.header = header;
        this.content = content;
        this.lastActiveDate = lastActiveDate;
        this.profileImageId = profileImageId;
        this.unreadMessages = unreadMessages;
        this.messageState = MessageState.NONE;
        this.messageSide = MessageSide.LEFT;
        this.channelId = channelId;
        this.counterpartId = counterpartId;
        this.type = Type.NORMAL;
    }

    String getHeader() {
        return header;
    }

    String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public DateTime getLastActiveDate() {
        return lastActiveDate;
    }

    long getProfileImageId() {
        return profileImageId;
    }

    int getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    MessageState getMessageState() {
        return messageState;
    }

    public void setMessageState(MessageState messageState) {
        this.messageState = messageState;
    }

    MessageSide getMessageSide() {
        return messageSide;
    }

    public void setMessageSide(MessageSide messageSide) {
        this.messageSide = messageSide;
    }

    public long getChannelId() {
        return channelId;
    }

    public long getCounterpartId() {
        return counterpartId;
    }

    public Type getType() {
        if (content.equals(ChatMessage.DELETED))
            return Type.DELETED;
        else return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        NORMAL,
        DELETED,
        HIGHLIGHTED,
        DRAFT
    }

}
