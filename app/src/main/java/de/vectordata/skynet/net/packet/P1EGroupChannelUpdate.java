package de.vectordata.skynet.net.packet;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.libjvsl.crypt.AesStatic;
import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.crypto.keys.KeyStore;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
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
        KeyStore channelKeys = keyProvider.getChannelKeys(getParent().channelId);
        PacketBuffer encrypted = new PacketBuffer();
        encrypted.writeByteArray(channelKey, true);
        encrypted.writeByteArray(historyKey, true);
        AesStatic.encryptWithHmac(encrypted.toArray(), buffer, true, channelKeys.getHmacKey(), channelKeys.getAesKey());
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        members.clear();
        groupRevision = buffer.readInt64();
        int count = buffer.readUInt16();
        for (int i = 0; i < count; i++) {
            members.add(new Member(buffer.readInt64(), buffer.readByte()));
        }
        KeyStore keyStore = keyProvider.getChannelKeys(getParent().channelId);
        PacketBuffer decrypted = new PacketBuffer(AesStatic.decryptWithHmac(buffer, 0, keyStore.getHmacKey(), keyStore.getAesKey()));

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
    public void writeToDatabase() {
    }

    public class Member {
        long accountId;
        byte groupMemberFlags;

        public Member(long accountId, byte groupMemberFlags) {
            this.accountId = accountId;
            this.groupMemberFlags = groupMemberFlags;
        }
    }
}
