package de.vectordata.skynet.ui.notification;

import android.app.NotificationManager;
import android.content.Context;

import de.vectordata.skynet.data.model.ChatMessage;

public class NotificationManagerNew implements INotificationManager {

    private static final String CHANNEL_ID = "de.vectordata.skynet.messages";

    private boolean inForeground;

    private NotificationManager notificationManager;

    @Override
    public void onInitialize(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onMessageReceived(ChatMessage chatMessage) {
        if (inForeground) return;

    }

    @Override
    public void onMessageDeleted(ChatMessage chatMessage) {

    }

    @Override
    public void onForeground() {
        inForeground = true;
    }

    @Override
    public void onBackground() {
        inForeground = false;
    }
}
