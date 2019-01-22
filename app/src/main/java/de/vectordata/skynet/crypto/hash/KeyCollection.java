package de.vectordata.skynet.crypto.hash;

import java.io.Serializable;

import de.vectordata.skynet.crypto.keys.KeyStore;

/**
 * Created by Twometer on 11.12.2018.
 * (c) 2018 Twometer
 */
public class KeyCollection implements Serializable {

    private KeyStore loopbackChannelKeys;
    private byte[] keyHash;

    KeyCollection(KeyStore loopbackChannelKeys, byte[] keyHash) {
        this.loopbackChannelKeys = loopbackChannelKeys;
        this.keyHash = keyHash;
    }

    public KeyStore getLoopbackChannelKeys() {
        return loopbackChannelKeys;
    }

    public byte[] getKeyHash() {
        return keyHash;
    }

}
