package de.vectordata.skynet.net.response;

import de.vectordata.skynet.net.packet.base.Packet;

@SuppressWarnings("unchecked")
public abstract class BaseAwaiterItem<T extends Packet> implements IAwaiterItem {

    private ResponseHandler<T> handler;

    BaseAwaiterItem(ResponseHandler<T> handler) {
        this.handler = handler;
    }

    public abstract boolean matches(Packet packet);

    @Override
    public void handle(Packet packet) {
        handler.handle((T) packet);
    }

}
