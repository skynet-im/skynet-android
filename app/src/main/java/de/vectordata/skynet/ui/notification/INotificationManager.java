package de.vectordata.skynet.ui.notification;

import android.content.Context;

import de.vectordata.skynet.data.model.ChatMessage;

public interface INotificationManager {

    void onInitialize(Context context);

    void onMessageReceived(ChatMessage chatMessage);

    void onMessageDeleted(ChatMessage chatMessage);

    void onForeground();

    void onBackground();

}
