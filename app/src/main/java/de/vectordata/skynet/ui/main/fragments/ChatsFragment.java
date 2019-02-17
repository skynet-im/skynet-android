package de.vectordata.skynet.ui.main.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
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
import de.vectordata.skynet.net.packet.P0FSyncFinished;
import de.vectordata.skynet.ui.chat.ChatActivityBase;
import de.vectordata.skynet.ui.chat.ChatActivityDirect;
import de.vectordata.skynet.ui.main.recycler.ChatsAdapter;
import de.vectordata.skynet.ui.main.recycler.ChatsItem;

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

        reload();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        SkynetContext.getCurrent().getNetworkManager().setPacketListener(p -> {
            if (p instanceof P0FSyncFinished)
                reload();
        });
    }

    private void reload() {
        new Thread(() -> {
            if (isReloading) return;
            isReloading = true;
            List<Channel> channels = Storage.getDatabase().channelDao().getAllOfType(ChannelType.DIRECT);
            List<ChatsItem> items = new ArrayList<>();
            for (Channel channel : channels) {
                ChatMessage latestMessage = Storage.getDatabase().chatMessageDao().queryLast(channel.getChannelId());
                ChatsItem item;
                if (latestMessage != null) {
                    ChannelMessage channelMessage = Storage.getDatabase().channelMessageDao().getById(latestMessage.getChannelId(), latestMessage.getMessageId());
                    item = new ChatsItem(Long.toHexString(channel.getOther()), latestMessage.getText(), channelMessage.getDispatchTime(), 0, 0, channel.getChannelId());
                } else
                    item = new ChatsItem(Long.toHexString(channel.getOther()), "", DateTime.now(), 0, 0, channel.getChannelId());
                items.add(item);
            }
            context.runOnUiThread(() -> {
                dataset.clear();
                dataset.addAll(items);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

}
