package de.vectordata.skynet.ui.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.util.LongSparseArray;

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
import de.vectordata.skynet.ui.util.NameUtil;
import de.vectordata.skynet.util.Handlers;

public class NotificationManagerNew implements INotificationManager {

    private static final String GROUP_KEY = "de.vectordata.skynet.notification_group";
    private static final String CHANNEL_ID = "de.vectordata.skynet.notification_channel";

    private static final int SUMMARY_ID = 42;

    private Context context;
    private Handler handler;
    private NotificationManager notificationManager;

    private List<MessageInfo> messages = new ArrayList<>();
    private LongSparseArray<Integer> notificationIdMap = new LongSparseArray<>();

    private long foregroundChannelId;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onInitialize(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.handler = Handlers.createOnThread("NotificationDatabaseThread");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, context.getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(context.getString(R.string.notification_channel_desc));
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onMessageReceived(P20ChatMessage chatMessage) {
        if (chatMessage.getParent().channelId == foregroundChannelId) return;
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
    public void onForeground(long channelId) {
        foregroundChannelId = channelId;
        List<MessageInfo> toBeDeleted = new ArrayList<>();
        for (MessageInfo info : messages)
            if (info.getChannelId() == channelId)
                toBeDeleted.add(info);
        messages.removeAll(toBeDeleted);
        if (notificationIdMap.indexOfKey(channelId) >= 0) {
            notificationManager.cancel(notificationIdMap.get(channelId));
            notificationIdMap.remove(channelId);
        }
        if (messages.size() == 0)
            notificationManager.cancel(SUMMARY_ID);
    }

    @Override
    public void onBackground() {
        foregroundChannelId = 0;
    }

    private void resendNotification() {
        handler.post(this::resendNotificationImpl);
    }

    private void resendNotificationImpl() {
        int color = ContextCompat.getColor(context, R.color.colorPrimary);

        Set<Long> channelSet = new HashSet<>();
        for (MessageInfo messageInfo : messages) channelSet.add(messageInfo.getChannelId());

        int messageCount = messages.size();
        int channelCount = channelSet.size();
        Resources resources = context.getResources();

        String subtitle = messageCount > 1 && channelCount > 1
                ? String.format(resources.getString(R.string.notification_subtitle), messageCount, channelCount)
                : resources.getQuantityString(R.plurals.new_messages, messageCount, messageCount);

        notificationIdMap.clear();

        int idx = 0;
        for (long channelId : channelSet) {
            NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();

            int msgCount = 0;
            String lastMessage = "";
            for (MessageInfo msg : messages)
                if (msg.getChannelId() == channelId) {
                    msgCount++;
                    style.addLine(msg.getContent());
                    lastMessage = msg.getContent();
                }

            String title = String.format("%s (%s)", NameUtil.getFriendlyName(channelId), context.getResources().getQuantityString(R.plurals.new_messages, msgCount, msgCount));

            Intent intent = new Intent(context, MainActivity.class);
            intent.setAction(MainActivity.ACTION_OPEN_CHAT);
            intent.putExtra(MainActivity.EXTRA_CHANNEL_ID, channelId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.skynet_icon) // TODO Use different notification icon
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setStyle(style)
                    .setColor(color)
                    .setContentTitle(title)
                    .setContentText(lastMessage)
                    .setAutoCancel(true)
                    .setGroup(GROUP_KEY)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setContentIntent(pendingIntent)
                    .build();

            notificationIdMap.put(channelId, idx);
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
                        .setAutoCancel(true)
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
