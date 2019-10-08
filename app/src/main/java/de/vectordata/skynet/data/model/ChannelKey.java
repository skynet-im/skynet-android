package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.TypeConverters;

import de.vectordata.skynet.data.model.enums.KeyType;
import de.vectordata.skynet.data.sql.converters.KeyFormatConverter;
import de.vectordata.skynet.data.sql.converters.KeyTypeConverter;
import de.vectordata.skynet.net.packet.P17PrivateKeys;
import de.vectordata.skynet.net.packet.P18PublicKeys;
import de.vectordata.skynet.net.packet.model.KeyFormat;

@Entity(tableName = "channelKeys", foreignKeys = @ForeignKey(
        entity = ChannelMessage.class,
        parentColumns = {"channelId", "messageId"},
        childColumns = {"channelId", "messageId"},
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
        primaryKeys = {"channelId", "messageId"}
)
public class ChannelKey {

    private long channelId;

    private long messageId;

    @TypeConverters(KeyTypeConverter.class)
    private KeyType keyType;

    @TypeConverters(KeyFormatConverter.class)
    private KeyFormat signatureKeyFormat;

    private byte[] signatureKey;

    @TypeConverters(KeyFormatConverter.class)
    private KeyFormat derivationKeyFormat;

    private byte[] derivationKey;

    public static ChannelKey fromPacket(P17PrivateKeys packet) {
        ChannelKey channelKey = new ChannelKey();
        channelKey.channelId = packet.getParent().channelId;
        channelKey.messageId = packet.getParent().messageId;
        channelKey.keyType = KeyType.PRIVATE;
        channelKey.signatureKeyFormat = packet.signatureKey.format;
        channelKey.signatureKey = packet.signatureKey.key;
        channelKey.derivationKeyFormat = packet.derivationKey.format;
        channelKey.derivationKey = packet.derivationKey.key;
        return channelKey;
    }

    public static ChannelKey fromPacket(P18PublicKeys packet) {
        ChannelKey channelKey = new ChannelKey();
        channelKey.channelId = packet.getParent().channelId;
        channelKey.messageId = packet.getParent().messageId;
        channelKey.keyType = KeyType.PUBLIC;
        channelKey.signatureKeyFormat = packet.signatureKey.format;
        channelKey.signatureKey = packet.signatureKey.key;
        channelKey.derivationKeyFormat = packet.derivationKey.format;
        channelKey.derivationKey = packet.derivationKey.key;
        return channelKey;
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public KeyType getKeyType() {
        return keyType;
    }

    public void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }

    public KeyFormat getSignatureKeyFormat() {
        return signatureKeyFormat;
    }

    public void setSignatureKeyFormat(KeyFormat signatureKeyFormat) {
        this.signatureKeyFormat = signatureKeyFormat;
    }

    public byte[] getSignatureKey() {
        return signatureKey;
    }

    public void setSignatureKey(byte[] signatureKey) {
        this.signatureKey = signatureKey;
    }

    public KeyFormat getDerivationKeyFormat() {
        return derivationKeyFormat;
    }

    public void setDerivationKeyFormat(KeyFormat derivationKeyFormat) {
        this.derivationKeyFormat = derivationKeyFormat;
    }

    public byte[] getDerivationKey() {
        return derivationKey;
    }

    public void setDerivationKey(byte[] derivationKey) {
        this.derivationKey = derivationKey;
    }
}
