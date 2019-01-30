package de.vectordata.skynet.ui.chat;

import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.R;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChatMessage;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.ui.chat.recycler.MessageAdapter;
import de.vectordata.skynet.ui.chat.recycler.MessageItem;
import de.vectordata.skynet.ui.util.DefaultProfileImage;
import de.vectordata.skynet.ui.util.MessageSide;
import de.vectordata.skynet.ui.util.MessageState;
import de.vectordata.skynet.util.Handlers;

public class ChatActivityDirect extends ChatActivityBase {

    private Channel directChannel;
    private Channel profileDataChannel;

    private Handler databaseHandler = Handlers.createOnThread("DatabaseThread");

    @Override
    public void initialize() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        long channelId = getIntent().getLongExtra(EXTRA_CHANNEL_ID, 0);
        databaseHandler.post(() -> {
            directChannel = Storage.getDatabase().channelDao().getById(channelId);
            if (directChannel != null) {
                profileDataChannel = Storage.getDatabase().channelDao().getByType(directChannel.getOther(), ChannelType.PROFILE_DATA);
                List<ChatMessage> messages = Storage.getDatabase().chatMessageDao().queryLast(channelId, 50);
            }
        });

        List<MessageItem> items = new ArrayList<>();
        items.add(MessageItem.newSystemMessage("YESTERDAY"));
        items.add(new MessageItem("Hi", ago(16, 55, 0), MessageState.SEEN, MessageSide.RIGHT));
        items.add(MessageItem.newSystemMessage("TODAY"));
        items.add(new MessageItem("Eyy moin", ago(16, 45, 0), MessageState.SEEN, MessageSide.LEFT));
        items.add(new MessageItem("Lass uns mal ne Runde zocken, ich will meine neue Grafikkarte ausprobieren \uD83D\uDE02", ago(16, 45, 0), MessageState.SEEN, MessageSide.RIGHT));
        items.add(new MessageItem("Ne mann ich geh jetzt ins Bett, morgen dann", ago(16, 40, 0), MessageState.SEEN, MessageSide.LEFT));
        items.add(new MessageItem("Mhh okay, ich kann aber morgen erst nachmittags.", ago(16, 39, 0), MessageState.SEEN, MessageSide.RIGHT));
        items.add(new MessageItem("Jetzt könnte ich", ago(0, 59, 0), MessageState.SEEN, MessageSide.RIGHT));
        items.add(new MessageItem("Okay komm online, jetzt bin ich am PC", ago(0, 39, 0), MessageState.SEEN, MessageSide.LEFT));
        items.add(new MessageItem("Sehr gut", ago(0, 35, 0), MessageState.SENT, MessageSide.RIGHT));

        recyclerView.setAdapter(new MessageAdapter(items));
    }

    @Override
    public void configureActionBar(ImageView avatar, TextView nickname, TextView onlineState) {
        if (profileDataChannel == null)
            return;
        String nicknameVal = Storage.getDatabase().nicknameDao().last(profileDataChannel.getChannelId()).getNickname();

        nickname.setText(nicknameVal);
        onlineState.setText("unknown last seen state");
        DefaultProfileImage.create(nicknameVal.substring(0, 1), profileDataChannel.getOwnerId(), 128, 128)
                .loadInto(avatar);
    }

    private DateTime ago(int hr, int min, int sec) {
        return DateTime.fromMillis(System.currentTimeMillis() - sec * 1000 - min * 60000 - hr * 3600000);
    }
}
