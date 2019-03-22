package de.vectordata.skynet.ui.notification;

import android.content.Context;

import de.vectordata.skynet.net.packet.P20ChatMessage;

public interface INotificationManager {

    void onInitialize(Context context);

    void onMessageReceived(P20ChatMessage chatMessage);

    void onMessageDeleted(long channelId, long messageId);

    void onMessagesDeleted(long channelId);

    void onForeground();

    void onBackground();

}
