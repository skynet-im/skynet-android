package de.vectordata.skynet.net.packet;

import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import de.vectordata.skynet.crypto.Aes;
import de.vectordata.skynet.crypto.keys.ChannelKeys;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.LengthPrefix;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.MessageFlags;

@Flags(MessageFlags.UNENCRYPTED)
public class P1EGroupChannelUpdate extends ChannelMessagePacket {

    public long groupRevision;
    public List<Member> members = new ArrayList<>();
    public byte[] channelKey;
    public byte[] historyKey;

    @Override
    public void writeContents(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeInt64(groupRevision);
        buffer.writeUInt16(members.size());
        for (Member member : members) {
            buffer.writeInt64(member.accountId);
            buffer.writeByte(member.groupMemberFlags);
        }

        ChannelKeys channelKeys = keyProvider.getChannelKeys(channelId);
        PacketBuffer encrypted = new PacketBuffer();
        encrypted.writeByteArray(channelKey, LengthPrefix.NONE);
        encrypted.writeByteArray(historyKey, LengthPrefix.NONE);
        buffer.writeByteArray(Aes.encryptSigned(encrypted.toArray(), channelKeys), LengthPrefix.MEDIUM);
    }

    @Override
    public void readContents(PacketBuffer buffer, KeyProvider keyProvider) {
        groupRevision = buffer.readInt64();
        int count = buffer.readUInt16();
        for (int i = 0; i < count; i++) {
            members.add(new Member(buffer.readInt64(), buffer.readByte()));
        }

        byte[] keyHistory = buffer.readByteArray(LengthPrefix.MEDIUM);
        if (keyHistory.length > 0) {
            try {
                PacketBuffer decrypted = new PacketBuffer(Aes.decryptSigned(keyHistory, keyProvider.getChannelKeys(channelId)));
                channelKey = decrypted.readBytes(64);
                historyKey = decrypted.readBytes(64);
            } catch (StreamCorruptedException e) {
                isCorrupted = true;
            }
        }
    }

    @Override
    public void persistContents(PacketDirection direction) {
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x1E;
    }

    public static class Member {
        long accountId;
        byte groupMemberFlags;

        Member(long accountId, byte groupMemberFlags) {
            this.accountId = accountId;
            this.groupMemberFlags = groupMemberFlags;
        }
    }
}
