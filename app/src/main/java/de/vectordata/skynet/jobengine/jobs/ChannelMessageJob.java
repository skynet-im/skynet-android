package de.vectordata.skynet.jobengine.jobs;

import org.greenrobot.eventbus.Subscribe;

import de.vectordata.skynet.event.ConnectionFailedEvent;
import de.vectordata.skynet.jobengine.annotations.Retry;
import de.vectordata.skynet.jobengine.api.Job;
import de.vectordata.skynet.jobengine.api.JobState;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.packet.P0CChannelMessageResponse;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.MessageSendStatus;

@Retry(Retry.Mode.RECONNECT)
public class ChannelMessageJob extends Job<Void> {

    private ChannelMessagePacket message;

    public ChannelMessageJob(ChannelMessagePacket message) {
        this.message = message;
    }

    @Override
    public void onExecute() {
        SkynetContext context = SkynetContext.getCurrent();

        context.getNetworkManager().sendPacket(message).waitForPacket(P0CChannelMessageResponse.class,
                response -> reportState(response.statusCode == MessageSendStatus.SUCCESS ? JobState.SUCCESSFUL : JobState.FAILED),
                () -> {
                    // No response from the server after 5 seconds, assume timeout
                    reportState(JobState.FAILED);
                }
        );
    }

    @Override
    public void onCancel() {
        throw new UnsupportedOperationException("Single message jobs cannot be cancelled");
    }

    @Subscribe
    public void onConnectionLost(ConnectionFailedEvent event) {
        reportState(JobState.FAILED);
    }

    @Override
    public boolean hasEvents() {
        return true;
    }
}
