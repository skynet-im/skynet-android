package de.vectordata.skynet.net.packet;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.packet.base.AbstractPacket;
import de.vectordata.skynet.net.packet.model.SessionInfo;

public class P33DeviceListResponse extends AbstractPacket {

    public List<SessionInfo> sessionDetails = new ArrayList<>();

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeUInt16(sessionDetails.size());
        for (SessionInfo info : sessionDetails) {
            buffer.writeInt64(info.getSessionId());
            buffer.writeDate(info.getLastConnected());
            buffer.writeInt32(info.getLastVersionCode());
        }
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        sessionDetails.clear();
        int size = buffer.readUInt16();
        for (int i = 0; i < size; i++)
            sessionDetails.add(new SessionInfo(buffer.readInt64(), buffer.readDate(), buffer.readInt32()));
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x33;
    }
}
