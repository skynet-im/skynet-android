package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.net.PacketHandler;

public class P10RealTimeMessage implements Packet {

    public long channelId;
    public long senderId;
    public byte messageFlags;
    public byte contentPacketId;
    public byte[] contentPacket;

    @Override
    public void writePacket(PacketBuffer buffer) {
        buffer.writeInt64(channelId);
        buffer.writeByte(messageFlags);
        buffer.writeByte(contentPacketId);
        buffer.writeByteArray(contentPacket, true);
    }

    @Override
    public void readPacket(PacketBuffer buffer) {
        channelId = buffer.readInt64();
        senderId = buffer.readInt64();
        messageFlags = buffer.readByte();
        contentPacketId = buffer.readByte();
        contentPacket = buffer.readByteArray();
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x10;
    }
}
