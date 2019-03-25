package de.vectordata.skynet.ui.notification;

import android.content.Context;

import de.vectordata.skynet.net.packet.P20ChatMessage;

public class NotificationManagerOld implements INotificationManager {
    @Override
    public void onInitialize(Context context) {

    }

    @Override
    public void onMessageReceived(P20ChatMessage chatMessage) {

    }

    @Override
    public void onMessageDeleted(long channelId, long messageId) {

    }

    @Override
    public void onMessagesDeleted(long channelId) {

    }

    @Override
    public void onForeground() {

    }

    @Override
    public void onBackground() {

    }
}
