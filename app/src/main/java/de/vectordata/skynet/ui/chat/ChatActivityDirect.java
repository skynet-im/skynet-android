package de.vectordata.skynet.ui.chat;

import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.vanniktech.emoji.EmojiEditText;

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
import de.vectordata.skynet.data.model.enums.MessageState;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.listener.PacketListener;
import de.vectordata.skynet.net.messages.ChannelMessageConfig;
import de.vectordata.skynet.net.packet.P0CChannelMessageResponse;
import de.vectordata.skynet.net.packet.P20ChatMessage;
import de.vectordata.skynet.net.packet.base.Packet;
import de.vectordata.skynet.net.packet.model.MessageType;
import de.vectordata.skynet.ui.chat.recycler.MessageAdapter;
import de.vectordata.skynet.ui.chat.recycler.MessageItem;
import de.vectordata.skynet.ui.util.DefaultProfileImage;
import de.vectordata.skynet.ui.util.MessageSide;
import de.vectordata.skynet.util.Handlers;

public class ChatActivityDirect extends ChatActivityBase {

    private Channel directChannel;
    private Channel profileDataChannel;

    private Handler databaseHandler = Handlers.createOnThread("DatabaseThread");

    List<MessageItem> messageItems;

    private RecyclerView recyclerView;
    private MessageAdapter adapter;

    @Override
    public void initialize() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageItems = new ArrayList<>();

        long channelId = getIntent().getLongExtra(EXTRA_CHANNEL_ID, 0);
        long myAccountId = Storage.getSession().getAccountId();
        databaseHandler.post(() -> {
            directChannel = Storage.getDatabase().channelDao().getById(channelId);
            if (directChannel == null) return; // This should not happen in production

            profileDataChannel = Storage.getDatabase().channelDao().getByType(directChannel.getOther(), ChannelType.PROFILE_DATA);

            List<ChatMessage> messagesx = Storage.getDatabase().chatMessageDao().query(channelId);
            for (ChatMessage x : messagesx)
                System.out.println("MESSAGE => " + x.getMessageId() + "; " + x.getText());

            List<ChatMessage> messages = Storage.getDatabase().chatMessageDao().queryAfter(channelId, directChannel.getLatestMessage() - 50);
            for (ChatMessage message : messages) {
                ChannelMessage parent = Storage.getDatabase().channelMessageDao().getById(message.getChannelId(), message.getMessageId());
                MessageState messageState = message.getMessageState();
                MessageSide messageSide = parent.getSenderId() == myAccountId ? MessageSide.RIGHT : MessageSide.LEFT;
                messageItems.add(new MessageItem(message.getMessageId(), message.getText(), parent.getDispatchTime(), messageState, messageSide));
            }

            runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageItems.size() - 1);
            });
        });

        adapter = new MessageAdapter(messageItems);
        recyclerView.setAdapter(adapter);

        SkynetContext.getCurrent().getNetworkManager().setPacketListener(new PacketHandler());

        EmojiEditText editText = findViewById(R.id.input_message);
        findViewById(R.id.button_send).setOnClickListener(v -> {
            P20ChatMessage packet = new P20ChatMessage(MessageType.PLAINTEXT, editText.getText().toString(), 0);
            databaseHandler.post(() -> {
                SkynetContext.getCurrent().getMessageInterface().sendChannelMessage(directChannel, new ChannelMessageConfig(), packet);
                insertMessage(packet);
            });
            editText.setText("");
        });
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

    private void insertMessage(P20ChatMessage msg) {
        long myAccountId = Storage.getSession().getAccountId();
        MessageState messageState = MessageState.SENDING;
        MessageSide messageSide = msg.getParent().senderId == myAccountId ? MessageSide.RIGHT : MessageSide.LEFT;
        messageItems.add(new MessageItem(msg.getParent().messageId, msg.text, msg.getParent().dispatchTime, messageState, messageSide));
        runOnUiThread(() -> {
            adapter.notifyItemInserted(messageItems.size() - 1);
            recyclerView.scrollToPosition(messageItems.size() - 1);
        });
    }

    /**
     * Updates the current activity with
     * new messages / message changes
     */
    private class PacketHandler implements PacketListener {

        @Override
        public void onPacket(Packet packet) {
            if (packet instanceof P20ChatMessage) {
                insertMessage((P20ChatMessage) packet);
            } else if (packet instanceof P0CChannelMessageResponse) {
                P0CChannelMessageResponse r = ((P0CChannelMessageResponse) packet);
                int idx = 0;
                for (MessageItem item : messageItems) {
                    if (item.getMessageId() == r.tempMessageId) {
                        item.setMessageId(r.messageId);
                        item.setMessageState(MessageState.SENT);
                        int currentIdx = idx;
                        runOnUiThread(() -> adapter.notifyItemChanged(currentIdx));
                    }
                    idx++;
                }
            }
        }

    }

}
