package de.vectordata.skynet.ui.chat;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.vanniktech.emoji.EmojiEditText;

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
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.data.model.enums.MessageState;
import de.vectordata.skynet.event.PacketEvent;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.messages.ChannelMessageConfig;
import de.vectordata.skynet.net.packet.P0BChannelMessage;
import de.vectordata.skynet.net.packet.P0CChannelMessageResponse;
import de.vectordata.skynet.net.packet.P20ChatMessage;
import de.vectordata.skynet.net.packet.P21MessageOverride;
import de.vectordata.skynet.net.packet.P22MessageReceived;
import de.vectordata.skynet.net.packet.P23MessageRead;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.base.Packet;
import de.vectordata.skynet.net.packet.model.MessageType;
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
import de.vectordata.skynet.ui.view.CheckableRecyclerView;
import de.vectordata.skynet.util.Callback;

public class ChatActivityDirect extends ChatActivityBase implements MultiChoiceListener {

    private Channel profileDataChannel;
    private Channel accountDataChannel;

    private List<MessageItem> messageItems;

    private CheckableRecyclerView recyclerView;
    private MessageAdapter adapter;

    private ImageView avatarView;
    private TextView nicknameView;
    private TextView lastSeenView;
    private EmojiEditText messageInput;

    private MessageActionController messageActionController;

    @Override
    public void initialize() {
        messageActionController = new MessageActionController(this);
        findViewById(R.id.button_exit_message_action).setOnClickListener(v -> messageActionController.exit());

        messageItems = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        adapter = new MessageAdapter(messageItems);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setActionModeCallback(this);

        messageInput = findViewById(R.id.input_message);

        findViewById(R.id.button_send).setOnClickListener(v -> {
            String text;
            if (messageInput.getText() == null || (text = messageInput.getText().toString()).trim().isEmpty())
                return;

            backgroundHandler.post(() -> {
                if (!messageActionController.isOpen() || messageActionController.getAction() == MessageAction.QUOTE) {
                    P20ChatMessage packet = new P20ChatMessage(MessageType.PLAINTEXT, text, messageActionController.getAffectedMessage());
                    getSkynetContext().getMessageInterface().schedule(messageChannel.getChannelId(), ChannelMessageConfig.createDefault(), packet);
                    insertMessage(packet, MessageState.SENDING);
                } else if (messageActionController.getAction() == MessageAction.EDIT) {
                    P21MessageOverride packet = new P21MessageOverride(messageActionController.getAffectedMessage(), OverrideAction.EDIT, text);
                    getSkynetContext().getMessageInterface().schedule(messageChannel.getChannelId(), ChannelMessageConfig.createDefault(), packet);
                    modifyMessageItem(messageActionController.getAffectedMessage(), data -> data.setContent(text));
                }

                runOnUiThread(() -> messageActionController.exit());
            });

            messageInput.setText("");
        });

        KeyboardUtil.registerOnKeyboardOpen(recyclerView, this::scrollToBottom);

        setup();
    }

    @Override
    public void configureActionBar(ImageView avatar, TextView nickname, TextView onlineState) {
        this.avatarView = avatar;
        this.nicknameView = nickname;
        this.lastSeenView = onlineState;
    }

    private void setup() {
        long channelId = getIntent().getLongExtra(EXTRA_CHANNEL_ID, 0);
        long myAccountId = Storage.getSession().getAccountId();

        backgroundHandler.post(() -> {
            messageChannel = Storage.getDatabase().channelDao().getById(channelId);
            if (messageChannel == null) return; // This should not happen in production

            profileDataChannel = Storage.getDatabase().channelDao().getByType(messageChannel.getCounterpartId(), ChannelType.PROFILE_DATA);
            accountDataChannel = Storage.getDatabase().channelDao().getByType(messageChannel.getCounterpartId(), ChannelType.ACCOUNT_DATA);

            SkynetContext.getCurrent().getNotificationManager().onForeground(messageChannel.getChannelId());

            String friendlyName = NameUtil.getFriendlyName(messageChannel.getCounterpartId(), accountDataChannel);
            DefaultProfileImage profileImage = DefaultProfileImage.create(friendlyName.substring(0, 1), accountDataChannel.getOwnerId(), 128, 128);
            runOnUiThread(() -> {
                nicknameView.setText(friendlyName);
                lastSeenView.setVisibility(View.GONE);
                profileImage.loadInto(avatarView);
            });

            List<ChatMessage> messages = Storage.getDatabase().chatMessageDao().queryLast(channelId, 50);
            Collections.reverse(messages);

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
                messageItems.add(new MessageItem(message.getMessageId(), message.getText(), dispatchTime, message.getMessageState(), messageSide, quotedMessage));
                previous = parent;
            }

            runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                scrollToBottom();
            });

