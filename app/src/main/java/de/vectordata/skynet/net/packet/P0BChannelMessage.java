package de.vectordata.skynet.net.packet;

import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.libjvsl.crypt.AesStatic;
import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.crypto.keys.KeyStore;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChannelMessage;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.base.Packet;
import de.vectordata.skynet.net.packet.model.MessageFlags;

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

    public boolean hasMessageFlag(byte flag) {
        return (messageFlags & flag) != 0;
    }

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {

        buffer.writeByte(packetVersion);
        buffer.writeInt64(channelId);
        buffer.writeInt64(messageId);
        buffer.writeByte(messageFlags);
        if (hasMessageFlag(MessageFlags.FILE_ATTACHED)) buffer.writeInt64(fileId);
        buffer.writeByte(contentPacketId);
        buffer.writeByte(contentPacketVersion);
        Log.d("P0BChannelMessage", String.format("Writing channel Message with content id %s: unencrypted=%s fileAttached=%s", contentPacketId, hasMessageFlag(MessageFlags.UNENCRYPTED), hasMessageFlag(MessageFlags.FILE_ATTACHED)));
        if (hasMessageFlag(MessageFlags.UNENCRYPTED)) writeContents(buffer);
        else {
            KeyStore channelKeys = keyProvider.getChannelKeys(channelId);
            PacketBuffer encryptedBuffer = new PacketBuffer();
            writeContents(encryptedBuffer);
            AesStatic.encryptWithHmac(encryptedBuffer.toArray(), buffer, true, channelKeys.getHmacKey(), channelKeys.getAesKey());
        }

        buffer.writeUInt16(dependencies.size());
        for (Dependency dependency : dependencies) {
            buffer.writeInt64(dependency.accountId);
            buffer.writeInt64(dependency.channelId);
            buffer.writeInt64(dependency.messageId);
        }
    }

    private void writeContents(PacketBuffer buffer) {
        buffer.writeByteArray(contentPacket, true);
        if (hasMessageFlag(MessageFlags.FILE_ATTACHED))
            buffer.writeByteArray(fileKey, true);
    }

    private void readContents(PacketBuffer packetBuffer) {
        contentPacket = packetBuffer.readByteArray();
        if ((messageFlags & MessageFlags.FILE_ATTACHED) != 0)
            fileKey = packetBuffer.readByteArray();
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        dependencies.clear();
        packetVersion = buffer.readByte();
        channelId = buffer.readInt64();
        senderId = buffer.readInt64();
        messageId = buffer.readInt64();
        skipCount = buffer.readInt64();
        dispatchTime = buffer.readDate();
        messageFlags = buffer.readByte();
        if (hasMessageFlag(MessageFlags.FILE_ATTACHED)) fileId = buffer.readInt64();
        contentPacketId = buffer.readByte();
        contentPacketVersion = buffer.readByte();

        if (!hasMessageFlag(MessageFlags.UNENCRYPTED)) {
            KeyStore channelKeys = keyProvider.getChannelKeys(channelId);
            byte[] decryptedData = AesStatic.decryptWithHmac(buffer, 0, channelKeys.getHmacKey(), channelKeys.getAesKey());
            readContents(new PacketBuffer(decryptedData));
        } else readContents(buffer);

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

    public void writeToDatabase(PacketDirection packetDirection) {
        Channel channel = Storage.getDatabase().channelDao().getById(channelId);
        if (messageId > channel.getLatestMessage()) {
            channel.setLatestMessage(messageId);
            Storage.getDatabase().channelDao().update(channel);
        }
        Storage.getDatabase().channelMessageDao().insert(ChannelMessage.fromPacket(this));
        Storage.getDatabase().dependencyDao().insert(de.vectordata.skynet.data.model.Dependency.arrayFromPacket(this, dependencies));
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
