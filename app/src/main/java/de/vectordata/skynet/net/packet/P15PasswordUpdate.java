package de.vectordata.skynet.net.packet;

import java.io.StreamCorruptedException;

import de.vectordata.skynet.crypto.Aes;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.PasswordUpdate;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.LengthPrefix;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.MessageFlags;

@Flags(MessageFlags.LOOPBACK | MessageFlags.UNENCRYPTED)
public class P15PasswordUpdate extends ChannelMessagePacket {

    public byte[] previousKeyHash;
    public byte[] keyHash;
    public byte[] previousKey;

    @Override
    public void writeContents(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeByteArray(previousKeyHash, LengthPrefix.NONE);
        buffer.writeByteArray(keyHash, LengthPrefix.NONE);
        buffer.writeByteArray(Aes.encryptSigned(previousKey, keyProvider.getChannelKeys(channelId)), LengthPrefix.MEDIUM);
    }

    @Override
    public void readContents(PacketBuffer buffer, KeyProvider keyProvider) {
        previousKeyHash = buffer.readBytes(32);
        keyHash = buffer.readBytes(32);
        byte[] keyHistory = buffer.readByteArray(LengthPrefix.MEDIUM);
        if (keyHistory.length > 0) {
            try {
                previousKey = Aes.decryptSigned(buffer.readByteArray(LengthPrefix.MEDIUM), keyProvider.getChannelKeys(channelId));
            } catch (StreamCorruptedException e) {
                isCorrupted = true;
            }
        }
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x15;
    }

    @Override
    public void persistContents(PacketDirection packetDirection) {
        Storage.getDatabase().passwordUpdateDao().insert(PasswordUpdate.fromPacket(this));
    }
}
