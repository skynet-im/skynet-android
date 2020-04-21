package de.vectordata.skynet;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChannelMessage;
import de.vectordata.skynet.data.model.ChatMessage;
import de.vectordata.skynet.data.model.Dependency;
import de.vectordata.skynet.data.sql.db.SkynetDatabase;
import de.vectordata.skynet.util.date.DateUtil;

@RunWith(AndroidJUnit4.class)
public class DatabaseDump {

    @Test
    public void test() {
        Context appContext = ApplicationProvider.getApplicationContext();
        Storage.initialize(appContext);
        SkynetDatabase database = Storage.getDatabase();

        List<Channel> channels = database.channelDao().getAll();
        for (Channel channel : channels) {

            System.out.printf("\n\n=== %d (%s) ===%n", channel.getChannelId(), channel.getChannelType());
            List<ChannelMessage> messages = database.channelMessageDao().query(channel.getChannelId());
            for (ChannelMessage message : messages) {
                ChatMessage chatMessage = database.chatMessageDao().query(message.getChannelId(), message.getMessageId());
                String messageText = "";
                if (chatMessage != null) {
                    if (chatMessage.getText().length() < 50)
                        messageText = chatMessage.getText();
                    else
                        messageText = chatMessage.getText().substring(0, 48) + "…";
                }
                System.out.printf("%d\t%d\t%s\t%s%n", message.getMessageId(), message.getSenderId(),
                        DateUtil.toDateString(appContext, message.getDispatchTime()), messageText);
                List<Dependency> dependencies = database.dependencyDao().getDependencies(message.getChannelId(), message.getMessageId());
                for (Dependency dependency : dependencies) {
                    System.out.printf("  %d -> %d %d%n", dependency.getSrcMessageId(), dependency.getDstMessageId(), dependency.getDstAccountId());
                }
            }

        }
    }

}
