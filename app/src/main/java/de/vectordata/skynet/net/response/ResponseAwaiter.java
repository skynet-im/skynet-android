package de.vectordata.skynet.net.response;


import android.os.Handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.vectordata.skynet.net.packet.base.Packet;

/**
 * Created by Twometer on 11.12.2018.
 * (c) 2018 Twometer
 */
@SuppressWarnings("unchecked")
public class ResponseAwaiter {

    private List<AwaiterItem> awaiterItems = new ArrayList<>();
    private Handler handler;

    public void initialize() {
        if (handler == null)
            handler = new Handler();
    }

    public void onPacket(Packet packet) {
        Iterator<AwaiterItem> iterator = awaiterItems.iterator();

        while (iterator.hasNext()) {
            AwaiterItem item = iterator.next();

            if (item.getPacketClass() == packet.getClass()) {
                iterator.remove();
                item.getHandler().handle(packet);
            }
        }
    }

    public <T extends Packet> void waitForPacket(Class<T> packetClass, ResponseHandler<T> handler) {
        waitForPacket(packetClass, handler, null);
    }

    public <T extends Packet> void waitForPacket(Class<T> packetClass, ResponseHandler<T> handler, final Runnable timeout) {
        if (timeout != null)
            this.handler.postDelayed(timeout, 5000);

        awaiterItems.add(new AwaiterItem(packetClass, packet -> {
            if (timeout != null)
                this.handler.removeCallbacks(timeout);
            handler.handle((T) packet);
        }));
    }

}
