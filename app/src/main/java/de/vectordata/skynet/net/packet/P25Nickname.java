package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Nickname;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.annotation.Channel;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.MessageFlags;

@Flags(MessageFlags.UNENCRYPTED)
@Channel(ChannelType.PROFILE_DATA)
public class P25Nickname extends ChannelMessagePacket {

    public String nickname;

    public P25Nickname(String nickname) {
        this.nickname = nickname;
    }

    public P25Nickname() {
    }

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeString(nickname);
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        nickname = buffer.readString();
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x25;
    }

    @Override
    public void writeToDatabase(PacketDirection packetDirection) {
        Storage.getDatabase().nicknameDao().insert(Nickname.fromPacket(this));
    }
}
