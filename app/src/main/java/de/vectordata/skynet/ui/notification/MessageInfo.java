package de.vectordata.skynet.ui.notification;

class MessageInfo {

    private long channelId;

    private long messageId;

    private String content;

    MessageInfo(long channelId, long messageId, String content) {
        this.channelId = channelId;
        this.messageId = messageId;
        this.content = content;
    }

    public long getChannelId() {
        return channelId;
    }

    public long getMessageId() {
        return messageId;
    }

    public String getContent() {
        return content;
    }
}
