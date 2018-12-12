package de.vectordata.skynet.net.response;


import android.os.Handler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.vectordata.skynet.net.packet.base.Packet;

/**
 * Created by Twometer on 11.12.2018.
 * (c) 2018 Twometer
 */
public class ResponseAwaiter {

    private List<AwaiterItem> awaiterItems = new CopyOnWriteArrayList<>();
    private Handler handler;

    public void initialize() {
        if (handler == null)
            handler = new Handler();
    }

    public void onPacket(Packet packet) {
        for (AwaiterItem item : awaiterItems) {
            if (item.getPacketClass() == packet.getClass()) {
                item.getHandler().handle(packet);
                awaiterItems.remove(item);
                break;
            }
        }
    }

    public <T extends Packet> void waitForPacket(Class<T> packetClass, ResponseHandler<T> handler) {
        awaiterItems.add(new AwaiterItem(packetClass, handler));
    }

}
