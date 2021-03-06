package de.vectordata.skynet.net.packet.base;

import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import de.vectordata.skynet.crypto.Aes;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChannelMessage;
import de.vectordata.skynet.data.model.Dependency;
import de.vectordata.skynet.data.model.FileInfo;
import de.vectordata.skynet.net.client.LengthPrefix;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.model.MessageFlags;
import de.vectordata.skynet.util.Selector;
import de.vectordata.skynet.util.date.DateTime;

public abstract class ChannelMessagePacket extends AbstractPacket {
    public byte packetVersion;
    public long channelId;
    public long senderId;
    public long messageId;
    public long skipCount;
    public DateTime dispatchTime;
    public byte messageFlags;
    public long fileId;
    public FileInfo attachedFile;
    public List<NetDependency> dependencies = new ArrayList<>();
    public boolean isCorrupted;

    public abstract void readContents(PacketBuffer buffer, KeyProvider keyProvider);

    public abstract void writeContents(PacketBuffer buffer, KeyProvider keyProvider);

    public abstract void persistContents(PacketDirection direction);

    private void readFile(PacketBuffer buf) {
        if (!hasFlag(MessageFlags.MEDIA_MESSAGE)) {
            attachedFile = null;
            return;
        }

        attachedFile = new FileInfo(
                channelId,
                messageId,
                buf.readString(LengthPrefix.SHORT),     // Name
                buf.readDate(),                         // Creation Time
                buf.readDate(),                         // Last Write TIme
                buf.readString(LengthPrefix.SHORT),     // Thumbnail Content Type
                buf.readByteArray(LengthPrefix.MEDIUM)  // Thumbnail Data
        );

        if (hasFlag(MessageFlags.EXTERNAL_FILE)) {
            attachedFile.setContentType(buf.readString(LengthPrefix.SHORT));
            attachedFile.setLength(buf.readInt64());
            attachedFile.setKey(buf.readBytes(32));
        }
    }

    private void writeFile(PacketBuffer buf) {
        if (!hasFlag(MessageFlags.MEDIA_MESSAGE) || attachedFile == null)
            return;

        buf.writeString(attachedFile.getName(), LengthPrefix.SHORT);
        buf.writeDate(attachedFile.getCreationTime());
        buf.writeDate(attachedFile.getLastWriteTime());
        buf.writeString(attachedFile.getThumbnailContentType(), LengthPrefix.SHORT);
        buf.writeByteArray(attachedFile.getThumbnail(), LengthPrefix.MEDIUM);

        if (hasFlag(MessageFlags.EXTERNAL_FILE)) {
            buf.writeString(attachedFile.getContentType(), LengthPrefix.SHORT);
            buf.writeInt64(attachedFile.getLength());
            buf.writeByteArray(attachedFile.getKey(), LengthPrefix.NONE);
        }
    }

    @Override
    public final void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        isCorrupted = false;

        packetVersion = buffer.readByte();
        channelId = buffer.readInt64();
        senderId = buffer.readInt64();
        messageId = buffer.readInt64();
        skipCount = buffer.readInt64();
        dispatchTime = buffer.readDate();
        messageFlags = buffer.readByte();
        if (hasFlag(MessageFlags.EXTERNAL_FILE))
            fileId = buffer.readInt64();

        byte[] packetContent = buffer.readByteArray(LengthPrefix.MEDIUM);
        if (hasFlag(MessageFlags.UNENCRYPTED)) {
            PacketBuffer contentBuffer = new PacketBuffer(packetContent);
            readContents(contentBuffer, keyProvider);
            readFile(contentBuffer);
        } else {
            try {
                PacketBuffer contentBuffer = new PacketBuffer(Aes.decryptSigned(packetContent, keyProvider.getChannelKeys(channelId)));
                readContents(contentBuffer, keyProvider);
                readFile(contentBuffer);
            } catch (StreamCorruptedException e) {
                isCorrupted = true;
            }
        }

        int dependencyCount = buffer.readUInt16();
        for (int i = 0; i < dependencyCount; i++)
            dependencies.add(new NetDependency(buffer.readInt64(), buffer.readInt64()));
    }

    @Override
    public final void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeByte(packetVersion);
        buffer.writeInt64(channelId);
        buffer.writeInt64(messageId);
        buffer.writeByte(messageFlags);
        if (hasFlag(MessageFlags.EXTERNAL_FILE))
            buffer.writeInt64(fileId);

        PacketBuffer contentBuffer = new PacketBuffer(PacketBuffer.SIZE_MEDIUM);
        writeContents(contentBuffer, keyProvider);
        writeFile(contentBuffer);

        byte[] packetContent;
        if (hasFlag(MessageFlags.UNENCRYPTED)) {
            packetContent = contentBuffer.toArray();
        } else {
            packetContent = Aes.encryptSigned(contentBuffer.toArray(), keyProvider.getChannelKeys(channelId));
        }
        buffer.writeByteArray(packetContent, LengthPrefix.MEDIUM);

        buffer.writeUInt16(dependencies.size());
        for (NetDependency dependency : dependencies) {
            buffer.writeInt64(dependency.accountId);
            buffer.writeInt64(dependency.messageId);
        }
    }

    public void persist(PacketDirection packetDirection) {
        if (packetDirection == PacketDirection.RECEIVE) {
            Channel channel = Storage.getDatabase().channelDao().getById(channelId);
            if (messageId > channel.getLatestMessage()) {
                channel.setLatestMessage(messageId);
                Storage.getDatabase().channelDao().update(channel);
            }
        }
        Storage.getDatabase().channelMessageDao().insert(ChannelMessage.fromPacket(this));
        Storage.getDatabase().dependencyDao().insert(Dependency.arrayFromPacket(this, dependencies));
        if (hasFlag(MessageFlags.MEDIA_MESSAGE) && attachedFile != null)
            Storage.getDatabase().fileInfoDao().insert(attachedFile);
        persistContents(packetDirection);
    }


    public NetDependency singleDependency() {
        if (dependencies.size() != 1)
            throw new IllegalStateException("Asked for exactly one dependency, but had " + dependencies.size());
        return dependencies.get(0);
    }

    public NetDependency findDependency(Selector<NetDependency> selector) {
        for (NetDependency dep : dependencies)
            if (selector.test(dep))
                return dep;
        return null;
    }

    public boolean isSentByMe() {
        return Storage.getSession().getAccountId() == senderId;
    }

    public boolean hasFlag(byte flag) {
        return (messageFlags & flag) != 0;
    }

    public static class NetDependency {
        public long accountId;
        public long messageId;

        public NetDependency(long accountId, long messageId) {
            this.accountId = accountId;
            this.messageId = messageId;
        }
    }

}
