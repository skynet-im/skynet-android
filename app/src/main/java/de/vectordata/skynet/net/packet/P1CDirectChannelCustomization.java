package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.DirectChannelCustomization;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.annotation.Channel;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.ImageShape;
import de.vectordata.skynet.net.packet.model.MessageFlags;

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

    @Override
    public void writeToDatabase(PacketDirection packetDirection) {
        Storage.getDatabase().directChannelCustomizationDao().insert(DirectChannelCustomization.fromPacket(this));
    }
}
