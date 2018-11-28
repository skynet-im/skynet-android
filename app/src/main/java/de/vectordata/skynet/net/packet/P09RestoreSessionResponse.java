package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.model.RestoreSessionError;

public class P09RestoreSessionResponse implements Packet {

    public RestoreSessionError errorCode;

    @Override
    public void writePacket(PacketBuffer buffer) {
    }

    @Override
    public void readPacket(PacketBuffer buffer) {
        errorCode = RestoreSessionError.values()[buffer.readByte()];
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x09;
    }
}