            List<ChatMessage> unread = Storage.getDatabase().chatMessageDao().queryUnread(messageChannel.getChannelId());
            for (ChatMessage message : unread)
                readMessage(message.getMessageId());
        });
    }

    private void insertMessage(P20ChatMessage msg, MessageState messageState) {
        MessageItem oldLatest = messageItems.size() > 0 ? messageItems.get(messageItems.size() - 1) : null;

        long myAccountId = Storage.getSession().getAccountId();
        MessageSide messageSide = msg.getParent().senderId == myAccountId ? MessageSide.RIGHT : MessageSide.LEFT;
        QuotedMessage quotedMessage = null;
        if (msg.quotedMessage != 0)
            quotedMessage = QuotedMessage.load(this, msg.quotedMessage, messageChannel, accountDataChannel);
        MessageItem newLatest = new MessageItem(msg.getParent().messageId, msg.text, msg.getParent().dispatchTime, messageState, messageSide, quotedMessage);
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

    @Subscribe
    public void onPacketEvent(PacketEvent event) {
        Packet packet = event.getPacket();
        if (packet instanceof ChannelMessagePacket && ((ChannelMessagePacket) packet).getParent().channelId != messageChannel.getChannelId())
            return;

        if (packet instanceof P20ChatMessage) {
            P20ChatMessage chatMessage = (P20ChatMessage) packet;
            insertMessage(chatMessage, MessageState.SENT);
            if (chatMessage.getParent().senderId != Storage.getSession().getAccountId())
                readMessage(chatMessage.getParent().messageId);
        } else if (packet instanceof P0CChannelMessageResponse) {
            P0CChannelMessageResponse response = ((P0CChannelMessageResponse) packet);
            if (response.channelId != messageChannel.getChannelId()) return;
            modifyMessageItem(response.tempMessageId, i -> {
                i.setMessageId(response.messageId);
                i.setMessageState(MessageState.SENT);
            });
        } else if (packet instanceof P22MessageReceived) {
            P0BChannelMessage.Dependency dependency = ((P22MessageReceived) packet).getParent().singleDependency();
            modifyMessageItem(dependency.messageId, i -> {
                if (i.getMessageState() != MessageState.SEEN)
                    i.setMessageState(MessageState.DELIVERED);
            });
        } else if (packet instanceof P23MessageRead) {
            P0BChannelMessage.Dependency dependency = ((P23MessageRead) packet).getParent().singleDependency();
            modifyMessageItem(dependency.messageId, i -> i.setMessageState(MessageState.SEEN));
        } else if (packet instanceof P21MessageOverride) {
            P21MessageOverride override = (P21MessageOverride) packet;
            modifyMessageItem(override.messageId, i -> i.setContent(override.newText));
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
                mode.getMenu().findItem(R.id.action_info).setVisible(false);
            } else {
                mode.getMenu().findItem(R.id.action_quote).setVisible(true);
                mode.getMenu().findItem(R.id.action_info).setVisible(true);

                MessageItem selectedMessage = getSelectedMessage();
                if (selectedMessage != null)
                    mode.getMenu().findItem(R.id.action_edit).setVisible(selectedMessage.getMessageSide() == MessageSide.RIGHT);
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
                        getSkynetContext().getMessageInterface().schedule(messageChannel.getChannelId(), ChannelMessageConfig.createDefault(), packet);
                    });
                    modifyMessageItem(selectedMessage.getMessageId(), data -> data.setContent("\0"));
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

    @Override
    protected void onResume() {
        super.onResume();
        SkynetContext.getCurrent().getNotificationManager().onForeground(messageChannel.getChannelId());
    }

    private String getFriendlySenderName(MessageItem item) {
        return item.getMessageSide() == MessageSide.LEFT ? nicknameView.getText().toString() : getString(R.string.you);
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

}