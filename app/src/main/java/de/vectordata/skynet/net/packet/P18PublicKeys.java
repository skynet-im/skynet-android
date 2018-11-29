package de.vectordata.skynet.net.packet;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.KeyProvider;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.model.KeyFormat;
import de.vectordata.skynet.net.model.KeyRole;
import de.vectordata.skynet.net.model.MessageFlags;
import de.vectordata.skynet.net.packet.annotation.Channel;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;

@Flags(MessageFlags.UNENCRYPTED)
@Channel(ChannelType.LOOPBACK)
public class P18PublicKeys extends ChannelMessagePacket {

    public long accountId;
    public List<Key> keys = new ArrayList<>();

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeInt64(accountId);
        buffer.writeByte((byte) keys.size());
        for (Key key : keys) {
            buffer.writeByte((byte) key.format.ordinal());
            buffer.writeByte((byte) key.role.ordinal());
            buffer.writeByteArray(key.key, true);
        }
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
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
