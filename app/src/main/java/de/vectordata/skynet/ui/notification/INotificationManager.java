package de.vectordata.skynet.ui.notification;

import android.content.Context;

public interface INotificationManager {

    void onInitialize(Context context);

    void onMessageReceived(long channelId, long messageId, String content);

    void onMessageDeleted(long channelId, long messageId);

    void onForeground(long channelId);

    void onBackground();

}
