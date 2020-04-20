package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.LengthPrefix;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.AsymmetricKey;
import de.vectordata.skynet.net.packet.model.KeyFormat;
import de.vectordata.skynet.net.packet.model.MessageFlags;

@Flags(MessageFlags.UNENCRYPTED)
public class P18PublicKeys extends ChannelMessagePacket {
    public AsymmetricKey signatureKey;
    public AsymmetricKey derivationKey;

    public P18PublicKeys(AsymmetricKey signatureKey, AsymmetricKey derivationKey) {
        this.signatureKey = signatureKey;
        this.derivationKey = derivationKey;
    }

    public P18PublicKeys() {
    }

    @Override
    public void writeContents(PacketBuffer buffer, KeyProvider keyProvider) {
        writeKey(signatureKey, buffer);
        writeKey(derivationKey, buffer);
    }

    @Override
    public void readContents(PacketBuffer buffer, KeyProvider keyProvider) {
        signatureKey = readKey(buffer);
        derivationKey = readKey(buffer);
    }

    private void writeKey(AsymmetricKey key, PacketBuffer buffer) {
        buffer.writeByte((byte) key.format.ordinal());
        buffer.writeByteArray(key.key, LengthPrefix.MEDIUM);
    }

    private AsymmetricKey readKey(PacketBuffer buffer) {
        return new AsymmetricKey(KeyFormat.values()[buffer.readByte()], buffer.readByteArray(LengthPrefix.MEDIUM));
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
    public void persistContents(PacketDirection packetDirection) {
    }
}
