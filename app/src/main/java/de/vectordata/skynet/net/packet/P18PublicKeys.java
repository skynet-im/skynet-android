package de.vectordata.skynet.net.packet;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.model.KeyFormat;
import de.vectordata.skynet.net.model.KeyRole;
import de.vectordata.skynet.net.model.MessageFlags;
import de.vectordata.skynet.net.packet.annotation.ChannelMessage;
import de.vectordata.skynet.net.packet.annotation.Flags;

@Flags(MessageFlags.UNENCRYPTED)
@ChannelMessage(ChannelType.LOOPBACK)
public class P18PublicKeys implements Packet {

    public long accountId;
    public List<Key> keys = new ArrayList<>();

    @Override
    public void writePacket(PacketBuffer buffer) {
        buffer.writeInt64(accountId);
        buffer.writeByte((byte) keys.size());
        for (Key key : keys) {
            buffer.writeByte((byte) key.format.ordinal());
            buffer.writeByte((byte) key.role.ordinal());
            buffer.writeByteArray(key.key, true);
        }
    }

    @Override
    public void readPacket(PacketBuffer buffer) {
        accountId = buffer.readInt64();
        byte count = buffer.readByte();
        for (int i = 0; i < count; i++) {
            keys.add(new Key(
                    KeyFormat.values()[buffer.readByte()],
                    KeyRole.values()[buffer.readByte()],
                    buffer.readByteArray()
            ));
        }
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x18;
    }

    public class Key {
        public KeyFormat format;
        public KeyRole role;
        public byte[] key;

        public Key(KeyFormat format, KeyRole role, byte[] key) {
            this.format = format;
            this.role = role;
            this.key = key;
        }
    }
}
