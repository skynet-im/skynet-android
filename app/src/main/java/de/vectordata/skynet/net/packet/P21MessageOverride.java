package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.crypt.AesStatic;
import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.crypto.keys.KeyStore;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChannelMessage;
import de.vectordata.skynet.data.model.ChatMessage;
import de.vectordata.skynet.data.model.DaystreamMessage;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.OverrideAction;

public class P21MessageOverride extends ChannelMessagePacket {

    public static final long OVERWITE_TIMEOUT = 10 * 60 * 1000;

    public long messageId;
    public OverrideAction action;
    public String newText;

    public P21MessageOverride() {
    }

    public P21MessageOverride(long messageId, OverrideAction action) {
        this.messageId = messageId;
        this.action = action;
    }

    public P21MessageOverride(long messageId, OverrideAction action, String newText) {
        this.messageId = messageId;
        this.action = action;
        this.newText = newText;
    }

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        KeyStore keyStore = keyProvider.getMessageKeys(getParent());
        PacketBuffer encrypted = new PacketBuffer();
        encrypted.writeInt64(messageId);
        encrypted.writeByte((byte) action.ordinal());
        if (action == OverrideAction.EDIT)
            encrypted.writeString(newText);
        AesStatic.encryptWithHmac(encrypted.toArray(), buffer, true, keyStore.getHmacKey(), keyStore.getAesKey());
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        KeyStore keyStore = keyProvider.getMessageKeys(getParent());
        PacketBuffer decrypted = new PacketBuffer(AesStatic.decryptWithHmac(buffer, 0, keyStore.getHmacKey(), keyStore.getAesKey()));
        messageId = decrypted.readInt64();
        action = OverrideAction.values()[decrypted.readByte()];
        if (action == OverrideAction.EDIT)
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

    @Override
    public void writeToDatabase(PacketDirection packetDirection) {
        Channel channel = Storage.getDatabase().channelDao().getById(getParent().channelId);
        if (channel.getChannelType() == ChannelType.PROFILE_DATA) {
            DaystreamMessage message = Storage.getDatabase().daystreamMessageDao().get(channel.getChannelId(), messageId);
            if (action == OverrideAction.DELETE)
                Storage.getDatabase().daystreamMessageDao().delete(message);
            else {
                message.setText(newText);
                message.setEdited(true);
                Storage.getDatabase().daystreamMessageDao().update(message);
            }
        } else {
            ChatMessage message = Storage.getDatabase().chatMessageDao().query(getParent().channelId, messageId);
            if (action == OverrideAction.DELETE) {
                message.setText(ChatMessage.DELETED);
                SkynetContext.getCurrent().getNotificationManager().onMessageDeleted(getParent().channelId, messageId);
            } else {
                message.setText(newText);
                message.setEdited(true);
            }
            Storage.getDatabase().chatMessageDao().update(message);
        }
    }

    @Override
    public boolean validatePacket() {
        ChannelMessage targetMessage = Storage.getDatabase().channelMessageDao().getById(getParent().channelId, messageId);
        long originalDate = targetMessage.getDispatchTime().toJavaDate().getTime();
        long modifyDate = getParent().dispatchTime.toJavaDate().getTime();
        if (modifyDate - originalDate > OVERWITE_TIMEOUT)
            return false;
        return getParent().senderId == targetMessage.getSenderId();
    }

}
