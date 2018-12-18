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

}
