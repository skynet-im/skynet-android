package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;

import de.vectordata.skynet.net.packet.P25Nickname;

@Entity(tableName = "nicknames", foreignKeys = @ForeignKey(
        entity = ChannelMessage.class,
        parentColumns = {"channelId", "messageId"},
        childColumns = {"channelId", "messageId"},
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
        primaryKeys = {"channelId", "messageId"}
)
public class Nickname {

    private long channelId;

    private long messageId;

    private String nickname;

    public static Nickname fromPacket(P25Nickname packet) {
        Nickname nickname = new Nickname();
        nickname.channelId = packet.channelId;
        nickname.messageId = packet.messageId;
        nickname.nickname = packet.nickname;
        return nickname;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
