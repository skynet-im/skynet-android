package de.vectordata.skynet.net.messages;

import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.P0BChannelMessage;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;

class PreparedMessage {

    private P0BChannelMessage channelMessage;

    private ChannelMessagePacket child;

    PreparedMessage(P0BChannelMessage channelMessage, ChannelMessagePacket child) {
        this.channelMessage = channelMessage;
        this.child = child;
    }

    void persist(PersistenceMode persistenceMode) {
        if (persistenceMode == PersistenceMode.DATABASE)
            writeToDatabase();
    }

    private void writeToDatabase() {
        channelMessage.writeToDatabase(PacketDirection.SEND);
        child.writeToDatabase(PacketDirection.SEND);
    }

    P0BChannelMessage getChannelMessage() {
        return channelMessage;
    }

}
