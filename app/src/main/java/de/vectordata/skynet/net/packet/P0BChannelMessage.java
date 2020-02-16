package de.vectordata.skynet.net.packet;

import android.util.Log;

import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import de.vectordata.skynet.crypto.Aes;
import de.vectordata.skynet.crypto.keys.ChannelKeys;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChannelMessage;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.base.AbstractPacket;
import de.vectordata.skynet.net.packet.model.MessageFlags;
import de.vectordata.skynet.util.Selector;
import de.vectordata.skynet.util.date.DateTime;


public class P0BChannelMessage extends AbstractPacket {

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
    public boolean isCorrupted;


    boolean hasFlag(byte flag) {
        return (messageFlags & flag) != 0;
    }

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeByte(packetVersion);
        buffer.writeInt64(channelId);
        buffer.writeInt64(messageId);
        buffer.writeByte(messageFlags);
        if (hasFlag(MessageFlags.FILE_ATTACHED)) buffer.writeInt64(fileId);
        buffer.writeByte(contentPacketId);
        buffer.writeByte(contentPacketVersion);
        Log.d("P0BChannelMessage", String.format("Writing channel Message with content id %s: unencrypted=%s fileAttached=%s", contentPacketId, hasFlag(MessageFlags.UNENCRYPTED), hasFlag(MessageFlags.FILE_ATTACHED)));
        if (hasFlag(MessageFlags.UNENCRYPTED)) writeContents(buffer);
        else {
            ChannelKeys channelKeys = keyProvider.getChannelKeys(this);
            PacketBuffer encryptedBuffer = new PacketBuffer();
            writeContents(encryptedBuffer);
            Aes.encryptSigned(encryptedBuffer.toArray(), buffer, true, channelKeys);
        }

        buffer.writeUInt16(dependencies.size());
        for (Dependency dependency : dependencies) {
            buffer.writeInt64(dependency.accountId);
            buffer.writeInt64(dependency.channelId);
            buffer.writeInt64(dependency.messageId);
        }
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        isCorrupted = false;

        dependencies.clear();
        packetVersion = buffer.readByte();
        channelId = buffer.readInt64();
        senderId = buffer.readInt64();
        messageId = buffer.readInt64();
        skipCount = buffer.readInt64();
        dispatchTime = buffer.readDate();
        messageFlags = buffer.readByte();
        if (hasFlag(MessageFlags.FILE_ATTACHED)) fileId = buffer.readInt64();
        contentPacketId = buffer.readByte();
        contentPacketVersion = buffer.readByte();

        if (!hasFlag(MessageFlags.UNENCRYPTED)) {
            ChannelKeys channelKeys = keyProvider.getChannelKeys(this);
            try {
                byte[] decryptedData = Aes.decryptSigned(buffer, 0, channelKeys);
                readContents(new PacketBuffer(decryptedData));
            } catch (StreamCorruptedException e) {
                isCorrupted = true;
                return;
            }
        } else readContents(buffer);

        int dependencyCount = buffer.readUInt16();
        for (int i = 0; i < dependencyCount; i++)
            dependencies.add(new Dependency(buffer.readInt64(), buffer.readInt64(), buffer.readInt64()));
    }

    private void writeContents(PacketBuffer buffer) {
        buffer.writeByteArray(contentPacket, true);
        if (hasFlag(MessageFlags.FILE_ATTACHED))
            buffer.writeByteArray(fileKey, true);
    }

    private void readContents(PacketBuffer packetBuffer) {
        contentPacket = packetBuffer.readByteArray();
        if ((messageFlags & MessageFlags.FILE_ATTACHED) != 0)
            fileKey = packetBuffer.readByteArray();
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
        if (packetDirection == PacketDirection.RECEIVE) {
            Channel channel = Storage.getDatabase().channelDao().getById(channelId);
            if (messageId > channel.getLatestMessage()) {
                channel.setLatestMessage(messageId);
                Storage.getDatabase().channelDao().update(channel);
            }
        }
        Storage.getDatabase().channelMessageDao().insert(ChannelMessage.fromPacket(this));
        Storage.getDatabase().dependencyDao().insert(de.vectordata.skynet.data.model.Dependency.arrayFromPacket(this, dependencies));
    }

    public Dependency singleDependency() {
        if (dependencies.size() != 1)
            throw new IllegalStateException("Asked for exactly one dependency, but had " + dependencies.size());
        return dependencies.get(0);
    }

    public Dependency findDependency(Selector<Dependency> selector) {
        for (Dependency dep : dependencies)
            if (selector.test(dep))
                return dep;
        return null;
    }

    public static class Dependency {
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
