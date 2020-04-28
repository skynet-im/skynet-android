package de.vectordata.skynet.net.response;

import de.vectordata.skynet.net.packet.P0CChannelMessageResponse;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.base.Packet;

public class MsgAwaiterItem extends BaseAwaiterItem<P0CChannelMessageResponse> {

    private ChannelMessagePacket sourcePacket;

    MsgAwaiterItem(ResponseHandler<P0CChannelMessageResponse> handler, ChannelMessagePacket sourcePacket) {
        super(handler);
        this.sourcePacket = sourcePacket;
    }

    @Override
    public boolean matches(Packet packet) {
        if (packet instanceof P0CChannelMessageResponse) {
            P0CChannelMessageResponse response = (P0CChannelMessageResponse) packet;
            return response.tempMessageId == sourcePacket.messageId;
        }
        return false;
    }
}
