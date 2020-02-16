package de.vectordata.skynet.ui.main.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.vectordata.skynet.R;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChannelMessage;
import de.vectordata.skynet.data.model.ChatMessage;
import de.vectordata.skynet.data.model.MessageDraft;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.data.model.enums.MessageState;
import de.vectordata.skynet.data.sql.db.SkynetDatabase;
import de.vectordata.skynet.event.ChatMessageSentEvent;
import de.vectordata.skynet.event.PacketEvent;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.packet.P0ACreateChannel;
import de.vectordata.skynet.net.packet.P0FSyncFinished;
import de.vectordata.skynet.net.packet.P14MailAddress;
import de.vectordata.skynet.net.packet.P20ChatMessage;
import de.vectordata.skynet.net.packet.P21MessageOverride;
import de.vectordata.skynet.net.packet.P22MessageReceived;
import de.vectordata.skynet.net.packet.P23MessageRead;
import de.vectordata.skynet.net.packet.P25Nickname;
import de.vectordata.skynet.net.packet.P2CChannelAction;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.base.Packet;
import de.vectordata.skynet.net.packet.model.ChannelAction;
import de.vectordata.skynet.ui.chat.ChatActivityBase;
import de.vectordata.skynet.ui.chat.ChatActivityDirect;
import de.vectordata.skynet.ui.main.recycler.ChatsAdapter;
import de.vectordata.skynet.ui.main.recycler.ChatsItem;
import de.vectordata.skynet.ui.util.MessageSide;
import de.vectordata.skynet.ui.util.NameUtil;
import de.vectordata.skynet.util.android.Handlers;
import de.vectordata.skynet.util.date.DateTime;

/**
 * Created by Twometer on 14.12.2018.
 * (c) 2018 Twometer
 */
public class ChatsFragment extends Fragment {

    private Activity context;

    private Handler handler;

    private ChatsAdapter adapter;

    private List<ChatsItem> dataset = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        context = Objects.requireNonNull(getActivity());
        if (handler == null)
            handler = Handlers.createOnThread("BackgroundThread");

        adapter = new ChatsAdapter(dataset);
        adapter.setItemClickListener(idx -> {
            ChatsItem item = dataset.get(idx);
            Intent intent = new Intent(getContext(), ChatActivityDirect.class);
            intent.putExtra(ChatActivityBase.EXTRA_CHANNEL_ID, item.getChannelId());
            context.startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);

        return rootView;
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        Packet packet = event.getPacket();
        if (packet instanceof P0FSyncFinished || packet instanceof P0ACreateChannel)
            reload();

        if (packet instanceof P20ChatMessage || packet instanceof P22MessageReceived || packet instanceof P23MessageRead || packet instanceof P21MessageOverride || packet instanceof P14MailAddress || packet instanceof P25Nickname)
            reloadSingle(((ChannelMessagePacket) packet).channelId);

        if (packet instanceof P2CChannelAction)
            reloadSingle(((P2CChannelAction) packet).channelId);
    }

    @Subscribe
    public void onMessageSent(ChatMessageSentEvent event) {
        reload();
    }

    private void reload() {
        if (handler == null) return;
        handler.post(() -> {
            List<ChatsItem> items = new ArrayList<>();
            List<Channel> channels = Storage.getDatabase().channelDao().getAllOfType(ChannelType.DIRECT);
            List<ChatMessage> unreadMessages = Storage.getDatabase().chatMessageDao().queryUnread();

            for (Channel channel : channels) {
                items.add(createItem(channel, unreadMessages));
            }

            Collections.sort(items, (a, b) -> -Long.compare(a.getLastActiveDate().toBinary(), b.getLastActiveDate().toBinary()));
            context.runOnUiThread(() -> {
                dataset.clear();
                dataset.addAll(items);
                adapter.notifyDataSetChanged();
            });
        });
    }

    private void reloadSingle(long channelId) {
        if (handler == null) return;
        handler.post(() -> {
            for (int i = 0; i < dataset.size(); i++) {
                ChatsItem item = dataset.get(i);
                if (item.getChannelId() == channelId) {
                    final int idx = i;

                    Channel channel = Storage.getDatabase().channelDao().getById(channelId);
                    List<ChatMessage> unreadMessages = Storage.getDatabase().chatMessageDao().queryUnread();
                    dataset.set(idx, createItem(channel, unreadMessages));

                    context.runOnUiThread(() -> adapter.notifyItemChanged(idx));
                    return;
                }
            }
        });
    }

    private ChatsItem createItem(Channel channel, List<ChatMessage> unreadMessages) {
        SkynetDatabase db = Storage.getDatabase();

        ChannelAction channelAction = SkynetContext.getCurrent().getAppState().getChannelAction(channel.getChannelId());
        ChatMessage latestMessage = db.chatMessageDao().queryLast(channel.getChannelId());
        MessageDraft messageDraft = db.messageDraftDao().query(channel.getChannelId());
        boolean hasContent = latestMessage != null;

        String header = NameUtil.getFriendlyName(channel.getChannelId());
        DateTime lastActive = DateTime.now();
        String content = context.getString(R.string.tip_start_chatting);
        MessageSide side = MessageSide.LEFT;
        MessageState state = MessageState.NONE;

        if (hasContent) {
            ChannelMessage latestChannelMessage = db.channelMessageDao().getById(latestMessage.getChannelId(), latestMessage.getMessageId());
            lastActive = latestChannelMessage.getDispatchTime();
            content = latestMessage.getText();
            side = latestChannelMessage.getSenderId() == Storage.getSession().getAccountId() ? MessageSide.RIGHT : MessageSide.LEFT;
            state = latestMessage.getMessageState();
        }

        ChatsItem item = new ChatsItem(header, lastActive, channel);
        if (channelAction == ChannelAction.NONE) {
            item.setContent(content);
            item.setUnreadMessages(countUnreadMessages(channel, unreadMessages));
            item.setMessageSide(side);
            item.setMessageState(state);
        } else if (channelAction == ChannelAction.TYPING) {
            item.setType(ChatsItem.Type.HIGHLIGHTED);
            item.setContent(context.getString(R.string.state_typing));
        } else {
            item.setType(ChatsItem.Type.HIGHLIGHTED);
            item.setContent(context.getString(R.string.state_recording));
        }

        if (messageDraft != null && !messageDraft.getText().isEmpty())
            item.setType(ChatsItem.Type.DRAFT);

        return item;
    }

    private int countUnreadMessages(Channel channel, List<ChatMessage> unreadMessages) {
        int unread = 0;
        for (ChatMessage message : unreadMessages)
            if (message.getChannelId() == channel.getChannelId())
                unread++;
        return unread;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


}
