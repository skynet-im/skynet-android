package de.vectordata.skynet.net.packet;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.packet.base.RealtimeMessagePacket;

public class P2CDeviceListDetails extends RealtimeMessagePacket {

    public List<Device> sessionDetails = new ArrayList<>();

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeUInt16(sessionDetails.size());
        for (Device device : sessionDetails) {
            buffer.writeInt64(device.sessionId);
            buffer.writeDate(device.lastConnected);
            buffer.writeInt32(device.lastVersionCode);
        }
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        sessionDetails.clear();
        int count = buffer.readUInt16();
        for (int i = 0; i < count; i++) {
            sessionDetails.add(new Device(buffer.readInt64(), buffer.readDate(), buffer.readInt32()));
        }
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x2C;
    }

    public class Device {
        public long sessionId;
        public DateTime lastConnected;
        public int lastVersionCode;

        public Device(long sessionId, DateTime lastConnected, int lastVersionCode) {
            this.sessionId = sessionId;
            this.lastConnected = lastConnected;
            this.lastVersionCode = lastVersionCode;
        }
    }

}
