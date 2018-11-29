package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.crypt.AesStatic;
import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.KeyProvider;
import de.vectordata.skynet.crypto.KeyStore;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.model.OverrideAction;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;

public class P21MessageOverride extends ChannelMessagePacket {

    public long messageId;
    public OverrideAction action;
    public String newText;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        KeyStore keyStore = keyProvider.getChannelKeys(getParent().channelId);
        PacketBuffer encrypted = new PacketBuffer();
        encrypted.writeInt64(messageId);
        encrypted.writeByte((byte) action.ordinal());
        if (action == OverrideAction.EDIT)
            encrypted.writeString(newText);
        AesStatic.encryptWithHmac(encrypted.toArray(), buffer, true, keyStore.getHmacKey(), keyStore.getAesKey());
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        KeyStore keyStore = keyProvider.getChannelKeys(getParent().channelId);
        PacketBuffer decrypted = new PacketBuffer(AesStatic.decryptWithHmac(buffer, 0, keyStore.getHmacKey(), keyStore.getAesKey()));
        messageId = decrypted.readInt64();
        action = OverrideAction.values()[decrypted.readByte()];
        if(action == OverrideAction.EDIT)
            newText = decrypted.readString();
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x21;
    }
}
