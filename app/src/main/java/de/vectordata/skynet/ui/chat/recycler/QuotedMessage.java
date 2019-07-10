package de.vectordata.skynet.ui.chat.recycler;

import android.content.Context;

import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChannelMessage;
import de.vectordata.skynet.data.model.ChatMessage;
import de.vectordata.skynet.ui.util.NameUtil;

public class QuotedMessage {

    private String name;

    private String message;

    public QuotedMessage(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public static QuotedMessage load(Context context, long messageId, Channel directChannel, Channel accountDataChannel) {
        ChatMessage quoted = Storage.getDatabase().chatMessageDao().query(directChannel.getChannelId(), messageId);
        ChannelMessage parent = Storage.getDatabase().channelMessageDao().getById(quoted.getChannelId(), quoted.getMessageId());
        return new QuotedMessage(NameUtil.getFriendlySenderName(context, parent.getSenderId(), accountDataChannel), quoted.getText());
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

}
