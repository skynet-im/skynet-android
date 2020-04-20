package de.vectordata.skynet.net.response;

import android.os.Handler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import de.vectordata.skynet.event.PacketEvent;
import de.vectordata.skynet.net.packet.P0CChannelMessageResponse;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.base.Packet;
import de.vectordata.skynet.net.packet.model.MessageSendStatus;
import de.vectordata.skynet.util.android.Handlers;

public class PacketTask {

    private static final int RESPONSE_TIMEOUT = 7500;

    private Handler timeoutScheduler = Handlers.createOnThread(Handlers.THREAD_BACKGROUND);

    private Packet sourcePacket;
    private IAwaiterItem awaiterItem;

    private boolean handled = false;

    public PacketTask(Packet sourcePacket) {
        this.sourcePacket = sourcePacket;
        EventBus.getDefault().register(this);
    }

    public <T extends Packet> void waitFor(Class<T> packetClass, ResponseHandler<T> handler) {
        waitFor(packetClass, handler, null);
    }

    private <T extends Packet> void waitFor(Class<T> packetClass, ResponseHandler<T> handler, final Runnable onTimeout) {
        if (onTimeout != null)
            timeoutScheduler.postDelayed(new TimeoutRunnableProxy(onTimeout), RESPONSE_TIMEOUT);

        awaiterItem = new AnyAwaiterItem<>(packetClass, packet -> {
            if (onTimeout != null)
                timeoutScheduler.removeCallbacks(onTimeout);
            handler.handle(packet);
            EventBus.getDefault().unregister(this);
        });
    }

    public void waitForSuccess(ResponseHandler<P0CChannelMessageResponse> handler) {
        waitForResponse(packet -> {
            if (packet.statusCode == MessageSendStatus.SUCCESS)
                handler.handle(packet);
        });
    }

    private void waitForResponse(ResponseHandler<P0CChannelMessageResponse> handler) {
        waitForResponse(handler, null);
    }

    public void waitForResponse(ResponseHandler<P0CChannelMessageResponse> handler, final Runnable onTimeout) {
        if (!(sourcePacket instanceof ChannelMessagePacket))
            throw new IllegalStateException("Cannot wait for a ChannelMessageResponse in response to a non-channel message");

        if (onTimeout != null)
            timeoutScheduler.postDelayed(new TimeoutRunnableProxy(onTimeout), RESPONSE_TIMEOUT);

        awaiterItem = new MsgAwaiterItem(p -> {
            if (onTimeout != null)
                timeoutScheduler.removeCallbacks(onTimeout);
            handler.handle(p);
            EventBus.getDefault().unregister(this);
        }, (ChannelMessagePacket) sourcePacket);
    }

    @Subscribe
    public void onPacketEvent(PacketEvent event) {
        if (awaiterItem == null || handled)
            return;

        if (awaiterItem.matches(event.getPacket())) {
            awaiterItem.handle(event.getPacket());
            handled = true;
        }
    }

    private class TimeoutRunnableProxy implements Runnable {

        private Runnable runnable;

        TimeoutRunnableProxy(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            EventBus.getDefault().unregister(PacketTask.this);
            runnable.run();
        }

    }

}
