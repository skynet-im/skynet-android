package de.vectordata.skynet.ui.chat;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.R;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChannelMessage;
import de.vectordata.skynet.data.model.ChatMessage;
import de.vectordata.skynet.data.model.OnlineStateDb;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.data.model.enums.MessageState;
import de.vectordata.skynet.event.AuthenticationSuccessfulEvent;
import de.vectordata.skynet.event.ConnectionFailedEvent;
import de.vectordata.skynet.event.PacketEvent;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.messages.ChannelMessageConfig;
import de.vectordata.skynet.net.model.ConnectionState;
import de.vectordata.skynet.net.packet.P0BChannelMessage;
import de.vectordata.skynet.net.packet.P0CChannelMessageResponse;
import de.vectordata.skynet.net.packet.P20ChatMessage;
import de.vectordata.skynet.net.packet.P21MessageOverride;
import de.vectordata.skynet.net.packet.P22MessageReceived;
import de.vectordata.skynet.net.packet.P23MessageRead;
import de.vectordata.skynet.net.packet.P2BOnlineState;
import de.vectordata.skynet.net.packet.P2CChannelAction;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.base.Packet;
import de.vectordata.skynet.net.packet.model.ChannelAction;
import de.vectordata.skynet.net.packet.model.MessageType;
import de.vectordata.skynet.net.packet.model.OnlineState;
import de.vectordata.skynet.net.packet.model.OverrideAction;
import de.vectordata.skynet.ui.ForwardActivity;
import de.vectordata.skynet.ui.chat.action.MessageAction;
import de.vectordata.skynet.ui.chat.action.MessageActionController;
import de.vectordata.skynet.ui.chat.recycler.MessageAdapter;
import de.vectordata.skynet.ui.chat.recycler.MessageItem;
import de.vectordata.skynet.ui.chat.recycler.MultiChoiceListener;
import de.vectordata.skynet.ui.chat.recycler.QuotedMessage;
import de.vectordata.skynet.ui.dialogs.Dialogs;
import de.vectordata.skynet.ui.util.DateUtil;
import de.vectordata.skynet.ui.util.DefaultProfileImage;
import de.vectordata.skynet.ui.util.KeyboardUtil;
import de.vectordata.skynet.ui.util.MessageSide;
import de.vectordata.skynet.ui.util.NameUtil;
import de.vectordata.skynet.util.Callback;

public class ChatActivityDirect extends ChatActivityBase implements MultiChoiceListener {

    private Channel profileDataChannel;
    private Channel accountDataChannel;

    private List<MessageItem> messageItems;
    private MessageAdapter adapter;

    private MessageActionController messageActionController;

    private boolean isLoading;
    private boolean isFullyLoaded;

