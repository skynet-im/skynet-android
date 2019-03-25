package de.vectordata.skynet.ui.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import de.vectordata.skynet.R;
import de.vectordata.skynet.net.packet.P20ChatMessage;
import de.vectordata.skynet.ui.main.MainActivity;

public class NotificationManagerNew implements INotificationManager {

    private static final String GROUP_KEY = "de.vectordata.skynet.notification_group";
    private static final String CHANNEL_ID = "de.vectordata.skynet.notification_channel";

    private static final int SUMMARY_ID = 42;

    private Context context;

    private NotificationManager notificationManager;

    private List<MessageInfo> messages = new ArrayList<>();

    private boolean inForeground;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onInitialize(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, context.getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(context.getString(R.string.notification_channel_desc));
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onMessageReceived(P20ChatMessage chatMessage) {
        if (inForeground) return;
        messages.add(new MessageInfo(chatMessage.getParent().channelId, chatMessage.getParent().messageId, chatMessage.text));
        resendNotification();
    }

    @Override
    public void onMessageDeleted(long channelId, long messageId) {
        MessageInfo toBeDeleted = null;
        for (MessageInfo info : messages)
            if (info.getChannelId() == channelId && info.getMessageId() == messageId)
                toBeDeleted = info;
        if (toBeDeleted != null) {
            messages.remove(toBeDeleted);
            resendNotification();
        }
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

    private void resendNotification() {
        int color = ContextCompat.getColor(context, R.color.colorPrimary);

        Set<Long> channelSet = new HashSet<>();
        for (MessageInfo messageInfo : messages) channelSet.add(messageInfo.getChannelId());

        int messageCount = messages.size();
        int channelCount = channelSet.size();
        Resources resources = context.getResources();

        String subtitle = messageCount > 1 && channelCount > 1
                ? String.format(resources.getString(R.string.notification_subtitle), messageCount, channelCount)
                : resources.getQuantityString(R.plurals.new_messages, messageCount, messageCount);

        int idx = 0;
        for (long channelId : channelSet) {
            NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();

            int msgCount = 0;
            for (MessageInfo msg : messages)
                if (msg.getChannelId() == channelId) {
                    msgCount++;
                    style.addLine(msg.getContent());
                }

            String title = context.getResources().getQuantityString(R.plurals.new_messages, msgCount, msgCount);

            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.skynet_icon) // TODO Use different notification icon
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setStyle(style)
                    .setColor(color)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setGroup(GROUP_KEY)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .build();

            notificationManager.notify(idx, notification);
            idx++;
        }

        Notification summaryNotification =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle("")
                        .setContentText("")
                        .setSmallIcon(R.drawable.skynet_icon)
                        .setStyle(new NotificationCompat.InboxStyle().setSummaryText(subtitle))
                        .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0))
                        .setColor(color)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setGroup(GROUP_KEY)
                        .setGroupSummary(true)
                        .setPublicVersion(new NotificationCompat.Builder(context, CHANNEL_ID)
                                .setContentTitle(context.getString(R.string.app_name))
                                .setContentText(subtitle)
                                .setLargeIcon(null)
                                .setSmallIcon(R.drawable.skynet_icon)
                                .setColor(color)
                                .build()
                        )
                        .build();
        notificationManager.notify(SUMMARY_ID, summaryNotification);
    }

}
