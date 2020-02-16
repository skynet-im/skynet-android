package de.vectordata.skynet.net.packet;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.skynet.crypto.Aes;
import de.vectordata.skynet.crypto.keys.ChannelKeys;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.annotation.Channel;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.MessageFlags;

@Flags(MessageFlags.UNENCRYPTED)
@Channel(ChannelType.DIRECT)
public class P1EGroupChannelUpdate extends ChannelMessagePacket {

    public long groupRevision;
    public List<Member> members = new ArrayList<>();
    public byte[] channelKey;
    public byte[] historyKey;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeInt64(groupRevision);
        buffer.writeUInt16(members.size());
        for (Member member : members) {
            buffer.writeInt64(member.accountId);
            buffer.writeByte(member.groupMemberFlags);
        }
        ChannelKeys channelKeys = keyProvider.getChannelKeys(getParent());
        PacketBuffer encrypted = new PacketBuffer();
        encrypted.writeByteArray(channelKey, true);
        encrypted.writeByteArray(historyKey, true);
        Aes.encryptSigned(encrypted.toArray(), buffer, true, channelKeys);
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        members.clear();
        groupRevision = buffer.readInt64();
        int count = buffer.readUInt16();
        for (int i = 0; i < count; i++) {
            members.add(new Member(buffer.readInt64(), buffer.readByte()));
        }
        ChannelKeys channelKeys = keyProvider.getChannelKeys(getParent());
        PacketBuffer decrypted = new PacketBuffer(Aes.decryptSigned(buffer, 0, channelKeys));

    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x1E;
    }

    @Override
    public void writeToDatabase(PacketDirection packetDirection) {
    }

    public class Member {
        long accountId;
        byte groupMemberFlags;

        Member(long accountId, byte groupMemberFlags) {
            this.accountId = accountId;
            this.groupMemberFlags = groupMemberFlags;
        }
    }
}
