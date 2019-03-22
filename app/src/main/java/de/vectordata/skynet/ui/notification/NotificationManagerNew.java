package de.vectordata.skynet.ui.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import de.vectordata.skynet.R;
import de.vectordata.skynet.net.packet.P20ChatMessage;

public class NotificationManagerNew implements INotificationManager {

    private static final String CHANNEL_ID = "de.vectordata.skynet.messages";

    private Context context;
    private NotificationManager notificationManager;

    private boolean inForeground;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onInitialize(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, context.getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(context.getString(R.string.notification_channel_desc));
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onMessageReceived(P20ChatMessage chatMessage) {
        if (inForeground) return;
        // TODO Use Inbox Style notificaton with message groups
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentText(chatMessage.text)
                .build();
        notificationManager.notify(51, notification);
    }

    @Override
    public void onMessageDeleted(long channelId, long messageId) {

    }

    @Override
    public void onMessagesDeleted(long channelId) {

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
