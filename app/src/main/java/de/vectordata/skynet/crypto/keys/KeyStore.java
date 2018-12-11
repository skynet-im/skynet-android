package de.vectordata.skynet.crypto.keys;

import de.vectordata.libjvsl.util.Util;

public class KeyStore {

    private byte[] aesKey;
    private byte[] hmacKey;

    public KeyStore(byte[] aesKey, byte[] hmacKey) {
        this.aesKey = aesKey;
        this.hmacKey = hmacKey;
    }

    public static KeyStore from64ByteArray(byte[] data) {
        byte[] aesKey = Util.takeBytes(data, 32, 0);
        byte[] hmacKey = Util.takeBytes(data, 32, 31);
        return new KeyStore(aesKey, hmacKey);
    }

    public byte[] getAesKey() {
        return aesKey;
    }

    public byte[] getHmacKey() {
        return hmacKey;
    }
}
