package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;

import de.vectordata.skynet.net.packet.P27ProfileImage;

@Entity(tableName = "profileImages", foreignKeys = @ForeignKey(
        entity = ChannelMessage.class,
        parentColumns = {"channelId", "messageId"},
        childColumns = {"channelId", "messageId"},
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
        primaryKeys = {"channelId", "messageId"}
)
public class ProfileImage {

    private long channelId;

    private long messageId;

    private String caption;

    public static ProfileImage fromPacket(P27ProfileImage packet) {
        ProfileImage profileImage = new ProfileImage();
        profileImage.channelId = packet.getParent().channelId;
        profileImage.messageId = packet.getParent().messageId;
        profileImage.caption = packet.caption;
        return profileImage;
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

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
