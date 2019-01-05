package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.StorageAccess;
import de.vectordata.skynet.data.model.ChannelKey;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.model.AsymmetricKey;
import de.vectordata.skynet.net.model.KeyFormat;
import de.vectordata.skynet.net.model.MessageFlags;
import de.vectordata.skynet.net.packet.annotation.Channel;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.base.Persistable;

@Flags(MessageFlags.UNENCRYPTED)
@Channel(ChannelType.LOOPBACK)
public class P18PublicKeys extends ChannelMessagePacket implements Persistable {
    public AsymmetricKey signatureKey;
    public AsymmetricKey derivationKey;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        writeKey(signatureKey, buffer);
        writeKey(derivationKey, buffer);
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        signatureKey = readKey(buffer);
        derivationKey = readKey(buffer);
    }

    private void writeKey(AsymmetricKey key, PacketBuffer buffer) {
        buffer.writeByte((byte) key.format.ordinal());
        buffer.writeByteArray(key.key, true);
    }

    private AsymmetricKey readKey(PacketBuffer buffer) {
        return new AsymmetricKey(KeyFormat.values()[buffer.readByte()], buffer.readByteArray());
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x18;
    }

    @Override
    public void writeToDatabase() {
        StorageAccess.getDatabase().channelKeyDao().insert(ChannelKey.fromPacket(this));
    }
}