    @Override
    public void initialize() {
        messageActionController = new MessageActionController(this);
        findViewById(R.id.button_exit_message_action).setOnClickListener(v -> messageActionController.exit());

        messageItems = new ArrayList<>();
        adapter = new MessageAdapter(messageItems);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setActionModeCallback(this);

        findViewById(R.id.button_send).setOnClickListener(v -> {
            String text;
            if (messageInput.getText() == null || (text = messageInput.getText().toString()).trim().isEmpty())
                return;

            backgroundHandler.post(() -> {
                if (!messageActionController.isOpen()) {
                    P20ChatMessage packet = new P20ChatMessage(MessageType.PLAINTEXT, text, messageActionController.getAffectedMessage());
                    getSkynetContext().getMessageInterface().schedule(messageChannel.getChannelId(), ChannelMessageConfig.create(), packet);
                    insertMessage(packet, MessageState.SENDING);
                } else if (messageActionController.getAction() == MessageAction.QUOTE) {
                    P20ChatMessage packet = new P20ChatMessage(MessageType.PLAINTEXT, text, messageActionController.getAffectedMessage());
                    ChannelMessageConfig config = createConfigWithDependencyTo(messageActionController.getAffectedMessage());
                    getSkynetContext().getMessageInterface().schedule(messageChannel.getChannelId(), config, packet);
                    insertMessage(packet, MessageState.SENDING);
                } else if (messageActionController.getAction() == MessageAction.EDIT) {
                    P21MessageOverride packet = new P21MessageOverride(messageActionController.getAffectedMessage(), OverrideAction.EDIT, text);
                    ChannelMessageConfig config = createConfigWithDependencyTo(messageActionController.getAffectedMessage());
                    getSkynetContext().getMessageInterface().schedule(messageChannel.getChannelId(), config, packet);
                    modifyMessageItem(messageActionController.getAffectedMessage(), data -> {
                        data.setContent(text);
                        data.setEdited(true);
                    });
                }
                runOnUiThread(() -> messageActionController.exit());
                clearDraft();
            });

            messageInput.setText("");
            onStopTyping();
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView v, int dx, int dy) {
                if (dx == 0 && dy == 0) return;
                if (recyclerView.getFirstVisiblePosition() <= 20 && adapter.getItemCount() > 0 && !isFullyLoaded) {
                    MessageItem item = adapter.getItem(0);
                    if (item.getMessageSide() == MessageSide.CENTER)
                        loadMoreMessages(adapter.getItem(1).getMessageId());
                    else loadMoreMessages(item.getMessageId());
                }
            }
        });

