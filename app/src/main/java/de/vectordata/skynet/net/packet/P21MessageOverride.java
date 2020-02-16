package de.vectordata.skynet.net.packet;

import java.io.StreamCorruptedException;

import de.vectordata.skynet.crypto.Aes;
import de.vectordata.skynet.crypto.keys.ChannelKeys;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChannelMessage;
import de.vectordata.skynet.data.model.ChatMessage;
import de.vectordata.skynet.data.model.DaystreamMessage;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.client.LengthPrefix;
import de.vectordata.skynet.net.client.PacketBuffer;
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
    public void writeContents(PacketBuffer buffer, KeyProvider keyProvider) {
        ChannelKeys channelKeys = keyProvider.getChannelKeys(channelId);
        PacketBuffer encrypted = new PacketBuffer();
        encrypted.writeInt64(messageId);
        encrypted.writeByte((byte) action.ordinal());
        if (action == OverrideAction.EDIT)
            encrypted.writeString(newText, LengthPrefix.MEDIUM);
        Aes.encryptSigned(encrypted.toArray(), buffer, true, channelKeys);
    }

    @Override
    public void readContents(PacketBuffer buffer, KeyProvider keyProvider) {
        ChannelKeys channelKeys = keyProvider.getChannelKeys(channelId);
        try {
            PacketBuffer decrypted = new PacketBuffer(Aes.decryptSigned(buffer, 0, channelKeys));
            messageId = decrypted.readInt64();
            action = OverrideAction.values()[decrypted.readByte()];
            if (action == OverrideAction.EDIT)
                newText = decrypted.readString(LengthPrefix.MEDIUM);
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        }
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
    public void persistContents(PacketDirection packetDirection) {
        Channel channel = Storage.getDatabase().channelDao().getById(channelId);
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
            ChatMessage message = Storage.getDatabase().chatMessageDao().query(channelId, messageId);
            if (action == OverrideAction.DELETE) {
                message.setText(ChatMessage.DELETED);
                message.setEdited(false);
                SkynetContext.getCurrent().getNotificationManager().onMessageDeleted(channelId, messageId);
            } else {
                message.setText(newText);
                message.setEdited(true);
            }
            Storage.getDatabase().chatMessageDao().update(message);
        }
    }

    @Override
    public boolean validatePacket() {
        ChannelMessage targetMessage = Storage.getDatabase().channelMessageDao().getById(channelId, messageId);
        long originalDate = targetMessage.getDispatchTime().toJavaDate().getTime();
        long modifyDate = dispatchTime.toJavaDate().getTime();
        if (modifyDate - originalDate > OVERWITE_TIMEOUT)
            return false;
        return senderId == targetMessage.getSenderId();
    }

}
