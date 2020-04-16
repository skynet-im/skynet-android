package de.vectordata.skynet.crypto.hash;

import java.io.Serializable;

import de.vectordata.skynet.crypto.keys.ChannelKeys;

/**
 * Created by Twometer on 11.12.2018.
 * (c) 2018 Twometer
 */
public class KeyCollection implements Serializable {

    private ChannelKeys loopbackChannelKeys;
    private byte[] keyHash;

    KeyCollection(ChannelKeys loopbackChannelKeys, byte[] keyHash) {
        this.loopbackChannelKeys = loopbackChannelKeys;
        this.keyHash = keyHash;
    }

    public ChannelKeys getLoopbackChannelKeys() {
        return loopbackChannelKeys;
    }

    public byte[] getKeyHash() {
        return keyHash;
    }

}
