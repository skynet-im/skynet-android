package de.vectordata.skynet.jobengine;

import android.util.Log;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.ChatMessage;
import de.vectordata.skynet.data.model.Dependency;
import de.vectordata.skynet.event.SyncFinishedEvent;
import de.vectordata.skynet.jobengine.jobs.ChannelMessageJob;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.messages.ChannelMessageConfig;
import de.vectordata.skynet.net.packet.P0BChannelMessage;
import de.vectordata.skynet.net.packet.P20ChatMessage;
import de.vectordata.skynet.net.packet.model.MessageType;

class RetryController {

    private static final String TAG = "RetryController";

    @Subscribe
    public void onSyncFinished(SyncFinishedEvent event) {
        if (getSkynetContext().getJobEngine().isEmpty()) {
            List<ChatMessage> chatMessages = Storage.getDatabase().chatMessageDao().queryPending();
            Log.i(TAG, "Retrying " + chatMessages.size() + " chat messages");
            for (ChatMessage message : chatMessages)
                scheduleMessage(message);
        }
    }

    private void scheduleMessage(ChatMessage chatMessage) {
        P20ChatMessage packet = new P20ChatMessage(MessageType.PLAINTEXT, chatMessage.getText(), chatMessage.getQuotedMessage());

        ChannelMessageConfig config = new ChannelMessageConfig();
        List<Dependency> dependencies = Storage.getDatabase().dependencyDao().getDependencies(chatMessage.getChannelId(), chatMessage.getMessageId());
        for (Dependency dependency : dependencies)
            config.addDependency(dependency.getDstAccountId(), dependency.getDstChannelId(), dependency.getDstMessageId());

        P0BChannelMessage message = getSkynetContext().getMessageInterface().prepare(chatMessage.getChannelId(), chatMessage.getMessageId(), config, packet, false); // Do not save it to the database again

        getSkynetContext().getJobEngine().schedule(new ChannelMessageJob(message));
    }

    private SkynetContext getSkynetContext() {
        return SkynetContext.getCurrent();
    }

}
