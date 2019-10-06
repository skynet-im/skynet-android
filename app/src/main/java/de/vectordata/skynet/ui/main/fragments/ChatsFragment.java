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

import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.R;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChannelMessage;
import de.vectordata.skynet.data.model.ChatMessage;
import de.vectordata.skynet.data.model.MessageDraft;
import de.vectordata.skynet.data.model.enums.ChannelType;
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
import de.vectordata.skynet.net.packet.base.Packet;
import de.vectordata.skynet.net.packet.model.ChannelAction;
import de.vectordata.skynet.ui.chat.ChatActivityBase;
import de.vectordata.skynet.ui.chat.ChatActivityDirect;
import de.vectordata.skynet.ui.main.recycler.ChatsAdapter;
import de.vectordata.skynet.ui.main.recycler.ChatsItem;
import de.vectordata.skynet.ui.util.MessageSide;
import de.vectordata.skynet.ui.util.NameUtil;
import de.vectordata.skynet.util.Handlers;

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
            handler = Handlers.createOnThread("DatabaseThread");

        adapter = new ChatsAdapter(dataset);
        adapter.setItemClickListener(idx -> {
            ChatsItem item = dataset.get(idx);
            Intent intent = new Intent(getContext(), ChatActivityDirect.class);
            intent.putExtra(ChatActivityBase.EXTRA_CHANNEL_ID, item.getChannelId());
            context.startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        Packet packet = event.getPacket();
        if (packet instanceof P0FSyncFinished || packet instanceof P20ChatMessage || packet instanceof P0ACreateChannel
                || packet instanceof P22MessageReceived || packet instanceof P23MessageRead || packet instanceof P21MessageOverride
                || packet instanceof P14MailAddress || packet instanceof P25Nickname || packet instanceof P2CChannelAction)
            reload();
    }

    @Subscribe
    public void onMessageSent(ChatMessageSentEvent event) {
        reload();
    }

    private void reload() {
        if (handler == null) return;
        handler.post(() -> {
            List<Channel> channels = Storage.getDatabase().channelDao().getAllOfType(ChannelType.DIRECT);
            List<ChatsItem> items = new ArrayList<>();
            List<ChatMessage> unreadMessages = Storage.getDatabase().chatMessageDao().queryUnread();
            for (Channel channel : channels) {
                ChannelAction action = SkynetContext.getCurrent().getAppState().getChannelAction(channel.getChannelId());
                Channel accountDataChannel = Storage.getDatabase().channelDao().getByType(channel.getCounterpartId(), ChannelType.ACCOUNT_DATA);
                MessageDraft draft = Storage.getDatabase().messageDraftDao().query(channel.getChannelId());
                boolean isDraft = draft != null && !draft.getText().isEmpty();
                String friendlyName = NameUtil.getFriendlyName(channel.getCounterpartId(), accountDataChannel);

                if (action == ChannelAction.NONE) {
                    ChatMessage latestMessage = Storage.getDatabase().chatMessageDao().queryLast(channel.getChannelId());
                    ChatsItem item;
                    if (latestMessage != null) {
                        int unread = 0;
                        for (ChatMessage message : unreadMessages)
                            if (message.getChannelId() == channel.getChannelId())
                                unread++;

                        ChannelMessage channelMessage = Storage.getDatabase().channelMessageDao().getById(latestMessage.getChannelId(), latestMessage.getMessageId());
                        MessageSide side = channelMessage.getSenderId() == Storage.getSession().getAccountId() ? MessageSide.RIGHT : MessageSide.LEFT;
                        item = new ChatsItem(friendlyName, latestMessage.getText(), channelMessage.getDispatchTime(), 0, side, latestMessage.getMessageState(), unread, channel.getChannelId(), channel.getCounterpartId()).setDraft(isDraft);
                    } else
                        item = new ChatsItem(friendlyName, context.getString(R.string.tip_start_chatting), DateTime.now(), 0, 0, channel.getChannelId(), channel.getCounterpartId()).setDraft(isDraft);
                    items.add(item);
                } else if (action == ChannelAction.TYPING)
                    items.add(new ChatsItem(friendlyName, context.getString(R.string.state_typing), DateTime.now(), 0, 0, channel.getChannelId(), channel.getCounterpartId()).setHighlighted().setDraft(isDraft));
                else
                    items.add(new ChatsItem(friendlyName, context.getString(R.string.state_recording), DateTime.now(), 0, 0, channel.getChannelId(), channel.getCounterpartId()).setHighlighted().setDraft(isDraft));
            }
            Collections.sort(items, (a, b) -> -Long.compare(a.getLastActiveDate().toBinary(), b.getLastActiveDate().toBinary()));
            context.runOnUiThread(() -> {
                dataset.clear();
                dataset.addAll(items);
                adapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
