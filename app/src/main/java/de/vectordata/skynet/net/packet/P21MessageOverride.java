package de.vectordata.skynet.net.packet;

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

    public static final long OVERWRITE_TIMEOUT = 10 * 60 * 1000;

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
        buffer.writeByte((byte) action.ordinal());
        if (action == OverrideAction.EDIT)
            buffer.writeString(newText, LengthPrefix.MEDIUM);
    }

    @Override
    public void readContents(PacketBuffer buffer, KeyProvider keyProvider) {
       action = OverrideAction.values()[buffer.readByte()];
        if (action == OverrideAction.EDIT)
            newText = buffer.readString(LengthPrefix.MEDIUM);
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
        long targetMessageId = singleDependency().messageId;
        Channel channel = Storage.getDatabase().channelDao().getById(channelId);
        if (channel.getChannelType() == ChannelType.PROFILE_DATA) {
            DaystreamMessage message = Storage.getDatabase().daystreamMessageDao().get(channelId, targetMessageId);
            if (action == OverrideAction.DELETE)
                Storage.getDatabase().daystreamMessageDao().delete(message);
            else {
                message.setText(newText);
                message.setEdited(true);
                Storage.getDatabase().daystreamMessageDao().update(message);
            }
        } else {
            ChatMessage message = Storage.getDatabase().chatMessageDao().query(channelId, targetMessageId);
            if (action == OverrideAction.DELETE) {
                message.setText(ChatMessage.DELETED);
                message.setEdited(false);
                SkynetContext.getCurrent().getNotificationManager().onMessageDeleted(channelId, targetMessageId);
            } else {
                message.setText(newText);
                message.setEdited(true);
            }
            Storage.getDatabase().chatMessageDao().update(message);
        }
    }

    @Override
    public boolean validatePacket() {
        if (dependencies.size() == 0)
            return false;

        ChannelMessage targetMessage = Storage.getDatabase().channelMessageDao().getById(channelId, dependencies.get(0).messageId);
        long originalDate = targetMessage.getDispatchTime().toJavaDate().getTime();
        long modifyDate = dispatchTime.toJavaDate().getTime();
        if (modifyDate - originalDate > OVERWRITE_TIMEOUT)
            return false;
        return senderId == targetMessage.getSenderId();
    }

}
