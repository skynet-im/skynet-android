package de.vectordata.skynet.net.packet;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Device;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.LengthPrefix;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.MessageFlags;
import de.vectordata.skynet.util.date.DateTime;

@Flags(MessageFlags.LOOPBACK | MessageFlags.UNENCRYPTED)
public class P29DeviceList extends ChannelMessagePacket {

    public List<PDevice> devices = new ArrayList<>();

    @Override
    public void writeContents(PacketBuffer buffer, KeyProvider keyProvider) {
    }

    @Override
    public void readContents(PacketBuffer buffer, KeyProvider keyProvider) {
        devices.clear();
        int count = buffer.readUInt16();
        for (int i = 0; i < count; i++)
            devices.add(new PDevice(buffer.readInt64(), buffer.readDate(), buffer.readString(LengthPrefix.SHORT)));
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
    public void persistContents(PacketDirection packetDirection) {
        Storage.getDatabase().deviceListDao().clear();
        Device[] devices = new Device[this.devices.size()];
        for (int i = 0; i < devices.length; i++)
            devices[i] = Device.fromPacket(this.devices.get(i));
        Storage.getDatabase().deviceListDao().insert(devices);
    }

    public class PDevice {
        public long sessionId;
        public DateTime creationTime;
        public String applicationIdentifier;

        public PDevice(long sessionId, DateTime creationTime, String applicationIdentifier) {
            this.sessionId = sessionId;
            this.creationTime = creationTime;
            this.applicationIdentifier = applicationIdentifier;
        }
    }
}
