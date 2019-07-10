package de.vectordata.skynet.ui;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import de.vectordata.skynet.ui.base.ThemedActivity;
import de.vectordata.skynet.ui.main.recycler.ChatsAdapter;
import de.vectordata.skynet.ui.main.recycler.ChatsItem;
import de.vectordata.skynet.ui.util.MessageSide;
import de.vectordata.skynet.ui.util.NameUtil;
import de.vectordata.skynet.util.Activities;

public class ForwardActivity extends ThemedActivity {

    public static final String EXTRA_SRC_CHANNEL = "skynet.forward.srcChannel";

    public static final String EXTRA_SRC_MESSAGE = "skynet.forward.srcMessage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward);
        Activities.enableUpButton(this);

        long srcChannel = getIntent().getLongExtra(EXTRA_SRC_CHANNEL, 0);
        long srcMessage = getIntent().getLongExtra(EXTRA_SRC_MESSAGE, 0);

        if (srcChannel == 0 || srcMessage == 0) {
            finish();
            return;
        }

        (new Thread(() -> {
            List<ChatsItem> dataset = new ArrayList<>();

            ChatsAdapter adapter = new ChatsAdapter(dataset);
            RecyclerView recyclerView = findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

            List<Channel> channels = Storage.getDatabase().channelDao().getAllOfType(ChannelType.DIRECT);
            for (Channel channel : channels) {
                Channel accountDataChannel = Storage.getDatabase().channelDao().getByType(channel.getCounterpartId(), ChannelType.ACCOUNT_DATA);
                String friendlyName = NameUtil.getFriendlyName(channel.getCounterpartId(), accountDataChannel);

                ChatMessage latestMessage = Storage.getDatabase().chatMessageDao().queryLast(channel.getChannelId());
                ChatsItem item;
                if (latestMessage != null) {
                    ChannelMessage channelMessage = Storage.getDatabase().channelMessageDao().getById(latestMessage.getChannelId(), latestMessage.getMessageId());
                    MessageSide side = channelMessage.getSenderId() == Storage.getSession().getAccountId() ? MessageSide.RIGHT : MessageSide.LEFT;
                    item = new ChatsItem(friendlyName, latestMessage.getText(), channelMessage.getDispatchTime(), 0, side, latestMessage.getMessageState(), 0, channel.getChannelId(), channel.getCounterpartId());
                } else
                    item = new ChatsItem(friendlyName, getString(R.string.tip_start_chatting), DateTime.now(), 0, 0, channel.getChannelId(), channel.getCounterpartId());
                dataset.add(item);
            }
            Collections.sort(dataset, (a, b) -> -Long.compare(a.getLastActiveDate().toBinary(), b.getLastActiveDate().toBinary()));
            runOnUiThread(adapter::notifyDataSetChanged);
        })).start();
    }

}
