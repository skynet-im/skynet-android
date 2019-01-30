package de.vectordata.skynet.ui.chat;

import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.R;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChannelMessage;
import de.vectordata.skynet.data.model.ChatMessage;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.listener.PacketListener;
import de.vectordata.skynet.net.packet.P20ChatMessage;
import de.vectordata.skynet.net.packet.base.Packet;
import de.vectordata.skynet.ui.chat.recycler.MessageAdapter;
import de.vectordata.skynet.ui.chat.recycler.MessageItem;
import de.vectordata.skynet.ui.util.DefaultProfileImage;
import de.vectordata.skynet.ui.util.MessageSide;
import de.vectordata.skynet.ui.util.MessageState;
import de.vectordata.skynet.util.Handlers;

public class ChatActivityDirect extends ChatActivityBase {

    private Channel directChannel;
    private Channel profileDataChannel;

    private Handler databaseHandler = Handlers.createOnThread("DatabaseThread");

    List<MessageItem> messageItems;
    private MessageAdapter adapter;

    @Override
    public void initialize() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        long channelId = getIntent().getLongExtra(EXTRA_CHANNEL_ID, 0);
        long myAccountId = Storage.getSession().getAccountId();
        databaseHandler.post(() -> {
            directChannel = Storage.getDatabase().channelDao().getById(channelId);
            if (directChannel == null) return; // This should not happen in production

            profileDataChannel = Storage.getDatabase().channelDao().getByType(directChannel.getOther(), ChannelType.PROFILE_DATA);

            List<ChatMessage> messages = Storage.getDatabase().chatMessageDao().queryLast(channelId, 50);
            for (ChatMessage message : messages) {
                ChannelMessage parent = Storage.getDatabase().channelMessageDao().getById(message.getChannelId(), message.getMessageId());
                MessageState messageState = /* TODO */ MessageState.SENDING;
                MessageSide messageSide = parent.getSenderId() == myAccountId ? MessageSide.RIGHT : MessageSide.LEFT;
                messageItems.add(new MessageItem(message.getText(), parent.getDispatchTime(), messageState, messageSide));
            }

            runOnUiThread(adapter::notifyDataSetChanged);
        });

        messageItems = new ArrayList<>();
        adapter = new MessageAdapter(messageItems);

        // This is test data for UI demonstration purposes
        messageItems.add(MessageItem.newSystemMessage("YESTERDAY"));
        messageItems.add(new MessageItem("Hi", ago(16, 55, 0), MessageState.SEEN, MessageSide.RIGHT));
        messageItems.add(MessageItem.newSystemMessage("TODAY"));
        messageItems.add(new MessageItem("Eyy moin", ago(16, 45, 0), MessageState.SEEN, MessageSide.LEFT));
        messageItems.add(new MessageItem("Lass uns mal ne Runde zocken, ich will meine neue Grafikkarte ausprobieren \uD83D\uDE02", ago(16, 45, 0), MessageState.SEEN, MessageSide.RIGHT));
        messageItems.add(new MessageItem("Ne mann ich geh jetzt ins Bett, morgen dann", ago(16, 40, 0), MessageState.SEEN, MessageSide.LEFT));
        messageItems.add(new MessageItem("Mhh okay, ich kann aber morgen erst nachmittags.", ago(16, 39, 0), MessageState.SEEN, MessageSide.RIGHT));
        messageItems.add(new MessageItem("Jetzt könnte ich", ago(0, 59, 0), MessageState.SEEN, MessageSide.RIGHT));
        messageItems.add(new MessageItem("Okay komm online, jetzt bin ich am PC", ago(0, 39, 0), MessageState.SEEN, MessageSide.LEFT));
        messageItems.add(new MessageItem("Sehr gut", ago(0, 35, 0), MessageState.SENT, MessageSide.RIGHT));

        recyclerView.setAdapter(adapter);

        SkynetContext.getCurrent().getNetworkManager().setPacketListener(new PacketHandler());
    }

    @Override
    public void configureActionBar(ImageView avatar, TextView nickname, TextView onlineState) {
        if (profileDataChannel == null)
            return;
        String nicknameVal = Storage.getDatabase().nicknameDao().last(profileDataChannel.getChannelId()).getNickname();

        nickname.setText(nicknameVal);
        onlineState.setText("unknown last seen state");
        DefaultProfileImage.create(nicknameVal.substring(0, 1), profileDataChannel.getOwnerId(), 128, 128)
                .loadInto(avatar);
    }

    private DateTime ago(int hr, int min, int sec) {
        return DateTime.fromMillis(System.currentTimeMillis() - sec * 1000 - min * 60000 - hr * 3600000);
    }

    /**
     * Updates the current activity with
     * new messages / message changes
     */
    private class PacketHandler implements PacketListener {

        @Override
        public void onPacket(Packet packet) {
            long myAccountId = Storage.getSession().getAccountId();

            if (packet instanceof P20ChatMessage) {
                P20ChatMessage chatMessage = (P20ChatMessage) packet;
                MessageState messageState = /* TODO */ MessageState.SENDING;
                MessageSide messageSide = chatMessage.getParent().senderId == myAccountId ? MessageSide.RIGHT : MessageSide.LEFT;
                messageItems.add(new MessageItem(chatMessage.text, chatMessage.getParent().dispatchTime, messageState, messageSide));
                runOnUiThread(() -> adapter.notifyItemInserted(messageItems.size() - 1));
            }

        }

    }

}
