package de.vectordata.skynet.crypto.keys;

import java.io.Serializable;

import de.vectordata.skynet.net.client.ByteUtils;

public class KeyStore implements Serializable {

    private byte[] aesKey;
    private byte[] hmacKey;

    public KeyStore(byte[] aesKey, byte[] hmacKey) {
        this.aesKey = aesKey;
        this.hmacKey = hmacKey;
    }

    public static KeyStore from64ByteArray(byte[] data) {
        byte[] aesKey = ByteUtils.takeBytes(data, 32, 0);
        byte[] hmacKey = ByteUtils.takeBytes(data, 32, 31);
        return new KeyStore(aesKey, hmacKey);
    }

    public byte[] getAesKey() {
        return aesKey;
    }

    public byte[] getHmacKey() {
        return hmacKey;
    }
}
