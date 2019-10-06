package de.vectordata.skynet.ui;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

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
import de.vectordata.skynet.net.messages.ChannelMessageConfig;
import de.vectordata.skynet.net.packet.P20ChatMessage;
import de.vectordata.skynet.net.packet.model.MessageType;
import de.vectordata.skynet.ui.base.ThemedActivity;
import de.vectordata.skynet.ui.main.recycler.ChatsAdapter;
import de.vectordata.skynet.ui.main.recycler.ChatsItem;
import de.vectordata.skynet.ui.util.NameUtil;
import de.vectordata.skynet.ui.view.CheckableBehavior;
import de.vectordata.skynet.ui.view.CheckableRecyclerView;
import de.vectordata.skynet.util.Activities;
import de.vectordata.skynet.util.Handlers;

public class ForwardActivity extends ThemedActivity {

    public static final String EXTRA_SRC_CHANNEL = "skynet.forward.srcChannel";

    public static final String EXTRA_SRC_MESSAGE = "skynet.forward.srcMessage";

    private Handler backgroundHandler = Handlers.createOnThread("BackgroundThread");

    private ChatMessage source;

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

        List<ChatsItem> dataset = new ArrayList<>();

        CheckableRecyclerView recyclerView = findViewById(R.id.recycler_view);

        ChatsAdapter adapter = new ChatsAdapter(dataset);
        adapter.setSingleLine(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setBehavior(CheckableBehavior.SINGLE_CLICK);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fab).setOnClickListener(v -> backgroundHandler.post(() -> {
            for (int i = 0; i < adapter.getItemCount(); i++) {
                if (!recyclerView.isItemChecked(i)) continue;
                ChatsItem target = dataset.get(i);

                P20ChatMessage packet = new P20ChatMessage(MessageType.PLAINTEXT, source.getText(), 0);
                getSkynetContext().getMessageInterface().schedule(target.getChannelId(), ChannelMessageConfig.create(), packet);
            }
            Toast.makeText(this, R.string.progress_sending_messages, Toast.LENGTH_SHORT).show();
            finish();
        }));

        backgroundHandler.post(() -> {
            source = Storage.getDatabase().chatMessageDao().query(srcChannel, srcMessage);

            List<Channel> channels = Storage.getDatabase().channelDao().getAllOfType(ChannelType.DIRECT);
            for (Channel channel : channels) {
                String friendlyName = NameUtil.getFriendlyName(channel.getChannelId());
                ChannelMessage latestMessage = Storage.getDatabase().channelMessageDao().queryLast(channel.getChannelId());
                ChatsItem item = new ChatsItem(friendlyName, latestMessage != null ? latestMessage.getDispatchTime() : DateTime.now(), channel);
                dataset.add(item);
            }
            Collections.sort(dataset, (a, b) -> -Long.compare(a.getLastActiveDate().toBinary(), b.getLastActiveDate().toBinary()));
            runOnUiThread(adapter::notifyDataSetChanged);
        });
    }

}
