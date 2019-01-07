package de.vectordata.skynet.net.packet;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;

public class P29DeviceList extends ChannelMessagePacket {

    public List<Device> devices = new ArrayList<>();

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        devices.clear();
        int count = buffer.readUInt16();
        for (int i = 0; i < count; i++)
            devices.add(new Device(buffer.readInt64(), buffer.readDate(), buffer.readString()));
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x29;
    }

    @Override
    public void writeToDatabase() {
    }

    public class Device {
        public long sessionId;
        public DateTime creationTime;
        public String applicationIdentifier;

        public Device(long sessionId, DateTime creationTime, String applicationIdentifier) {
            this.sessionId = sessionId;
            this.creationTime = creationTime;
            this.applicationIdentifier = applicationIdentifier;
        }
    }
}
