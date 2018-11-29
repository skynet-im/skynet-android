package de.vectordata.skynet.net.packet;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.libjvsl.crypt.AesStatic;
import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.crypto.KeyProvider;
import de.vectordata.skynet.crypto.KeyStore;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.model.MessageFlags;
import de.vectordata.skynet.net.packet.base.Packet;

public class P0BChannelMessage implements Packet {

    public byte packetVersion;
    public long channelId;
    public long senderId;
    public long messageId;
    public long skipCount;
    public DateTime dispatchTime;
    public byte messageFlags;
    public long fileId;
    public byte contentPacketId;
    public byte contentPacketVersion;
    public byte[] contentPacket;
    public byte[] fileKey;
    public List<Dependency> dependencies = new ArrayList<>();

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeByte(packetVersion);
        buffer.writeInt64(channelId);
        buffer.writeInt64(messageId);
        buffer.writeByte(messageFlags);
        if ((messageFlags & MessageFlags.FILE_ATTACHED) != 0) buffer.writeInt64(fileId);
        buffer.writeByte(contentPacketId);
        buffer.writeByte(contentPacketVersion);

        KeyStore channelKeys = keyProvider.getChannelKeys(channelId);
        PacketBuffer encryptedBuffer = new PacketBuffer();
        encryptedBuffer.writeByteArray(contentPacket, true);
        if ((messageFlags & MessageFlags.FILE_ATTACHED) != 0)
            encryptedBuffer.writeByteArray(fileKey, true);
        AesStatic.encryptWithHmac(encryptedBuffer.toArray(), buffer, true, channelKeys.getHmacKey(), channelKeys.getAesKey());

        buffer.writeUInt16(dependencies.size());
        for (Dependency dependency : dependencies) {
            buffer.writeInt64(dependency.accountId);
            buffer.writeInt64(dependency.channelId);
            buffer.writeInt64(dependency.messageId);
        }
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        packetVersion = buffer.readByte();
        channelId = buffer.readInt64();
        senderId = buffer.readInt64();
        messageId = buffer.readInt64();
        skipCount = buffer.readInt64();
        dispatchTime = buffer.readDate();
        messageFlags = buffer.readByte();
        if ((messageFlags & MessageFlags.FILE_ATTACHED) != 0) fileId = buffer.readInt64();
        contentPacketId = buffer.readByte();
        contentPacketVersion = buffer.readByte();

        KeyStore channelKeys = keyProvider.getChannelKeys(channelId);
        byte[] decryptedData = AesStatic.decryptWithHmac(buffer, 0, channelKeys.getHmacKey(), channelKeys.getAesKey());
        PacketBuffer decryptedBuffer = new PacketBuffer(decryptedData);
        contentPacket = decryptedBuffer.readByteArray();
        if ((messageFlags & MessageFlags.FILE_ATTACHED) != 0) fileKey = decryptedBuffer.readByteArray();

        int dependencyCount = buffer.readUInt16();
        for (int i = 0; i < dependencyCount; i++)
            dependencies.add(new Dependency(buffer.readInt64(), buffer.readInt64(), buffer.readInt64()));
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x0B;
    }

    public class Dependency {
        public long accountId;
        public long channelId;
        public long messageId;

        public Dependency(long accountId, long channelId, long messageId) {
            this.accountId = accountId;
            this.channelId = channelId;
            this.messageId = messageId;
        }
    }
}
