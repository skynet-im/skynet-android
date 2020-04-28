package de.vectordata.skynet;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChannelMessage;
import de.vectordata.skynet.data.model.ChatMessage;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.data.model.enums.MessageState;
import de.vectordata.skynet.data.sql.db.SkynetDatabase;
import de.vectordata.skynet.net.packet.model.MessageType;
import de.vectordata.skynet.util.date.DateTime;

@RunWith(AndroidJUnit4.class)
public class DatabaseTests {

    @Test
    public void testForeignKeys() {
        Context appContext = ApplicationProvider.getApplicationContext();
        Storage.initialize(appContext);
        SkynetDatabase database = Storage.getDatabase();

        Channel channel = new Channel();
        channel.setChannelId(123);
        channel.setChannelType(ChannelType.DIRECT);
        database.channelDao().insert(channel);
        System.out.println("Created channel " + channel.getChannelId());

        ChannelMessage channelMessage = new ChannelMessage();
        channelMessage.setChannelId(channel.getChannelId());
        channelMessage.setMessageId(-123456);
        channelMessage.setDispatchTime(DateTime.now());
        database.channelMessageDao().insert(channelMessage);
        System.out.println("Created channel message " + channelMessage.getChannelId() + ":" + channelMessage.getMessageId());

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChannelId(channelMessage.getChannelId());
        chatMessage.setMessageId(channelMessage.getMessageId());
        chatMessage.setMessageType(MessageType.PLAINTEXT);
        chatMessage.setMessageState(MessageState.SENT);
        chatMessage.setText("Test message");
        database.chatMessageDao().insert(chatMessage);
        System.out.println("Created chat message " + chatMessage.getChannelId() + ":" + chatMessage.getMessageId() + ":" + chatMessage.getText());

        ChannelMessage newChannelMessage = database.channelMessageDao().getById(123, -123456);
        newChannelMessage.setMessageId(5);
        database.channelMessageDao().update(newChannelMessage);
        System.out.println("Updated channel message id -123456 to 5");

        System.out.println("Dumping database...");

        for (ChannelMessage msg : database.channelMessageDao().query(123)) {
            System.out.println("CHANNEL MESSAGE: " + msg.getMessageId());
        }

        for (ChatMessage msg : database.chatMessageDao().query(123)) {
            System.out.println("CHAT MESSAGE: " + msg.getMessageId() + ": " + msg.getText());
        }

    }

}
