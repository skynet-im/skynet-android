package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.KeyProvider;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.model.ImageShape;
import de.vectordata.skynet.net.model.MessageFlags;
import de.vectordata.skynet.net.packet.annotation.ChannelMessage;
import de.vectordata.skynet.net.packet.annotation.Flags;

@Flags(MessageFlags.LOOPBACK)
@ChannelMessage(ChannelType.DIRECT)
public class P1CDirectChannelCustomization implements Packet {

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

    }

    @Override
    public byte getId() {
        return 0x1C;
    }
}
