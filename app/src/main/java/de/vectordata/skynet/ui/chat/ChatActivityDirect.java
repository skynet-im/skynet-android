package de.vectordata.skynet.ui.chat;

import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.vanniktech.emoji.EmojiEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import de.vectordata.skynet.net.packet.P0BChannelMessage;
import de.vectordata.skynet.net.packet.P0CChannelMessageResponse;
import de.vectordata.skynet.net.packet.P20ChatMessage;
import de.vectordata.skynet.net.packet.P22MessageReceived;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.base.Packet;
import de.vectordata.skynet.net.packet.model.MessageType;
import de.vectordata.skynet.ui.chat.recycler.MessageAdapter;
import de.vectordata.skynet.ui.chat.recycler.MessageItem;
import de.vectordata.skynet.ui.util.DefaultProfileImage;
import de.vectordata.skynet.ui.util.MessageSide;
import de.vectordata.skynet.util.Callback;
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

            List<ChatMessage> messages = Storage.getDatabase().chatMessageDao().queryLast(channelId, 50);
            Collections.reverse(messages);
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
        if (profileDataChannel == null) {
            nickname.setText(Long.toHexString(directChannel.getChannelId()));
            return;
        }
        String nicknameVal = Storage.getDatabase().nicknameDao().last(profileDataChannel.getChannelId()).getNickname();

        nickname.setText(nicknameVal);
        onlineState.setText("unknown last seen state");
        DefaultProfileImage.create(nicknameVal.substring(0, 1), profileDataChannel.getOwnerId(), 128, 128)
                .loadInto(avatar);
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

    private void modifyMessageItem(long messageId, Callback<MessageItem> modifier) {
        int idx = 0;
        for (MessageItem item : messageItems) {
            if (item.getMessageId() == messageId) {
                modifier.onCallback(item);
                int currentIdx = idx;
                runOnUiThread(() -> adapter.notifyItemChanged(currentIdx));
                break;
            }
            idx++;
        }
    }

    /**
     * Updates the current activity with
     * new messages / message changes
     */
    private class PacketHandler implements PacketListener {
        @Override
        public void onPacket(Packet packet) {
            if (packet instanceof ChannelMessagePacket && ((ChannelMessagePacket) packet).getParent().channelId != directChannel.getChannelId())
                return;

            if (packet instanceof P20ChatMessage) {
                insertMessage((P20ChatMessage) packet);
            } else if (packet instanceof P0CChannelMessageResponse) {
                P0CChannelMessageResponse response = ((P0CChannelMessageResponse) packet);
                if (response.channelId != directChannel.getChannelId()) return;
                modifyMessageItem(response.tempMessageId, i -> {
                    i.setMessageId(response.messageId);
                    i.setMessageState(MessageState.SENT);
                });
            } else if (packet instanceof P22MessageReceived) {
                P0BChannelMessage.Dependency dependency = ((P22MessageReceived) packet).getParent().singleDependency();
                modifyMessageItem(dependency.messageId, i -> i.setMessageState(MessageState.DELIVERED));
            }
        }
    }

}
