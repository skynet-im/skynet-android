package de.vectordata.skynet.ui.main.recycler;

import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.ui.util.MessageState;

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

    public ChatsItem(String header, String content, DateTime lastActiveDate, long profileImageId, int unreadMessages, MessageState messageState) {
        this.header = header;
        this.content = content;
        this.lastActiveDate = lastActiveDate;
        this.profileImageId = profileImageId;
        this.unreadMessages = unreadMessages;
        this.messageState = messageState;
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

}