        KeyboardUtil.registerOnKeyboardOpen(recyclerView, this::scrollToBottom);
    }

    @Override
    public void loadData() {
        messageChannel = Storage.getDatabase().channelDao().getById(messageChannelId);
        if (messageChannel == null) return; // This should not happen in production

        profileDataChannel = Storage.getDatabase().channelDao().getByType(messageChannel.getCounterpartId(), ChannelType.PROFILE_DATA);
        accountDataChannel = Storage.getDatabase().channelDao().getByType(messageChannel.getCounterpartId(), ChannelType.ACCOUNT_DATA);

        SkynetContext.getCurrent().getNotificationManager().onForeground(messageChannel.getChannelId());

        String friendlyName = NameUtil.getFriendlyName(messageChannel.getCounterpartId(), accountDataChannel);
        DefaultProfileImage profileImage = DefaultProfileImage.create(friendlyName.substring(0, 1), accountDataChannel.getOwnerId(), 128, 128);
        runOnUiThread(() -> {
            titleView.setText(friendlyName);
            profileImage.loadInto(avatarView);
        });
    }

    @Override
    public void reload() {
        loadMessages();

        List<ChatMessage> unread = Storage.getDatabase().chatMessageDao().queryUnread(messageChannel.getChannelId());
        for (ChatMessage message : unread)
            readMessage(message.getMessageId());

        OnlineStateDb onlineState = Storage.getDatabase().onlineStateDao().get(accountDataChannel.getChannelId());
        ChannelAction channelAction = SkynetContext.getCurrent().getAppState().getChannelAction(messageChannelId);

        if (onlineState == null)
            subtitleView.setVisibility(View.GONE);
        else if (channelAction != ChannelAction.NONE)
            applyChannelAction(channelAction);
        else if (onlineState.getOnlineState() == OnlineState.ACTIVE)
            setSubtitle(R.string.state_online);
        else if (onlineState.getOnlineState() == OnlineState.INACTIVE)
            setSubtitle(DateUtil.toLastSeen(this, onlineState.getLastSeen()));

        if (SkynetContext.getCurrent().getNetworkManager().getConnectionState() != ConnectionState.AUTHENTICATED)
            subtitleView.setVisibility(View.GONE);
    }

    @Subscribe
    public void onConnectionLost(ConnectionFailedEvent event) {
        runOnUiThread(() -> subtitleView.setVisibility(View.GONE));
    }

    @Subscribe
    public void onConnected(AuthenticationSuccessfulEvent event) {
        runOnUiThread(() -> subtitleView.setVisibility(View.VISIBLE));
    }

    @Subscribe
    public void onPacketEvent(PacketEvent event) {
        Packet packetIn = event.getPacket();
        if (packetIn instanceof ChannelMessagePacket) {
            long channelId = ((ChannelMessagePacket) packetIn).getParent().channelId;
            if (channelId != messageChannel.getChannelId() && channelId != accountDataChannel.getChannelId() && channelId != profileDataChannel.getChannelId())
                return;
        }

        if (packetIn instanceof P20ChatMessage) {
            P20ChatMessage chatMessage = (P20ChatMessage) packetIn;
            insertMessage(chatMessage, MessageState.SENT);
            if (chatMessage.getParent().senderId != Storage.getSession().getAccountId())
                readMessage(chatMessage.getParent().messageId);
        } else if (packetIn instanceof P0CChannelMessageResponse) {
            P0CChannelMessageResponse response = ((P0CChannelMessageResponse) packetIn);
            if (response.channelId != messageChannel.getChannelId()) return;
            modifyMessageItem(response.tempMessageId, i -> {
                i.setMessageId(response.messageId);
                i.setMessageState(MessageState.SENT);
            });
        } else if (packetIn instanceof P22MessageReceived) {
            P0BChannelMessage.Dependency dependency = ((P22MessageReceived) packetIn).getParent().singleDependency();
            modifyMessageItem(dependency.messageId, i -> {
                if (i.getMessageState() != MessageState.SEEN)
                    i.setMessageState(MessageState.DELIVERED);
            });
        } else if (packetIn instanceof P23MessageRead) {
            P0BChannelMessage.Dependency dependency = ((P23MessageRead) packetIn).getParent().singleDependency();
            modifyMessageItem(dependency.messageId, i -> i.setMessageState(MessageState.SEEN));
        } else if (packetIn instanceof P21MessageOverride) {
            P21MessageOverride override = (P21MessageOverride) packetIn;
            modifyMessageItem(override.messageId, i -> {
                if (override.action == OverrideAction.EDIT) {
                    i.setEdited(true);
                    i.setContent(override.newText);
                } else i.setContent(ChatMessage.DELETED);
            });
        } else if (packetIn instanceof P2BOnlineState) {
            P2BOnlineState packet = (P2BOnlineState) packetIn;
            if (packet.getParent().channelId != this.accountDataChannel.getChannelId()) return;

            switch (packet.onlineState) {
                case ACTIVE:
                    setSubtitle(R.string.state_online);
                    break;
                case INACTIVE:
                    setSubtitle(DateUtil.toLastSeen(this, packet.lastActive));
                    break;
            }

        } else if (packetIn instanceof P2CChannelAction) {
            P2CChannelAction packet = (P2CChannelAction) packetIn;
            applyChannelAction(packet.channelAction);
        }
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, boolean checked) {
        if (position < adapter.getItemCount() - 1 && adapter.getItem(position).getMessageSide() == MessageSide.CENTER && checked) {
            recyclerView.setItemChecked(position, false, false);
            recyclerView.toggleItem(position + 1);
        }
        if (mode != null) {
            int checkedItems = recyclerView.getCheckedItemCount();
            mode.setTitle(String.valueOf(checkedItems));
            if (checkedItems > 1) {
                mode.getMenu().findItem(R.id.action_quote).setVisible(false);
                mode.getMenu().findItem(R.id.action_edit).setVisible(false);
                mode.getMenu().findItem(R.id.action_delete).setVisible(false);
                mode.getMenu().findItem(R.id.action_info).setVisible(false);
            } else {
                mode.getMenu().findItem(R.id.action_quote).setVisible(true);
                mode.getMenu().findItem(R.id.action_info).setVisible(true);

                MessageItem selectedMessage = getSelectedMessage();
                if (selectedMessage != null) {
                    boolean mayOverwrite = mayOverwrite(selectedMessage);

                    mode.getMenu().findItem(R.id.action_delete).setVisible(mayOverwrite);
                    mode.getMenu().findItem(R.id.action_edit).setVisible(mayOverwrite);
                }
            }
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.context_menu_chat, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        int id = item.getItemId();
        // TODO implement actions
        MessageItem selectedMessage = getSelectedMessage();
        switch (id) {
            case R.id.action_quote:
                messageActionController.begin(MessageAction.QUOTE, selectedMessage.getMessageId());
                messageActionController.setHeader(getFriendlySenderName(selectedMessage));
                messageActionController.setContent(selectedMessage.getContent());
                mode.finish();
                break;
            case R.id.action_edit:
                messageActionController.begin(MessageAction.EDIT, getSelectedMessage().getMessageId());
                messageActionController.setHeader(getFriendlySenderName(selectedMessage));
                messageActionController.setContent(selectedMessage.getContent());
                messageInput.setText(selectedMessage.getContent());
                mode.finish();
                break;
            case R.id.action_delete:
                Dialogs.showYesNoBox(this, R.string.question_header_delete, R.string.question_delete, (dialog, which) -> {
                    backgroundHandler.post(() -> {
                        P21MessageOverride packet = new P21MessageOverride(selectedMessage.getMessageId(), OverrideAction.DELETE);
                        ChannelMessageConfig config = createConfigWithDependencyTo(selectedMessage.getMessageId());
                        getSkynetContext().getMessageInterface().schedule(messageChannel.getChannelId(), config, packet);
                    });
                    modifyMessageItem(selectedMessage.getMessageId(), data -> data.setContent(ChatMessage.DELETED));
                    mode.finish();
                }, null);
                break;
            case R.id.action_info:
                break;
            case R.id.action_forward:
                Intent intent = new Intent(this, ForwardActivity.class);
                intent.putExtra(ForwardActivity.EXTRA_SRC_CHANNEL, messageChannel.getChannelId());
                intent.putExtra(ForwardActivity.EXTRA_SRC_MESSAGE, selectedMessage.getMessageId());
                startActivity(intent);
                mode.finish();
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

    @Override
    public void onBackPressed() {
        if (messageActionController.isOpen())
            messageActionController.exit();
        else super.onBackPressed();
    }

    private void applyChannelAction(ChannelAction channelAction) {
        switch (channelAction) {
            case NONE:
                setSubtitle(R.string.state_online);
                break;
            case TYPING:
                setSubtitle(R.string.state_typing);
                break;
            case RECORDING_AUDIO:
                setSubtitle(R.string.state_recording);
                break;
        }
    }

    private void insertMessage(P20ChatMessage msg, MessageState messageState) {
        MessageItem oldLatest = messageItems.size() > 0 ? messageItems.get(messageItems.size() - 1) : null;

        long myAccountId = Storage.getSession().getAccountId();
        MessageSide messageSide = msg.getParent().senderId == myAccountId ? MessageSide.RIGHT : MessageSide.LEFT;
        QuotedMessage quotedMessage = null;
        if (msg.quotedMessage != 0)
            quotedMessage = QuotedMessage.load(this, msg.quotedMessage, messageChannel, accountDataChannel);
        MessageItem newLatest = new MessageItem(msg.getParent().messageId, msg.text, msg.getParent().dispatchTime, messageState, messageSide, quotedMessage, false);
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

    private void loadMessages() {
        isLoading = true;
        long myAccountId = Storage.getSession().getAccountId();

        List<ChatMessage> messages = Storage.getDatabase().chatMessageDao().queryLast(messageChannel.getChannelId(), 20);
        Collections.reverse(messages);

        messageItems.clear();

        ChannelMessage previous = null;
        for (ChatMessage message : messages) {
            ChannelMessage parent = Storage.getDatabase().channelMessageDao().getById(message.getChannelId(), message.getMessageId());
            MessageSide messageSide = parent.getSenderId() == myAccountId ? MessageSide.RIGHT : MessageSide.LEFT;
            DateTime dispatchTime = parent.getDispatchTime();

            if (previous == null || !previous.getDispatchTime().isSameDay(dispatchTime))
                messageItems.add(MessageItem.newSystemMessage(DateUtil.toDateString(this, dispatchTime)));

            QuotedMessage quotedMessage = null;
            if (message.getQuotedMessage() != 0)
                quotedMessage = QuotedMessage.load(this, message.getQuotedMessage(), messageChannel, accountDataChannel);
            messageItems.add(new MessageItem(message.getMessageId(), message.getText(), dispatchTime, message.getMessageState(), messageSide, quotedMessage, message.isEdited()));
            previous = parent;
        }

        runOnUiThread(() -> {
            adapter.notifyDataSetChanged();
            scrollToBottom();
            isLoading = false;
        });
    }

    private void loadMoreMessages(long firstMessage) {
        if (isLoading || isFullyLoaded) return;
        isLoading = true;
        backgroundHandler.post(() -> {
            long myAccountId = Storage.getSession().getAccountId();

            List<ChatMessage> messages = Storage.getDatabase().chatMessageDao().queryLast(messageChannel.getChannelId(), firstMessage, 20);
            if (messages.size() == 0) {
                isFullyLoaded = true;
                return;
            }
            Collections.reverse(messages);

            int idx = 0;

            ChannelMessage previous = null;
            for (ChatMessage message : messages) {
                ChannelMessage parent = Storage.getDatabase().channelMessageDao().getById(message.getChannelId(), message.getMessageId());
                MessageSide messageSide = parent.getSenderId() == myAccountId ? MessageSide.RIGHT : MessageSide.LEFT;
                DateTime dispatchTime = parent.getDispatchTime();

                if (previous == null || !previous.getDispatchTime().isSameDay(dispatchTime)) {
                    messageItems.add(idx, MessageItem.newSystemMessage(DateUtil.toDateString(this, dispatchTime)));
                    idx++;
                }

                QuotedMessage quotedMessage = null;
                if (message.getQuotedMessage() != 0)
                    quotedMessage = QuotedMessage.load(this, message.getQuotedMessage(), messageChannel, accountDataChannel);
                messageItems.add(idx, new MessageItem(message.getMessageId(), message.getText(), dispatchTime, message.getMessageState(), messageSide, quotedMessage, message.isEdited()));
                idx++;
                previous = parent;
            }

            final int lastIdx = idx;
            runOnUiThread(() -> {
                adapter.notifyItemRangeInserted(0, lastIdx);
                MessageSide side = adapter.getItem(lastIdx).getMessageSide();
                if (side == MessageSide.CENTER) {
                    messageItems.remove(lastIdx);
                    adapter.notifyItemRemoved(lastIdx);
                }
            });


            isLoading = false;
        });
    }

    private void scrollToBottom() {
        recyclerView.scrollToPosition(messageItems.size() - 1);
    }

    private String getFriendlySenderName(MessageItem item) {
        return item.getMessageSide() == MessageSide.LEFT ? titleView.getText().toString() : getString(R.string.you);
    }

    private MessageItem getSelectedMessage() {
        MessageItem message = null;
        for (int i = 0; i < adapter.getItemCount(); i++)
            if (recyclerView.isItemChecked(i)) {
                message = adapter.getItem(i);
                break;
            }
        return message;
    }

    private boolean mayOverwrite(MessageItem messageItem) {
        boolean noTimeout = System.currentTimeMillis() - messageItem.getSentDate().toJavaDate().getTime() <= P21MessageOverride.OVERWITE_TIMEOUT;
        boolean ownMessage = messageItem.getMessageSide() == MessageSide.RIGHT;
        return noTimeout && ownMessage;
    }

}