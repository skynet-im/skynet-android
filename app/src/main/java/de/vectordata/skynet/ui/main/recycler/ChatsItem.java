package de.vectordata.skynet.ui.main.recycler;

import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.data.model.enums.MessageState;
import de.vectordata.skynet.ui.util.MessageSide;

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

    private long otherId;

    public ChatsItem(String header, String content, DateTime lastActiveDate, long profileImageId, int unreadMessages, long channelId) {
        this.header = header;
        this.content = content;
        this.lastActiveDate = lastActiveDate;
        this.profileImageId = profileImageId;
        this.unreadMessages = unreadMessages;
        this.messageState = MessageState.NONE;
        this.messageSide = MessageSide.LEFT;
        this.channelId = channelId;
    }

    public ChatsItem(String header, String content, DateTime lastActiveDate, long profileImageId, MessageSide messageSide, MessageState messageState, int unreadMessages, long channelId, long otherId) {
        this.header = header;
        this.content = content;
        this.lastActiveDate = lastActiveDate;
        this.profileImageId = profileImageId;
        this.unreadMessages = unreadMessages;
        this.messageState = messageState;
        this.messageSide = messageSide;
        this.channelId = channelId;
        this.otherId = otherId;
    }

    String getHeader() {
        return header;
    }

    String getContent() {
        return content;
    }

    DateTime getLastActiveDate() {
        return lastActiveDate;
    }

    long getProfileImageId() {
        return profileImageId;
    }

    int getUnreadMessages() {
        return unreadMessages;
    }

    MessageState getMessageState() {
        return messageState;
    }

    MessageSide getMessageSide() {
        return messageSide;
    }

    public long getChannelId() {
        return channelId;
    }

    long getOtherId() {
        return otherId;
    }
}
