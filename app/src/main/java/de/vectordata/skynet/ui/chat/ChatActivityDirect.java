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
import de.vectordata.skynet.net.packet.P0BChannelMessage;
import de.vectordata.skynet.net.packet.P0CChannelMessageResponse;
import de.vectordata.skynet.net.packet.P20ChatMessage;
import de.vectordata.skynet.net.packet.P22MessageReceived;
import de.vectordata.skynet.net.packet.P23MessageRead;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.base.Packet;
import de.vectordata.skynet.net.packet.model.MessageType;
import de.vectordata.skynet.ui.chat.recycler.MessageAdapter;
import de.vectordata.skynet.ui.chat.recycler.MessageItem;
import de.vectordata.skynet.ui.util.DateUtil;
import de.vectordata.skynet.ui.util.DefaultProfileImage;
import de.vectordata.skynet.ui.util.MessageSide;
import de.vectordata.skynet.util.Callback;
import de.vectordata.skynet.util.Handlers;

public class ChatActivityDirect extends ChatActivityBase {

    private Channel directChannel;
    private Channel profileDataChannel;

    private Handler backgroundHandler = Handlers.createOnThread("BackgroundThread");

    private List<MessageItem> messageItems;

    private RecyclerView recyclerView;
    private MessageAdapter adapter;

    @Override
    public void initialize() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageItems = new ArrayList<>();

        long channelId = getIntent().getLongExtra(EXTRA_CHANNEL_ID, 0);
        long myAccountId = Storage.getSession().getAccountId();
        backgroundHandler.post(() -> {
            directChannel = Storage.getDatabase().channelDao().getById(channelId);
            if (directChannel == null) return; // This should not happen in production

            profileDataChannel = Storage.getDatabase().channelDao().getByType(directChannel.getCounterpartId(), ChannelType.PROFILE_DATA);

            List<ChatMessage> messages = Storage.getDatabase().chatMessageDao().queryLast(channelId, 50);
            Collections.reverse(messages);

            ChannelMessage previous = null;
            for (ChatMessage message : messages) {
                ChannelMessage parent = Storage.getDatabase().channelMessageDao().getById(message.getChannelId(), message.getMessageId());
                MessageSide messageSide = parent.getSenderId() == myAccountId ? MessageSide.RIGHT : MessageSide.LEFT;
                DateTime dispatchTime = parent.getDispatchTime();
                if (previous == null || !previous.getDispatchTime().isSameDay(dispatchTime))
                    messageItems.add(MessageItem.newSystemMessage(DateUtil.toDateString(this, dispatchTime)));
                messageItems.add(new MessageItem(message.getMessageId(), message.getText(), dispatchTime, message.getMessageState(), messageSide));
                previous = parent;
            }

            runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                scrollToBottom();
            });
        });

        adapter = new MessageAdapter(messageItems);
        recyclerView.setAdapter(adapter);

        SkynetContext.getCurrent().getNetworkManager().setPacketListener(new PacketHandler());

        EmojiEditText editText = findViewById(R.id.input_message);
        findViewById(R.id.button_send).setOnClickListener(v -> {
            String text;
            if (editText.getText() == null || (text = editText.getText().toString()).trim().isEmpty())
                return;

            P20ChatMessage packet = new P20ChatMessage(MessageType.PLAINTEXT, text, 0);
            backgroundHandler.post(() -> {
                SkynetContext.getCurrent().getMessageInterface().sendChannelMessage(directChannel, new ChannelMessageConfig(), packet);
                insertMessage(packet);
            });
            editText.setText("");
        });

        backgroundHandler.post(() -> {
            List<ChatMessage> unread = Storage.getDatabase().chatMessageDao().queryUnread(directChannel.getChannelId());
            for (ChatMessage message : unread)
                readMessage(message.getMessageId());
        });
    }

    @Override
    public void configureActionBar(ImageView avatar, TextView nickname, TextView onlineState) {
        if (profileDataChannel == null) {
            // TODO: Remove this once the profile data channels are implemented
            nickname.setText(Long.toHexString(directChannel.getCounterpartId()));
            onlineState.setText("unknown last seen state");
            DefaultProfileImage.create(nickname.getText().toString().substring(0, 1), directChannel.getCounterpartId(), 128, 128)
                    .loadInto(avatar);
            return;
        }
        String nicknameVal = Storage.getDatabase().nicknameDao().last(profileDataChannel.getChannelId()).getNickname();

        nickname.setText(nicknameVal);
        onlineState.setText("unknown last seen state");
        DefaultProfileImage.create(nicknameVal.substring(0, 1), profileDataChannel.getOwnerId(), 128, 128)
                .loadInto(avatar);
    }

    private void insertMessage(P20ChatMessage msg) {
        MessageItem oldLatest = messageItems.size() > 0 ? messageItems.get(messageItems.size() - 1) : null;

        long myAccountId = Storage.getSession().getAccountId();
        MessageState messageState = MessageState.SENDING;
        MessageSide messageSide = msg.getParent().senderId == myAccountId ? MessageSide.RIGHT : MessageSide.LEFT;
        MessageItem newLatest = new MessageItem(msg.getParent().messageId, msg.text, msg.getParent().dispatchTime, messageState, messageSide);
        runOnUiThread(() -> {
            if (oldLatest == null || !oldLatest.getSentDate().isSameDay(newLatest.getSentDate())) {
                messageItems.add(MessageItem.newSystemMessage(DateUtil.toDateString(this, newLatest.getSentDate())));
                adapter.notifyItemInserted(messageItems.size() - 1);
            }
            messageItems.add(newLatest);
            adapter.notifyItemInserted(messageItems.size() - 1);
            scrollToBottom();
        });
    }

    private void modifyMessageItem(long messageId, Callback<MessageItem> modifier) {
        for (int i = 0; i < messageItems.size(); i++) {
            MessageItem item = messageItems.get(i);
            if (item.getMessageId() == messageId) {
                modifier.onCallback(item);
                int idx = i;
                runOnUiThread(() -> adapter.notifyItemChanged(idx));
                break;
            }
        }
    }

    private void scrollToBottom() {
        recyclerView.scrollToPosition(messageItems.size() - 1);
    }

    private void readMessage(long messageId) {
        SkynetContext.getCurrent().getMessageInterface()
                .sendChannelMessage(directChannel.getChannelId(),
                        new ChannelMessageConfig().addDependency(Storage.getSession().getAccountId(), directChannel.getChannelId(), messageId),
                        new P23MessageRead()
                );
        backgroundHandler.post(() -> {
            ChatMessage message = Storage.getDatabase().chatMessageDao().query(directChannel.getChannelId(), messageId);
            message.setUnread(false);
            Storage.getDatabase().chatMessageDao().update(message);
        });
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
                P20ChatMessage chatMessage = (P20ChatMessage) packet;
                insertMessage(chatMessage);
                readMessage(chatMessage.getParent().messageId);
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
            } else if (packet instanceof P23MessageRead) {
                P0BChannelMessage.Dependency dependency = ((P23MessageRead) packet).getParent().singleDependency();
                modifyMessageItem(dependency.messageId, i -> i.setMessageState(MessageState.SEEN));
            }
        }
    }
}