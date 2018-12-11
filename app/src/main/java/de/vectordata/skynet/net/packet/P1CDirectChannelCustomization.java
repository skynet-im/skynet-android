package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.model.ImageShape;
import de.vectordata.skynet.net.model.MessageFlags;
import de.vectordata.skynet.net.packet.annotation.Channel;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;

@Flags(MessageFlags.LOOPBACK)
@Channel(ChannelType.DIRECT)
public class P1CDirectChannelCustomization extends ChannelMessagePacket {

    public String customNickname;
    public ImageShape imageShape;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeString(customNickname);
        buffer.writeByte((byte) imageShape.ordinal());
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        customNickname = buffer.readString();
        imageShape = ImageShape.values()[buffer.readByte()];
    }

    @Override
    public void handlePacket(PacketHandler handler) {
    handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x1C;
    }
}
