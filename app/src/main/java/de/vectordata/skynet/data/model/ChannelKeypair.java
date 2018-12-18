package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import de.vectordata.skynet.net.model.KeyRole;

/**
 * Created by Twometer on 18.12.2018.
 * (c) 2018 Twometer
 */
@Entity(tableName = "channelKeypairs", foreignKeys = @ForeignKey(
        entity = Channel.class,
        parentColumns = "channelId",
        childColumns = "channelId",
        onDelete = ForeignKey.CASCADE
))
public class ChannelKeypair {

    private long channelId;

    private KeyRole keyRole;

    private byte[] publicKey;

    private byte[] privateKey;

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public KeyRole getKeyRole() {
        return keyRole;
    }

    public void setKeyRole(KeyRole keyRole) {
        this.keyRole = keyRole;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }
}
