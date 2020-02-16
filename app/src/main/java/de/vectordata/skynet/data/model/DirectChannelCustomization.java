package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.TypeConverters;

import de.vectordata.skynet.data.sql.converters.ImageShapeConverter;
import de.vectordata.skynet.net.packet.P1CDirectChannelCustomization;
import de.vectordata.skynet.net.packet.model.ImageShape;

@Entity(tableName = "directChannelCustomizations", foreignKeys = @ForeignKey(
        entity = ChannelMessage.class,
        parentColumns = {"channelId", "messageId"},
        childColumns = {"channelId", "messageId"},
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
        primaryKeys = {"channelId", "messageId"}
)
public class DirectChannelCustomization {

    private long channelId;

    private long messageId;

    private String customNickname;

    @TypeConverters(ImageShapeConverter.class)
    private ImageShape profileImageShape;

    public static DirectChannelCustomization fromPacket(P1CDirectChannelCustomization packet) {
        DirectChannelCustomization customization = new DirectChannelCustomization();
        customization.channelId = packet.channelId;
        customization.messageId = packet.messageId;
        customization.customNickname = packet.customNickname;
        customization.profileImageShape = packet.imageShape;
        return customization;
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

    public String getCustomNickname() {
        return customNickname;
    }

    public void setCustomNickname(String customNickname) {
        this.customNickname = customNickname;
    }

    public ImageShape getProfileImageShape() {
        return profileImageShape;
    }

    public void setProfileImageShape(ImageShape profileImageShape) {
        this.profileImageShape = profileImageShape;
    }
}
