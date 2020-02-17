package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.DirectChannelCustomization;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.LengthPrefix;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.ImageShape;
import de.vectordata.skynet.net.packet.model.MessageFlags;

@Flags(MessageFlags.LOOPBACK)
public class P1CDirectChannelCustomization extends ChannelMessagePacket {

    public String customNickname;
    public ImageShape imageShape;

    @Override
    public void writeContents(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeString(customNickname, LengthPrefix.SHORT);
        buffer.writeByte((byte) imageShape.ordinal());
    }

    @Override
    public void readContents(PacketBuffer buffer, KeyProvider keyProvider) {
        customNickname = buffer.readString(LengthPrefix.SHORT);
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
    public void persistContents(PacketDirection packetDirection) {
        Storage.getDatabase().directChannelCustomizationDao().insert(DirectChannelCustomization.fromPacket(this));
    }
}
