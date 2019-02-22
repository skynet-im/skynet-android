package de.vectordata.skynet.ui.main.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import de.vectordata.skynet.net.packet.P0ACreateChannel;
import de.vectordata.skynet.net.packet.P0FSyncFinished;
import de.vectordata.skynet.net.packet.P20ChatMessage;
import de.vectordata.skynet.ui.chat.ChatActivityBase;
import de.vectordata.skynet.ui.chat.ChatActivityDirect;
import de.vectordata.skynet.ui.main.recycler.ChatsAdapter;
import de.vectordata.skynet.ui.main.recycler.ChatsItem;
import de.vectordata.skynet.ui.util.MessageSide;
import de.vectordata.skynet.ui.util.NameUtil;

/**
 * Created by Twometer on 14.12.2018.
 * (c) 2018 Twometer
 */
public class ChatsFragment extends Fragment {

    private Activity context;

    private ChatsAdapter adapter;

    private List<ChatsItem> dataset = new ArrayList<>();

    private boolean isReloading;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        context = Objects.requireNonNull(getActivity());

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
        SkynetContext.getCurrent().getNetworkManager().setPacketListener(p -> {
            if (p instanceof P0FSyncFinished || p instanceof P20ChatMessage || p instanceof P0ACreateChannel)
                reload();
        });
        reload();
    }

    private void reload() {
        new Thread(() -> {
            if (isReloading) return;
            isReloading = true;
            List<Channel> channels = Storage.getDatabase().channelDao().getAllOfType(ChannelType.DIRECT);
            List<ChatsItem> items = new ArrayList<>();
            List<ChatMessage> unreadMessages = Storage.getDatabase().chatMessageDao().queryUnread();
            for (Channel channel : channels) {
                Channel accountDataChannel = Storage.getDatabase().channelDao().getByType(channel.getCounterpartId(), ChannelType.ACCOUNT_DATA);
                String friendlyName = NameUtil.getFriendlyName(channel.getCounterpartId(), accountDataChannel);

                ChatMessage latestMessage = Storage.getDatabase().chatMessageDao().queryLast(channel.getChannelId());
                ChatsItem item;
                if (latestMessage != null) {
                    int unread = 0;
                    for (ChatMessage message : unreadMessages)
                        if (message.getChannelId() == channel.getChannelId())
                            unread++;

                    ChannelMessage channelMessage = Storage.getDatabase().channelMessageDao().getById(latestMessage.getChannelId(), latestMessage.getMessageId());
                    MessageSide side = channelMessage.getSenderId() == Storage.getSession().getAccountId() ? MessageSide.RIGHT : MessageSide.LEFT;
                    item = new ChatsItem(friendlyName, latestMessage.getText(), channelMessage.getDispatchTime(), 0, side, latestMessage.getMessageState(), unread, channel.getChannelId(), channel.getCounterpartId());
                } else
                    item = new ChatsItem(friendlyName, context.getString(R.string.tip_start_chatting), DateTime.now(), 0, 0, channel.getChannelId(), channel.getCounterpartId());
                items.add(item);
            }
            Collections.sort(items, (a, b) -> -(int) (a.getLastActiveDate().toBinary() - b.getLastActiveDate().toBinary()));
            context.runOnUiThread(() -> {
                dataset.clear();
                dataset.addAll(items);
                adapter.notifyDataSetChanged();
                isReloading = false;
            });
        }).start();
    }

}
